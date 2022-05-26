package noob.web;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class VerifyCodeServlet extends BaseServlet {
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 创建图片、获取画笔
        final BufferedImage image = new BufferedImage(80, 20, BufferedImage.TYPE_INT_RGB);
        final Graphics g = image.getGraphics();
        // 设置白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 80, 20);
        // 生成随机验证码
        final String code = String.valueOf(Math.random()).substring(2, 6);
        // 保存到session
        request.getSession().setAttribute("VerifyCode", code);
        // 设置画笔颜色
        g.setColor(Color.BLUE);
        g.setFont(new Font(null, Font.BOLD, 20));
        // 将验证码写入到图片中
        g.drawString(code, 20, 18);
        // 干扰线
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(
                    random.nextInt(80), random.nextInt(20),
                    random.nextInt(80), random.nextInt(20)
            );
        }
        // 响应图片
        //每隔 10秒刷新一下
//        resp.setIntHeader("Refresh", 10);
        //设置类型
        response.setContentType("image/jpg");
        //防止浏览器缓存，该资源已经过期
        response.setDateHeader("expires", -1);
        //需要重新验证
        response.setHeader("Cache-Control", "no-cache");
        //HTTP/1.0 强制要求缓存服务器在返回缓存的版本之前将请求提交到源头服务器进行验证。
        response.setHeader("Pragme", "no-cache");
        ImageIO.write(image, "jpg", response.getOutputStream());
    }
}
