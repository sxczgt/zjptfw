package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.Clob;
import java.util.Date;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_LOG4J_RECORDS2 a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class Log4jRecords2DynamicSqlSupport {

     public static final Log4jRecords2Dynamic tableLog4jRecords2 = new Log4jRecords2Dynamic();
    /**
     * ID
     */
    public static final SqlColumn<Long> id = tableLog4jRecords2.id;
    /**
     * 行号
     */
    public static final SqlColumn<String> callerLine = tableLog4jRecords2.callerLine;
    /**
     * 方法名
     */
    public static final SqlColumn<String> callerMethod = tableLog4jRecords2.callerMethod;
    /**
     * 类
     */
    public static final SqlColumn<String> callerClass = tableLog4jRecords2.callerClass;
    /**
     * 文件名
     */
    public static final SqlColumn<String> callerFilename = tableLog4jRecords2.callerFilename;
    /**
     * 包含标识：1-MDC或上下文属性;2-异常;3-均包含
     */
    public static final SqlColumn<Double> referenceFlag = tableLog4jRecords2.referenceFlag;
    /**
     * 日志线程名
     */
    public static final SqlColumn<String> threadName = tableLog4jRecords2.threadName;
    /**
     * 日志级别
     */
    public static final SqlColumn<String> levelString = tableLog4jRecords2.levelString;
    /**
     * 子系统名称
     */
    public static final SqlColumn<String> systemName = tableLog4jRecords2.systemName;
    /**
     * 记录时间
     */
    public static final SqlColumn<Date> timestmp = tableLog4jRecords2.timestmp;
    /**
     * 格式化后的日志信息
     */
    public static final SqlColumn<Clob> formattedMessage = tableLog4jRecords2.formattedMessage;


    public static final class Log4jRecords2Dynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<String> callerLine = column("CALLER_LINE");


        public final SqlColumn<String> callerMethod = column("CALLER_METHOD");


        public final SqlColumn<String> callerClass = column("CALLER_CLASS");


        public final SqlColumn<String> callerFilename = column("CALLER_FILENAME");


        public final SqlColumn<Double> referenceFlag = column("REFERENCE_FLAG");


        public final SqlColumn<String> threadName = column("THREAD_NAME");


        public final SqlColumn<String> levelString = column("LEVEL_STRING");


        public final SqlColumn<String> systemName = column("SYSTEM_NAME");


        public final SqlColumn<Date> timestmp = column("TIMESTMP");


        public final SqlColumn<Clob> formattedMessage = column("FORMATTED_MESSAGE");

         public Log4jRecords2Dynamic() {
            super("ZJJS_LOG4J_RECORDS2");
        }
    }
}
