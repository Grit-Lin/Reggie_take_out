package com.grit.controller;

import com.grit.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author:
 * @date:
 * @description
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.basePath}")
    private String basePath;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        //通过UUID随机生成文件名，并拼接文件后缀
        UUID uuid = UUID.randomUUID();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = uuid.toString() + suffix;


        //转存文件前，先判断一下目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            //将项目上传的文件转存到期望存储的文件地址
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //返回文件名称，便于后续添加菜品等操作
        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //通过输入流来读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，将文件写回浏览器，在浏览器显示图片
            response.setContentType("image/jpeg");//设置响应返回的文件类型为图片类型

            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            //关闭流
            fileInputStream.close();
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
