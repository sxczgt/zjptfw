package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.AuthLog;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.AuthLogDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class AuthLogDao  implements IBaseDao<AuthLog>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        id,
        dataTablename,
        opTime,
        opDesc,
        opSql,
        opZjh,
        opXm,
        opIp,
        opSystem,
    };
     private static final  String[] fileds = {
        "dataTablename",
        "opTime",
        "opDesc",
        "opSql",
        "opZjh",
        "opXm",
        "opIp",
        "opSystem",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(AuthLog entity) {
        InsertStatementProvider<AuthLog> is = insert(entity)
        .into(tableAuthLog)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(dataTablename).toProperty("dataTablename")
        .map(opTime).toProperty("opTime")
        .map(opDesc).toProperty("opDesc")
        .map(opSql).toProperty("opSql")
        .map(opZjh).toProperty("opZjh")
        .map(opXm).toProperty("opXm")
        .map(opIp).toProperty("opIp")
        .map(opSystem).toProperty("opSystem")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<AuthLog> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<AuthLog> batchInsert = insert(entitys)
        .into(tableAuthLog)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(dataTablename).toProperty("dataTablename")
        .map(opTime).toProperty("opTime")
        .map(opDesc).toProperty("opDesc")
        .map(opSql).toProperty("opSql")
        .map(opZjh).toProperty("opZjh")
        .map(opXm).toProperty("opXm")
        .map(opIp).toProperty("opIp")
        .map(opSystem).toProperty("opSystem")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(AuthLog entity) {
        if(entity.getId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableAuthLog)
                    .set(dataTablename).equalToWhenPresent(entity::getDataTablename)
                    .set(opTime).equalToWhenPresent(entity::getOpTime)
                    .set(opDesc).equalToWhenPresent(entity::getOpDesc)
                    .set(opSql).equalToWhenPresent(entity::getOpSql)
                    .set(opZjh).equalToWhenPresent(entity::getOpZjh)
                    .set(opXm).equalToWhenPresent(entity::getOpXm)
                    .set(opIp).equalToWhenPresent(entity::getOpIp)
                    .set(opSystem).equalToWhenPresent(entity::getOpSystem)
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
                DeleteStatementProvider dsp = deleteFrom(tableAuthLog)
                .where(tableAuthLog.id, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public AuthLog findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableAuthLog)
        .where(tableAuthLog.id, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<AuthLog> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<AuthLog>(AuthLog.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new AuthLog();
    }

    @Override
    public List<AuthLog> findByConditions(AuthLog entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableAuthLog)
            .where(id, isEqualToWhenPresent(entity::getId))
            .and(dataTablename, isEqualToWhenPresent(entity::getDataTablename))
            .and(opTime, isEqualToWhenPresent(entity::getOpTime))
            .and(opDesc, isEqualToWhenPresent(entity::getOpDesc))
            .and(opSql, isEqualToWhenPresent(entity::getOpSql))
            .and(opZjh, isEqualToWhenPresent(entity::getOpZjh))
            .and(opXm, isEqualToWhenPresent(entity::getOpXm))
            .and(opIp, isEqualToWhenPresent(entity::getOpIp))
            .and(opSystem, isEqualToWhenPresent(entity::getOpSystem))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<AuthLog>(AuthLog.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<AuthLog> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableAuthLog)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<AuthLog>(AuthLog.class));
    }

}