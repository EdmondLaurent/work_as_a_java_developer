/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.com.edmond.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author edmond
 * 远程调用 eureka-client 服务的接口
 * @date 2022/11/7
 */
@FeignClient("eureka-client")
public interface DCClient {
    @GetMapping("/dc")
    String consumer();
}
