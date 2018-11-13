package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 字典列表
 * table select sql:
   select * from ZJJS_MO_DIC a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class MoDicDynamicSqlSupport {

     public static final MoDicDynamic table = new MoDicDynamic();
    /**
     * 字典标识
     */
    public static final SqlColumn<String> dic = table.dic;
    /**
     * 字典名称
     */
    public static final SqlColumn<String> dicname = table.dicname;
    /**
     * 所在系统名称
     */
    public static final SqlColumn<String> systemName = table.systemName;


    public static final class MoDicDynamic extends SqlTable {


        public final SqlColumn<String> dic = column("DIC");


        public final SqlColumn<String> dicname = column("DICNAME");


        public final SqlColumn<String> systemName = column("SYSTEM_NAME");

         public MoDicDynamic() {
            super("ZJJS_MO_DIC");
        }
    }
}
