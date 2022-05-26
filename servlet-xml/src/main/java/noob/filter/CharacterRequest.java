package noob.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;

/**
 * 解决get参数中文乱码
 */
public class CharacterRequest extends HttpServletRequestWrapper {
    private HttpServletRequest request;

    public CharacterRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    //重新父类方法，进行编码处理
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        //只对get请求参数处理
        String method = request.getMethod();
        if ("get".equalsIgnoreCase(method)) {
            try {
                value = new String(value.getBytes("iso-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }
}