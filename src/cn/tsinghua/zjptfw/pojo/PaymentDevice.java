package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 支付设备申请表
 * table select sql:
   select * from ZJJS_PAYMENT_DEVICE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */

@Table(name="ZJJS_PAYMENT_DEVICE")
public class PaymentDevice extends Model<PaymentDevice> {


    /**
     * 申请ID
     */
    @TableId(value = "APPLY_ID", type = IdType.INPUT)
    private Long applyId;

    /**
     * 项目ID
     */
    @TableField("PAYMENT_ITEM_ID")
    private String paymentItemId;

    /**
     * 设备数量
     */
    @TableField("DEVICE_COUNT")
    private Integer deviceCount;

    /**
     * 经办人证号
     */
    @TableField("OPERATOR_CODE")
    private String operatorCode;

    /**
     * 经办人名称
     */
    @TableField("OPERATOR_NAME")
    private String operatorName;

    /**
     * 经办人电话
     */
    @TableField("OPERATOR_PHONE")
    private String operatorPhone;

    /**
     * 设备申请单(保存文件名，大小，路径的json串)
     */
    @TableField("DEVICE_REQUISITION")
    private String deviceRequisition;

    /**
     * 申请时间
     */
    @TableField("APPLY_TIME")
    private Date applyTime;

    /**
     * 财务审核人
     */
    @TableField("AUDITOR")
    private String auditor;

    /**
     * 审核时间
     */
    @TableField("AUDIT_TIME")
    private Date auditTime;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }
    public String getPaymentItemId() {
        return paymentItemId;
    }

    public void setPaymentItemId(String paymentItemId) {
        this.paymentItemId = paymentItemId;
    }
    public Integer getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }
    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    public String getOperatorPhone() {
        return operatorPhone;
    }

    public void setOperatorPhone(String operatorPhone) {
        this.operatorPhone = operatorPhone;
    }
    public String getDeviceRequisition() {
        return deviceRequisition;
    }

    public void setDeviceRequisition(String deviceRequisition) {
        this.deviceRequisition = deviceRequisition;
    }
    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.applyId;
    }

    @Override
    public String toString() {
        return "PaymentDevice{" +
        "applyId=" + applyId +
        ", paymentItemId=" + paymentItemId +
        ", deviceCount=" + deviceCount +
        ", operatorCode=" + operatorCode +
        ", operatorName=" + operatorName +
        ", operatorPhone=" + operatorPhone +
        ", deviceRequisition=" + deviceRequisition +
        ", applyTime=" + applyTime +
        ", auditor=" + auditor +
        ", auditTime=" + auditTime +
        "}";
    }
}
