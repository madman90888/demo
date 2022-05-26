package noob.jdbc.test;

import noob.jdbc.JdbcTemplate;
import noob.jdbc.JdbcUtils;
import noob.jdbc.entity.User;
import org.junit.Test;

import java.util.List;

public class CustomerTest {

    // 增
    @Test
    public void insert() {
        final User user = new User(null, "alice", "123456", 500.0);
        String sql = "insert into user(username, password, money) values(?, ?, ?)";
        final int result = JdbcTemplate.execute(sql, user.getUsername(), user.getPassword(), user.getMoney());
        System.out.println("插入操作：" + result);
    }

    // 更新
    @Test
    public void update() {
        final User user = new User(3, null, null, 500.0);
        String sql = "update user set money=? where id=?";
        final int result = JdbcTemplate.execute(sql, user.getMoney(), user.getId());
        System.out.println("更新操作：" + result);
    }

    // 删除
    @Test
    public void delete() {
        String sql = "delete from user where id = ?";
        final int result = JdbcTemplate.execute(sql, 3);
        System.out.println("删除操作：" + result);
    }

    // 结果bean对象
    @Test
    public void selectById() {
        String sql = "select * from user where id = ?";
        final User result = JdbcTemplate.queryObject(sql, User.class, 1);
        System.out.println("查询单个：" + result);
    }

    // 结果List
    @Test
    public void page() {
        int page = 1;
        int limit = 10;
        String sql = "select * from user limit ?, ?";
        final List<User> list = JdbcTemplate.query(sql, User.class, (page-1) * limit, limit);
        System.out.println("分页查询：" + list);
    }

    // 一行一列
    @Test
    public void total() {
        final int total =  JdbcTemplate.queryInt("select count(*) from user");
        System.out.println("总条数：" + total);
    }

    // 事务控制
    @Test
    public void transaction() {
        try {
            // 开启事务
            JdbcUtils.begin();
            final User user1 = selectById(1);
            final User user2 = selectById(2);
            user1.setMoney(user1.getMoney() - 100);
            user2.setMoney(user2.getMoney() + 100);
            update(user1);
//            int i = 1/0;
            update(user2);
            JdbcUtils.commit();
        }catch (Exception e) {
            JdbcUtils.rollback();
        }
    }

    public User selectById(int id) {
        String sql = "select * from user where id = ?";
        return JdbcTemplate.queryObject(sql, User.class, id);
    }

    public void update(User user) {
        String sql = "update user set money=? where id=?";
        JdbcTemplate.execute(sql, user.getMoney(), user.getId());
    }
}
