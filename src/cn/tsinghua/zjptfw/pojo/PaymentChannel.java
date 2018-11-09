package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 支付渠道表
 * table select sql:
   select * from ZJJS_PAYMENT_CHANNEL a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_PAYMENT_CHANNEL")
public class PaymentChannel extends Model<PaymentChannel> {

    private static final long serialVersionUID = 1L;

    /**
     * 支付渠道ID
     */
    @TableId(value = "PAYMENT_CHANNEL_ID", type = IdType.INPUT)
    private String paymentChannelId;

    /**
     * 支付渠道编码
     */
    @TableField("PAYMENT_CHANNEL_CODE")
    private String paymentChannelCode;

    /**
     * 支付渠道名称
     */
    @TableField("PAYMENT_CHANNEL_NAME")
    private String paymentChannelName;

    /**
     * 对账表名
     */
    @TableField("ACCOUNT_TABLE")
    private String accountTable;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getPaymentChannelId() {
        return paymentChannelId;
    }

    public void setPaymentChannelId(String paymentChannelId) {
        this.paymentChannelId = paymentChannelId;
    }
    public String getPaymentChannelCode() {
        return paymentChannelCode;
    }

    public void setPaymentChannelCode(String paymentChannelCode) {
        this.paymentChannelCode = paymentChannelCode;
    }
    public String getPaymentChannelName() {
        return paymentChannelName;
    }

    public void setPaymentChannelName(String paymentChannelName) {
        this.paymentChannelName = paymentChannelName;
    }
    public String getAccountTable() {
        return accountTable;
    }

    public void setAccountTable(String accountTable) {
        this.accountTable = accountTable;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public static final String PAYMENT_CHANNEL_ID = "PAYMENT_CHANNEL_ID";

    public static final String PAYMENT_CHANNEL_CODE = "PAYMENT_CHANNEL_CODE";

    public static final String PAYMENT_CHANNEL_NAME = "PAYMENT_CHANNEL_NAME";

    public static final String ACCOUNT_TABLE = "ACCOUNT_TABLE";

    public static final String ENABLE_FLAG = "ENABLE_FLAG";

    @Override
    protected Serializable pkVal() {
        return this.paymentChannelId;
    }

    @Override
    public String toString() {
        return "PaymentChannel{" +
        "paymentChannelId=" + paymentChannelId +
        ", paymentChannelCode=" + paymentChannelCode +
        ", paymentChannelName=" + paymentChannelName +
        ", accountTable=" + accountTable +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
