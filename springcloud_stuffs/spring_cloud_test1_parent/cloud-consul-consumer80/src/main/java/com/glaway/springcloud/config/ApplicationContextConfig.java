package com.glaway.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 通用 restTemplate 配置类 返回新的 restTemplate 对象
 */
@Configuration
public class ApplicationContextConfig {


    @Bean
    @LoadBalanced       //  负载均衡注解 开启RestTemplate 的负载均衡功能
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
