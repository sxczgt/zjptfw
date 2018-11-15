package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentDevice;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentDeviceDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentDeviceDao  implements IBaseDao<PaymentDevice>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        applyId,
        paymentItemId,
        deviceCount,
        operatorCode,
        operatorName,
        operatorPhone,
        deviceRequisition,
        applyTime,
        auditor,
        auditTime,
    };
     private static final  String[] fileds = {
        "applyId",
        "paymentItemId",
        "deviceCount",
        "operatorCode",
        "operatorName",
        "operatorPhone",
        "deviceRequisition",
        "applyTime",
        "auditor",
        "auditTime",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentDevice entity) {
        InsertStatementProvider<PaymentDevice> is = insert(entity)
        .into(tablePaymentDevice)
        .map(applyId).toProperty("applyId")
        .map(paymentItemId).toProperty("paymentItemId")
        .map(deviceCount).toProperty("deviceCount")
        .map(operatorCode).toProperty("operatorCode")
        .map(operatorName).toProperty("operatorName")
        .map(operatorPhone).toProperty("operatorPhone")
        .map(deviceRequisition).toProperty("deviceRequisition")
        .map(applyTime).toProperty("applyTime")
        .map(auditor).toProperty("auditor")
        .map(auditTime).toProperty("auditTime")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentDevice> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentDevice> batchInsert = insert(entitys)
        .into(tablePaymentDevice)
        .map(applyId).toProperty("applyId")
        .map(paymentItemId).toProperty("paymentItemId")
        .map(deviceCount).toProperty("deviceCount")
        .map(operatorCode).toProperty("operatorCode")
        .map(operatorName).toProperty("operatorName")
        .map(operatorPhone).toProperty("operatorPhone")
        .map(deviceRequisition).toProperty("deviceRequisition")
        .map(applyTime).toProperty("applyTime")
        .map(auditor).toProperty("auditor")
        .map(auditTime).toProperty("auditTime")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentDevice entity) {
        if(entity.getApplyId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentDevice)
                    .set(applyId).equalToWhenPresent(entity::getApplyId)
                    .set(paymentItemId).equalToWhenPresent(entity::getPaymentItemId)
                    .set(deviceCount).equalToWhenPresent(entity::getDeviceCount)
                    .set(operatorCode).equalToWhenPresent(entity::getOperatorCode)
                    .set(operatorName).equalToWhenPresent(entity::getOperatorName)
                    .set(operatorPhone).equalToWhenPresent(entity::getOperatorPhone)
                    .set(deviceRequisition).equalToWhenPresent(entity::getDeviceRequisition)
                    .set(applyTime).equalToWhenPresent(entity::getApplyTime)
                    .set(auditor).equalToWhenPresent(entity::getAuditor)
                    .set(auditTime).equalToWhenPresent(entity::getAuditTime)
                    .where(applyId,  isEqualTo(entity::getApplyId))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tablePaymentDevice)
                .where(tablePaymentDevice.applyId, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentDevice findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentDevice)
        .where(tablePaymentDevice.applyId, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentDevice> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentDevice>(PaymentDevice.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentDevice();
    }

    @Override
    public List<PaymentDevice> findByConditions(PaymentDevice entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentDevice)
            .where(applyId, isEqualToWhenPresent(entity::getApplyId))
            .and(paymentItemId, isEqualToWhenPresent(entity::getPaymentItemId))
            .and(deviceCount, isEqualToWhenPresent(entity::getDeviceCount))
            .and(operatorCode, isEqualToWhenPresent(entity::getOperatorCode))
            .and(operatorName, isEqualToWhenPresent(entity::getOperatorName))
            .and(operatorPhone, isEqualToWhenPresent(entity::getOperatorPhone))
            .and(deviceRequisition, isEqualToWhenPresent(entity::getDeviceRequisition))
            .and(applyTime, isEqualToWhenPresent(entity::getApplyTime))
            .and(auditor, isEqualToWhenPresent(entity::getAuditor))
            .and(auditTime, isEqualToWhenPresent(entity::getAuditTime))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentDevice>(PaymentDevice.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentDevice> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentDevice)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentDevice>(PaymentDevice.class));
    }

}