package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 字典维护权限
 * table select sql:
   select * from ZJJS_MO_GRANT a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class MoGrantDynamicSqlSupport {

     public static final MoGrantDynamic tableMoGrant = new MoGrantDynamic();
    /**
     * ID
     */
    public static final SqlColumn<Long> id = tableMoGrant.id;
    /**
     * mo_dic表中的dic字典代码
     */
    public static final SqlColumn<String> dicCode = tableMoGrant.dicCode;
    /**
     * 角色表ID
     */
    public static final SqlColumn<String> roleId = tableMoGrant.roleId;
    /**
     * auth_dept中的dwbh单位编号
     */
    public static final SqlColumn<String> dwbh = tableMoGrant.dwbh;


    public static final class MoGrantDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<String> dicCode = column("DIC_CODE");


        public final SqlColumn<String> roleId = column("ROLE_ID");


        public final SqlColumn<String> dwbh = column("DWBH");

         public MoGrantDynamic() {
            super("ZJJS_MO_GRANT");
        }
    }
}
