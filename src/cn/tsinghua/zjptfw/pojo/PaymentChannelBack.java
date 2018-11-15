package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

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

@Table(name="ZJJS_PAYMENT_CHANNEL_BACK")
public class PaymentChannelBack extends Model<PaymentChannelBack> {


    @TableField("PAYMENT_CHANNEL_ID")
    private String paymentChannelId;

    @TableField("PAYMENT_CHANNEL_CODE")
    private String paymentChannelCode;

    @TableField("PAYMENT_CHANNEL_NAME")
    private String paymentChannelName;

    @TableField("ACCOUNT_TABLE")
    private String accountTable;

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

    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "PaymentChannelBack{" +
        "paymentChannelId=" + paymentChannelId +
        ", paymentChannelCode=" + paymentChannelCode +
        ", paymentChannelName=" + paymentChannelName +
        ", accountTable=" + accountTable +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
