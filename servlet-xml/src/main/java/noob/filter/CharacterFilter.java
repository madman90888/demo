package noob.filter;

import javax.servlet.*;
import java.io.IOException;

public class CharacterFilter implements Filter {
    private static String encoding;     //编码

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = filterConfig.getInitParameter("CharsetEncoding");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(encoding);
        servletResponse.setContentType("text/html; charset=" + encoding);
        //对servletRequest进行包装，解决Tomcat旧版本 get请求参数中文乱码
        //servletRequest = new CharacterRequest((HttpServletRequest) servletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
