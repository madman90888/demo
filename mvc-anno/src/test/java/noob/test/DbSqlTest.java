package noob.test;

import noob.config.ApplicationConfiguration;
import noob.entity.User;
import noob.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class DbSqlTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
        final List<User> list = userMapper.selectAll();
        System.out.println(list);
    }
}
