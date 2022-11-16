/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.com.edmond.controller;

import com.edmond.com.edmond.feign.DCClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author edmond
 * @date 2022/11/7
 */
@RestController
public class DCController {

    private DCClient dcClient;

    public DCController(DCClient dcClient) {
        this.dcClient = dcClient;
    }

    @GetMapping("/consumer")
    public String dc() {
        return dcClient.consumer();
    }
}
