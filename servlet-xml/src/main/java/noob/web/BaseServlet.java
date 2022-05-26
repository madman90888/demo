package noob.web;

import noob.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class BaseServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(BaseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        log.debug("请求方法：{}", action);
        if (ObjectUtils.isEmpty(action)) {
            action = "index";
        }
        final Method method;
        try {
            method = this.getClass().getMethod(action, HttpServletRequest.class, HttpServletResponse.class);
            if (Objects.isNull(method)) {
                throw new RuntimeException("无效请求方法");
            }
            Object o = method.invoke(this, req, resp);
            responseHandler(o, req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据返回结果类型进行处理
     */
    private void responseHandler(Object res, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 字符串类型，判断是否转发、重定向、还是访问指定页面
        if (res instanceof String) {
            String str = (String) res;
            if (str.startsWith("forward:")) {
                request.getRequestDispatcher(str.replaceAll("forward:", "")).forward(request, response);
            }else if (str.startsWith("redirect:")) {
                response.sendRedirect(str.replaceAll("redirect:", ""));
            }else {
                request.getRequestDispatcher("/page/" + str + ".jsp").forward(request, response);
            }
        }
    }
}
