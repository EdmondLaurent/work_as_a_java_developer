package com.edmond.springboot.elastic;

import com.edmond.elastic.springboot.ScheldulingMain;
import com.edmond.elastic.springboot.model.FileCustom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = ScheldulingMain.class)
@RunWith(SpringRunner.class)
public class JdbcTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 单个查询
     */
    @Test
    public void testQuery() {
        String sql = "select * from t_file where id =?";
        FileCustom fileCustom = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(FileCustom.class), 1);
        System.out.println(fileCustom);
    }

    /**
     * 列表查询
     */
    @Test
    public void testQueryForList() {
        String sql = "select * from t_file";
        List<FileCustom> fileCustoms = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(FileCustom.class));
        for (FileCustom fileCustom : fileCustoms) {
            System.out.println(fileCustom);
        }
    }
}
