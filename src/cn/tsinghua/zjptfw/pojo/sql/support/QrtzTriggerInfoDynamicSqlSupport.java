package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_INFO a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QrtzTriggerInfoDynamicSqlSupport {

     public static final QrtzTriggerInfoDynamic table = new QrtzTriggerInfoDynamic();
    public static final SqlColumn<Long> id = table.id;
    public static final SqlColumn<Long> jobGroup = table.jobGroup;
    public static final SqlColumn<String> jobCron = table.jobCron;
    public static final SqlColumn<String> jobDesc = table.jobDesc;
    public static final SqlColumn<LocalDateTime> addTime = table.addTime;
    public static final SqlColumn<LocalDateTime> updateTime = table.updateTime;
    public static final SqlColumn<String> author = table.author;
    public static final SqlColumn<String> alarmEmail = table.alarmEmail;
    public static final SqlColumn<String> executorRouteStrategy = table.executorRouteStrategy;
    public static final SqlColumn<String> executorHandler = table.executorHandler;
    public static final SqlColumn<String> executorParam = table.executorParam;
    public static final SqlColumn<String> executorBlockStrategy = table.executorBlockStrategy;
    public static final SqlColumn<Long> executorTimeout = table.executorTimeout;
    public static final SqlColumn<Long> executorFailRetryCount = table.executorFailRetryCount;
    public static final SqlColumn<String> glueType = table.glueType;
    public static final SqlColumn<String> glueSource = table.glueSource;
    public static final SqlColumn<String> glueRemark = table.glueRemark;
    public static final SqlColumn<LocalDateTime> glueUpdatetime = table.glueUpdatetime;
    public static final SqlColumn<String> childJobid = table.childJobid;


    public static final class QrtzTriggerInfoDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<Long> jobGroup = column("JOB_GROUP");


        public final SqlColumn<String> jobCron = column("JOB_CRON");


        public final SqlColumn<String> jobDesc = column("JOB_DESC");


        public final SqlColumn<LocalDateTime> addTime = column("ADD_TIME");


        public final SqlColumn<LocalDateTime> updateTime = column("UPDATE_TIME");


        public final SqlColumn<String> author = column("AUTHOR");


        public final SqlColumn<String> alarmEmail = column("ALARM_EMAIL");


        public final SqlColumn<String> executorRouteStrategy = column("EXECUTOR_ROUTE_STRATEGY");


        public final SqlColumn<String> executorHandler = column("EXECUTOR_HANDLER");


        public final SqlColumn<String> executorParam = column("EXECUTOR_PARAM");


        public final SqlColumn<String> executorBlockStrategy = column("EXECUTOR_BLOCK_STRATEGY");


        public final SqlColumn<Long> executorTimeout = column("EXECUTOR_TIMEOUT");


        public final SqlColumn<Long> executorFailRetryCount = column("EXECUTOR_FAIL_RETRY_COUNT");


        public final SqlColumn<String> glueType = column("GLUE_TYPE");


        public final SqlColumn<String> glueSource = column("GLUE_SOURCE");


        public final SqlColumn<String> glueRemark = column("GLUE_REMARK");


        public final SqlColumn<LocalDateTime> glueUpdatetime = column("GLUE_UPDATETIME");


        public final SqlColumn<String> childJobid = column("CHILD_JOBID");

         public QrtzTriggerInfoDynamic() {
            super("ZJJS_QRTZ_TRIGGER_INFO");
        }
    }
}
