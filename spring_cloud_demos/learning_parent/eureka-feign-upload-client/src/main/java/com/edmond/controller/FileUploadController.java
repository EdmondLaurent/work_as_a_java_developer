/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.controller;

import com.edmond.feign.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LiuShuo
 * @date 2022/11/8
 */
@RestController
@Slf4j
public class FileUploadController {

    private UploadService uploadService;

    public FileUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("上传的文件名称：" + file.getOriginalFilename());
        return uploadService.handleUploadFile(file);
    }
}
