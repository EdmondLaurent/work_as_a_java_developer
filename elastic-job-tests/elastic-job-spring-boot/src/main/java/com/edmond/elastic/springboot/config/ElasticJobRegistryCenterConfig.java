package com.edmond.elastic.springboot.config;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ִ�� ��ʱ����elastic-job ��ע������
 */
@Configuration
public class ElasticJobRegistryCenterConfig {

    //zookeeper�����ַ��� localhost:2181
    private String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    //��ʱ���������ռ�
    private String JOB_NAMESPACE = "elastic-job-example-java";

    @Bean(initMethod = "init")
    public CoordinatorRegistryCenter setUpRegistryCenter() {
        //  �½�һ�� zookeeper ����
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);

        //  ���� zookeeper ��ʱʱ��
        zookeeperConfiguration.setSessionTimeoutMilliseconds(5000);

        //  ����ע������
        CoordinatorRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);

        //  initMethod �����˵�ǰbean�ڴ�����ʱ�� �Զ����� init����
        return zookeeperRegistryCenter;
    }

}
