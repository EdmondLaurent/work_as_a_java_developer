/*
 * 南京国睿信维软件有限公司
 */
package com.edmond;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author LS
 * @date 2022/11/4
 */
@EnableEurekaServer     //  将当前 springboot 应用 作为 Eureka 服务注册中心
@SpringBootApplication
public class EurekaServerApplication {
    public static void main(String[] args) {
        //  通过创建者模式 启动 SpringBoot 应用主函数
        new SpringApplicationBuilder(EurekaServerApplication.class)
                .web(true)
                .run(args);
    }
}
