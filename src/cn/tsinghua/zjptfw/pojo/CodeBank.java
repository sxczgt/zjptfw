package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 银行代码表
 * table select sql:
   select * from ZJJS_CODE_BANK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_CODE_BANK")
public class CodeBank extends Model<CodeBank> {

    private static final long serialVersionUID = 1L;

    /**
     * 银行编码
     */
    @TableId(value = "BANK_CODE", type = IdType.INPUT)
    private String bankCode;

    /**
     * 银行名称
     */
    @TableField("BANK_NAME")
    private String bankName;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public static final String BANK_CODE = "BANK_CODE";

    public static final String BANK_NAME = "BANK_NAME";

    public static final String ENABLE_FLAG = "ENABLE_FLAG";

    @Override
    protected Serializable pkVal() {
        return this.bankCode;
    }

    @Override
    public String toString() {
        return "CodeBank{" +
        "bankCode=" + bankCode +
        ", bankName=" + bankName +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
