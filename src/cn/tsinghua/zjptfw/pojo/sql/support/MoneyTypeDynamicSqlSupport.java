package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 支付币种表
 * table select sql:
   select * from ZJJS_MONEY_TYPE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class MoneyTypeDynamicSqlSupport {

     public static final MoneyTypeDynamic table = new MoneyTypeDynamic();
    /**
     * 币种ID
     */
    public static final SqlColumn<String> moneyTypeId = table.moneyTypeId;
    /**
     * 币种编码
     */
    public static final SqlColumn<String> moneyTypeCode = table.moneyTypeCode;
    /**
     * 币种名称
     */
    public static final SqlColumn<String> moneyTypeName = table.moneyTypeName;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = table.enableFlag;


    public static final class MoneyTypeDynamic extends SqlTable {


        public final SqlColumn<String> moneyTypeId = column("MONEY_TYPE_ID");


        public final SqlColumn<String> moneyTypeCode = column("MONEY_TYPE_CODE");


        public final SqlColumn<String> moneyTypeName = column("MONEY_TYPE_NAME");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public MoneyTypeDynamic() {
            super("ZJJS_MONEY_TYPE");
        }
    }
}
