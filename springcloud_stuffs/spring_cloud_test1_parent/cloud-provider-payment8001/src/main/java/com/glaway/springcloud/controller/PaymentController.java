package com.glaway.springcloud.controller;

import com.glaway.springcloud.entities.CommonResult;
import com.glaway.springcloud.entities.Payment;
import com.glaway.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j      //  lombok 日志
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    //  服务发现（提供注册中心中的服务信息）
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("/payment/create")
    public CommonResult create(@RequestBody Payment payment) {
        //  @RequestBody 注解 将前端调用 或微服务接口调用传过来的json 串 反序列化为 实体类
        log.info("插入数据参数：" + payment);
        int result = paymentService.create(payment);
        log.info("插入结果：" + result);
        if (result > 0) {
            return new CommonResult(200, "插入数据成功", result + "提供服务的接口为：" + serverPort);
        } else {
            return new CommonResult(444, "插入数据失败", null);
        }
    }

    @GetMapping("/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询结果：" + payment);
        if (payment != null) {
            return new CommonResult(200, "查询成功", payment + "提供服务的接口为：" + serverPort);
        } else {
            return new CommonResult(444, "数据库中不存在对应的记录，查询 id：" + id);
        }
    }

    @GetMapping("/payment/discovery")
    public Object discovery() {
        log.info("*****************服务清单：*********************");
        //  获取注册中心中所有的服务列表
        for (String service : discoveryClient.getServices()) {
            log.info("service:" + service);
        }
        //  根据服务名称 获取提供对应服务的实例
        log.info("*****************实例清单：*********************");
        for (ServiceInstance instance : discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE")) {
            log.info(instance.getInstanceId() + "\t" + instance.getPort() +"\t" + instance.getUri());
        }
        return discoveryClient;
    }
}
