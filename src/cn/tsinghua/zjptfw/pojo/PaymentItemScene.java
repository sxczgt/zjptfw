package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

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

@Table(name="ZJJS_PAYMENT_ITEM_SCENE")
public class PaymentItemScene extends Model<PaymentItemScene> {


    /**
     * 支付项目
     */
    @TableId(value = "PAYMENT_ITEM_ID", type = IdType.INPUT)
    private String paymentItemId;

    /**
     * 支付场景
     */
    @TableField("PAYMENT_SCENE_ID")
    private String paymentSceneId;

    public String getPaymentItemId() {
        return paymentItemId;
    }

    public void setPaymentItemId(String paymentItemId) {
        this.paymentItemId = paymentItemId;
    }
    public String getPaymentSceneId() {
        return paymentSceneId;
    }

    public void setPaymentSceneId(String paymentSceneId) {
        this.paymentSceneId = paymentSceneId;
    }

    @Override
    protected Serializable pkVal() {
        return this.paymentItemId;
    }

    @Override
    public String toString() {
        return "PaymentItemScene{" +
        "paymentItemId=" + paymentItemId +
        ", paymentSceneId=" + paymentSceneId +
        "}";
    }
}
