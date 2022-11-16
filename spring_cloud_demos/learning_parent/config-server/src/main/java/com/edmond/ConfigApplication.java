/*
 * 南京国睿信维软件有限公司
 */
package com.edmond;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author LiuShuo
 * @date 2022/11/8
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer     //  声明当前的应用为配置中心
public class ConfigApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigApplication.class).web(true).run(args);
    }
}
