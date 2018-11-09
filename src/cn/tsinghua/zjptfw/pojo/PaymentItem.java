package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 支付项目表
 * table select sql:
   select * from ZJJS_PAYMENT_ITEM a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_PAYMENT_ITEM")
public class PaymentItem extends Model<PaymentItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "PAYMENT_ITEM_ID", type = IdType.INPUT)
    private String paymentItemId;

    /**
     * 部门ID（申请单位）
     */
    @TableField("PARTNER_ID")
    private String partnerId;

    /**
     * 项目中文名称
     */
    @TableField("PAYMENT_ITEM_NAME")
    private String paymentItemName;

    /**
     * 项目中文简称(限制四个汉字)
     */
    @TableField("PAYMENT_ITEM_SNAME")
    private String paymentItemSname;

    /**
     * 项目英文名称
     */
    @TableField("PAYMENT_ITEM_ENAME")
    private String paymentItemEname;

    /**
     * 项目收费内容
     */
    @TableField("CHARGE_CONTENT")
    private String chargeContent;

    /**
     * 支付开始日期
     */
    @TableField("PAY_BEGIN")
    private LocalDateTime payBegin;

    /**
     * 支付结束日期
     */
    @TableField("PAY_END")
    private LocalDateTime payEnd;

    /**
     * 在用设备个数(设备申请数保存在设备申请表中)
     */
    @TableField("DEVICE_COUNT")
    private Double deviceCount;

    /**
     * 财务部门编码
     */
    @TableField("FINANCIAL_DEPT_CODE")
    private String financialDeptCode;

    /**
     * 财务部门名称
     */
    @TableField("FINANCIAL_DEPT_NAME")
    private String financialDeptName;

    /**
     * 财务项目编码
     */
    @TableField("FINANCIAL_ITEM_CODE")
    private String financialItemCode;

    /**
     * 财务项目名称
     */
    @TableField("FINANCIAL_ITEM_NAME")
    private String financialItemName;

    /**
     * 项目负责人证号
     */
    @TableField("PROJECT_MANAGER_CODE")
    private String projectManagerCode;

    /**
     * 项目负责人姓名
     */
    @TableField("PROJECT_MANAGER_NAME")
    private String projectManagerName;

    /**
     * 经办人证号
     */
    @TableField("OPERATOR_CODE")
    private String operatorCode;

    /**
     * 经办人姓名
     */
    @TableField("OPERATOR_NAME")
    private String operatorName;

    /**
     * 经办人电话
     */
    @TableField("OPERATOR_PHONE")
    private String operatorPhone;

    /**
     * 经办人邮箱
     */
    @TableField("OPERATOR_EMAIL")
    private String operatorEmail;

    /**
     * 经办人开通项目负责人权限
     */
    @TableField("OPERATOR_PM_POWER")
    private String operatorPmPower;

    /**
     * 支付网站网址
     */
    @TableField("PAYMENT_WEB_URL")
    private String paymentWebUrl;

    /**
     * 人民币计价
     */
    @TableField("RMB_VALUATION")
    private String rmbValuation;

    /**
     * 交易币种(默认为人民币)
     */
    @TableField("MONEY_TYPE_ID")
    private String moneyTypeId;

    /**
     * 手续费承担(项目组、持卡人、免费（需要添加到MO_DICTIONARY表中）)
     */
    @TableField("COMMISSION_CHARGE")
    private String commissionCharge;

    /**
     * 项目收费类型(收费类型存在于MO表中，关键字：PAYMENT_TYPE)
     */
    @TableField("PAYMENT_ITEM_TYPE")
    private String paymentItemType;

    /**
     * 项目审批状态
     */
    @TableField("PAYMENT_ITEM_STATE")
    private String paymentItemState;

    /**
     * 导步通知地址(如果商户不在流水中传入异步通知地址，则从此处读取)
     */
    @TableField("NOTIFY_URL")
    private String notifyUrl;

    /**
     * 商户公钥(商户提供)
     */
    @TableField("MERCHANT_PUBLIC_KEY")
    private String merchantPublicKey;

    /**
     * 签名私钥(平台生成)
     */
    @TableField("SIGNATURE_PRIVATE_KEY")
    private String signaturePrivateKey;

    /**
     * 项目承诺书(保存文件名，大小，路径的json串)
     */
    @TableField("PROJECT_COMMITMENT")
    private String projectCommitment;

    /**
     * 项目申请单(保存文件名，大小，路径的json串)
     */
    @TableField("PROJECT_REQUISITION")
    private String projectRequisition;

    /**
     * 收款银行ID(关联收款银行表ID)
     */
    @TableField("CASH_BANK_ID")
    private String cashBankId;

    /**
     * 是否按渠道提款(是-按渠道提款，否-按渠道类型提款)
     */
    @TableField("IS_CHANNEL_CASH")
    private String isChannelCash;

    /**
     * 是否自动提款
     */
    @TableField("IS_AUTO_CASH")
    private String isAutoCash;

    /**
     * 是否开发票
     */
    @TableField("IS_DRAW_BILL")
    private String isDrawBill;

    /**
     * 是否控制退款(是：允许隔天退款，自动提款时预留款；否：不允许隔天退款)
     */
    @TableField("IS_CONTROL_REFUND")
    private String isControlRefund;

    /**
     * 项目创建人
     */
    @TableField("CREATER")
    private String creater;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    /**
     * 财务审核人
     */
    @TableField("AUDITOR")
    private String auditor;

    /**
     * 审核时间
     */
    @TableField("AUDIT_TIME")
    private LocalDateTime auditTime;

    public String getPaymentItemId() {
        return paymentItemId;
    }

    public void setPaymentItemId(String paymentItemId) {
        this.paymentItemId = paymentItemId;
    }
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    public String getPaymentItemName() {
        return paymentItemName;
    }

    public void setPaymentItemName(String paymentItemName) {
        this.paymentItemName = paymentItemName;
    }
    public String getPaymentItemSname() {
        return paymentItemSname;
    }

    public void setPaymentItemSname(String paymentItemSname) {
        this.paymentItemSname = paymentItemSname;
    }
    public String getPaymentItemEname() {
        return paymentItemEname;
    }

    public void setPaymentItemEname(String paymentItemEname) {
        this.paymentItemEname = paymentItemEname;
    }
    public String getChargeContent() {
        return chargeContent;
    }

    public void setChargeContent(String chargeContent) {
        this.chargeContent = chargeContent;
    }
    public LocalDateTime getPayBegin() {
        return payBegin;
    }

    public void setPayBegin(LocalDateTime payBegin) {
        this.payBegin = payBegin;
    }
    public LocalDateTime getPayEnd() {
        return payEnd;
    }

    public void setPayEnd(LocalDateTime payEnd) {
        this.payEnd = payEnd;
    }
    public Double getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(Double deviceCount) {
        this.deviceCount = deviceCount;
    }
    public String getFinancialDeptCode() {
        return financialDeptCode;
    }

    public void setFinancialDeptCode(String financialDeptCode) {
        this.financialDeptCode = financialDeptCode;
    }
    public String getFinancialDeptName() {
        return financialDeptName;
    }

    public void setFinancialDeptName(String financialDeptName) {
        this.financialDeptName = financialDeptName;
    }
    public String getFinancialItemCode() {
        return financialItemCode;
    }

    public void setFinancialItemCode(String financialItemCode) {
        this.financialItemCode = financialItemCode;
    }
    public String getFinancialItemName() {
        return financialItemName;
    }

    public void setFinancialItemName(String financialItemName) {
        this.financialItemName = financialItemName;
    }
    public String getProjectManagerCode() {
        return projectManagerCode;
    }

    public void setProjectManagerCode(String projectManagerCode) {
        this.projectManagerCode = projectManagerCode;
    }
    public String getProjectManagerName() {
        return projectManagerName;
    }

    public void setProjectManagerName(String projectManagerName) {
        this.projectManagerName = projectManagerName;
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
    public String getOperatorEmail() {
        return operatorEmail;
    }

    public void setOperatorEmail(String operatorEmail) {
        this.operatorEmail = operatorEmail;
    }
    public String getOperatorPmPower() {
        return operatorPmPower;
    }

    public void setOperatorPmPower(String operatorPmPower) {
        this.operatorPmPower = operatorPmPower;
    }
    public String getPaymentWebUrl() {
        return paymentWebUrl;
    }

    public void setPaymentWebUrl(String paymentWebUrl) {
        this.paymentWebUrl = paymentWebUrl;
    }
    public String getRmbValuation() {
        return rmbValuation;
    }

    public void setRmbValuation(String rmbValuation) {
        this.rmbValuation = rmbValuation;
    }
    public String getMoneyTypeId() {
        return moneyTypeId;
    }

    public void setMoneyTypeId(String moneyTypeId) {
        this.moneyTypeId = moneyTypeId;
    }
    public String getCommissionCharge() {
        return commissionCharge;
    }

    public void setCommissionCharge(String commissionCharge) {
        this.commissionCharge = commissionCharge;
    }
    public String getPaymentItemType() {
        return paymentItemType;
    }

    public void setPaymentItemType(String paymentItemType) {
        this.paymentItemType = paymentItemType;
    }
    public String getPaymentItemState() {
        return paymentItemState;
    }

    public void setPaymentItemState(String paymentItemState) {
        this.paymentItemState = paymentItemState;
    }
    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    public String getMerchantPublicKey() {
        return merchantPublicKey;
    }

    public void setMerchantPublicKey(String merchantPublicKey) {
        this.merchantPublicKey = merchantPublicKey;
    }
    public String getSignaturePrivateKey() {
        return signaturePrivateKey;
    }

    public void setSignaturePrivateKey(String signaturePrivateKey) {
        this.signaturePrivateKey = signaturePrivateKey;
    }
    public String getProjectCommitment() {
        return projectCommitment;
    }

    public void setProjectCommitment(String projectCommitment) {
        this.projectCommitment = projectCommitment;
    }
    public String getProjectRequisition() {
        return projectRequisition;
    }

    public void setProjectRequisition(String projectRequisition) {
        this.projectRequisition = projectRequisition;
    }
    public String getCashBankId() {
        return cashBankId;
    }

    public void setCashBankId(String cashBankId) {
        this.cashBankId = cashBankId;
    }
    public String getIsChannelCash() {
        return isChannelCash;
    }

    public void setIsChannelCash(String isChannelCash) {
        this.isChannelCash = isChannelCash;
    }
    public String getIsAutoCash() {
        return isAutoCash;
    }

    public void setIsAutoCash(String isAutoCash) {
        this.isAutoCash = isAutoCash;
    }
    public String getIsDrawBill() {
        return isDrawBill;
    }

    public void setIsDrawBill(String isDrawBill) {
        this.isDrawBill = isDrawBill;
    }
    public String getIsControlRefund() {
        return isControlRefund;
    }

    public void setIsControlRefund(String isControlRefund) {
        this.isControlRefund = isControlRefund;
    }
    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public static final String PAYMENT_ITEM_ID = "PAYMENT_ITEM_ID";

    public static final String PARTNER_ID = "PARTNER_ID";

    public static final String PAYMENT_ITEM_NAME = "PAYMENT_ITEM_NAME";

    public static final String PAYMENT_ITEM_SNAME = "PAYMENT_ITEM_SNAME";

    public static final String PAYMENT_ITEM_ENAME = "PAYMENT_ITEM_ENAME";

    public static final String CHARGE_CONTENT = "CHARGE_CONTENT";

    public static final String PAY_BEGIN = "PAY_BEGIN";

    public static final String PAY_END = "PAY_END";

    public static final String DEVICE_COUNT = "DEVICE_COUNT";

    public static final String FINANCIAL_DEPT_CODE = "FINANCIAL_DEPT_CODE";

    public static final String FINANCIAL_DEPT_NAME = "FINANCIAL_DEPT_NAME";

    public static final String FINANCIAL_ITEM_CODE = "FINANCIAL_ITEM_CODE";

    public static final String FINANCIAL_ITEM_NAME = "FINANCIAL_ITEM_NAME";

    public static final String PROJECT_MANAGER_CODE = "PROJECT_MANAGER_CODE";

    public static final String PROJECT_MANAGER_NAME = "PROJECT_MANAGER_NAME";

    public static final String OPERATOR_CODE = "OPERATOR_CODE";

    public static final String OPERATOR_NAME = "OPERATOR_NAME";

    public static final String OPERATOR_PHONE = "OPERATOR_PHONE";

    public static final String OPERATOR_EMAIL = "OPERATOR_EMAIL";

    public static final String OPERATOR_PM_POWER = "OPERATOR_PM_POWER";

    public static final String PAYMENT_WEB_URL = "PAYMENT_WEB_URL";

    public static final String RMB_VALUATION = "RMB_VALUATION";

    public static final String MONEY_TYPE_ID = "MONEY_TYPE_ID";

    public static final String COMMISSION_CHARGE = "COMMISSION_CHARGE";

    public static final String PAYMENT_ITEM_TYPE = "PAYMENT_ITEM_TYPE";

    public static final String PAYMENT_ITEM_STATE = "PAYMENT_ITEM_STATE";

    public static final String NOTIFY_URL = "NOTIFY_URL";

    public static final String MERCHANT_PUBLIC_KEY = "MERCHANT_PUBLIC_KEY";

    public static final String SIGNATURE_PRIVATE_KEY = "SIGNATURE_PRIVATE_KEY";

    public static final String PROJECT_COMMITMENT = "PROJECT_COMMITMENT";

    public static final String PROJECT_REQUISITION = "PROJECT_REQUISITION";

    public static final String CASH_BANK_ID = "CASH_BANK_ID";

    public static final String IS_CHANNEL_CASH = "IS_CHANNEL_CASH";

    public static final String IS_AUTO_CASH = "IS_AUTO_CASH";

    public static final String IS_DRAW_BILL = "IS_DRAW_BILL";

    public static final String IS_CONTROL_REFUND = "IS_CONTROL_REFUND";

    public static final String CREATER = "CREATER";

    public static final String CREATE_DATE = "CREATE_DATE";

    public static final String AUDITOR = "AUDITOR";

    public static final String AUDIT_TIME = "AUDIT_TIME";

    @Override
    protected Serializable pkVal() {
        return this.paymentItemId;
    }

    @Override
    public String toString() {
        return "PaymentItem{" +
        "paymentItemId=" + paymentItemId +
        ", partnerId=" + partnerId +
        ", paymentItemName=" + paymentItemName +
        ", paymentItemSname=" + paymentItemSname +
        ", paymentItemEname=" + paymentItemEname +
        ", chargeContent=" + chargeContent +
        ", payBegin=" + payBegin +
        ", payEnd=" + payEnd +
        ", deviceCount=" + deviceCount +
        ", financialDeptCode=" + financialDeptCode +
        ", financialDeptName=" + financialDeptName +
        ", financialItemCode=" + financialItemCode +
        ", financialItemName=" + financialItemName +
        ", projectManagerCode=" + projectManagerCode +
        ", projectManagerName=" + projectManagerName +
        ", operatorCode=" + operatorCode +
        ", operatorName=" + operatorName +
        ", operatorPhone=" + operatorPhone +
        ", operatorEmail=" + operatorEmail +
        ", operatorPmPower=" + operatorPmPower +
        ", paymentWebUrl=" + paymentWebUrl +
        ", rmbValuation=" + rmbValuation +
        ", moneyTypeId=" + moneyTypeId +
        ", commissionCharge=" + commissionCharge +
        ", paymentItemType=" + paymentItemType +
        ", paymentItemState=" + paymentItemState +
        ", notifyUrl=" + notifyUrl +
        ", merchantPublicKey=" + merchantPublicKey +
        ", signaturePrivateKey=" + signaturePrivateKey +
        ", projectCommitment=" + projectCommitment +
        ", projectRequisition=" + projectRequisition +
        ", cashBankId=" + cashBankId +
        ", isChannelCash=" + isChannelCash +
        ", isAutoCash=" + isAutoCash +
        ", isDrawBill=" + isDrawBill +
        ", isControlRefund=" + isControlRefund +
        ", creater=" + creater +
        ", createDate=" + createDate +
        ", auditor=" + auditor +
        ", auditTime=" + auditTime +
        "}";
    }
}
