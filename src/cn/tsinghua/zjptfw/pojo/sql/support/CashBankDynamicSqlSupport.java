package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 收款银行表
 * table select sql:
   select * from ZJJS_CASH_BANK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class CashBankDynamicSqlSupport {

     public static final CashBankDynamic table = new CashBankDynamic();
    /**
     * 银行编码+2位序号
     */
    public static final SqlColumn<String> id = table.id;
    /**
     * 收款银行账号
     */
    public static final SqlColumn<String> bankId = table.bankId;
    /**
     * 收款开户银行
     */
    public static final SqlColumn<String> bankName = table.bankName;
    /**
     * 银行显示名称
     */
    public static final SqlColumn<String> bankDisplay = table.bankDisplay;
    /**
     * 收款银行户名
     */
    public static final SqlColumn<String> acctName = table.acctName;
    /**
     * 收款银行所在省份
     */
    public static final SqlColumn<String> bankProvince = table.bankProvince;
    /**
     * 收款银行所在市
     */
    public static final SqlColumn<String> bankCity = table.bankCity;
    /**
     * 收款支行名称
     */
    public static final SqlColumn<String> bankBranch = table.bankBranch;
    /**
     * 对应银行代码表
     */
    public static final SqlColumn<String> bankCode = table.bankCode;
    /**
     * 对公对私标志
     */
    public static final SqlColumn<String> bankSign = table.bankSign;
    /**
     * 校内/校外
     */
    public static final SqlColumn<String> bankType = table.bankType;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = table.enableFlag;


    public static final class CashBankDynamic extends SqlTable {


        public final SqlColumn<String> id = column("ID");


        public final SqlColumn<String> bankId = column("BANK_ID");


        public final SqlColumn<String> bankName = column("BANK_NAME");


        public final SqlColumn<String> bankDisplay = column("BANK_DISPLAY");


        public final SqlColumn<String> acctName = column("ACCT_NAME");


        public final SqlColumn<String> bankProvince = column("BANK_PROVINCE");


        public final SqlColumn<String> bankCity = column("BANK_CITY");


        public final SqlColumn<String> bankBranch = column("BANK_BRANCH");


        public final SqlColumn<String> bankCode = column("BANK_CODE");


        public final SqlColumn<String> bankSign = column("BANK_SIGN");


        public final SqlColumn<String> bankType = column("BANK_TYPE");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public CashBankDynamic() {
            super("ZJJS_CASH_BANK");
        }
    }
}
