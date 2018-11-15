package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

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

@Table(name="ZJJS_PAYMENT_SCENE")
public class PaymentScene extends Model<PaymentScene> {


    /**
     * 支付场景ID
     */
    @TableId(value = "PAYMENT_SCENE_ID", type = IdType.INPUT)
    private String paymentSceneId;

    /**
     * 支付场景编码
     */
    @TableField("PAYMENT_SCENE_CODE")
    private String paymentSceneCode;

    /**
     * 支付场景名称
     */
    @TableField("PAYMENT_SCENE_NAME")
    private String paymentSceneName;

    /**
     * 支付场景描述
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 交易类型(线上交易/线下交易)
     */
    @TableField("TRADE_TYPE")
    private String tradeType;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getPaymentSceneId() {
        return paymentSceneId;
    }

    public void setPaymentSceneId(String paymentSceneId) {
        this.paymentSceneId = paymentSceneId;
    }
    public String getPaymentSceneCode() {
        return paymentSceneCode;
    }

    public void setPaymentSceneCode(String paymentSceneCode) {
        this.paymentSceneCode = paymentSceneCode;
    }
    public String getPaymentSceneName() {
        return paymentSceneName;
    }

    public void setPaymentSceneName(String paymentSceneName) {
        this.paymentSceneName = paymentSceneName;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.paymentSceneId;
    }

    @Override
    public String toString() {
        return "PaymentScene{" +
        "paymentSceneId=" + paymentSceneId +
        ", paymentSceneCode=" + paymentSceneCode +
        ", paymentSceneName=" + paymentSceneName +
        ", remark=" + remark +
        ", tradeType=" + tradeType +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
