package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_LOGGLUE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QrtzTriggerLogglueDynamicSqlSupport {

     public static final QrtzTriggerLogglueDynamic table = new QrtzTriggerLogglueDynamic();
    public static final SqlColumn<Long> id = table.id;
    public static final SqlColumn<Long> jobId = table.jobId;
    public static final SqlColumn<String> glueType = table.glueType;
    public static final SqlColumn<String> glueSource = table.glueSource;
    public static final SqlColumn<String> glueRemark = table.glueRemark;
    public static final SqlColumn<LocalDateTime> addTime = table.addTime;
    public static final SqlColumn<LocalDateTime> updateTime = table.updateTime;


    public static final class QrtzTriggerLogglueDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<Long> jobId = column("JOB_ID");


        public final SqlColumn<String> glueType = column("GLUE_TYPE");


        public final SqlColumn<String> glueSource = column("GLUE_SOURCE");


        public final SqlColumn<String> glueRemark = column("GLUE_REMARK");


        public final SqlColumn<LocalDateTime> addTime = column("ADD_TIME");


        public final SqlColumn<LocalDateTime> updateTime = column("UPDATE_TIME");

         public QrtzTriggerLogglueDynamic() {
            super("ZJJS_QRTZ_TRIGGER_LOGGLUE");
        }
    }
}
