/*
 * 南京国睿信维软件有限公司
 */
package com.glaway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LS
 * @date 2022/11/4
 */
@RestController
public class DCcontroller {
    @Autowired
    DiscoveryClient discoveryClient;

    /**
     * 通过DiscoveryClient对象，在日志中打印出服务实例的相关内容。
     * @return
     */
    @RequestMapping("/dc")
    public String getDC() {
        String services = "Services:" + discoveryClient.getServices();
        System.out.println(services);
        return services;
    }

}
