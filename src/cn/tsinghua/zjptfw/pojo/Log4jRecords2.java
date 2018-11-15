package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
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

@Table(name="ZJJS_LOG4J_RECORDS2")
public class Log4jRecords2 extends Model<Log4jRecords2> {


    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    /**
     * 行号
     */
    @TableField("CALLER_LINE")
    private String callerLine;

    /**
     * 方法名
     */
    @TableField("CALLER_METHOD")
    private String callerMethod;

    /**
     * 类
     */
    @TableField("CALLER_CLASS")
    private String callerClass;

    /**
     * 文件名
     */
    @TableField("CALLER_FILENAME")
    private String callerFilename;

    /**
     * 包含标识：1-MDC或上下文属性;2-异常;3-均包含
     */
    @TableField("REFERENCE_FLAG")
    private Double referenceFlag;

    /**
     * 日志线程名
     */
    @TableField("THREAD_NAME")
    private String threadName;

    /**
     * 日志级别
     */
    @TableField("LEVEL_STRING")
    private String levelString;

    /**
     * 子系统名称
     */
    @TableField("SYSTEM_NAME")
    private String systemName;

    /**
     * 记录时间
     */
    @TableField("TIMESTMP")
    private Date timestmp;

    /**
     * 格式化后的日志信息
     */
    @TableField("FORMATTED_MESSAGE")
    private Clob formattedMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCallerLine() {
        return callerLine;
    }

    public void setCallerLine(String callerLine) {
        this.callerLine = callerLine;
    }
    public String getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }
    public String getCallerClass() {
        return callerClass;
    }

    public void setCallerClass(String callerClass) {
        this.callerClass = callerClass;
    }
    public String getCallerFilename() {
        return callerFilename;
    }

    public void setCallerFilename(String callerFilename) {
        this.callerFilename = callerFilename;
    }
    public Double getReferenceFlag() {
        return referenceFlag;
    }

    public void setReferenceFlag(Double referenceFlag) {
        this.referenceFlag = referenceFlag;
    }
    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
    public String getLevelString() {
        return levelString;
    }

    public void setLevelString(String levelString) {
        this.levelString = levelString;
    }
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }
    public Clob getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(Clob formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Log4jRecords2{" +
        "id=" + id +
        ", callerLine=" + callerLine +
        ", callerMethod=" + callerMethod +
        ", callerClass=" + callerClass +
        ", callerFilename=" + callerFilename +
        ", referenceFlag=" + referenceFlag +
        ", threadName=" + threadName +
        ", levelString=" + levelString +
        ", systemName=" + systemName +
        ", timestmp=" + timestmp +
        ", formattedMessage=" + formattedMessage +
        "}";
    }
}
