# Elastic Job 分布式任务调度

## 概述

### 任务调度

> 任务调度的概念

**任务调度系统指的是系统为了自动完成任务，从约定的特定时刻去执行任务的过程。有了任务调度可以释放更多的人力去执行相应的任务**

也可以简单的理解为定时任务；

> 任务调度的实现方式：

间隔一段时间也是一种任务调度

第三方框架 Quartz 说的时基于日历的方式实现任务调度

### 分布式任务调度

> 分布式任务调度的概念

分布式：将单体结构拆分成若干服务，服务之间通过网络通信完成数据交换和用户业务处理

>  分布式服务的特点：



![image-20221026211005691](F:\new_work_study_space\elastic-job\笔记\Elastic Job 分布式任务调度.assets\image-20221026211005691.png)

> 分布式任务调度

在分布式架构的环境下运行任务调度

**分布式任务调度特点：**

可伸缩性强，可以将同一个服务部署到多个节点上 单一项目的并行任务调度依靠的是单一节点的CPU 算力 

。。。

**避免任务的重复执行**

zookeeper 会选举特定的节点执行任务，任务按照指定的调度策略执行，能够避免同一任务多次执行

作业分片的一致性：

当任务被分片之后，保证同一分片在分布式环境中仅执行一个实例



## 创建 Elastic - job 案例

使用 Elastic-Job 创建指定的定时任务，并通过不同的客户端运行这个任务 查看任务运行结果

特定任务案例：对未备份的文件进行 备份

### 环境搭建

zookeeper 要求 3.4.6 以上的版本 

创建 空的maven 工程 导入以下依赖（`elastic-job-lite-core`  ； `lombok`）

完整的 pom 配置文件如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.edmond</groupId>
    <artifactId>elastic-job-starter</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- https://mvnrepository.com/artifact/com.dangdang/elastic-job-lite-core -->
        <dependency>
            <groupId>com.dangdang</groupId>
            <artifactId>elastic-job-lite-core</artifactId>
            <version>2.1.5</version>
        </dependency>
        <!-- Lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
        </dependency>
    </dependencies>

</project>
```

### 代码实现

明确任务需求 创建 实体类代表文件，备份文件使用修改文件的`是否备份`属性表达

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCustom {
    /**
     * 标识
     */
    private String id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件类型，如text、image、radio、vedio
     */
    private String type;

    /**
     * 文件内容
     */
    private String content;

    /**
     * 是否已备份
     */
    private Boolean backedUp = false;

    public FileCustom(String id, String name, String type, String content) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.content = content;
    }
}

```

定义文件备份定时任务内容 - 获取所有未备份的文件 并进行备份

```java
public class FileBackUpJob implements SimpleJob {

    //  每次执行时需要备份文件的数量
    private final int FETCH_SIZE = 1;
    //  需要备份的文件列表
    public static List<FileCustom> files = new ArrayList<>();


    /**
     * 实现 SimpleJob 接口，实现的方法
     * 定义定时任务的代码逻辑
     *
     * @param shardingContext
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("作业分片：" + shardingContext.getShardingItem());
        //  获取为备份的文件列表
        List<FileCustom> fileCustoms = fetchBackupFiles(FETCH_SIZE);
        //  进行文件备份
        backupFiles(fileCustoms);
    }

    /**
     * 遍历 文件列表 获取所有未备份的文件
     *
     * @param count
     * @return
     */
    public List<FileCustom> fetchBackupFiles(int count) {
        //  用于保存获取的为备份的文件
        ArrayList<FileCustom> fileCustoms = new ArrayList<>();
        //  模拟记录获取文件的个数
        int num = 0;
        for (FileCustom fileCustom : files) {
            if (num >= count) break;
            //  若当前文件未被备份 将当前文件添加到未备份的的文件列表中
            if (!fileCustom.getBackedUp()) {
                fileCustoms.add(fileCustom);
                num++;
            }
        }
        System.out.printf("获取文件列表的时间 %s , 文件列表个数 %d个\n", LocalTime.now(), num);
        return fileCustoms;
    }

    /**
     * 进行文件备份，参数是需要备份的文件列表
     *
     * @param files
     */
    public void backupFiles(List<FileCustom> files) {
        for (FileCustom file : files) {
            file.setBackedUp(true);
            System.out.printf("time:%s,备份文件，名称：%s，类型：%s\n", LocalDateTime.now(), file.getName(), file.getType());
        }
    }
}
```

编写备份文件的定时任务主启动类

* 配置zookeeper注册中心
* 创建新的任务配置将定时任务的内容参数 cron 表达式 以及时间分片相关信息进行绑定
* 将任务内容与注册中心进行绑定（创建任务计划）通过任务计划执行定时任务

```java
public class JobMain {
    //  配置 zookeeper 相关的信息
    //  zk 默认端口号
    private static final int ZOOKEEPER_PORT = 2181;
    //  zookeeper 链接字符串
    private static final String ZOOKEEPER_CONNECTION_STRING = "127.0.0.1:" + ZOOKEEPER_PORT;
    //  当前定时任务的命名空间
    private static final String JOB_NAMESPACE = "elastic-job-example-java";


    public static void main(String[] args) {
        //  制造测试数据
        generateTestFiles();
        //  配置 zookeeper 注册中心
        CoordinatorRegistryCenter coordinatorRegistryCenter = setUpRegistryCenter();
        //  启动任务
        startJob(coordinatorRegistryCenter);
    }

    /**
     * zookeeper 的配置信息以及创建注册中心
     *
     * @return zookeeper 注册中心
     */
    private static CoordinatorRegistryCenter setUpRegistryCenter() {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);
        //  设置链接zookeeper的超时时间
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(1000);
        //  创建注册中心
        ZookeeperRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        zookeeperRegistryCenter.init();
        return zookeeperRegistryCenter;
    }

    /**
     * 启动当前任务
     */
    private static void startJob(CoordinatorRegistryCenter registryCenter) {
        //  通过创建者模式的对象构建任务配置 （设置当前任务分为三个时间片执行）
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("files-job", "0/3 * * * * ? ", 3).build();
        //  给当前的任务配置 添加任务内容 （创建SimpleJobCoreCof ） 第二个参数是任务的内容 指定任务类的全限定名
        SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, FileBackUpJob.class.getCanonicalName());
        //  将新的任务配置注册到注册中心中，创建新的任务计划 , 并进行初始化
        new JobScheduler(registryCenter, LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build()).init();
    }


    //制造一些测试数据
    //生成测试文件
    private static void generateTestFiles(){
        for(int i=1;i<11;i++){
            FileBackUpJob.files.add(new FileCustom(String.valueOf(i+10),"文件"+(i+10),"text","content"+ (i+10)));
            FileBackUpJob.files.add(new FileCustom(String.valueOf(i+20),"文件"+(i+20),"image","content"+ (i+20)));
            FileBackUpJob.files.add(new FileCustom(String.valueOf(i+30),"文件"+(i+30),"radio","content"+ (i+30)));
            FileBackUpJob.files.add(new FileCustom(String.valueOf(i+40),"文件"+(i+40),"video","content"+ (i+40)));
        }
        System.out.println("生产测试数据完成");
    }
}

```

 ## SpringBoot 整合 Elastic-job

### 环境搭建 

Elastic-Job 与 springboot 进行整合所需的依赖

```xml
        <!--Elastic-job 整合 springboot 依赖-->
        <dependency>
            <groupId>com.dangdang</groupId>
            <artifactId>elastic-job-lite-spring</artifactId>
            <version>2.1.5</version>
        </dependency>
```

项目的相关配置（当前定时任务不涉及sql）

```properties
server.port=${PORT:56081}
spring.application.name = task-scheduling-springboot
logging.level.root = info
```

项目主启动类

```java
@SpringBootApplication
public class ScheldulingMain {
    public static void main(String[] args) {
        SpringApplication.run(ScheldulingMain.class, args);
    }
}
```

当前项目的目录结构（所有的相关类都放在主启动类同级的目录下）

<img src="F:\new_work_study_space\elastic-job\笔记\Elastic Job 分布式任务调度.assets\image-20221029221216136.png" alt="image-20221029221216136" style="zoom:50%;" />

案例操作的对象实体类（有相关的代码实现）

### 代码实现

当前定时任务的代码描述

```java
@Component
public class FileBackupJob implements SimpleJob {

    //  每次执行任务时获取文件的数量
    private final int FETCH_SIZE = 1;
    //文件列表（模拟）
    public static List<FileCustom> files = new ArrayList<>();

    //  生成测试数据
    static {
        for (int i = 1; i < 11; i++) {
            FileBackupJob.files.add(new FileCustom(String.valueOf(i + 10), "文件" + (i + 10), "text", "content" + (i + 10)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i + 20), "文件" + (i + 20), "image", "content" + (i + 20)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i + 30), "文件" + (i + 30), "radio", "content" + (i + 30)));
            FileBackupJob.files.add(new FileCustom(String.valueOf(i + 40), "文件" + (i + 40), "video", "content" + (i + 40)));
        }
        System.out.println("生产测试数据完成");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("作业分片："+shardingContext.getShardingItem());
        //分片参数，（0=text,1=image,2=radio,3=vedio，参数就是text、image...）
        String jobParameter = shardingContext.getJobParameter();
        //  获取未备份文件
        List<FileCustom> fileCustoms = fetchUnBackupFiles(FETCH_SIZE);
        //  备份文件
        backupFiles(fileCustoms);
    }

    /**
     * 获取为备份的文件
     *
     * @param count
     * @return
     */
    public List<FileCustom> fetchUnBackupFiles(int count) {
        //  创建一个新的列表用于保存所有未备份的文件
        ArrayList<FileCustom> fileCustoms = new ArrayList<>();
        int num = 0;
        //  将所有文件列表中v属性状态为未备份的文件放在新的列表中并返回
        for (FileCustom fileCustom : files) {
            if (num >= count) break;
            if (!fileCustom.getBackedUp()) {
                fileCustoms.add(fileCustom);
                num++;
            }
        }
        return fileCustoms;
    }

    /**
     * 对所有文件进行备份
     * @param files
     */
    public void backupFiles(List<FileCustom> files){
        for(FileCustom fileCustom:files){
            fileCustom.setBackedUp(true);
            System.out.printf("time:%s,备份文件，名称：%s，类型：%s\n", LocalDateTime.now(),fileCustom.getName(),fileCustom.getType());
        }
    }
}
```

注意当前定时任务类交由Spring 进行管理 （类上有 @Component 注解）

因为 Elastic-Job 通过 zookeeper 进行任务管理，在与springboot整合的过程中需要将zookeeper的相关配置通过 Bean的方式配置到Spring 容器中

使用 配置类声明对zookeeper的相关配置

```java
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
        //  创建注册中心
        ZookeeperRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);

        //  initMethod 标明了当前bean在创建的时候 自动调用 init方法
        return zookeeperRegistryCenter;
    }

}
```

Elastic-Job 与springboot 进行整合 最后使用的是 SpringJobScheduler 提供的接口 （初始化一个SpringJobScheduler 任务计划 ）

SpringScheduler ：

```java
public SpringJobScheduler(ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, ElasticJobListener... elasticJobListeners) 
```

对应的参数列表 ：

* ElasticJob elasticJob  定时任务的执行类
*  CoordinatorRegistryCenter regCenter 任务注册中心
* LiteJobConfiguration jobConfig LiteJobConfiguration 对象
* ElasticJobListener  `ES-JOB` 监听器对象

其中第三个参数是LiteJobConfiguration 配置对象 在传入这个参数之前我们使用一个 方法创建 LiteJobConfiguration 

创建 LiteJobConfiguration 的流程为：

1. 通过构造器创建 JobCoreConfiguration对象
2. 创建 SimpleJobConfiguration 作用 整合 JobCoreConfiguration 与当前任务的执行类 ；参数1： JobCoreConfiguration；参数2：执行定时任务类的全路径名称
3.  通过 SimpleJobConfiguration 创建 LiteJobConfiguration 将对应配置返回

```java
    /**
     * 创建LiteJobConfiguration
     *
     * @param jobClass  执行任务的任务类
     * @param cron	cron 表达式
     * @param shardingTotalCount 	作业分片的个数
     * @param shardingItemParameters	作业的其他参数
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
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }

```

通过 SpringJobScheduler 初始化定时任务

```java
    @Bean(initMethod = "init")
    public SpringJobScheduler initElasticJob() {
        //  创建 SpringJobScheduler
        //  参数：ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, JobEventConfiguration jobEventConfig, ElasticJobListener... elasticJobListeners
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(fileBackupJob, registryCenter,
                createJobConfiguration(fileBackupJob.getClass(), "0/3 * * * * ?", 1, null));
        return springJobScheduler;
    }

```

整个配置的完整代码为：

```java
@Configuration
public class ElasticJobConfig {

    //  将配置好的注册中心自动注入
    @Autowired
    private CoordinatorRegistryCenter registryCenter;
    //  注入自定义的定时任务
    @Autowired
    private FileBackupJob fileBackupJob;

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
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(simpleJobConfiguration).overwrite(true).build();
        return liteJobConfiguration;
    }

    @Bean(initMethod = "init")
    public SpringJobScheduler initElasticJob() {
        //  创建 SpringJobScheduler
        //  参数：ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, JobEventConfiguration jobEventConfig, ElasticJobListener... elasticJobListeners
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(fileBackupJob, registryCenter,
                createJobConfiguration(fileBackupJob.getClass(), "0/3 * * * * ?", 1, null));
        return springJobScheduler;
    }

}

```

### 整合DB实现定时任务

> **整合环境搭建**

```xml
        <!-- mysql-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
```

> **编写 service 业务逻辑代码**

```java
@Service
public class FileService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取未备份的文件
     *
     * @param fileType
     * @param count
     * @return
     */
    public List<FileCustom> fetchUnBackUpFiles(String fileType, int count) {
        String sql = "select * from t_file where type = ? and backedUp = 0 limit 0,?";
        //  查询未备份的文件
        List<FileCustom> fileCustoms = jdbcTemplate.query(sql, new Object[]{fileType, count}, new BeanPropertyRowMapper<>(FileCustom.class));
        return fileCustoms;
    }

    /**
     * 更新文件的备份状态（进行文件备份）
     *
     * @param files
     */
    public void backUpFiles(List<FileCustom> files) {
        String sql = "update t_file set backedUp  =1 where id = ?";
        for (FileCustom file : files) {
            jdbcTemplate.update(sql, new Object[]{file.getId()});
            System.out.println(String.format("线程 %d | 已备份文件:%s 文件类型:%s"
                    , Thread.currentThread().getId()
                    , file.getName()
                    , file.getType()));
        }
    }
}
```

编写定时任务配置类 ，在实现SimpleJob 接口之后，需要执行的方法 `execute()` 方法 

可以通过 ShardingContext 参数 的 get 方法获取当前任务的参数：在当前案例中 shardingContext  的参数是当前文件的扩展名

```java
@Component
public class FileBackupJobDB implements SimpleJob {

    //  每次执行任务时获取文件的数量
    private final int FETCH_SIZE = 1;

    @Autowired
    private FileService fileService;

    /**
     * Elastic-Job 只负责作业分片但是作业分片之后的业务内容
     * 由程序员编写业务逻辑代码进行处理
     *
     * @param shardingContext
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("作业分片：" + shardingContext.getShardingItem());
        //分片参数，（0=text,1=image,2=radio,3=vedio，参数就是text、image...）
        String jobParameter = shardingContext.getShardingParameter();   //  获取任务分片的参数
        //  获取未备份文件
        List<FileCustom> fileCustoms = fetchUnBackupFiles(jobParameter, FETCH_SIZE);
        //  备份文件
        backupFiles(fileCustoms);
    }

    /**
     * 获取未备份的文件
     *
     * @param count
     * @return
     */
    public List<FileCustom> fetchUnBackupFiles(String fileType, int count) {
        //  定时任务的参数相当于 传递的文件类型
        List<FileCustom> fileCustoms = fileService.fetchUnBackUpFiles(fileType, count);
        return fileCustoms;
    }
    /**
     * 对所有文件进行备份
     *
     * @param files
     */
    public void backupFiles(List<FileCustom> files) {
        fileService.backUpFiles(files);
    }
}

```

在 Elastic-Job 中新增配置

```java
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

  
    @Bean(initMethod = "init")
    public SpringJobScheduler initElasticJob() {
        //  创建 SpringJobScheduler
        //  参数：ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig, JobEventConfiguration jobEventConfig, ElasticJobListener... elasticJobListeners
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(fileBackupJobDB, registryCenter,
                //  设置 任务分片参数 （在本案例中根据需要备份的文件类型设置参数，不同分片的作业实例获取的文件类型是不同的）
                createDataFlowJobConfiguration(fileBackupJobFlow.getClass(), "0/3 * * * * ?", 4, "0=text,1=image,2=radio,3=vedio"));
        return springJobScheduler;
    }

}

```

设置任务分片参数 

 



### 分片策略



> 分片案例 ： 配置分片任务类的全路径名

```java
        //  创建 LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration
                .newBuilder(simpleJobConfiguration)
                //  在 LiteJboConfiguration 中配置策略类的全路径名
                .jobShardingStrategyClass("com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy")
                .overwrite(true).build();
```

## DataFlow 类型定时任务





