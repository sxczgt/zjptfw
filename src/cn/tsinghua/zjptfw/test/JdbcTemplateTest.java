package cn.tsinghua.zjptfw.test;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;

/**
 * @Auther: guotao
 * @Date: 2018/11/9 14:55
 * @Description: 测试jdbcTemlate查询
 */
public class JdbcTemplateTest extends BaseJunit4Test{

    @Resource
    private JdbcTemplate jdbcTemplate_dev;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void testQuery(){

    }
}
