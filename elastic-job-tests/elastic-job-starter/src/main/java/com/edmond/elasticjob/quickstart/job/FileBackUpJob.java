package com.edmond.elasticjob.quickstart.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.edmond.elasticjob.quickstart.model.FileCustom;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

