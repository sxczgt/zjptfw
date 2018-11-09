package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 支付币种表
 * table select sql:
   select * from ZJJS_MONEY_TYPE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_MONEY_TYPE")
public class MoneyType extends Model<MoneyType> {

    private static final long serialVersionUID = 1L;

    /**
     * 币种ID
     */
    @TableId(value = "MONEY_TYPE_ID", type = IdType.INPUT)
    private String moneyTypeId;

    /**
     * 币种编码
     */
    @TableField("MONEY_TYPE_CODE")
    private String moneyTypeCode;

    /**
     * 币种名称
     */
    @TableField("MONEY_TYPE_NAME")
    private String moneyTypeName;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getMoneyTypeId() {
        return moneyTypeId;
    }

    public void setMoneyTypeId(String moneyTypeId) {
        this.moneyTypeId = moneyTypeId;
    }
    public String getMoneyTypeCode() {
        return moneyTypeCode;
    }

    public void setMoneyTypeCode(String moneyTypeCode) {
        this.moneyTypeCode = moneyTypeCode;
    }
    public String getMoneyTypeName() {
        return moneyTypeName;
    }

    public void setMoneyTypeName(String moneyTypeName) {
        this.moneyTypeName = moneyTypeName;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public static final String MONEY_TYPE_ID = "MONEY_TYPE_ID";

    public static final String MONEY_TYPE_CODE = "MONEY_TYPE_CODE";

    public static final String MONEY_TYPE_NAME = "MONEY_TYPE_NAME";

    public static final String ENABLE_FLAG = "ENABLE_FLAG";

    @Override
    protected Serializable pkVal() {
        return this.moneyTypeId;
    }

    @Override
    public String toString() {
        return "MoneyType{" +
        "moneyTypeId=" + moneyTypeId +
        ", moneyTypeCode=" + moneyTypeCode +
        ", moneyTypeName=" + moneyTypeName +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
