package noob.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class FileController {
    public static final String folderName = "WEB-INF/file";

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(HttpSession session, @RequestParam(value = "name", defaultValue = "1.jpg") String name) throws IOException {
        //获取ServletContext对象
        ServletContext context = session.getServletContext();
        //获取真实路径
        String realPath = context.getRealPath(folderName + File.separator + name);
        //判断文件是否存在
        File file = new File(realPath);
        if (!file.exists()) {
            return null;
        }
        //创建输入流
        FileInputStream fis = new FileInputStream(realPath);
        //创建字节数组
        byte[] bytes = new byte[fis.available()];
        //将流读取到内存数组
        fis.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置要下载方式以及下载文件的名字
        headers.add("Content-Disposition", "attachment;filename=" + name);
        //设置响应状态码
        HttpStatus status = HttpStatus.OK;
        //创建ResponseEntity对象
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(bytes, headers, status);
        //关闭流
        fis.close();
        return responseEntity;
    }

    @PostMapping("/upload")
    public String upload(HttpSession session, MultipartFile uploadFile) throws IOException {
        ServletContext context = session.getServletContext();
        //获取真实路径
        String realPath = context.getRealPath(folderName);
        File folder = new File(realPath);
        //获取文件名
        String filename = uploadFile.getOriginalFilename();
        //文件路径
        String filePath = realPath + File.separator + filename;
        File realFile = new File(filePath);
        if (folder.exists()) {
            if (realFile.exists()) {
                String newFile = UUID.randomUUID().toString().replaceAll("-", "") + filename.substring(filename.lastIndexOf("."));
                filePath = realPath + File.separator + newFile;
                realFile = new File(filePath);
            }
        }else {
            //文件夹不存在创建
            folder.mkdirs();
        }
        uploadFile.transferTo(realFile);
        return "success";
    }
}
