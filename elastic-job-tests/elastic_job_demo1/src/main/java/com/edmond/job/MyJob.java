/*
 * 南京国睿信维软件有限公司
 */
package com.edmond.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LiuShuo
 * @date 2022/11/9
 */
public class MyJob implements SimpleJob {

    Logger logger = LoggerFactory.getLogger(MyJob.class);

    /**
     * 自定义作业以及分片内容
     *
     * @param shardingContext
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        logger.info(String.format("Thread ID: %s, 作业分片总数: %s, " +
                        "当前分片项: %s.当前参数: %s," +
                        "作业名称: %s.作业自定义参数: %s",
                Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(),
                shardingContext.getJobParameter()
        ));
    }
}
