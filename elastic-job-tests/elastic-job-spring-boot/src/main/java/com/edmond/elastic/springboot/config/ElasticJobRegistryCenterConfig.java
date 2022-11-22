package com.edmond.elastic.springboot.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 执行 定时任务elastic-job 的注册中心
 */
@Configuration
public class ElasticJobRegistryCenterConfig {

    //zookeeper链接字符串 localhost:2181
    private String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    //定时任务命名空间
    private String JOB_NAMESPACE = "elastic-job-example-java";

    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter setUpRegistryCenter() {
        //  新建一个 zookeeper 配置
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);

        //  设置 zookeeper 超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(5000);

        //  创建注册中心
        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);

        //  initMethod 标明了当前bean在创建的时候 自动调用 init方法
        return zookeeperRegistryCenter;
    }

}
