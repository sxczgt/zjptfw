package cn.tsinghua.zjptfw.test;

import cn.tsinghua.zjptfw.dao.AuthLogDao;
import cn.tsinghua.zjptfw.pojo.AuthLog;
import cn.tsinghua.zjptfw.pojo.dto.AuthLogXDto;
import org.junit.Test;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.util.*;

import static cn.tsinghua.zjptfw.pojo.sql.support.AuthLogDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @Auther: guotao
 * @Date: 2018/11/9 14:55
 * @Description: 测试jdbcTemlate查询
 */
public class JdbcTemplateTest extends BaseJunit4Test {

    private static final String seq = "zjjs_authlog_sequence.nextval";
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;
    @Resource
    private AuthLogDao authLogDao;

    @Test
    public void testQuery1() {
        Map<String, Object> map = new HashMap<>();
        map.put("ID", 1);
        map.put("FIRST_NAME", "tao");
        map.put("LAST_NAME", "guo");
//        map.put("BIRTH_DATE",new Date());
        map.put("EMPLOYED","obs");
        int num = jdbcTemplate_dev.update("insert into SIMPLETABLE(ID,FIRST_NAME,LAST_NAME,BIRTH_DATE,EMPLOYED) values (:ID,:FIRST_NAME,:LAST_NAME,:BIRTH_DATE,:EMPLOYED)", map);
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
                .from(tableAuthLog)
                .where(id, isGreaterThan(Long.valueOf(0)))
                .and(opDesc,isLike("%UPDATE%"))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        System.out.println(selectStatement.getSelectStatement());
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(selectStatement.getParameters());
        BeanPropertyRowMapper rowMapper = new BeanPropertyRowMapper<AuthLogXDto>(AuthLogXDto.class);
        List<AuthLogXDto> list = jdbcTemplate_dev.query(selectStatement.getSelectStatement(), mapSqlParameterSource, rowMapper);
        System.out.println(list.toString());
    }

    @Test
    public  void  testInsert1() throws IllegalAccessException {
        AuthLog authLog = new AuthLog();
//        authLog.setId(2l);
        authLog.setOpDesc("test");
        authLog.setOpSql("1111");
        authLog.setOpTime(new Date());
        InsertStatementProvider<AuthLog> insertStatement = insert(authLog)
                .into(tableAuthLog)
                .map(id).toConstant(seq)
                .map(opDesc).toProperty("opDesc")
                .map(opSql).toProperty("opSql")
                .map(opTime).toProperty("opTime")
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        System.out.println(insertStatement.getInsertStatement());
        int update = jdbcTemplate_dev.update(insertStatement.getInsertStatement(), new BeanPropertySqlParameterSource(insertStatement.getRecord()));
//        jdbcTemplate的方式按序列生成主键
//        Map<String, Object> map = new HashMap<>();
//        map.put("id","seq_zjjs.curval");
//        map.put("opDesc", authLog.getOpDesc());
//        map.put("opSql", authLog.getOpSql());
//        map.put("opTime", authLog.getOpTime());
//        int update = jdbcTemplate.update("insert into ZJJS_AUTH_LOG ( ID,OP_DESC, OP_SQL, OP_TIME) values (SEQ_ZJJS.nextval,?, ?, ?)", new PreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement preparedStatement) throws SQLException {
//                preparedStatement.setString(1, authLog.getOpDesc());
//                preparedStatement.setString(2, authLog.getOpSql());
//                preparedStatement.setDate(3, authLog.getOpTime());
//            }
//        });
        System.out.println(update);
    }

    @Test
    public void testInsert2(){
        AuthLog authLog = new AuthLog();
        authLog.setOpDesc("test");
        authLog.setOpSql("1111");
        authLog.setOpTime(new Date());
        authLogDao.save(authLog);
    }
    @Test
    public void testInsert3(){
        ArrayList<AuthLog> records = new ArrayList<>();
        AuthLog authLog = new AuthLog();
        authLog.setOpDesc("test");
        authLog.setOpSql("1111");
        authLog.setOpTime(new Date());
        records.add(authLog);
        authLog = new AuthLog();
        authLog.setOpDesc("test2");
        authLog.setOpSql("2222");
        authLog.setOpTime(new Date());
        records.add(authLog);
        authLogDao.save(records);
    }
    @Test
    public void testUpdate1(){
        AuthLog authLog = new AuthLog();
        authLog.setId(8L);
//        authLog.setOpDesc("1111");
//        authLog.setOpTime(new Date());
        authLogDao.updateRowById(authLog);
    }
    @Test
    public void testDelete1(){
        authLogDao.deleteById(1L);
    }
    @Test
    public void testSelect1(){
        AuthLog authLog = authLogDao.findById(2L);
        System.out.println(authLog);
    }
    @Test
    public void testSelect2(){
        AuthLog log = new AuthLog();
        log.setOpDesc("test2");
        log.setOpSql("1111");
        List<AuthLog> logs = authLogDao.findByConditions(log);
        System.out.println(logs);
    }
    @Test
    public void testSelect3(){
        List<AuthLog> all = authLogDao.findAll();
        System.out.println(all);
    }
}
