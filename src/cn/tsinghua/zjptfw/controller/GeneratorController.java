package cn.tsinghua.zjptfw.controller;


import cn.tsinghua.zjptfw.utils.generator.CodeGenerator2018;
import cn.tsinghua.zjptfw.utils.generator.TableCommentRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/generator")
public class GeneratorController {

    @Resource
    JdbcTemplate jdbcTemplate;

    @RequestMapping("doGenerator")
    public String doGenerator() {
        System.out.println("1111");
        List<TableCommentRowMapper.TableComment> list = jdbcTemplate.query("select * from user_tab_comments where table_name like 'ZJJS_%'", new TableCommentRowMapper());

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < list.size(); i++) {
            TableCommentRowMapper.TableComment tableComment = list.get(i);
            executorService.execute(() -> CodeGenerator2018.generateByTables(CodeGenerator2018.packageName, tableComment.toString()));
        }
        return "index";
    }
}
