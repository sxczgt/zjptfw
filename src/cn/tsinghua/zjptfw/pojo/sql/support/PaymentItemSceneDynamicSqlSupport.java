package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 支付项目场景表
 * table select sql:
   select * from ZJJS_PAYMENT_ITEM_SCENE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class PaymentItemSceneDynamicSqlSupport {

     public static final PaymentItemSceneDynamic tablePaymentItemScene = new PaymentItemSceneDynamic();
    /**
     * 支付项目
     */
    public static final SqlColumn<String> paymentItemId = tablePaymentItemScene.paymentItemId;
    /**
     * 支付场景
     */
    public static final SqlColumn<String> paymentSceneId = tablePaymentItemScene.paymentSceneId;


    public static final class PaymentItemSceneDynamic extends SqlTable {


        public final SqlColumn<String> paymentItemId = column("PAYMENT_ITEM_ID");


        public final SqlColumn<String> paymentSceneId = column("PAYMENT_SCENE_ID");

         public PaymentItemSceneDynamic() {
            super("ZJJS_PAYMENT_ITEM_SCENE");
        }
    }
}
