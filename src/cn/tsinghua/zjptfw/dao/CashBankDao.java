package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.CashBank;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import cn.tsinghua.zjptfw.utils.string.SequenceParam;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.CashBankDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class CashBankDao  implements IBaseDao<CashBank>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        id,
        bankId,
        bankName,
        bankDisplay,
        acctName,
        bankProvince,
        bankCity,
        bankBranch,
        bankCode,
        bankSign,
        bankType,
        enableFlag,
    };
     private static final  String[] fileds = {
        "bankId",
        "bankName",
        "bankDisplay",
        "acctName",
        "bankProvince",
        "bankCity",
        "bankBranch",
        "bankCode",
        "bankSign",
        "bankType",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(CashBank entity) {
        InsertStatementProvider<CashBank> is = insert(entity)
        .into(tableCashBank)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(bankId).toProperty("bankId")
        .map(bankName).toProperty("bankName")
        .map(bankDisplay).toProperty("bankDisplay")
        .map(acctName).toProperty("acctName")
        .map(bankProvince).toProperty("bankProvince")
        .map(bankCity).toProperty("bankCity")
        .map(bankBranch).toProperty("bankBranch")
        .map(bankCode).toProperty("bankCode")
        .map(bankSign).toProperty("bankSign")
        .map(bankType).toProperty("bankType")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<CashBank> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<CashBank> batchInsert = insert(entitys)
        .into(tableCashBank)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(bankId).toProperty("bankId")
        .map(bankName).toProperty("bankName")
        .map(bankDisplay).toProperty("bankDisplay")
        .map(acctName).toProperty("acctName")
        .map(bankProvince).toProperty("bankProvince")
        .map(bankCity).toProperty("bankCity")
        .map(bankBranch).toProperty("bankBranch")
        .map(bankCode).toProperty("bankCode")
        .map(bankSign).toProperty("bankSign")
        .map(bankType).toProperty("bankType")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(CashBank entity) {
        if(entity.getId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableCashBank)
                    .set(bankId).equalToWhenPresent(entity::getBankId)
                    .set(bankName).equalToWhenPresent(entity::getBankName)
                    .set(bankDisplay).equalToWhenPresent(entity::getBankDisplay)
                    .set(acctName).equalToWhenPresent(entity::getAcctName)
                    .set(bankProvince).equalToWhenPresent(entity::getBankProvince)
                    .set(bankCity).equalToWhenPresent(entity::getBankCity)
                    .set(bankBranch).equalToWhenPresent(entity::getBankBranch)
                    .set(bankCode).equalToWhenPresent(entity::getBankCode)
                    .set(bankSign).equalToWhenPresent(entity::getBankSign)
                    .set(bankType).equalToWhenPresent(entity::getBankType)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
                    .where(id,  isEqualTo(entity::getId))
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
                 DeleteStatementProvider dsp = deleteFrom(tableCashBank)
                 .where(tableCashBank.id, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public CashBank findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new CashBank();
        SelectStatementProvider render = select(basicColumns)
        .from(tableCashBank)
        .where(tableCashBank.id, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<CashBank> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<CashBank>(CashBank.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new CashBank();
    }

    @Override
    public List<CashBank> findByConditions(CashBank entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableCashBank)
            .where(id, isEqualToWhenPresent(entity::getId))
            .and(bankId, isEqualToWhenPresent(entity::getBankId))
            .and(bankName, isEqualToWhenPresent(entity::getBankName))
            .and(bankDisplay, isEqualToWhenPresent(entity::getBankDisplay))
            .and(acctName, isEqualToWhenPresent(entity::getAcctName))
            .and(bankProvince, isEqualToWhenPresent(entity::getBankProvince))
            .and(bankCity, isEqualToWhenPresent(entity::getBankCity))
            .and(bankBranch, isEqualToWhenPresent(entity::getBankBranch))
            .and(bankCode, isEqualToWhenPresent(entity::getBankCode))
            .and(bankSign, isEqualToWhenPresent(entity::getBankSign))
            .and(bankType, isEqualToWhenPresent(entity::getBankType))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<CashBank>(CashBank.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<CashBank> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableCashBank)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<CashBank>(CashBank.class));
    }

}