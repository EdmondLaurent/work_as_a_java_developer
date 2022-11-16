/*
 * 南京国睿信维软件有限公司
 */
package com.edmond;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author LiuShuo
 * @date 2022/11/8
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FeignUploadClientApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(FeignUploadClientApplication.class)
                .web(true)
                .run(args);
    }
}
