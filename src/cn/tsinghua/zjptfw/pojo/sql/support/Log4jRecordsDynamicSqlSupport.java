package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.Clob;
import java.util.Date;

/**
 * <p>
 * 异常日志表
 * table select sql:
   select * from ZJJS_LOG4J_RECORDS a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class Log4jRecordsDynamicSqlSupport {

     public static final Log4jRecordsDynamic tableLog4jRecords = new Log4jRecordsDynamic();
    /**
     * uuid（主键）
     */
    public static final SqlColumn<Long> uuid = tableLog4jRecords.uuid;
    /**
     * 时间
     */
    public static final SqlColumn<Date> cdated = tableLog4jRecords.cdated;
    /**
     * 异常类
     */
    public static final SqlColumn<String> clogger = tableLog4jRecords.clogger;
    /**
     * 异常级别
     */
    public static final SqlColumn<String> clevel = tableLog4jRecords.clevel;
    /**
     * 具体异常信息
     */
    public static final SqlColumn<Clob> cmessage = tableLog4jRecords.cmessage;
    /**
     * 异常所属系统
     */
    public static final SqlColumn<String> csystem = tableLog4jRecords.csystem;


    public static final class Log4jRecordsDynamic extends SqlTable {


        public final SqlColumn<Long> uuid = column("UUID");


        public final SqlColumn<Date> cdated = column("CDATED");


        public final SqlColumn<String> clogger = column("CLOGGER");


        public final SqlColumn<String> clevel = column("CLEVEL");


        public final SqlColumn<Clob> cmessage = column("CMESSAGE");


        public final SqlColumn<String> csystem = column("CSYSTEM");

         public Log4jRecordsDynamic() {
            super("ZJJS_LOG4J_RECORDS");
        }
    }
}
