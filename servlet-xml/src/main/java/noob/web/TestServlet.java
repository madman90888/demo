package noob.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends BaseServlet {
    // 测试页面 /xml/test?action=index
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return "test";
    }
}
