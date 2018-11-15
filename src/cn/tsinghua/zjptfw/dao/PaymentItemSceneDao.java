package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentItemScene;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentItemSceneDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentItemSceneDao  implements IBaseDao<PaymentItemScene>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        paymentItemId,
        paymentSceneId,
    };
     private static final  String[] fileds = {
        "paymentItemId",
        "paymentSceneId",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentItemScene entity) {
        InsertStatementProvider<PaymentItemScene> is = insert(entity)
        .into(tablePaymentItemScene)
        .map(paymentItemId).toProperty("paymentItemId")
        .map(paymentSceneId).toProperty("paymentSceneId")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentItemScene> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentItemScene> batchInsert = insert(entitys)
        .into(tablePaymentItemScene)
        .map(paymentItemId).toProperty("paymentItemId")
        .map(paymentSceneId).toProperty("paymentSceneId")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentItemScene entity) {
        if(entity.getPaymentItemId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentItemScene)
                    .set(paymentItemId).equalToWhenPresent(entity::getPaymentItemId)
                    .set(paymentSceneId).equalToWhenPresent(entity::getPaymentSceneId)
                    .where(paymentItemId,  isEqualTo(entity::getPaymentItemId))
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
                 DeleteStatementProvider dsp = deleteFrom(tablePaymentItemScene)
                 .where(tablePaymentItemScene.paymentItemId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentItemScene findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new PaymentItemScene();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentItemScene)
        .where(tablePaymentItemScene.paymentItemId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentItemScene> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentItemScene>(PaymentItemScene.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentItemScene();
    }

    @Override
    public List<PaymentItemScene> findByConditions(PaymentItemScene entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentItemScene)
            .where(paymentItemId, isEqualToWhenPresent(entity::getPaymentItemId))
            .and(paymentSceneId, isEqualToWhenPresent(entity::getPaymentSceneId))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentItemScene>(PaymentItemScene.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentItemScene> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentItemScene)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentItemScene>(PaymentItemScene.class));
    }

}