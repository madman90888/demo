package noob.jdbc.test;

import noob.jdbc.entity.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 使用 DBUtil + DBCP 连接池完成数据库操作
 */
public class DBCP2Test {

    private static DataSource dataSource;

    /**
     * 硬编码 使用DBCP连接池子
     */
    @Before
    public void create01() {
        BasicDataSource source = new BasicDataSource();
        // 设置连接信息
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/test");
        source.setUsername("root");
        source.setPassword("123456");
        dataSource = source;
        queryRunner = new QueryRunner(dataSource);
    }

    //创建QueryRunner对象
    private QueryRunner queryRunner;

    @Test
    public void test() throws SQLException {
        final User user = new User(3, "alice", "123456", 500.0);
        String sql = "insert into user(id, username, password, money) values(?, ?, ?, ?)";
        int result = queryRunner.execute(sql, user.getId(), user.getUsername(), user.getPassword(), user.getMoney());
        System.out.println("插入操作：" + result);
    }

    @Test
    public void update() throws SQLException {
        final User user = new User(3, null, null, 100.0);
        String sql = "update user set money=? where id=?";
        final int result = queryRunner.execute(sql, user.getMoney(), user.getId());
        System.out.println("更新操作：" + result);
    }

    @Test
    public void delete() throws SQLException {
        String sql = "delete from user where id = ?";
        final int result = queryRunner.execute(sql, 3);
        System.out.println("删除操作：" + result);
    }

    @Test
    public void selectById() throws SQLException {
        String sql = "select * from user where id = ?";
        final User result = queryRunner.query(sql, new BeanHandler<>(User.class), 1);
        System.out.println("查询单个：" + result);
    }

    @Test
    public void page() throws SQLException {
        int page = 1;
        int limit = 10;
        String sql = "select * from user limit ?, ?";
        final List<User> list = queryRunner.query(sql, new BeanListHandler<>(User.class), (page-1) * limit, limit);
        System.out.println("分页查询：" + list);
    }

    @Test
    public void total() throws SQLException {
        final long total =  queryRunner.query("select count(*) from user", new ScalarHandler<>());
        System.out.println("总条数：" + total);
    }

}
