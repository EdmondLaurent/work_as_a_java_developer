/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiuShuo
 * @date 2022/11/8
 */
@Configuration
public class MultipartSupportConfig {
    /**
     * 通过 feign 接口表单传输文件格式化
     *
     * @return
     */
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
