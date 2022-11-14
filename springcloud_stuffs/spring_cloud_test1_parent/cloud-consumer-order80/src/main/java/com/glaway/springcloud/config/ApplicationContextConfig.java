package com.glaway.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {
    /**
     * 获取RestTemplate模板
     *
     * @return
     */
    @Bean  // 相当于 在 applicationContext.xml 中配置 <bean id = "" class = ""/>
    @LoadBalanced   //  根据从服务注册中心根据服务名称获取的主机和地址  （ 开启负载均衡的功能 - 默认策略： 轮询
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
