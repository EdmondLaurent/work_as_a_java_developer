/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @date 2022/11/9
 */
@Configuration
@ConditionalOnExpression("'${regCenter.serverList}'.length()>0 ")
//  当前配置生效的条件 ：配置文件中 regCenter.serverList 的长度 > 0
public class JobRegistryCenterConfig {
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(@Value("${regCenter.serverList}") final String serverList,
                                                  @Value("${regCenter.namespace}") final String nameSpace) {
        //  初始化一个 zookeeper注册中心 对象 参数是 zookeeper注册中心的相关配置
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, nameSpace));
    }
}
