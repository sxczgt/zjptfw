package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
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
public final class PaymentDeviceDynamicSqlSupport {

     public static final PaymentDeviceDynamic tablePaymentDevice = new PaymentDeviceDynamic();
    /**
     * 申请ID
     */
    public static final SqlColumn<Long> applyId = tablePaymentDevice.applyId;
    /**
     * 项目ID
     */
    public static final SqlColumn<String> paymentItemId = tablePaymentDevice.paymentItemId;
    /**
     * 设备数量
     */
    public static final SqlColumn<Integer> deviceCount = tablePaymentDevice.deviceCount;
    /**
     * 经办人证号
     */
    public static final SqlColumn<String> operatorCode = tablePaymentDevice.operatorCode;
    /**
     * 经办人名称
     */
    public static final SqlColumn<String> operatorName = tablePaymentDevice.operatorName;
    /**
     * 经办人电话
     */
    public static final SqlColumn<String> operatorPhone = tablePaymentDevice.operatorPhone;
    /**
     * 设备申请单(保存文件名，大小，路径的json串)
     */
    public static final SqlColumn<String> deviceRequisition = tablePaymentDevice.deviceRequisition;
    /**
     * 申请时间
     */
    public static final SqlColumn<Date> applyTime = tablePaymentDevice.applyTime;
    /**
     * 财务审核人
     */
    public static final SqlColumn<String> auditor = tablePaymentDevice.auditor;
    /**
     * 审核时间
     */
    public static final SqlColumn<Date> auditTime = tablePaymentDevice.auditTime;


    public static final class PaymentDeviceDynamic extends SqlTable {


        public final SqlColumn<Long> applyId = column("APPLY_ID");


        public final SqlColumn<String> paymentItemId = column("PAYMENT_ITEM_ID");


        public final SqlColumn<Integer> deviceCount = column("DEVICE_COUNT");


        public final SqlColumn<String> operatorCode = column("OPERATOR_CODE");


        public final SqlColumn<String> operatorName = column("OPERATOR_NAME");


        public final SqlColumn<String> operatorPhone = column("OPERATOR_PHONE");


        public final SqlColumn<String> deviceRequisition = column("DEVICE_REQUISITION");


        public final SqlColumn<Date> applyTime = column("APPLY_TIME");


        public final SqlColumn<String> auditor = column("AUDITOR");


        public final SqlColumn<Date> auditTime = column("AUDIT_TIME");

         public PaymentDeviceDynamic() {
            super("ZJJS_PAYMENT_DEVICE");
        }
    }
}
