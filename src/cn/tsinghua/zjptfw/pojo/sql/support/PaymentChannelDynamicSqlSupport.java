package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 支付渠道表
 * table select sql:
   select * from ZJJS_PAYMENT_CHANNEL a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentChannelDynamicSqlSupport {

     public static final PaymentChannelDynamic tablePaymentChannel = new PaymentChannelDynamic();
    /**
     * 支付渠道ID
     */
    public static final SqlColumn<String> paymentChannelId = tablePaymentChannel.paymentChannelId;
    /**
     * 支付渠道编码
     */
    public static final SqlColumn<String> paymentChannelCode = tablePaymentChannel.paymentChannelCode;
    /**
     * 支付渠道名称
     */
    public static final SqlColumn<String> paymentChannelName = tablePaymentChannel.paymentChannelName;
    /**
     * 对账表名
     */
    public static final SqlColumn<String> accountTable = tablePaymentChannel.accountTable;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = tablePaymentChannel.enableFlag;


    public static final class PaymentChannelDynamic extends SqlTable {


        public final SqlColumn<String> paymentChannelId = column("PAYMENT_CHANNEL_ID");


        public final SqlColumn<String> paymentChannelCode = column("PAYMENT_CHANNEL_CODE");


        public final SqlColumn<String> paymentChannelName = column("PAYMENT_CHANNEL_NAME");


        public final SqlColumn<String> accountTable = column("ACCOUNT_TABLE");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public PaymentChannelDynamic() {
            super("ZJJS_PAYMENT_CHANNEL");
        }
    }
}
