package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.SerialNumber;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import org.apache.commons.lang.StringUtils;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.SerialNumberDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class SerialNumberDao  implements IBaseDao<SerialNumber>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        snDate,
        snFlowNo,
        snTradeNo,
        snNo,
        snPaymentitemNo,
    };
     private static final  String[] fileds = {
        "snDate",
        "snFlowNo",
        "snTradeNo",
        "snNo",
        "snPaymentitemNo",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(SerialNumber entity) {
        InsertStatementProvider<SerialNumber> is = insert(entity)
        .into(tableSerialNumber)
        .map(snDate).toProperty("snDate")
        .map(snFlowNo).toProperty("snFlowNo")
        .map(snTradeNo).toProperty("snTradeNo")
        .map(snNo).toProperty("snNo")
        .map(snPaymentitemNo).toProperty("snPaymentitemNo")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<SerialNumber> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<SerialNumber> batchInsert = insert(entitys)
        .into(tableSerialNumber)
        .map(snDate).toProperty("snDate")
        .map(snFlowNo).toProperty("snFlowNo")
        .map(snTradeNo).toProperty("snTradeNo")
        .map(snNo).toProperty("snNo")
        .map(snPaymentitemNo).toProperty("snPaymentitemNo")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(SerialNumber entity) {
        if(entity.getSnDate() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableSerialNumber)
                    .set(snDate).equalToWhenPresent(entity::getSnDate)
                    .set(snFlowNo).equalToWhenPresent(entity::getSnFlowNo)
                    .set(snTradeNo).equalToWhenPresent(entity::getSnTradeNo)
                    .set(snNo).equalToWhenPresent(entity::getSnNo)
                    .set(snPaymentitemNo).equalToWhenPresent(entity::getSnPaymentitemNo)
                    .where(snDate,  isEqualTo(entity::getSnDate))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                 String str_id = id+"";
                 if(StringUtils.isEmpty(str_id)) return 0;
                 DeleteStatementProvider dsp = deleteFrom(tableSerialNumber)
                 .where(tableSerialNumber.snDate, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public SerialNumber findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new SerialNumber();
        SelectStatementProvider render = select(basicColumns)
        .from(tableSerialNumber)
        .where(tableSerialNumber.snDate, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<SerialNumber> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<SerialNumber>(SerialNumber.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new SerialNumber();
    }

    @Override
    public List<SerialNumber> findByConditions(SerialNumber entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableSerialNumber)
            .where(snDate, isEqualToWhenPresent(entity::getSnDate))
            .and(snFlowNo, isEqualToWhenPresent(entity::getSnFlowNo))
            .and(snTradeNo, isEqualToWhenPresent(entity::getSnTradeNo))
            .and(snNo, isEqualToWhenPresent(entity::getSnNo))
            .and(snPaymentitemNo, isEqualToWhenPresent(entity::getSnPaymentitemNo))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<SerialNumber>(SerialNumber.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<SerialNumber> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableSerialNumber)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<SerialNumber>(SerialNumber.class));
    }

}