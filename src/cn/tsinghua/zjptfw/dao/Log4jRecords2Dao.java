package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.Log4jRecords2;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import cn.tsinghua.zjptfw.utils.string.SequenceParam;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.Log4jRecords2DynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class Log4jRecords2Dao  implements IBaseDao<Log4jRecords2>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        id,
        callerLine,
        callerMethod,
        callerClass,
        callerFilename,
        referenceFlag,
        threadName,
        levelString,
        systemName,
        timestmp,
        formattedMessage,
    };
     private static final  String[] fileds = {
        "callerLine",
        "callerMethod",
        "callerClass",
        "callerFilename",
        "referenceFlag",
        "threadName",
        "levelString",
        "systemName",
        "timestmp",
        "formattedMessage",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(Log4jRecords2 entity) {
        InsertStatementProvider<Log4jRecords2> is = insert(entity)
        .into(tableLog4jRecords2)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(callerLine).toProperty("callerLine")
        .map(callerMethod).toProperty("callerMethod")
        .map(callerClass).toProperty("callerClass")
        .map(callerFilename).toProperty("callerFilename")
        .map(referenceFlag).toProperty("referenceFlag")
        .map(threadName).toProperty("threadName")
        .map(levelString).toProperty("levelString")
        .map(systemName).toProperty("systemName")
        .map(timestmp).toProperty("timestmp")
        .map(formattedMessage).toProperty("formattedMessage")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<Log4jRecords2> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<Log4jRecords2> batchInsert = insert(entitys)
        .into(tableLog4jRecords2)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(callerLine).toProperty("callerLine")
        .map(callerMethod).toProperty("callerMethod")
        .map(callerClass).toProperty("callerClass")
        .map(callerFilename).toProperty("callerFilename")
        .map(referenceFlag).toProperty("referenceFlag")
        .map(threadName).toProperty("threadName")
        .map(levelString).toProperty("levelString")
        .map(systemName).toProperty("systemName")
        .map(timestmp).toProperty("timestmp")
        .map(formattedMessage).toProperty("formattedMessage")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(Log4jRecords2 entity) {
        if(entity.getId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableLog4jRecords2)
                    .set(callerLine).equalToWhenPresent(entity::getCallerLine)
                    .set(callerMethod).equalToWhenPresent(entity::getCallerMethod)
                    .set(callerClass).equalToWhenPresent(entity::getCallerClass)
                    .set(callerFilename).equalToWhenPresent(entity::getCallerFilename)
                    .set(referenceFlag).equalToWhenPresent(entity::getReferenceFlag)
                    .set(threadName).equalToWhenPresent(entity::getThreadName)
                    .set(levelString).equalToWhenPresent(entity::getLevelString)
                    .set(systemName).equalToWhenPresent(entity::getSystemName)
                    .set(timestmp).equalToWhenPresent(entity::getTimestmp)
                    .set(formattedMessage).equalToWhenPresent(entity::getFormattedMessage)
                    .where(id,  isEqualTo(entity::getId))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableLog4jRecords2)
                .where(tableLog4jRecords2.id, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public Log4jRecords2 findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableLog4jRecords2)
        .where(tableLog4jRecords2.id, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<Log4jRecords2> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<Log4jRecords2>(Log4jRecords2.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new Log4jRecords2();
    }

    @Override
    public List<Log4jRecords2> findByConditions(Log4jRecords2 entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableLog4jRecords2)
            .where(id, isEqualToWhenPresent(entity::getId))
            .and(callerLine, isEqualToWhenPresent(entity::getCallerLine))
            .and(callerMethod, isEqualToWhenPresent(entity::getCallerMethod))
            .and(callerClass, isEqualToWhenPresent(entity::getCallerClass))
            .and(callerFilename, isEqualToWhenPresent(entity::getCallerFilename))
            .and(referenceFlag, isEqualToWhenPresent(entity::getReferenceFlag))
            .and(threadName, isEqualToWhenPresent(entity::getThreadName))
            .and(levelString, isEqualToWhenPresent(entity::getLevelString))
            .and(systemName, isEqualToWhenPresent(entity::getSystemName))
            .and(timestmp, isEqualToWhenPresent(entity::getTimestmp))
            .and(formattedMessage, isEqualToWhenPresent(entity::getFormattedMessage))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<Log4jRecords2>(Log4jRecords2.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<Log4jRecords2> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableLog4jRecords2)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<Log4jRecords2>(Log4jRecords2.class));
    }

}