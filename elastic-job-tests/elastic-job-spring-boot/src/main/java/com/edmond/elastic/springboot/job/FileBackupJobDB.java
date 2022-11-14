package com.edmond.elastic.springboot.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.edmond.elastic.springboot.model.FileCustom;
import com.edmond.elastic.springboot.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
