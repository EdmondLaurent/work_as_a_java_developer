package com.edmond.elasticjob.quickstart;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.edmond.elasticjob.quickstart.job.FileBackUpJob;
import com.edmond.elasticjob.quickstart.model.FileCustom;

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
