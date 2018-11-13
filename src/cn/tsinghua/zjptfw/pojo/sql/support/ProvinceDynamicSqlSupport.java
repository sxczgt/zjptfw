package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_PROVINCE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class ProvinceDynamicSqlSupport {

     public static final ProvinceDynamic table = new ProvinceDynamic();
    public static final SqlColumn<Long> sid = table.sid;
    public static final SqlColumn<String> provinceid = table.provinceid;
    public static final SqlColumn<String> province = table.province;


    public static final class ProvinceDynamic extends SqlTable {


        public final SqlColumn<Long> sid = column("SID");


        public final SqlColumn<String> provinceid = column("PROVINCEID");


        public final SqlColumn<String> province = column("PROVINCE");

         public ProvinceDynamic() {
            super("ZJJS_PROVINCE");
        }
    }
}
