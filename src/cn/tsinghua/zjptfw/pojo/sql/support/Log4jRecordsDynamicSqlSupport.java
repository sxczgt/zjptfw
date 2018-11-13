package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.Clob;
import java.time.LocalDateTime;

/**
 * <p>
 * 异常日志表
 * table select sql:
   select * from ZJJS_LOG4J_RECORDS a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class Log4jRecordsDynamicSqlSupport {

     public static final Log4jRecordsDynamic table = new Log4jRecordsDynamic();
    /**
     * uuid（主键）
     */
    public static final SqlColumn<String> uuid = table.uuid;
    /**
     * 时间
     */
    public static final SqlColumn<LocalDateTime> cdated = table.cdated;
    /**
     * 异常类
     */
    public static final SqlColumn<String> clogger = table.clogger;
    /**
     * 异常级别
     */
    public static final SqlColumn<String> clevel = table.clevel;
    /**
     * 具体异常信息
     */
    public static final SqlColumn<Clob> cmessage = table.cmessage;
    /**
     * 异常所属系统
     */
    public static final SqlColumn<String> csystem = table.csystem;


    public static final class Log4jRecordsDynamic extends SqlTable {


        public final SqlColumn<String> uuid = column("UUID");


        public final SqlColumn<LocalDateTime> cdated = column("CDATED");


        public final SqlColumn<String> clogger = column("CLOGGER");


        public final SqlColumn<String> clevel = column("CLEVEL");


        public final SqlColumn<Clob> cmessage = column("CMESSAGE");


        public final SqlColumn<String> csystem = column("CSYSTEM");

         public Log4jRecordsDynamic() {
            super("ZJJS_LOG4J_RECORDS");
        }
    }
}
