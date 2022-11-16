/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.feign;

import com.edmond.config.MultipartSupportConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LiuShuo
 * @date 2022/11/8
 */
@FeignClient(value = "EUREKA-FEIGN-UPLOAD-SERVER", configuration = MultipartSupportConfig.class)
public interface UploadService {
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String handleUploadFile(@RequestPart(value = "file") MultipartFile file);
}
