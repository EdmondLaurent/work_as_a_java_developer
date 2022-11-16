/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author edmond
 * @date 2022/11/7
 */
@RestController
public class RibbonController {

    private RestTemplate restTemplate;

    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/consumer")
    public String getDC() {
        String services = restTemplate.getForObject("http://EUREKA-CLIENT/dc", String.class);
        System.out.println(services);
        return services;
    }
}
