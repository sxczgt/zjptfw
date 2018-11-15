package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentScene;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentSceneDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentSceneDao  implements IBaseDao<PaymentScene>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        paymentSceneId,
        paymentSceneCode,
        paymentSceneName,
        remark,
        tradeType,
        enableFlag,
    };
     private static final  String[] fileds = {
        "paymentSceneId",
        "paymentSceneCode",
        "paymentSceneName",
        "remark",
        "tradeType",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentScene entity) {
        InsertStatementProvider<PaymentScene> is = insert(entity)
        .into(tablePaymentScene)
        .map(paymentSceneId).toProperty("paymentSceneId")
        .map(paymentSceneCode).toProperty("paymentSceneCode")
        .map(paymentSceneName).toProperty("paymentSceneName")
        .map(remark).toProperty("remark")
        .map(tradeType).toProperty("tradeType")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentScene> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentScene> batchInsert = insert(entitys)
        .into(tablePaymentScene)
        .map(paymentSceneId).toProperty("paymentSceneId")
        .map(paymentSceneCode).toProperty("paymentSceneCode")
        .map(paymentSceneName).toProperty("paymentSceneName")
        .map(remark).toProperty("remark")
        .map(tradeType).toProperty("tradeType")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentScene entity) {
        if(entity.getPaymentSceneId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentScene)
                    .set(paymentSceneId).equalToWhenPresent(entity::getPaymentSceneId)
                    .set(paymentSceneCode).equalToWhenPresent(entity::getPaymentSceneCode)
                    .set(paymentSceneName).equalToWhenPresent(entity::getPaymentSceneName)
                    .set(remark).equalToWhenPresent(entity::getRemark)
                    .set(tradeType).equalToWhenPresent(entity::getTradeType)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
                    .where(paymentSceneId,  isEqualTo(entity::getPaymentSceneId))
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
                 DeleteStatementProvider dsp = deleteFrom(tablePaymentScene)
                 .where(tablePaymentScene.paymentSceneId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentScene findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new PaymentScene();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentScene)
        .where(tablePaymentScene.paymentSceneId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentScene> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentScene>(PaymentScene.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentScene();
    }

    @Override
    public List<PaymentScene> findByConditions(PaymentScene entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentScene)
            .where(paymentSceneId, isEqualToWhenPresent(entity::getPaymentSceneId))
            .and(paymentSceneCode, isEqualToWhenPresent(entity::getPaymentSceneCode))
            .and(paymentSceneName, isEqualToWhenPresent(entity::getPaymentSceneName))
            .and(remark, isEqualToWhenPresent(entity::getRemark))
            .and(tradeType, isEqualToWhenPresent(entity::getTradeType))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentScene>(PaymentScene.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentScene> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentScene)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentScene>(PaymentScene.class));
    }

}