package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.CodeBank;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.CodeBankDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class CodeBankDao  implements IBaseDao<CodeBank>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        bankCode,
        bankName,
        enableFlag,
    };
     private static final  String[] fileds = {
        "bankCode",
        "bankName",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(CodeBank entity) {
        InsertStatementProvider<CodeBank> is = insert(entity)
        .into(tableCodeBank)
        .map(bankCode).toProperty("bankCode")
        .map(bankName).toProperty("bankName")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<CodeBank> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<CodeBank> batchInsert = insert(entitys)
        .into(tableCodeBank)
        .map(bankCode).toProperty("bankCode")
        .map(bankName).toProperty("bankName")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(CodeBank entity) {
        if(entity.getBankCode() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableCodeBank)
                    .set(bankCode).equalToWhenPresent(entity::getBankCode)
                    .set(bankName).equalToWhenPresent(entity::getBankName)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
                    .where(bankCode,  isEqualTo(entity::getBankCode))
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
                 DeleteStatementProvider dsp = deleteFrom(tableCodeBank)
                 .where(tableCodeBank.bankCode, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public CodeBank findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new CodeBank();
        SelectStatementProvider render = select(basicColumns)
        .from(tableCodeBank)
        .where(tableCodeBank.bankCode, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<CodeBank> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<CodeBank>(CodeBank.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new CodeBank();
    }

    @Override
    public List<CodeBank> findByConditions(CodeBank entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableCodeBank)
            .where(bankCode, isEqualToWhenPresent(entity::getBankCode))
            .and(bankName, isEqualToWhenPresent(entity::getBankName))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<CodeBank>(CodeBank.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<CodeBank> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableCodeBank)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<CodeBank>(CodeBank.class));
    }

}