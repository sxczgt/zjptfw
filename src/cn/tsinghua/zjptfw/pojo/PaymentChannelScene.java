package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

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

@Table(name="ZJJS_PAYMENT_CHANNEL_SCENE")
public class PaymentChannelScene extends Model<PaymentChannelScene> {


    /**
     * 支付渠道ID
     */
    @TableId(value = "PAYMENT_CHANNEL_ID", type = IdType.INPUT)
    private String paymentChannelId;

    /**
     * 支付场景ID
     */
    @TableField("PAYMENT_SCENE_ID")
    private String paymentSceneId;

    public String getPaymentChannelId() {
        return paymentChannelId;
    }

    public void setPaymentChannelId(String paymentChannelId) {
        this.paymentChannelId = paymentChannelId;
    }
    public String getPaymentSceneId() {
        return paymentSceneId;
    }

    public void setPaymentSceneId(String paymentSceneId) {
        this.paymentSceneId = paymentSceneId;
    }

    @Override
    protected Serializable pkVal() {
        return this.paymentChannelId;
    }

    @Override
    public String toString() {
        return "PaymentChannelScene{" +
        "paymentChannelId=" + paymentChannelId +
        ", paymentSceneId=" + paymentSceneId +
        "}";
    }
}
