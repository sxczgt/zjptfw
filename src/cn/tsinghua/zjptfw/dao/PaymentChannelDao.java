package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentChannel;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentChannelDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentChannelDao  implements IBaseDao<PaymentChannel>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        paymentChannelId,
        paymentChannelCode,
        paymentChannelName,
        accountTable,
        enableFlag,
    };
     private static final  String[] fileds = {
        "paymentChannelId",
        "paymentChannelCode",
        "paymentChannelName",
        "accountTable",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentChannel entity) {
        InsertStatementProvider<PaymentChannel> is = insert(entity)
        .into(tablePaymentChannel)
        .map(paymentChannelId).toProperty("paymentChannelId")
        .map(paymentChannelCode).toProperty("paymentChannelCode")
        .map(paymentChannelName).toProperty("paymentChannelName")
        .map(accountTable).toProperty("accountTable")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentChannel> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentChannel> batchInsert = insert(entitys)
        .into(tablePaymentChannel)
        .map(paymentChannelId).toProperty("paymentChannelId")
        .map(paymentChannelCode).toProperty("paymentChannelCode")
        .map(paymentChannelName).toProperty("paymentChannelName")
        .map(accountTable).toProperty("accountTable")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentChannel entity) {
        if(entity.getPaymentChannelId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentChannel)
                    .set(paymentChannelId).equalToWhenPresent(entity::getPaymentChannelId)
                    .set(paymentChannelCode).equalToWhenPresent(entity::getPaymentChannelCode)
                    .set(paymentChannelName).equalToWhenPresent(entity::getPaymentChannelName)
                    .set(accountTable).equalToWhenPresent(entity::getAccountTable)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
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
                 DeleteStatementProvider dsp = deleteFrom(tablePaymentChannel)
                 .where(tablePaymentChannel.paymentChannelId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentChannel findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) new PaymentChannel();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentChannel)
        .where(tablePaymentChannel.paymentChannelId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentChannel> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentChannel>(PaymentChannel.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentChannel();
    }

    @Override
    public List<PaymentChannel> findByConditions(PaymentChannel entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentChannel)
            .where(paymentChannelId, isEqualToWhenPresent(entity::getPaymentChannelId))
            .and(paymentChannelCode, isEqualToWhenPresent(entity::getPaymentChannelCode))
            .and(paymentChannelName, isEqualToWhenPresent(entity::getPaymentChannelName))
            .and(accountTable, isEqualToWhenPresent(entity::getAccountTable))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentChannel>(PaymentChannel.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentChannel> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentChannel)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentChannel>(PaymentChannel.class));
    }

}