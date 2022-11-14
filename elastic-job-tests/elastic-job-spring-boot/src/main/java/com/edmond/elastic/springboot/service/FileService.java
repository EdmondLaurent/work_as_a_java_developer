package com.edmond.elastic.springboot.service;

import com.edmond.elastic.springboot.model.FileCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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