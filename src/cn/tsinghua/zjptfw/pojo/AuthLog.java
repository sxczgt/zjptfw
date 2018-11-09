package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * 操作日志表(insert,uppdate,delete操作都要记录)
 * table select sql:
   select * from ZJJS_AUTH_LOG a where 1=1
 *
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_AUTH_LOG")
public class AuthLog extends Model<AuthLog> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    /**
     * 被del,insert,update的数据所在表名
     */
    @TableField("DATA_TABLENAME")
    private String dataTablename;

    /**
     * 操作时间
     */
    @TableField("OP_TIME")
    private LocalDateTime opTime;

    /**
     * 操作描述
     */
    @TableField("OP_DESC")
    private String opDesc;

    /**
     * 数据操作的具体SQL语句
     */
    @TableField("OP_SQL")
    private String opSql;

    /**
     * 校内用户用校园卡号，校外用户用auth_user表的username字段
     */
    @TableField("OP_ZJH")
    private String opZjh;

    /**
     * 操作人姓名
     */
    @TableField("OP_XM")
    private String opXm;

    /**
     * 所在IP
     */
    @TableField("OP_IP")
    private String opIp;

    /**
     * 子系统名称
     */
    @TableField("OP_SYSTEM")
    private String opSystem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getDataTablename() {
        return dataTablename;
    }

    public void setDataTablename(String dataTablename) {
        this.dataTablename = dataTablename;
    }
    public LocalDateTime getOpTime() {
        return opTime;
    }

    public void setOpTime(LocalDateTime opTime) {
        this.opTime = opTime;
    }
    public String getOpDesc() {
        return opDesc;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }
    public String getOpSql() {
        return opSql;
    }

    public void setOpSql(String opSql) {
        this.opSql = opSql;
    }
    public String getOpZjh() {
        return opZjh;
    }

    public void setOpZjh(String opZjh) {
        this.opZjh = opZjh;
    }
    public String getOpXm() {
        return opXm;
    }

    public void setOpXm(String opXm) {
        this.opXm = opXm;
    }
    public String getOpIp() {
        return opIp;
    }

    public void setOpIp(String opIp) {
        this.opIp = opIp;
    }
    public String getOpSystem() {
        return opSystem;
    }

    public void setOpSystem(String opSystem) {
        this.opSystem = opSystem;
    }

    public static final String ID = "ID";

    public static final String DATA_TABLENAME = "DATA_TABLENAME";

    public static final String OP_TIME = "OP_TIME";

    public static final String OP_DESC = "OP_DESC";

    public static final String OP_SQL = "OP_SQL";

    public static final String OP_ZJH = "OP_ZJH";

    public static final String OP_XM = "OP_XM";

    public static final String OP_IP = "OP_IP";

    public static final String OP_SYSTEM = "OP_SYSTEM";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AuthLog{" +
        "id=" + id +
        ", dataTablename=" + dataTablename +
        ", opTime=" + opTime +
        ", opDesc=" + opDesc +
        ", opSql=" + opSql +
        ", opZjh=" + opZjh +
        ", opXm=" + opXm +
        ", opIp=" + opIp +
        ", opSystem=" + opSystem +
        "}";
    }
}
