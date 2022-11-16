/*
 * 南京国睿信维软件有限公司
 */
package com.glaway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author LS
 * @date 2022/11/4
 */
@EnableDiscoveryClient  //  允许注册中心发现该服务
@SpringBootApplication
public class EurekaClientApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(EurekaClientApplication.class)
                .web(true)
                .run(args);
    }
}
