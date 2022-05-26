package noob.test;

import noob.entity.User;
import noob.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:application.xml")
public class DbTest {
    @Autowired
    private UserMapper mapper;

    @Test
    public void test() {
        final List<User> list = mapper.selectAll();
        System.out.println(list);
    }
}
