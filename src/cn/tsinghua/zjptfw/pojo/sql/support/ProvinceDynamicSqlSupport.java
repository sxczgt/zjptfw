package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_PROVINCE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class ProvinceDynamicSqlSupport {

     public static final ProvinceDynamic tableProvince = new ProvinceDynamic();
    public static final SqlColumn<Long> sid = tableProvince.sid;
    public static final SqlColumn<String> provinceid = tableProvince.provinceid;
    public static final SqlColumn<String> province = tableProvince.province;


    public static final class ProvinceDynamic extends SqlTable {


        public final SqlColumn<Long> sid = column("SID");


        public final SqlColumn<String> provinceid = column("PROVINCEID");


        public final SqlColumn<String> province = column("PROVINCE");

         public ProvinceDynamic() {
            super("ZJJS_PROVINCE");
        }
    }
}
