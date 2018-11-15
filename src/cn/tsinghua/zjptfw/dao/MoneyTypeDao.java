package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.MoneyType;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.MoneyTypeDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class MoneyTypeDao  implements IBaseDao<MoneyType>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        moneyTypeId,
        moneyTypeCode,
        moneyTypeName,
        enableFlag,
    };
     private static final  String[] fileds = {
        "moneyTypeId",
        "moneyTypeCode",
        "moneyTypeName",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(MoneyType entity) {
        InsertStatementProvider<MoneyType> is = insert(entity)
        .into(tableMoneyType)
        .map(moneyTypeId).toProperty("moneyTypeId")
        .map(moneyTypeCode).toProperty("moneyTypeCode")
        .map(moneyTypeName).toProperty("moneyTypeName")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<MoneyType> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<MoneyType> batchInsert = insert(entitys)
        .into(tableMoneyType)
        .map(moneyTypeId).toProperty("moneyTypeId")
        .map(moneyTypeCode).toProperty("moneyTypeCode")
        .map(moneyTypeName).toProperty("moneyTypeName")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(MoneyType entity) {
        if(entity.getMoneyTypeId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableMoneyType)
                    .set(moneyTypeId).equalToWhenPresent(entity::getMoneyTypeId)
                    .set(moneyTypeCode).equalToWhenPresent(entity::getMoneyTypeCode)
                    .set(moneyTypeName).equalToWhenPresent(entity::getMoneyTypeName)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
                    .where(moneyTypeId,  isEqualTo(entity::getMoneyTypeId))
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
                 DeleteStatementProvider dsp = deleteFrom(tableMoneyType)
                 .where(tableMoneyType.moneyTypeId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public MoneyType findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new MoneyType();
        SelectStatementProvider render = select(basicColumns)
        .from(tableMoneyType)
        .where(tableMoneyType.moneyTypeId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<MoneyType> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<MoneyType>(MoneyType.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new MoneyType();
    }

    @Override
    public List<MoneyType> findByConditions(MoneyType entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableMoneyType)
            .where(moneyTypeId, isEqualToWhenPresent(entity::getMoneyTypeId))
            .and(moneyTypeCode, isEqualToWhenPresent(entity::getMoneyTypeCode))
            .and(moneyTypeName, isEqualToWhenPresent(entity::getMoneyTypeName))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<MoneyType>(MoneyType.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<MoneyType> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableMoneyType)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<MoneyType>(MoneyType.class));
    }

}