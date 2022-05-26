package noob.controller;

import noob.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TestController {
    @GetMapping("/testError")
    public void testError() {
        int i = 1/0;
    }

    @RequestMapping("/sendJson")
    public String sendJson(@RequestBody String json) {
        System.out.println("获取json----");
        System.out.println(json);
        return "success";

    }

    @RequestMapping("/getJson")
    @ResponseBody
    public User getJson() {
        User user = new User(1, "李四", "123", 100.5);
        return user;
    }
}
