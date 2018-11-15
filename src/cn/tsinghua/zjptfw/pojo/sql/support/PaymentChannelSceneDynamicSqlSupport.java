package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 支付渠道场景表
 * table select sql:
   select * from ZJJS_PAYMENT_CHANNEL_SCENE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentChannelSceneDynamicSqlSupport {

     public static final PaymentChannelSceneDynamic tablePaymentChannelScene = new PaymentChannelSceneDynamic();
    /**
     * 支付渠道ID
     */
    public static final SqlColumn<String> paymentChannelId = tablePaymentChannelScene.paymentChannelId;
    /**
     * 支付场景ID
     */
    public static final SqlColumn<String> paymentSceneId = tablePaymentChannelScene.paymentSceneId;


    public static final class PaymentChannelSceneDynamic extends SqlTable {


        public final SqlColumn<String> paymentChannelId = column("PAYMENT_CHANNEL_ID");


        public final SqlColumn<String> paymentSceneId = column("PAYMENT_SCENE_ID");

         public PaymentChannelSceneDynamic() {
            super("ZJJS_PAYMENT_CHANNEL_SCENE");
        }
    }
}
