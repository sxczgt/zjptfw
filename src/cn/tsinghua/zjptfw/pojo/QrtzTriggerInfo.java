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
   select * from ZJJS_QRTZ_TRIGGER_INFO a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QRTZ_TRIGGER_INFO")
public class QrtzTriggerInfo extends Model<QrtzTriggerInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("JOB_GROUP")
    private Long jobGroup;

    @TableField("JOB_CRON")
    private String jobCron;

    @TableField("JOB_DESC")
    private String jobDesc;

    @TableField("ADD_TIME")
    private LocalDateTime addTime;

    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    @TableField("AUTHOR")
    private String author;

    @TableField("ALARM_EMAIL")
    private String alarmEmail;

    @TableField("EXECUTOR_ROUTE_STRATEGY")
    private String executorRouteStrategy;

    @TableField("EXECUTOR_HANDLER")
    private String executorHandler;

    @TableField("EXECUTOR_PARAM")
    private String executorParam;

    @TableField("EXECUTOR_BLOCK_STRATEGY")
    private String executorBlockStrategy;

    @TableField("EXECUTOR_TIMEOUT")
    private Long executorTimeout;

    @TableField("EXECUTOR_FAIL_RETRY_COUNT")
    private Long executorFailRetryCount;

    @TableField("GLUE_TYPE")
    private String glueType;

    @TableField("GLUE_SOURCE")
    private String glueSource;

    @TableField("GLUE_REMARK")
    private String glueRemark;

    @TableField("GLUE_UPDATETIME")
    private LocalDateTime glueUpdatetime;

    @TableField("CHILD_JOBID")
    private String childJobid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(Long jobGroup) {
        this.jobGroup = jobGroup;
    }
    public String getJobCron() {
        return jobCron;
    }

    public void setJobCron(String jobCron) {
        this.jobCron = jobCron;
    }
    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
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
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAlarmEmail() {
        return alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }
    public String getExecutorRouteStrategy() {
        return executorRouteStrategy;
    }

    public void setExecutorRouteStrategy(String executorRouteStrategy) {
        this.executorRouteStrategy = executorRouteStrategy;
    }
    public String getExecutorHandler() {
        return executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }
    public String getExecutorParam() {
        return executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }
    public String getExecutorBlockStrategy() {
        return executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }
    public Long getExecutorTimeout() {
        return executorTimeout;
    }

    public void setExecutorTimeout(Long executorTimeout) {
        this.executorTimeout = executorTimeout;
    }
    public Long getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public void setExecutorFailRetryCount(Long executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
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
    public LocalDateTime getGlueUpdatetime() {
        return glueUpdatetime;
    }

    public void setGlueUpdatetime(LocalDateTime glueUpdatetime) {
        this.glueUpdatetime = glueUpdatetime;
    }
    public String getChildJobid() {
        return childJobid;
    }

    public void setChildJobid(String childJobid) {
        this.childJobid = childJobid;
    }

    public static final String ID = "ID";

    public static final String JOB_GROUP = "JOB_GROUP";

    public static final String JOB_CRON = "JOB_CRON";

    public static final String JOB_DESC = "JOB_DESC";

    public static final String ADD_TIME = "ADD_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";

    public static final String AUTHOR = "AUTHOR";

    public static final String ALARM_EMAIL = "ALARM_EMAIL";

    public static final String EXECUTOR_ROUTE_STRATEGY = "EXECUTOR_ROUTE_STRATEGY";

    public static final String EXECUTOR_HANDLER = "EXECUTOR_HANDLER";

    public static final String EXECUTOR_PARAM = "EXECUTOR_PARAM";

    public static final String EXECUTOR_BLOCK_STRATEGY = "EXECUTOR_BLOCK_STRATEGY";

    public static final String EXECUTOR_TIMEOUT = "EXECUTOR_TIMEOUT";

    public static final String EXECUTOR_FAIL_RETRY_COUNT = "EXECUTOR_FAIL_RETRY_COUNT";

    public static final String GLUE_TYPE = "GLUE_TYPE";

    public static final String GLUE_SOURCE = "GLUE_SOURCE";

    public static final String GLUE_REMARK = "GLUE_REMARK";

    public static final String GLUE_UPDATETIME = "GLUE_UPDATETIME";

    public static final String CHILD_JOBID = "CHILD_JOBID";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "QrtzTriggerInfo{" +
        "id=" + id +
        ", jobGroup=" + jobGroup +
        ", jobCron=" + jobCron +
        ", jobDesc=" + jobDesc +
        ", addTime=" + addTime +
        ", updateTime=" + updateTime +
        ", author=" + author +
        ", alarmEmail=" + alarmEmail +
        ", executorRouteStrategy=" + executorRouteStrategy +
        ", executorHandler=" + executorHandler +
        ", executorParam=" + executorParam +
        ", executorBlockStrategy=" + executorBlockStrategy +
        ", executorTimeout=" + executorTimeout +
        ", executorFailRetryCount=" + executorFailRetryCount +
        ", glueType=" + glueType +
        ", glueSource=" + glueSource +
        ", glueRemark=" + glueRemark +
        ", glueUpdatetime=" + glueUpdatetime +
        ", childJobid=" + childJobid +
        "}";
    }
}
