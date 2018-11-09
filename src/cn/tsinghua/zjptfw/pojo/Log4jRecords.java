package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
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
 * @since 2018-11-09
 */

@Table(name="ZJJS_LOG4J_RECORDS")
public class Log4jRecords extends Model<Log4jRecords> {

    private static final long serialVersionUID = 1L;

    /**
     * uuid（主键）
     */
    @TableId(value = "UUID", type = IdType.INPUT)
    private String uuid;

    /**
     * 时间
     */
    @TableField("CDATED")
    private LocalDateTime cdated;

    /**
     * 异常类
     */
    @TableField("CLOGGER")
    private String clogger;

    /**
     * 异常级别
     */
    @TableField("CLEVEL")
    private String clevel;

    /**
     * 具体异常信息
     */
    @TableField("CMESSAGE")
    private Clob cmessage;

    /**
     * 异常所属系统
     */
    @TableField("CSYSTEM")
    private String csystem;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public LocalDateTime getCdated() {
        return cdated;
    }

    public void setCdated(LocalDateTime cdated) {
        this.cdated = cdated;
    }
    public String getClogger() {
        return clogger;
    }

    public void setClogger(String clogger) {
        this.clogger = clogger;
    }
    public String getClevel() {
        return clevel;
    }

    public void setClevel(String clevel) {
        this.clevel = clevel;
    }
    public Clob getCmessage() {
        return cmessage;
    }

    public void setCmessage(Clob cmessage) {
        this.cmessage = cmessage;
    }
    public String getCsystem() {
        return csystem;
    }

    public void setCsystem(String csystem) {
        this.csystem = csystem;
    }

    public static final String UUID = "UUID";

    public static final String CDATED = "CDATED";

    public static final String CLOGGER = "CLOGGER";

    public static final String CLEVEL = "CLEVEL";

    public static final String CMESSAGE = "CMESSAGE";

    public static final String CSYSTEM = "CSYSTEM";

    @Override
    protected Serializable pkVal() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return "Log4jRecords{" +
        "uuid=" + uuid +
        ", cdated=" + cdated +
        ", clogger=" + clogger +
        ", clevel=" + clevel +
        ", cmessage=" + cmessage +
        ", csystem=" + csystem +
        "}";
    }
}
