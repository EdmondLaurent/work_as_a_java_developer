/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.edmond.job.MyJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @date 2022/11/9
 */
@Configuration
public class MyJobConfiguration {
    private final String cron = "0/5 * * * * ?";
    private final int shardingTotalCount = 3;       //  作业分片数量
    private final String shardingParameters = "0=A,1=B,2=C";       //  作业分片（不同分片的参数）
    private final String jobParameters = "parameter";


    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Bean
    public SimpleJob stockJob() {
        //  以 bean 的方式将自定义任务内容 配置到spring 容器中
        return new MyJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob) {
        return new SpringJobScheduler(simpleJob, registryCenter, getLiteJobConfiguration(simpleJob.getClass(),
                cron,shardingTotalCount,shardingParameters,jobParameters));
    }

    /**
     * 获取当前 作业 根配置 （核心配置 ）
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingParameters
     * @param jobParameters
     * @return
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                         final String cron,
                                                         final int shardingTotalCount,
                                                         final String shardingParameters,
                                                         final String jobParameters) {
        //  作业核心配置
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                .shardingItemParameters(shardingParameters)
                .jobParameter(jobParameters).build();
        //  Simple ；类型配置
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        //  Lite 作业类型配置 （作业根配置 ）
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();

        return liteJobConfiguration;
    }
}
