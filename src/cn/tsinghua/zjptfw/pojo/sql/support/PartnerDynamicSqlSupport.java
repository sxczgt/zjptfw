package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 部门信息表
 * table select sql:
   select * from ZJJS_PARTNER a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class PartnerDynamicSqlSupport {

     public static final PartnerDynamic table = new PartnerDynamic();
    /**
     * 部门ID
     */
    public static final SqlColumn<String> partnerId = table.partnerId;
    /**
     * 部门中文名
     */
    public static final SqlColumn<String> partnerName = table.partnerName;
    /**
     * 部门英文名
     */
    public static final SqlColumn<String> partnerEname = table.partnerEname;
    /**
     * 部门显示名
     */
    public static final SqlColumn<String> partnerVname = table.partnerVname;
    /**
     * 启用标志
     */
    public static final SqlColumn<String> enableFlag = table.enableFlag;


    public static final class PartnerDynamic extends SqlTable {


        public final SqlColumn<String> partnerId = column("PARTNER_ID");


        public final SqlColumn<String> partnerName = column("PARTNER_NAME");


        public final SqlColumn<String> partnerEname = column("PARTNER_ENAME");


        public final SqlColumn<String> partnerVname = column("PARTNER_VNAME");


        public final SqlColumn<String> enableFlag = column("ENABLE_FLAG");

         public PartnerDynamic() {
            super("ZJJS_PARTNER");
        }
    }
}
