package noob.jdbc.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import noob.jdbc.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * 使用 C3P0 + Spring JDBCTemplate 连接池完成数据库操作
 */
public class C3P0Test {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    /**
     * 硬编码 使用DBCP连接池子
     */
    @Before
    public void create01() {
        final ComboPooledDataSource source = new ComboPooledDataSource();
        dataSource = source;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Test
    public void insert() {
        final User user = new User(3, "alice", "123456", 500.0);
        String sql = "insert into user(id, username, password, money) values(?, ?, ?, ?)";
        int result = jdbcTemplate.update(sql, user.getId(), user.getUsername(), user.getPassword(), user.getMoney());
        System.out.println("插入操作：" + result);
    }

    @Test
    public void update() {
        final User user = new User(3, null, null, 100.0);
        String sql = "update user set money=? where id=?";
        final int result =  jdbcTemplate.update(sql, user.getMoney(), user.getId());
        System.out.println("更新操作：" + result);
    }

    @Test
    public void delete() {
        String sql = "delete from user where id = ?";
        final int result = jdbcTemplate.update(sql, 3);
        System.out.println("删除操作：" + result);
    }

    // 需要实现 RowMapper 接口
    @Test
    public void page() {
        int page = 1;
        int limit = 10;
        String sql = "select * from user limit ?, ?";
        final List<User> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new User(rs.getInt(1), null, null, rs.getDouble(4));
        }, (page-1) * limit, limit);
        System.out.println("分页查询：" + list);
    }

    @Test
    public void total() {
        final int total =  jdbcTemplate.queryForObject("select count(*) from user", Integer.class);
        System.out.println("总条数：" + total);
    }
}
