package noob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

@Controller
public class ExceptionController {

    @GetMapping("db")
    public void db() throws Exception {
        throw new SQLException("数据库异常");
    }

    @GetMapping("/err")
    public void no() throws Exception {
        throw new Exception("未知异常");
    }
}
