package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 支付场景表
 * table select sql:
   select * from ZJJS_PAYMENT_SCENE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentSceneDynamicSqlSupport {

     public static final PaymentSceneDynamic tablePaymentScene = new PaymentSceneDynamic();
    /**
     * 支付场景ID
     */
    public static final SqlColumn<String> paymentSceneId = tablePaymentScene.paymentSceneId;
    /**
     * 支付场景编码
     */
    public static final SqlColumn<String> paymentSceneCode = tablePaymentScene.paymentSceneCode;
    /**
     * 支付场景名称
     */
    public static final SqlColumn<String> paymentSceneName = tablePaymentScene.paymentSceneName;
    /**
     * 支付场景描述
     */
    public static final SqlColumn<String> remark = tablePaymentScene.remark;
    /**
     * 交易类型(线上交易/线下交易)
     */
    public static final SqlColumn<String> tradeType = tablePaymentScene.tradeType;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = tablePaymentScene.enableFlag;


    public static final class PaymentSceneDynamic extends SqlTable {


        public final SqlColumn<String> paymentSceneId = column("PAYMENT_SCENE_ID");


        public final SqlColumn<String> paymentSceneCode = column("PAYMENT_SCENE_CODE");


        public final SqlColumn<String> paymentSceneName = column("PAYMENT_SCENE_NAME");


        public final SqlColumn<String> remark = column("REMARK");


        public final SqlColumn<String> tradeType = column("TRADE_TYPE");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public PaymentSceneDynamic() {
            super("ZJJS_PAYMENT_SCENE");
        }
    }
}
