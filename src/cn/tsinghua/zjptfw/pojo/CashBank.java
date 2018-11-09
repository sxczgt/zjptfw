package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 收款银行表
 * table select sql:
   select * from ZJJS_CASH_BANK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_CASH_BANK")
public class CashBank extends Model<CashBank> {

    private static final long serialVersionUID = 1L;

    /**
     * 银行编码+2位序号
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 收款银行账号
     */
    @TableField("BANK_ID")
    private String bankId;

    /**
     * 收款开户银行
     */
    @TableField("BANK_NAME")
    private String bankName;

    /**
     * 银行显示名称
     */
    @TableField("BANK_DISPLAY")
    private String bankDisplay;

    /**
     * 收款银行户名
     */
    @TableField("ACCT_NAME")
    private String acctName;

    /**
     * 收款银行所在省份
     */
    @TableField("BANK_PROVINCE")
    private String bankProvince;

    /**
     * 收款银行所在市
     */
    @TableField("BANK_CITY")
    private String bankCity;

    /**
     * 收款支行名称
     */
    @TableField("BANK_BRANCH")
    private String bankBranch;

    /**
     * 对应银行代码表
     */
    @TableField("BANK_CODE")
    private String bankCode;

    /**
     * 对公对私标志
     */
    @TableField("BANK_SIGN")
    private String bankSign;

    /**
     * 校内/校外
     */
    @TableField("BANK_TYPE")
    private String bankType;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getBankDisplay() {
        return bankDisplay;
    }

    public void setBankDisplay(String bankDisplay) {
        this.bankDisplay = bankDisplay;
    }
    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }
    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }
    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }
    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    public String getBankSign() {
        return bankSign;
    }

    public void setBankSign(String bankSign) {
        this.bankSign = bankSign;
    }
    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public static final String ID = "ID";

    public static final String BANK_ID = "BANK_ID";

    public static final String BANK_NAME = "BANK_NAME";

    public static final String BANK_DISPLAY = "BANK_DISPLAY";

    public static final String ACCT_NAME = "ACCT_NAME";

    public static final String BANK_PROVINCE = "BANK_PROVINCE";

    public static final String BANK_CITY = "BANK_CITY";

    public static final String BANK_BRANCH = "BANK_BRANCH";

    public static final String BANK_CODE = "BANK_CODE";

    public static final String BANK_SIGN = "BANK_SIGN";

    public static final String BANK_TYPE = "BANK_TYPE";

    public static final String ENABLE_FLAG = "ENABLE_FLAG";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CashBank{" +
        "id=" + id +
        ", bankId=" + bankId +
        ", bankName=" + bankName +
        ", bankDisplay=" + bankDisplay +
        ", acctName=" + acctName +
        ", bankProvince=" + bankProvince +
        ", bankCity=" + bankCity +
        ", bankBranch=" + bankBranch +
        ", bankCode=" + bankCode +
        ", bankSign=" + bankSign +
        ", bankType=" + bankType +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
