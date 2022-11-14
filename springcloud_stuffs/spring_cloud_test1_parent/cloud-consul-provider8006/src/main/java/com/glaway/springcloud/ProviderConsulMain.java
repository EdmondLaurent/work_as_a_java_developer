package com.glaway.springcloud;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProviderConsulMain {
    public static void main(String[] args) {
        SpringApplication.run(ProviderConsulMain.class, args);
    }
}
