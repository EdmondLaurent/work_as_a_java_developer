package com.edmond.elastic.springboot.config;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.edmond.elastic.springboot.job.FileBackupJobDB;
import com.edmond.elastic.springboot.job.FileBackupJobFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class ElasticJobConfig {

    //  将配置好的注册中心自动注入
    @Autowired
    private CoordinatorRegistryCenter registryCenter;
    //  注入自定义的定时任务
    @Autowired
    private FileBackupJobDB fileBackupJobDB;

    //  DataFlow 数据流定时任务
    @Autowired
    private FileBackupJobFlow fileBackupJobFlow;

    //  自动注入数据源
    @Autowired
    private DataSource dataSource;

    /**
     * 创建LiteJobConfiguration
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @return
     */
    private LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron,
                                                        final int shardingTotalCount,
                                                        final String shardingItemParameters) {
        //  创建JobCoreConfigurationBuilder
        JobCoreConfiguration.Builder JobCoreConfigurationBuilder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
        //  判断作业是否含有参数，若含有参数则设定参数
        if (!StringUtils.isEmpty(shardingItemParameters)) {
            JobCoreConfigurationBuilder.shardingItemParameters(shardingItemParameters);
        }
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfigurationBuilder.build();
        //  创建SimpleJobConfiguration（参数是通过构造器构建好的 jobCoreConfiguration , 当前执行任务类的全路径名）
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName());
        //  创建 LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
                .newBuilder(simpleJobConfiguration)
                //  在 LiteJboConfiguration 中配置策略类的全路径名
                .jobShardingStrategyClass("com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy")
                .overwrite(true).build();
        return liteJobConfiguration;
    }

    /**
     * 创建 DataFlow 类型的LiteJobConfiguration
     *
     * @param jobClass
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @return
     */
    private LiteJobConfiguration createDataFlowJobConfiguration(final Class<? extends ElasticJob> jobClass,
                                                                final String cron,
                                                                final int shardingTotalCount,
                                                                final String shardingItemParameters) {
        //  创建任务核心 配置（构造器）对象
        JobCoreConfiguration.Builder jobCoreConfigurationBuilder = JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount);
        if (!StringUtils.isEmpty(shardingItemParameters)) {
            jobCoreConfigurationBuilder.shardingItemParameters(shardingItemParameters);
        }
        //  创建任务核心配置对象
        JobCoreConfiguration jobCoreConfiguration = jobCoreConfigurationBuilder.build();
        //  创建 DataFlow JobConfiguration  （数据流任务配置对象）  --- 第三个参数 开启数据流
        DataflowJobConfiguration dataflowJobConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, jobClass.getCanonicalName(), true);
        //  创建 LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(dataflowJobConfiguration)
                .jobShardingStrategyClass("com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy")
                .overwrite(true).build();
        return liteJobConfiguration;
    }

    @Bean(initMethod = "init")
    public SpringJobScheduler initElasticJob() {
        JobEventRdbConfiguration jobEventRdbConfiguration = new JobEventRdbConfiguration(dataSource);
        //  参数：ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, JobEventConfiguration jobEventConfig, ElasticJobListener... elasticJobListeners
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(fileBackupJobDB, registryCenter,
                //  设置 任务分片参数 （在本案例中根据需要备份的文件类型设置参数，不同分片的作业实例获取的文件类型是不同的）
                createDataFlowJobConfiguration(fileBackupJobFlow.getClass(),
                        "0/3 * * * * ?",
                        4,
                        "0=text,1=image,2=radio,3=vedio"),
                jobEventRdbConfiguration);
        return springJobScheduler;
    }

}
