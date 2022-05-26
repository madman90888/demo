package noob.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;

/**
 * 同线程获取到同一个数据库连接
 */
public class JdbcUtils {
    //简单实现的连接池
    private static DataSource dataSource;
    //确保同一线程获取到同一个对象
    private static final ThreadLocal<Connection> tl = new ThreadLocal<>();
    //事务的控制
    private static final ThreadLocal<Boolean> transaction = new ThreadLocal<>();

    //加载配置文件
    static {
        Properties prop = new Properties();
        try {
            //通过类路径读取配置文件
            prop.load(JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties"));
            //加载驱动
            Class.forName(prop.getProperty("jdbc.driver"));
            //创建连接池
            dataSource = new DataSourcePool(prop);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取连接池
     *
     * @return
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 同线程获取同一个连接对象
     *
     * @return
     */
    public static Connection getConnection() {
        final Optional<Connection> conn = Optional.ofNullable(tl.get());
        // tl没有获取到，则从连接池中获取
        return conn.orElseGet(() -> {
            try {
                Connection connection = dataSource.getConnection();
                tl.set(connection);
                return connection;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 默认关闭当前线程的数据库连接
     */
    public static void close() {
        close(tl.get(), null, null);
    }


    /**
     * 关闭连接
     *
     * @param conn      数据连接
     * @param statement 预编译SQL
     * @param resultSet 结果集
     */
    public static void close(Connection conn, Statement statement, ResultSet resultSet) {
        try {
            //判断是否在事务控制
            if (conn != null) {
                Boolean flag = transaction.get();
                if (flag == null || !flag) {
                    tl.remove();
                    conn.close();
                }
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始事务控制
     *
     * @throws SQLException
     */
    public static void begin() {
        try {
            transaction.set(true);
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 事务回滚
     *
     * @throws SQLException
     */
    public static void rollback() {
        try {
            transaction.set(false);
            Connection connection = getConnection();
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 事务提交
     *
     * @throws SQLException
     */
    public static void commit() {
        try {
            transaction.set(false);
            Connection connection = getConnection();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
