package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_CITY a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class CityDynamicSqlSupport {

     public static final CityDynamic table = new CityDynamic();
    public static final SqlColumn<Long> cid = table.cid;
    public static final SqlColumn<String> cityid = table.cityid;
    public static final SqlColumn<String> city = table.city;
    public static final SqlColumn<String> father = table.father;


    public static final class CityDynamic extends SqlTable {


        public final SqlColumn<Long> cid = column("CID");


        public final SqlColumn<String> cityid = column("CITYID");


        public final SqlColumn<String> city = column("CITY");


        public final SqlColumn<String> father = column("FATHER");

         public CityDynamic() {
            super("ZJJS_CITY");
        }
    }
}
