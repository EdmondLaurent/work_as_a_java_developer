/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    /**
     * 服务端接收文件的controller 返回文件名
     * @param file
     * @return
     */
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleUploadFile(@RequestPart(value = "file") MultipartFile file) {
        System.out.println("接收到的文件名称：" + file.getOriginalFilename());
        return file.getOriginalFilename();
    }
}
