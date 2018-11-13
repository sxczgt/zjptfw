package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_LOG a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QrtzTriggerLogDynamicSqlSupport {

     public static final QrtzTriggerLogDynamic table = new QrtzTriggerLogDynamic();
    public static final SqlColumn<Long> id = table.id;
    public static final SqlColumn<Long> jobGroup = table.jobGroup;
    public static final SqlColumn<Long> jobId = table.jobId;
    public static final SqlColumn<String> executorAddress = table.executorAddress;
    public static final SqlColumn<String> executorHandler = table.executorHandler;
    public static final SqlColumn<String> executorParam = table.executorParam;
    public static final SqlColumn<String> executorShardingParam = table.executorShardingParam;
    public static final SqlColumn<Long> executorFailRetryCount = table.executorFailRetryCount;
    public static final SqlColumn<LocalDateTime> triggerTime = table.triggerTime;
    public static final SqlColumn<Long> triggerCode = table.triggerCode;
    public static final SqlColumn<String> triggerMsg = table.triggerMsg;
    public static final SqlColumn<LocalDateTime> handleTime = table.handleTime;
    public static final SqlColumn<Long> handleCode = table.handleCode;
    public static final SqlColumn<String> handleMsg = table.handleMsg;


    public static final class QrtzTriggerLogDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<Long> jobGroup = column("JOB_GROUP");


        public final SqlColumn<Long> jobId = column("JOB_ID");


        public final SqlColumn<String> executorAddress = column("EXECUTOR_ADDRESS");


        public final SqlColumn<String> executorHandler = column("EXECUTOR_HANDLER");


        public final SqlColumn<String> executorParam = column("EXECUTOR_PARAM");


        public final SqlColumn<String> executorShardingParam = column("EXECUTOR_SHARDING_PARAM");


        public final SqlColumn<Long> executorFailRetryCount = column("EXECUTOR_FAIL_RETRY_COUNT");


        public final SqlColumn<LocalDateTime> triggerTime = column("TRIGGER_TIME");


        public final SqlColumn<Long> triggerCode = column("TRIGGER_CODE");


        public final SqlColumn<String> triggerMsg = column("TRIGGER_MSG");


        public final SqlColumn<LocalDateTime> handleTime = column("HANDLE_TIME");


        public final SqlColumn<Long> handleCode = column("HANDLE_CODE");


        public final SqlColumn<String> handleMsg = column("HANDLE_MSG");

         public QrtzTriggerLogDynamic() {
            super("ZJJS_QRTZ_TRIGGER_LOG");
        }
    }
}
