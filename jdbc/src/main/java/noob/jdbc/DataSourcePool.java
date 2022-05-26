package noob.jdbc;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * 简单实现数据库连接池
 */
public class DataSourcePool implements DataSource {
    //url、账号、密码
    private String url;
    private String username;
    private String password;
    //初始化创建连接
    private int initCount = 2;
    //最大创建连接
    private int maxCount = 5;
    //当前池内有多少链接
    private int currentCount = 0;
    //链接池
    private LinkedList<Connection> pool = new LinkedList<>();

    /**
     * 创建对象加载配置
     * @param prop  数据库连接参数的配置文件
     */
    public DataSourcePool(Properties prop) {
        //读取属性值
        url = prop.getProperty("jdbc.url");
        username = prop.getProperty("jdbc.username");
        password = prop.getProperty("jdbc.password");
        //初始化连接
        for (int i = 0; i < initCount; i++) {
            pool.add(createConnection());
        }
    }

    /**
     * 创建数据库连接对象
     * @return 返回数据库连接对象
     */
    private Connection createConnection() {
        Optional<Connection> proxyConn;
        try {
            //1. 获取原始的数据库连接对象
            final Connection conn = DriverManager.getConnection(url, username, password);
            //2. 通过代理，接管close方法
            proxyConn = Optional.of((Connection) Proxy.newProxyInstance(
                    conn.getClass().getClassLoader(),
                    new Class[] { Connection.class },
                    (proxy, method, args) -> {
                        //检测方法是否为close
                        if ("close".equals(method.getName())) {
                            close(conn);
                            return null;
                        }
                        return method.invoke(conn, args);
                    }
            ));
            //当前连接+1
            currentCount++;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return proxyConn.orElseThrow(RuntimeException::new);
    }

    /**
     * 重写原始数据库关闭连接
     *  当前池中小于最小连接，放回池中，复用
     * @param conn  原生数据库连接对象
     */
    private void close(Connection conn) throws SQLException {
        //当前池中的连接小于最少连接，放回池中
        if (pool.size() < initCount) {
            pool.addLast(conn);
        }else{
            currentCount--;
            conn.close();
        }
    }

    /**
     * 从连接池中获取数据库连接
     * @return  返回连接对象
     */
    @Override
    public Connection getConnection() throws SQLException {
        //池中有连接直接返回
        if (pool.size() > 0) {
            return pool.removeFirst();
        }
        //判断当前连接释放达到上限
        if (currentCount < maxCount) {
            return createConnection();
        }
        throw new RuntimeException("连接达到上限，无法获取连接");
    }

    /**
     * 获取连接池
     * @return
     */
    public LinkedList<Connection> getPool() {
        return pool;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
