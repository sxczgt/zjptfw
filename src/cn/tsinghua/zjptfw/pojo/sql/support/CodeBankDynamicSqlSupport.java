package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 银行代码表
 * table select sql:
   select * from ZJJS_CODE_BANK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class CodeBankDynamicSqlSupport {

     public static final CodeBankDynamic table = new CodeBankDynamic();
    /**
     * 银行编码
     */
    public static final SqlColumn<String> bankCode = table.bankCode;
    /**
     * 银行名称
     */
    public static final SqlColumn<String> bankName = table.bankName;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = table.enableFlag;


    public static final class CodeBankDynamic extends SqlTable {


        public final SqlColumn<String> bankCode = column("BANK_CODE");


        public final SqlColumn<String> bankName = column("BANK_NAME");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public CodeBankDynamic() {
            super("ZJJS_CODE_BANK");
        }
    }
}
