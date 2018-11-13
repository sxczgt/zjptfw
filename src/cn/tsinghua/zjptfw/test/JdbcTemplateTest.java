package cn.tsinghua.zjptfw.test;

import cn.tsinghua.zjptfw.pojo.AuthLog;
import cn.tsinghua.zjptfw.pojo.dto.AuthLogXDto;
import org.junit.Test;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Resource;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.tsinghua.zjptfw.pojo.sql.support.AuthLogDynamicSqlSupport.*;
import static org.junit.Assert.assertThat;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @Auther: guotao
 * @Date: 2018/11/9 14:55
 * @Description: 测试jdbcTemlate查询
 */
public class JdbcTemplateTest extends BaseJunit4Test {

    @Resource
    private JdbcTemplate jdbcTemplate_dev;

    @Resource
    private NamedParameterJdbcTemplate npjt;

    @Test
    public void testQuery1() {
        Map<String, Object> map = new HashMap<>();
        map.put("ID", 1);
        map.put("FIRST_NAME", "tao");
        map.put("LAST_NAME", "guo");
        map.put("BIRTH_DATE",new Date());
        map.put("EMPLOYED","obs");
        int num = npjt.update("insert into SIMPLETABLE(ID,FIRST_NAME,LAST_NAME,BIRTH_DATE,EMPLOYED) values (:ID,:FIRST_NAME,:LAST_NAME,:BIRTH_DATE,:EMPLOYED)", map);
        System.out.println(num);
    }

  /*  @Test
    public void testQuery2() {
        SelectStatementProvider selectStatement = select(firstName,lastName)
                .from(SimpleTableDynamicSqlSupport.simpleTable)
                .where(SimpleTableDynamicSqlSupport.id, isEqualTo(1))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        System.out.println(selectStatement.getSelectStatement());
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(selectStatement.getParameters());
        String s = npjt.queryForObject(selectStatement.getSelectStatement(), mapSqlParameterSource, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                System.out.println(resultSet.getString("first_name"));
                System.out.println(resultSet.getString("last_name"));
                return null;
            }
        });
    }*/
    @Test
    public void testQuery3() {
        SelectStatementProvider selectStatement = select(id,opDesc,opSystem)
                .from(table)
                .where(id, isGreaterThan(Long.valueOf(1112)))
                .and(opDesc,isLike("%UPDATE%"))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        System.out.println(selectStatement.getSelectStatement());
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(selectStatement.getParameters());
        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper<AuthLogXDto>(AuthLogXDto.class);
        List<AuthLogXDto> list = npjt.query(selectStatement.getSelectStatement(), mapSqlParameterSource, rowMapper);
        System.out.println(list.toString());
    }

    @Test
    public  void  testInsert1(){
        AuthLog authLog = new AuthLog();
        authLog.setId(112L);
        authLog.setOpDesc("test");
        authLog.setOpSql("1111");
        authLog.setOpTime(LocalDateTime.now());
        InsertStatementProvider<AuthLog> insertStatement = insert(authLog)
                .into(table)
                .map(id).toProperty("id")
                .map(opDesc).toProperty("opDesc")
                .map(opSql).toProperty("opSql")
                .map(opTime).toProperty("opTime")
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(insertStatement.getRecord());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = npjt.update(insertStatement.getInsertStatement(), parameterSource, keyHolder);
        String generatedKey = (String) keyHolder.getKeys().get(AuthLog.OP_TIME);
        System.out.println(generatedKey);

    }

}
