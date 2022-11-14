package com.glaway.springcloud.controller;

import com.glaway.springcloud.entities.CommonResult;
import com.glaway.springcloud.entities.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@Slf4j
public class OrderController {
    //  通过服务名称 +  负载均衡的方式 寻找提供服务的主机
    private static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    /**
     * 通过 RestTemplate  远程调用 8001 端口服务创建订单功能
     *
     * @param payment
     * @return
     */
    @PostMapping("/consumer/payment/create")
    public CommonResult create(Payment payment) {
        log.info("远程调用插入数据 参数 ：" + payment);
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, CommonResult.class);
    }

    /**
     * 远程调用 通过RestTemplate 根据 id 查询订单序列号内容
     *
     * @param id
     * @return
     */
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult getByPaymentId(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
    }
}
