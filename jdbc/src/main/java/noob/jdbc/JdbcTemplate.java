package noob.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 增删改查工具类
 */
public class JdbcTemplate {
    /**
     * 执行增、删、改方法
     * @param sql   sql语句
     * @param args  占位符参数
     * @return
     */
    public static int execute(String sql, Object... args) {
        try {
            //1.获取预编译SQL
            final Connection conn = JdbcUtils.getConnection();
            final PreparedStatement ps = conn.prepareStatement(sql);
            //2.填充参数
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //3.执行SQL
            final int result = ps.executeUpdate();
            //4.释放资源
            JdbcUtils.close(conn, ps, null);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行查询SQL，返回当行单列结果
     * @param sql   sql语句
     * @param args  参数
     * @return
     * @throws SQLException
     */
    public static Integer queryInt(String sql, Object... args) {
        try {
            //1. 获取预编译SQL
            Connection conn = JdbcUtils.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            //2. 填充参数
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //3. 执行Sql
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            int result = resultSet.getInt(1);
            JdbcUtils.close(conn, ps, resultSet);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询单个结果
     * @param sql   sql语句
     * @param clazz 要返回的类对象
     * @param args  参数
     * @param <T>   要返回的类型
     * @return
     * @throws Exception
     */
    public static <T> T queryObject(String sql, Class<T> clazz, Object... args) {
        List<T> list = query(sql, clazz, args);
        if (list.size() == 0) {
            return null;
        }else if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException("查询到多个结果");
    }

    /**
     * 执行查询SQL
     * @param sql   sql语句
     * @param clazz 要返回的类对象
     * @param args  查询参数
     * @param <T>   要返回的类型
     * @return
     * @throws Exception
     */
    public static <T> List<T> query(String sql, Class<T> clazz, Object... args) {
        try {
            //1. 获取预编译SQL
            final Connection conn = JdbcUtils.getConnection();
            final PreparedStatement ps = conn.prepareStatement(sql);
            //2. 填充参数
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //3. 执行Sql
            ResultSet resultSet = ps.executeQuery();
            //4. 封装数据返回
            ArrayList<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(handle(resultSet, clazz));
            }
            //5. 释放资源
            JdbcUtils.close(conn, ps, resultSet);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 封装行数据
     * @param rs        结果集
     * @param clazz     要封装的类对象
     * @param <T>       要返回的类型
     * @return      返回封装好的对象
     * @throws Exception
     */
    private static <T> T handle(ResultSet rs, Class<T> clazz) throws Exception {
        //1. 获取实例
        T bean = clazz.newInstance();
        //2. 获取行数据
        ResultSetMetaData metaData = rs.getMetaData();
        //3. 获取列数
        int length = metaData.getColumnCount();
        //4. 遍历读取属性
        for (int i = 0; i < length; i++) {
            //获取列名
            String columnLabel = metaData.getColumnLabel(i + 1);
            //获取对应的值
            Object value = rs.getObject(columnLabel);
            //创建 描述对象
            PropertyDescriptor pd = new PropertyDescriptor(columnLabel, clazz);
            //获得写方法
            Method writeMethod = pd.getWriteMethod();
            //执行方法，写入数据
            writeMethod.invoke(bean, value);
        }
        //返回封装好的对象
        return bean;
    }

}
