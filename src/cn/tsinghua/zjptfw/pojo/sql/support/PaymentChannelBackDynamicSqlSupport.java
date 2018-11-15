package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_PAYMENT_CHANNEL_BACK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentChannelBackDynamicSqlSupport {

     public static final PaymentChannelBackDynamic tablePaymentChannelBack = new PaymentChannelBackDynamic();
    public static final SqlColumn<String> paymentChannelId = tablePaymentChannelBack.paymentChannelId;
    public static final SqlColumn<String> paymentChannelCode = tablePaymentChannelBack.paymentChannelCode;
    public static final SqlColumn<String> paymentChannelName = tablePaymentChannelBack.paymentChannelName;
    public static final SqlColumn<String> accountTable = tablePaymentChannelBack.accountTable;
    public static final SqlColumn<String> enableFlag = tablePaymentChannelBack.enableFlag;


    public static final class PaymentChannelBackDynamic extends SqlTable {


        public final SqlColumn<String> paymentChannelId = column("PAYMENT_CHANNEL_ID");


        public final SqlColumn<String> paymentChannelCode = column("PAYMENT_CHANNEL_CODE");


        public final SqlColumn<String> paymentChannelName = column("PAYMENT_CHANNEL_NAME");


        public final SqlColumn<String> accountTable = column("ACCOUNT_TABLE");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public PaymentChannelBackDynamic() {
            super("ZJJS_PAYMENT_CHANNEL_BACK");
        }
    }
}
