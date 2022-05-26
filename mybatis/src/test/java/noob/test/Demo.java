package noob.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import noob.entity.User;
import noob.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class Demo {
    private UserMapper mapper;

    @Before
    public void create() throws Exception {
        // 1.加载配置文件
        final InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        // 2.创建工厂构造器
        final SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        // 3.通过配置文件，创建工厂对象
        final SqlSessionFactory factory = builder.build(is);
        // 4.创建 SqlSession 对象
        final SqlSession sqlSession = factory.openSession(true);
        // 5.通过代理模式创建 Mapper 接口的代理实现类
        mapper = sqlSession.getMapper(UserMapper.class);
    }

    // 新增
    @Test
    public void insert() {
        final User user = new User(3, "alice", "123456", 500.0);
        final int result = mapper.insert(user);
        System.out.println("插入操作：" + result);
    }

    // 更新
    @Test
    public void update() {
        final User user = new User(3, "newName", "123", 500.0);
        final int result = mapper.updateByPrimaryKey(user);
        System.out.println("更新操作：" + result);
    }

    // 删除
    @Test
    public void delete() {
        final int result = mapper.deleteByPrimaryKey(3);
        System.out.println("删除操作：" + result);
    }

    // 结果bean对象
    @Test
    public void selectById() {
        final User result = mapper.selectByPrimaryKey(1);
        System.out.println("查询单个：" + result);
    }

    // 结果List
    @Test
    public void page() {
        PageHelper.startPage(1,2);
        final List<User> list = mapper.selectAll();
        final PageInfo<User> info = new PageInfo<>(list);
        System.out.println("分页查询：" + info);
    }
}
