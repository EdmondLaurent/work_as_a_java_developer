/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuShuo
 * @date 2022/11/23
 */
@RestController
@RefreshScope
public class ConfigController {
    @Value("${info.from}")
    private String fromString;

    @RequestMapping("/from")
    public String fromConfig() {
        return this.fromString;
    }
}
