package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_REGISTRY a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QrtzTriggerRegistryDynamicSqlSupport {

     public static final QrtzTriggerRegistryDynamic table = new QrtzTriggerRegistryDynamic();
    public static final SqlColumn<Long> id = table.id;
    public static final SqlColumn<String> registryGroup = table.registryGroup;
    public static final SqlColumn<String> registryKey = table.registryKey;
    public static final SqlColumn<String> registryValue = table.registryValue;
    public static final SqlColumn<LocalDateTime> updateTime = table.updateTime;


    public static final class QrtzTriggerRegistryDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<String> registryGroup = column("REGISTRY_GROUP");


        public final SqlColumn<String> registryKey = column("REGISTRY_KEY");


        public final SqlColumn<String> registryValue = column("REGISTRY_VALUE");


        public final SqlColumn<LocalDateTime> updateTime = column("UPDATE_TIME");

         public QrtzTriggerRegistryDynamic() {
            super("ZJJS_QRTZ_TRIGGER_REGISTRY");
        }
    }
}
