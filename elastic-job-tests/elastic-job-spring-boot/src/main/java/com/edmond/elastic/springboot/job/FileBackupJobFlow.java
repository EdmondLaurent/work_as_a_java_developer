package com.edmond.elastic.springboot.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.edmond.elastic.springboot.model.FileCustom;
import com.edmond.elastic.springboot.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileBackupJobFlow implements DataflowJob<FileCustom> {

    //  每次执行任务时获取文件的数量
    private final int FETCH_SIZE = 1;

    @Autowired
    private FileService fileService;

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

    /**
     * 抓取数据的方法
     * DataFlow 类型的定时任务 与 SimpleJob 类型的定时任务不同的是
     * 他们会先处理抓取数据的方法 等到所有的数据抓取完毕后才处理执行任务（数据处理）的方法
     *
     * @param shardingContext
     * @return
     */
    @Override
    public List<FileCustom> fetchData(ShardingContext shardingContext) {
        System.out.println("作业分片：" + shardingContext.getShardingItem());
        //  获取作业分片的参数
        String shardingParameter = shardingContext.getShardingParameter();
        List<FileCustom> fileCustoms = fetchUnBackupFiles(shardingParameter, FETCH_SIZE);
        return fileCustoms;
    }

    /**
     * 处理数据的方法
     *
     * @param shardingContext
     * @param list
     */
    @Override
    public void processData(ShardingContext shardingContext, List<FileCustom> list) {
        backupFiles(list);
    }
}
