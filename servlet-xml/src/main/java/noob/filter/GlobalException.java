package noob.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.StringJoiner;

public class GlobalException implements Filter {
    public static final Logger log = LoggerFactory.getLogger(GlobalException.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final StringJoiner joiner = new StringJoiner("\n");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        joiner.add("\n========= request log ===========");
        joiner.add("请求URL：" + request.getRequestURI());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (Exception e) {
            joiner.add("Exception:" + e.getMessage());
        }
        log.debug(joiner.toString());
    }

    @Override
    public void destroy() {
    }
}
