package noob.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;

public class FileServlet extends BaseServlet {
    private final static Logger log = LoggerFactory.getLogger(FileServlet.class);

    //文件上传
    public void upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取文件存放路径
        String savePath = req.getServletContext().getRealPath("/WEB-INF/file");
        log.debug("文件存放路径：" + savePath);
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //获取表单普通参数
        String username = req.getParameter("username");
        log.debug("普通参数：" + username);

        //获取单个文件
        //Part part = req.getPart("file");
        //获取所有文件
        Collection<Part> parts = req.getParts();
        log.debug("文件数量：" + parts.size());
        for (Part part : parts) {
            log.debug("------------");
            log.debug("属性：" + part.getName());
            String type = part.getContentType();
            log.debug("文件类型：" + type);
            //判断是不是普通表单
            if (type == null) continue;
            //获取请求头，请求头的格式：form-data; name="file"; filename="test.jpg"
            String header = part.getHeader("content-disposition");
            //获取文件名
            String fileName = getFileName(header);
            //拼接 路径 + 文件名
//            String path = savePath + File.separator + fileName;
            // Servlet 3.1 实现了文件名获取
            String path = savePath + File.separator + part.getSubmittedFileName();
            //将文件放入指定目录
            part.write(path);
        }
        resp.getWriter().write("上传成功");
    }

    /**
     * 根据请求头解析出文件名
     * 请求头的格式：火狐和google浏览器下：form-data; name="file"; filename="test.jpg"
     * IE浏览器下：form-data; name="file"; filename="D:\test.jpg"
     * @param header 请求头
     * @return 文件名
     */
    public String getFileName(String header) {
        /**
         * String[] tempArr1= header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
         * 火狐或者google浏览器下：tempArr={form-data,name="file",filename="test.jpg"}
         * IE浏览器下：tempArr={form-data,name="file",filename="E:\test.jpg"}
         */
        String[] tempArr = header.split(";");
        /**
         *火狐或者google浏览器下：tempArr2={filename,"test.jpg"}
         *IE浏览器下：tempArr2={filename,"E:\test.jpg"}
         */
        String[] tempArr2 = tempArr[2].split("=");
        //获取文件名，兼容各种浏览器的写法
        String fileName = tempArr2[1].substring(tempArr2[1].lastIndexOf("\\")+1).replaceAll("\"", "");
        return fileName;
    }

    public void download(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //获取服务器文件存放的文件夹路径
        String realPath = req.getServletContext().getRealPath("/WEB-INF/file");
        //获取文件名
        String fileName = req.getParameter("fileName");
        log.debug("要下载的文件名：" + fileName);
        //拼接
        String path = realPath + File.separator + fileName;
        File file = new File(path);
        //判断文件是否存在
        if (!file.exists()) {
            resp.getWriter().write("文件不存在");
            return;
        }
        //防止jsp生成导致空白行
        resp.reset();
        //设置相应编码
        resp.setCharacterEncoding("UTF-8");
        //文件编码，防止中文乱码
        fileName = URLEncoder.encode(fileName, "UTF-8");
        //设置文件相应头，文件名
        resp.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        //设置文件大小
        String length = new Long(file.length()).toString();
        resp.setHeader("Content-Length", length);
        //使用流传输文件
        FileInputStream fis = new FileInputStream(file);
        ServletOutputStream out = resp.getOutputStream();
        byte[] buff = new byte[1024 * 10];
        int len;
        while ( (len=fis.read(buff)) != -1 ) {
            out.write(buff, 0, len);
            out.flush();
        }
        fis.close();
        out.close();
    }
}
