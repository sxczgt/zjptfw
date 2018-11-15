package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 支付项目表
 * table select sql:
   select * from ZJJS_PAYMENT_ITEM a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentItemDynamicSqlSupport {

     public static final PaymentItemDynamic tablePaymentItem = new PaymentItemDynamic();
    /**
     * 项目ID
     */
    public static final SqlColumn<String> paymentItemId = tablePaymentItem.paymentItemId;
    /**
     * 部门ID（申请单位）
     */
    public static final SqlColumn<String> partnerId = tablePaymentItem.partnerId;
    /**
     * 项目中文名称
     */
    public static final SqlColumn<String> paymentItemName = tablePaymentItem.paymentItemName;
    /**
     * 项目中文简称(限制四个汉字)
     */
    public static final SqlColumn<String> paymentItemSname = tablePaymentItem.paymentItemSname;
    /**
     * 项目英文名称
     */
    public static final SqlColumn<String> paymentItemEname = tablePaymentItem.paymentItemEname;
    /**
     * 项目收费内容
     */
    public static final SqlColumn<String> chargeContent = tablePaymentItem.chargeContent;
    /**
     * 支付开始日期
     */
    public static final SqlColumn<Date> payBegin = tablePaymentItem.payBegin;
    /**
     * 支付结束日期
     */
    public static final SqlColumn<Date> payEnd = tablePaymentItem.payEnd;
    /**
     * 在用设备个数(设备申请数保存在设备申请表中)
     */
    public static final SqlColumn<Integer> deviceCount = tablePaymentItem.deviceCount;
    /**
     * 财务部门编码
     */
    public static final SqlColumn<String> financialDeptCode = tablePaymentItem.financialDeptCode;
    /**
     * 财务部门名称
     */
    public static final SqlColumn<String> financialDeptName = tablePaymentItem.financialDeptName;
    /**
     * 财务项目编码
     */
    public static final SqlColumn<String> financialItemCode = tablePaymentItem.financialItemCode;
    /**
     * 财务项目名称
     */
    public static final SqlColumn<String> financialItemName = tablePaymentItem.financialItemName;
    /**
     * 项目负责人证号
     */
    public static final SqlColumn<String> projectManagerCode = tablePaymentItem.projectManagerCode;
    /**
     * 项目负责人姓名
     */
    public static final SqlColumn<String> projectManagerName = tablePaymentItem.projectManagerName;
    /**
     * 经办人证号
     */
    public static final SqlColumn<String> operatorCode = tablePaymentItem.operatorCode;
    /**
     * 经办人姓名
     */
    public static final SqlColumn<String> operatorName = tablePaymentItem.operatorName;
    /**
     * 经办人电话
     */
    public static final SqlColumn<String> operatorPhone = tablePaymentItem.operatorPhone;
    /**
     * 经办人邮箱
     */
    public static final SqlColumn<String> operatorEmail = tablePaymentItem.operatorEmail;
    /**
     * 经办人开通项目负责人权限
     */
    public static final SqlColumn<String> operatorPmPower = tablePaymentItem.operatorPmPower;
    /**
     * 支付网站网址
     */
    public static final SqlColumn<String> paymentWebUrl = tablePaymentItem.paymentWebUrl;
    /**
     * 人民币计价
     */
    public static final SqlColumn<String> rmbValuation = tablePaymentItem.rmbValuation;
    /**
     * 交易币种(默认为人民币)
     */
    public static final SqlColumn<String> moneyTypeId = tablePaymentItem.moneyTypeId;
    /**
     * 手续费承担(项目组、持卡人、免费（需要添加到MO_DICTIONARY表中）)
     */
    public static final SqlColumn<String> commissionCharge = tablePaymentItem.commissionCharge;
    /**
     * 项目收费类型(收费类型存在于MO表中，关键字：PAYMENT_TYPE)
     */
    public static final SqlColumn<String> paymentItemType = tablePaymentItem.paymentItemType;
    /**
     * 项目审批状态
     */
    public static final SqlColumn<String> paymentItemState = tablePaymentItem.paymentItemState;
    /**
     * 导步通知地址(如果商户不在流水中传入异步通知地址，则从此处读取)
     */
    public static final SqlColumn<String> notifyUrl = tablePaymentItem.notifyUrl;
    /**
     * 商户公钥(商户提供)
     */
    public static final SqlColumn<String> merchantPublicKey = tablePaymentItem.merchantPublicKey;
    /**
     * 签名私钥(平台生成)
     */
    public static final SqlColumn<String> signaturePrivateKey = tablePaymentItem.signaturePrivateKey;
    /**
     * 项目承诺书(保存文件名，大小，路径的json串)
     */
    public static final SqlColumn<String> projectCommitment = tablePaymentItem.projectCommitment;
    /**
     * 项目申请单(保存文件名，大小，路径的json串)
     */
    public static final SqlColumn<String> projectRequisition = tablePaymentItem.projectRequisition;
    /**
     * 收款银行ID(关联收款银行表ID)
     */
    public static final SqlColumn<String> cashBankId = tablePaymentItem.cashBankId;
    /**
     * 是否按渠道提款(是-按渠道提款，否-按渠道类型提款)
     */
    public static final SqlColumn<String> isChannelCash = tablePaymentItem.isChannelCash;
    /**
     * 是否自动提款
     */
    public static final SqlColumn<String> isAutoCash = tablePaymentItem.isAutoCash;
    /**
     * 是否开发票
     */
    public static final SqlColumn<String> isDrawBill = tablePaymentItem.isDrawBill;
    /**
     * 是否控制退款(是：允许隔天退款，自动提款时预留款；否：不允许隔天退款)
     */
    public static final SqlColumn<String> isControlRefund = tablePaymentItem.isControlRefund;
    /**
     * 项目创建人
     */
    public static final SqlColumn<String> creater = tablePaymentItem.creater;
    /**
     * 创建时间
     */
    public static final SqlColumn<Date> createDate = tablePaymentItem.createDate;
    /**
     * 财务审核人
     */
    public static final SqlColumn<String> auditor = tablePaymentItem.auditor;
    /**
     * 审核时间
     */
    public static final SqlColumn<Date> auditTime = tablePaymentItem.auditTime;


    public static final class PaymentItemDynamic extends SqlTable {


        public final SqlColumn<String> paymentItemId = column("PAYMENT_ITEM_ID");


        public final SqlColumn<String> partnerId = column("PARTNER_ID");


        public final SqlColumn<String> paymentItemName = column("PAYMENT_ITEM_NAME");


        public final SqlColumn<String> paymentItemSname = column("PAYMENT_ITEM_SNAME");


        public final SqlColumn<String> paymentItemEname = column("PAYMENT_ITEM_ENAME");


        public final SqlColumn<String> chargeContent = column("CHARGE_CONTENT");


        public final SqlColumn<Date> payBegin = column("PAY_BEGIN");


        public final SqlColumn<Date> payEnd = column("PAY_END");


        public final SqlColumn<Integer> deviceCount = column("DEVICE_COUNT");


        public final SqlColumn<String> financialDeptCode = column("FINANCIAL_DEPT_CODE");


        public final SqlColumn<String> financialDeptName = column("FINANCIAL_DEPT_NAME");


        public final SqlColumn<String> financialItemCode = column("FINANCIAL_ITEM_CODE");


        public final SqlColumn<String> financialItemName = column("FINANCIAL_ITEM_NAME");


        public final SqlColumn<String> projectManagerCode = column("PROJECT_MANAGER_CODE");


        public final SqlColumn<String> projectManagerName = column("PROJECT_MANAGER_NAME");


        public final SqlColumn<String> operatorCode = column("OPERATOR_CODE");


        public final SqlColumn<String> operatorName = column("OPERATOR_NAME");


        public final SqlColumn<String> operatorPhone = column("OPERATOR_PHONE");


        public final SqlColumn<String> operatorEmail = column("OPERATOR_EMAIL");


        public final SqlColumn<String> operatorPmPower = column("OPERATOR_PM_POWER");


        public final SqlColumn<String> paymentWebUrl = column("PAYMENT_WEB_URL");


        public final SqlColumn<String> rmbValuation = column("RMB_VALUATION");


        public final SqlColumn<String> moneyTypeId = column("MONEY_TYPE_ID");


        public final SqlColumn<String> commissionCharge = column("COMMISSION_CHARGE");


        public final SqlColumn<String> paymentItemType = column("PAYMENT_ITEM_TYPE");


        public final SqlColumn<String> paymentItemState = column("PAYMENT_ITEM_STATE");


        public final SqlColumn<String> notifyUrl = column("NOTIFY_URL");


        public final SqlColumn<String> merchantPublicKey = column("MERCHANT_PUBLIC_KEY");


        public final SqlColumn<String> signaturePrivateKey = column("SIGNATURE_PRIVATE_KEY");


        public final SqlColumn<String> projectCommitment = column("PROJECT_COMMITMENT");


        public final SqlColumn<String> projectRequisition = column("PROJECT_REQUISITION");


        public final SqlColumn<String> cashBankId = column("CASH_BANK_ID");


        public final SqlColumn<String> isChannelCash = column("IS_CHANNEL_CASH");


        public final SqlColumn<String> isAutoCash = column("IS_AUTO_CASH");


        public final SqlColumn<String> isDrawBill = column("IS_DRAW_BILL");


        public final SqlColumn<String> isControlRefund = column("IS_CONTROL_REFUND");


        public final SqlColumn<String> creater = column("CREATER");


        public final SqlColumn<Date> createDate = column("CREATE_DATE");


        public final SqlColumn<String> auditor = column("AUDITOR");


        public final SqlColumn<Date> auditTime = column("AUDIT_TIME");

         public PaymentItemDynamic() {
            super("ZJJS_PAYMENT_ITEM");
        }
    }
}
