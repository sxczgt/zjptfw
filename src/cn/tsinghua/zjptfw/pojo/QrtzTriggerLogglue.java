package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_LOGGLUE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QRTZ_TRIGGER_LOGGLUE")
public class QrtzTriggerLogglue extends Model<QrtzTriggerLogglue> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("JOB_ID")
    private Long jobId;

    @TableField("GLUE_TYPE")
    private String glueType;

    @TableField("GLUE_SOURCE")
    private String glueSource;

    @TableField("GLUE_REMARK")
    private String glueRemark;

    @TableField("ADD_TIME")
    private LocalDateTime addTime;

    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public String getGlueType() {
        return glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }
    public String getGlueSource() {
        return glueSource;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }
    public String getGlueRemark() {
        return glueRemark;
    }

    public void setGlueRemark(String glueRemark) {
        this.glueRemark = glueRemark;
    }
    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public static final String ID = "ID";

    public static final String JOB_ID = "JOB_ID";

    public static final String GLUE_TYPE = "GLUE_TYPE";

    public static final String GLUE_SOURCE = "GLUE_SOURCE";

    public static final String GLUE_REMARK = "GLUE_REMARK";

    public static final String ADD_TIME = "ADD_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "QrtzTriggerLogglue{" +
        "id=" + id +
        ", jobId=" + jobId +
        ", glueType=" + glueType +
        ", glueSource=" + glueSource +
        ", glueRemark=" + glueRemark +
        ", addTime=" + addTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
