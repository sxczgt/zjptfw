package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QUARTZ_TASK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class QuartzTaskDynamicSqlSupport {

     public static final QuartzTaskDynamic table = new QuartzTaskDynamic();
    public static final SqlColumn<Double> taskId = table.taskId;
    public static final SqlColumn<String> taskName = table.taskName;
    public static final SqlColumn<String> taskType = table.taskType;
    public static final SqlColumn<LocalDateTime> taskStart = table.taskStart;
    public static final SqlColumn<LocalDateTime> taskEnd = table.taskEnd;
    /**
     * 时间频率表达式
     */
    public static final SqlColumn<String> taskFrequency = table.taskFrequency;
    public static final SqlColumn<String> taskFlag = table.taskFlag;


    public static final class QuartzTaskDynamic extends SqlTable {


        public final SqlColumn<Double> taskId = column("TASK_ID");


        public final SqlColumn<String> taskName = column("TASK_NAME");


        public final SqlColumn<String> taskType = column("TASK_TYPE");


        public final SqlColumn<LocalDateTime> taskStart = column("TASK_START");


        public final SqlColumn<LocalDateTime> taskEnd = column("TASK_END");


        public final SqlColumn<String> taskFrequency = column("TASK_FREQUENCY");


        public final SqlColumn<String> taskFlag = column("TASK_FLAG");

         public QuartzTaskDynamic() {
            super("ZJJS_QUARTZ_TASK");
        }
    }
}
