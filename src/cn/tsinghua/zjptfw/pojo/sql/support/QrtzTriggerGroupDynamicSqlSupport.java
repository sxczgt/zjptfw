package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_GROUP a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QrtzTriggerGroupDynamicSqlSupport {

     public static final QrtzTriggerGroupDynamic table = new QrtzTriggerGroupDynamic();
    public static final SqlColumn<Long> id = table.id;
    public static final SqlColumn<String> appName = table.appName;
    public static final SqlColumn<Long> title = table.title;
    public static final SqlColumn<Long> orderxh = table.orderxh;
    public static final SqlColumn<Integer> addressType = table.addressType;
    public static final SqlColumn<String> addressList = table.addressList;


    public static final class QrtzTriggerGroupDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<String> appName = column("APP_NAME");


        public final SqlColumn<Long> title = column("TITLE");


        public final SqlColumn<Long> orderxh = column("ORDERXH");


        public final SqlColumn<Integer> addressType = column("ADDRESS_TYPE");


        public final SqlColumn<String> addressList = column("ADDRESS_LIST");

         public QrtzTriggerGroupDynamic() {
            super("ZJJS_QRTZ_TRIGGER_GROUP");
        }
    }
}
