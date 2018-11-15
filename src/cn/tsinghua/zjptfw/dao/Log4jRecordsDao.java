package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.Log4jRecords;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static cn.tsinghua.zjptfw.pojo.sql.support.Log4jRecordsDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class Log4jRecordsDao  implements IBaseDao<Log4jRecords>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        uuid,
        cdated,
        clogger,
        clevel,
        cmessage,
        csystem,
    };
     private static final  String[] fileds = {
        "uuid",
        "cdated",
        "clogger",
        "clevel",
        "cmessage",
        "csystem",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(Log4jRecords entity) {
        InsertStatementProvider<Log4jRecords> is = insert(entity)
        .into(tableLog4jRecords)
        .map(uuid).toProperty("uuid")
        .map(cdated).toProperty("cdated")
        .map(clogger).toProperty("clogger")
        .map(clevel).toProperty("clevel")
        .map(cmessage).toProperty("cmessage")
        .map(csystem).toProperty("csystem")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<Log4jRecords> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<Log4jRecords> batchInsert = insert(entitys)
        .into(tableLog4jRecords)
        .map(uuid).toProperty("uuid")
        .map(cdated).toProperty("cdated")
        .map(clogger).toProperty("clogger")
        .map(clevel).toProperty("clevel")
        .map(cmessage).toProperty("cmessage")
        .map(csystem).toProperty("csystem")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(Log4jRecords entity) {
        if(entity.getUuid() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableLog4jRecords)
                    .set(uuid).equalToWhenPresent(entity::getUuid)
                    .set(cdated).equalToWhenPresent(entity::getCdated)
                    .set(clogger).equalToWhenPresent(entity::getClogger)
                    .set(clevel).equalToWhenPresent(entity::getClevel)
                    .set(cmessage).equalToWhenPresent(entity::getCmessage)
                    .set(csystem).equalToWhenPresent(entity::getCsystem)
                    .where(uuid,  isEqualTo(entity::getUuid))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableLog4jRecords)
                .where(tableLog4jRecords.uuid, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public Log4jRecords findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableLog4jRecords)
        .where(tableLog4jRecords.uuid, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<Log4jRecords> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<Log4jRecords>(Log4jRecords.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new Log4jRecords();
    }

    @Override
    public List<Log4jRecords> findByConditions(Log4jRecords entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableLog4jRecords)
            .where(uuid, isEqualToWhenPresent(entity::getUuid))
            .and(cdated, isEqualToWhenPresent(entity::getCdated))
            .and(clogger, isEqualToWhenPresent(entity::getClogger))
            .and(clevel, isEqualToWhenPresent(entity::getClevel))
            .and(cmessage, isEqualToWhenPresent(entity::getCmessage))
            .and(csystem, isEqualToWhenPresent(entity::getCsystem))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<Log4jRecords>(Log4jRecords.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<Log4jRecords> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableLog4jRecords)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<Log4jRecords>(Log4jRecords.class));
    }

}