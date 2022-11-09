# Elastic-Job 

## SpringBoot 整合 Elastic-Job

> 前置准备 

* 服务注册中心 zookeeper

引入相对应的Elastic-job依赖 maven 坐标如下：

```xml
		<dependency>
            <groupId>com.dangdang</groupId>
            <artifactId>elastic-job-lite-core</artifactId>
            <version>2.1.5</version>
        </dependency>
        <dependency>
            <groupId>com.dangdang</groupId>
            <artifactId>elastic-job-lite-spring</artifactId>
            <version>2.1.5</version>
        </dependency>
```

在当前项目中引入 `spring-boot-starter-parent`  \ `spring-boot-starter-web`  \ `spring-boot-starter-test` 等相关依赖 

> 项目配置文件 

配置文件内容 ： 服务名称 、端口号 、 zookeeper 注册信息 （自定义key）

```properties
spring.application.name=springboot-elastic-job
server.port=9020

# 注册到zookeeper中 （ZK 作为服务注册中心）
regCenter.serverList=localhost:2181
regCenter.namespace=springboot-elastic-job
```

> 当前项目主启动类 

```java
@SpringBootApplication
public class ElasticJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticJobApplication.class, args);
    }
}
```

> 自定义 zookeeper 配置 

让当前应用 作为 ZK 客户端 ，使用 zookeeper作为服务注册中心 

新建 zookeeper注册中心对象 ，从配置文件中读取 zookeeper相关信息 将对应的相关信息作为参数，添加到对应的zookeeper配置中

```java
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

```

> 自定义简单作业内容

作业内容： 通过 日志输出 作业对象相关的内容  ：作业分片总数 ， 当前分片项，当前分片参数 作业名称和自定义参数 等

自定义作业：**实现 SimpleJob 接口 ， 重写 execute 方法 **

```java
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
```

> 整合 zookeeper 与 etastic-job  

使用 SimpleJob 相关的配置 通过创建者模式创建 当前自定义作业的核心配置 ，并返回当前对象的核心配置 

```java
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
```

通过 自动注入 或者 bean 的方式将 zookeeper注册中心 和 自定义作业对象 进行引入 

通过注解: `@Bean(initMethod = "init")` 标明项目启动后的初始化方法

```java
    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob) {
        return new SpringJobScheduler(simpleJob, registryCenter, getLiteJobConfiguration(simpleJob.getClass(),
                cron,shardingTotalCount,shardingParameters,jobParameters));
    }
```

通过初始化方法将zookeeper 和 自定义任务整合到 Spring 的任务对象 - `SpringjobScheduler` - 中 

完整的配置类内容为：

```java
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

```

启动当前项目主要启动类 查看日志输出的内容

```
2022-11-09 16:51:30.061  INFO 3068 --- [ond.job.MyJob-2] com.edmond.job.MyJob                     : Thread ID: 131, 作业分片总数: 3, 当前分片项: 1.当前参数: B,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
2022-11-09 16:51:30.061  INFO 3068 --- [ond.job.MyJob-1] com.edmond.job.MyJob                     : Thread ID: 130, 作业分片总数: 3, 当前分片项: 0.当前参数: A,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
2022-11-09 16:51:30.061  INFO 3068 --- [ond.job.MyJob-3] com.edmond.job.MyJob                     : Thread ID: 132, 作业分片总数: 3, 当前分片项: 2.当前参数: C,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
2022-11-09 16:51:35.015  INFO 3068 --- [ond.job.MyJob-4] com.edmond.job.MyJob                     : Thread ID: 133, 作业分片总数: 3, 当前分片项: 0.当前参数: A,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
2022-11-09 16:51:35.015  INFO 3068 --- [ond.job.MyJob-5] com.edmond.job.MyJob                     : Thread ID: 134, 作业分片总数: 3, 当前分片项: 1.当前参数: B,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
2022-11-09 16:51:35.015  INFO 3068 --- [ond.job.MyJob-6] com.edmond.job.MyJob                     : Thread ID: 135, 作业分片总数: 3, 当前分片项: 2.当前参数: C,作业名称: com.edmond.job.MyJob.作业自定义参数: parameter
```



