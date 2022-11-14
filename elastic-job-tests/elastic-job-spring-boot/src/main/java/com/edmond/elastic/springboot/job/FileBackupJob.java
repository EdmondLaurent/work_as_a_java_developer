package com.edmond.elastic.springboot.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.edmond.elastic.springboot.model.FileCustom;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     *
     * @param files
     */
    public void backupFiles(List<FileCustom> files) {
        for (FileCustom fileCustom : files) {
            fileCustom.setBackedUp(true);
            System.out.printf("time:%s,备份文件，名称：%s，类型：%s\n", LocalDateTime.now(), fileCustom.getName(), fileCustom.getType());
        }
    }
}
