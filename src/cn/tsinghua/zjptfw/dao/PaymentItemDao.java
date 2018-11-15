package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.PaymentItem;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.PaymentItemDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PaymentItemDao  implements IBaseDao<PaymentItem>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        paymentItemId,
        partnerId,
        paymentItemName,
        paymentItemSname,
        paymentItemEname,
        chargeContent,
        payBegin,
        payEnd,
        deviceCount,
        financialDeptCode,
        financialDeptName,
        financialItemCode,
        financialItemName,
        projectManagerCode,
        projectManagerName,
        operatorCode,
        operatorName,
        operatorPhone,
        operatorEmail,
        operatorPmPower,
        paymentWebUrl,
        rmbValuation,
        moneyTypeId,
        commissionCharge,
        paymentItemType,
        paymentItemState,
        notifyUrl,
        merchantPublicKey,
        signaturePrivateKey,
        projectCommitment,
        projectRequisition,
        cashBankId,
        isChannelCash,
        isAutoCash,
        isDrawBill,
        isControlRefund,
        creater,
        createDate,
        auditor,
        auditTime,
    };
     private static final  String[] fileds = {
        "paymentItemId",
        "partnerId",
        "paymentItemName",
        "paymentItemSname",
        "paymentItemEname",
        "chargeContent",
        "payBegin",
        "payEnd",
        "deviceCount",
        "financialDeptCode",
        "financialDeptName",
        "financialItemCode",
        "financialItemName",
        "projectManagerCode",
        "projectManagerName",
        "operatorCode",
        "operatorName",
        "operatorPhone",
        "operatorEmail",
        "operatorPmPower",
        "paymentWebUrl",
        "rmbValuation",
        "moneyTypeId",
        "commissionCharge",
        "paymentItemType",
        "paymentItemState",
        "notifyUrl",
        "merchantPublicKey",
        "signaturePrivateKey",
        "projectCommitment",
        "projectRequisition",
        "cashBankId",
        "isChannelCash",
        "isAutoCash",
        "isDrawBill",
        "isControlRefund",
        "creater",
        "createDate",
        "auditor",
        "auditTime",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(PaymentItem entity) {
        InsertStatementProvider<PaymentItem> is = insert(entity)
        .into(tablePaymentItem)
        .map(paymentItemId).toProperty("paymentItemId")
        .map(partnerId).toProperty("partnerId")
        .map(paymentItemName).toProperty("paymentItemName")
        .map(paymentItemSname).toProperty("paymentItemSname")
        .map(paymentItemEname).toProperty("paymentItemEname")
        .map(chargeContent).toProperty("chargeContent")
        .map(payBegin).toProperty("payBegin")
        .map(payEnd).toProperty("payEnd")
        .map(deviceCount).toProperty("deviceCount")
        .map(financialDeptCode).toProperty("financialDeptCode")
        .map(financialDeptName).toProperty("financialDeptName")
        .map(financialItemCode).toProperty("financialItemCode")
        .map(financialItemName).toProperty("financialItemName")
        .map(projectManagerCode).toProperty("projectManagerCode")
        .map(projectManagerName).toProperty("projectManagerName")
        .map(operatorCode).toProperty("operatorCode")
        .map(operatorName).toProperty("operatorName")
        .map(operatorPhone).toProperty("operatorPhone")
        .map(operatorEmail).toProperty("operatorEmail")
        .map(operatorPmPower).toProperty("operatorPmPower")
        .map(paymentWebUrl).toProperty("paymentWebUrl")
        .map(rmbValuation).toProperty("rmbValuation")
        .map(moneyTypeId).toProperty("moneyTypeId")
        .map(commissionCharge).toProperty("commissionCharge")
        .map(paymentItemType).toProperty("paymentItemType")
        .map(paymentItemState).toProperty("paymentItemState")
        .map(notifyUrl).toProperty("notifyUrl")
        .map(merchantPublicKey).toProperty("merchantPublicKey")
        .map(signaturePrivateKey).toProperty("signaturePrivateKey")
        .map(projectCommitment).toProperty("projectCommitment")
        .map(projectRequisition).toProperty("projectRequisition")
        .map(cashBankId).toProperty("cashBankId")
        .map(isChannelCash).toProperty("isChannelCash")
        .map(isAutoCash).toProperty("isAutoCash")
        .map(isDrawBill).toProperty("isDrawBill")
        .map(isControlRefund).toProperty("isControlRefund")
        .map(creater).toProperty("creater")
        .map(createDate).toProperty("createDate")
        .map(auditor).toProperty("auditor")
        .map(auditTime).toProperty("auditTime")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<PaymentItem> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<PaymentItem> batchInsert = insert(entitys)
        .into(tablePaymentItem)
        .map(paymentItemId).toProperty("paymentItemId")
        .map(partnerId).toProperty("partnerId")
        .map(paymentItemName).toProperty("paymentItemName")
        .map(paymentItemSname).toProperty("paymentItemSname")
        .map(paymentItemEname).toProperty("paymentItemEname")
        .map(chargeContent).toProperty("chargeContent")
        .map(payBegin).toProperty("payBegin")
        .map(payEnd).toProperty("payEnd")
        .map(deviceCount).toProperty("deviceCount")
        .map(financialDeptCode).toProperty("financialDeptCode")
        .map(financialDeptName).toProperty("financialDeptName")
        .map(financialItemCode).toProperty("financialItemCode")
        .map(financialItemName).toProperty("financialItemName")
        .map(projectManagerCode).toProperty("projectManagerCode")
        .map(projectManagerName).toProperty("projectManagerName")
        .map(operatorCode).toProperty("operatorCode")
        .map(operatorName).toProperty("operatorName")
        .map(operatorPhone).toProperty("operatorPhone")
        .map(operatorEmail).toProperty("operatorEmail")
        .map(operatorPmPower).toProperty("operatorPmPower")
        .map(paymentWebUrl).toProperty("paymentWebUrl")
        .map(rmbValuation).toProperty("rmbValuation")
        .map(moneyTypeId).toProperty("moneyTypeId")
        .map(commissionCharge).toProperty("commissionCharge")
        .map(paymentItemType).toProperty("paymentItemType")
        .map(paymentItemState).toProperty("paymentItemState")
        .map(notifyUrl).toProperty("notifyUrl")
        .map(merchantPublicKey).toProperty("merchantPublicKey")
        .map(signaturePrivateKey).toProperty("signaturePrivateKey")
        .map(projectCommitment).toProperty("projectCommitment")
        .map(projectRequisition).toProperty("projectRequisition")
        .map(cashBankId).toProperty("cashBankId")
        .map(isChannelCash).toProperty("isChannelCash")
        .map(isAutoCash).toProperty("isAutoCash")
        .map(isDrawBill).toProperty("isDrawBill")
        .map(isControlRefund).toProperty("isControlRefund")
        .map(creater).toProperty("creater")
        .map(createDate).toProperty("createDate")
        .map(auditor).toProperty("auditor")
        .map(auditTime).toProperty("auditTime")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(PaymentItem entity) {
        if(entity.getPaymentItemId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePaymentItem)
                    .set(paymentItemId).equalToWhenPresent(entity::getPaymentItemId)
                    .set(partnerId).equalToWhenPresent(entity::getPartnerId)
                    .set(paymentItemName).equalToWhenPresent(entity::getPaymentItemName)
                    .set(paymentItemSname).equalToWhenPresent(entity::getPaymentItemSname)
                    .set(paymentItemEname).equalToWhenPresent(entity::getPaymentItemEname)
                    .set(chargeContent).equalToWhenPresent(entity::getChargeContent)
                    .set(payBegin).equalToWhenPresent(entity::getPayBegin)
                    .set(payEnd).equalToWhenPresent(entity::getPayEnd)
                    .set(deviceCount).equalToWhenPresent(entity::getDeviceCount)
                    .set(financialDeptCode).equalToWhenPresent(entity::getFinancialDeptCode)
                    .set(financialDeptName).equalToWhenPresent(entity::getFinancialDeptName)
                    .set(financialItemCode).equalToWhenPresent(entity::getFinancialItemCode)
                    .set(financialItemName).equalToWhenPresent(entity::getFinancialItemName)
                    .set(projectManagerCode).equalToWhenPresent(entity::getProjectManagerCode)
                    .set(projectManagerName).equalToWhenPresent(entity::getProjectManagerName)
                    .set(operatorCode).equalToWhenPresent(entity::getOperatorCode)
                    .set(operatorName).equalToWhenPresent(entity::getOperatorName)
                    .set(operatorPhone).equalToWhenPresent(entity::getOperatorPhone)
                    .set(operatorEmail).equalToWhenPresent(entity::getOperatorEmail)
                    .set(operatorPmPower).equalToWhenPresent(entity::getOperatorPmPower)
                    .set(paymentWebUrl).equalToWhenPresent(entity::getPaymentWebUrl)
                    .set(rmbValuation).equalToWhenPresent(entity::getRmbValuation)
                    .set(moneyTypeId).equalToWhenPresent(entity::getMoneyTypeId)
                    .set(commissionCharge).equalToWhenPresent(entity::getCommissionCharge)
                    .set(paymentItemType).equalToWhenPresent(entity::getPaymentItemType)
                    .set(paymentItemState).equalToWhenPresent(entity::getPaymentItemState)
                    .set(notifyUrl).equalToWhenPresent(entity::getNotifyUrl)
                    .set(merchantPublicKey).equalToWhenPresent(entity::getMerchantPublicKey)
                    .set(signaturePrivateKey).equalToWhenPresent(entity::getSignaturePrivateKey)
                    .set(projectCommitment).equalToWhenPresent(entity::getProjectCommitment)
                    .set(projectRequisition).equalToWhenPresent(entity::getProjectRequisition)
                    .set(cashBankId).equalToWhenPresent(entity::getCashBankId)
                    .set(isChannelCash).equalToWhenPresent(entity::getIsChannelCash)
                    .set(isAutoCash).equalToWhenPresent(entity::getIsAutoCash)
                    .set(isDrawBill).equalToWhenPresent(entity::getIsDrawBill)
                    .set(isControlRefund).equalToWhenPresent(entity::getIsControlRefund)
                    .set(creater).equalToWhenPresent(entity::getCreater)
                    .set(createDate).equalToWhenPresent(entity::getCreateDate)
                    .set(auditor).equalToWhenPresent(entity::getAuditor)
                    .set(auditTime).equalToWhenPresent(entity::getAuditTime)
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
                 DeleteStatementProvider dsp = deleteFrom(tablePaymentItem)
                 .where(tablePaymentItem.paymentItemId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public PaymentItem findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new PaymentItem();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePaymentItem)
        .where(tablePaymentItem.paymentItemId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<PaymentItem> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<PaymentItem>(PaymentItem.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new PaymentItem();
    }

    @Override
    public List<PaymentItem> findByConditions(PaymentItem entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePaymentItem)
            .where(paymentItemId, isEqualToWhenPresent(entity::getPaymentItemId))
            .and(partnerId, isEqualToWhenPresent(entity::getPartnerId))
            .and(paymentItemName, isEqualToWhenPresent(entity::getPaymentItemName))
            .and(paymentItemSname, isEqualToWhenPresent(entity::getPaymentItemSname))
            .and(paymentItemEname, isEqualToWhenPresent(entity::getPaymentItemEname))
            .and(chargeContent, isEqualToWhenPresent(entity::getChargeContent))
            .and(payBegin, isEqualToWhenPresent(entity::getPayBegin))
            .and(payEnd, isEqualToWhenPresent(entity::getPayEnd))
            .and(deviceCount, isEqualToWhenPresent(entity::getDeviceCount))
            .and(financialDeptCode, isEqualToWhenPresent(entity::getFinancialDeptCode))
            .and(financialDeptName, isEqualToWhenPresent(entity::getFinancialDeptName))
            .and(financialItemCode, isEqualToWhenPresent(entity::getFinancialItemCode))
            .and(financialItemName, isEqualToWhenPresent(entity::getFinancialItemName))
            .and(projectManagerCode, isEqualToWhenPresent(entity::getProjectManagerCode))
            .and(projectManagerName, isEqualToWhenPresent(entity::getProjectManagerName))
            .and(operatorCode, isEqualToWhenPresent(entity::getOperatorCode))
            .and(operatorName, isEqualToWhenPresent(entity::getOperatorName))
            .and(operatorPhone, isEqualToWhenPresent(entity::getOperatorPhone))
            .and(operatorEmail, isEqualToWhenPresent(entity::getOperatorEmail))
            .and(operatorPmPower, isEqualToWhenPresent(entity::getOperatorPmPower))
            .and(paymentWebUrl, isEqualToWhenPresent(entity::getPaymentWebUrl))
            .and(rmbValuation, isEqualToWhenPresent(entity::getRmbValuation))
            .and(moneyTypeId, isEqualToWhenPresent(entity::getMoneyTypeId))
            .and(commissionCharge, isEqualToWhenPresent(entity::getCommissionCharge))
            .and(paymentItemType, isEqualToWhenPresent(entity::getPaymentItemType))
            .and(paymentItemState, isEqualToWhenPresent(entity::getPaymentItemState))
            .and(notifyUrl, isEqualToWhenPresent(entity::getNotifyUrl))
            .and(merchantPublicKey, isEqualToWhenPresent(entity::getMerchantPublicKey))
            .and(signaturePrivateKey, isEqualToWhenPresent(entity::getSignaturePrivateKey))
            .and(projectCommitment, isEqualToWhenPresent(entity::getProjectCommitment))
            .and(projectRequisition, isEqualToWhenPresent(entity::getProjectRequisition))
            .and(cashBankId, isEqualToWhenPresent(entity::getCashBankId))
            .and(isChannelCash, isEqualToWhenPresent(entity::getIsChannelCash))
            .and(isAutoCash, isEqualToWhenPresent(entity::getIsAutoCash))
            .and(isDrawBill, isEqualToWhenPresent(entity::getIsDrawBill))
            .and(isControlRefund, isEqualToWhenPresent(entity::getIsControlRefund))
            .and(creater, isEqualToWhenPresent(entity::getCreater))
            .and(createDate, isEqualToWhenPresent(entity::getCreateDate))
            .and(auditor, isEqualToWhenPresent(entity::getAuditor))
            .and(auditTime, isEqualToWhenPresent(entity::getAuditTime))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<PaymentItem>(PaymentItem.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<PaymentItem> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePaymentItem)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<PaymentItem>(PaymentItem.class));
    }

}