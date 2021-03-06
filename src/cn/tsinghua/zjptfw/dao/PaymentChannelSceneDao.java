package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentChannelScene;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentChannelSceneDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentChannelSceneDao  implements IBaseDao<PaymentChannelScene>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        paymentChannelId,
        paymentSceneId,
    };
     private static final  String[] fileds = {
        "paymentChannelId",
        "paymentSceneId",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentChannelScene entity) {
        InsertStatementProvider<PaymentChannelScene> is = insert(entity)
        .into(tablePaymentChannelScene)
        .map(paymentChannelId).toProperty("paymentChannelId")
        .map(paymentSceneId).toProperty("paymentSceneId")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentChannelScene> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentChannelScene> batchInsert = insert(entitys)
        .into(tablePaymentChannelScene)
        .map(paymentChannelId).toProperty("paymentChannelId")
        .map(paymentSceneId).toProperty("paymentSceneId")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentChannelScene entity) {
        if(entity.getPaymentChannelId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentChannelScene)
                    .set(paymentChannelId).equalToWhenPresent(entity::getPaymentChannelId)
                    .set(paymentSceneId).equalToWhenPresent(entity::getPaymentSceneId)
                    .where(paymentChannelId,  isEqualTo(entity::getPaymentChannelId))
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
                 DeleteStatementProvider dsp = deleteFrom(tablePaymentChannelScene)
                 .where(tablePaymentChannelScene.paymentChannelId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentChannelScene findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new PaymentChannelScene();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentChannelScene)
        .where(tablePaymentChannelScene.paymentChannelId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentChannelScene> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentChannelScene>(PaymentChannelScene.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentChannelScene();
    }

    @Override
    public List<PaymentChannelScene> findByConditions(PaymentChannelScene entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentChannelScene)
            .where(paymentChannelId, isEqualToWhenPresent(entity::getPaymentChannelId))
            .and(paymentSceneId, isEqualToWhenPresent(entity::getPaymentSceneId))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentChannelScene>(PaymentChannelScene.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentChannelScene> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentChannelScene)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentChannelScene>(PaymentChannelScene.class));
    }

}