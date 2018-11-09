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
   select * from ZJJS_QRTZ_TRIGGER_LOG a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QRTZ_TRIGGER_LOG")
public class QrtzTriggerLog extends Model<QrtzTriggerLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("JOB_GROUP")
    private Long jobGroup;

    @TableField("JOB_ID")
    private Long jobId;

    @TableField("EXECUTOR_ADDRESS")
    private String executorAddress;

    @TableField("EXECUTOR_HANDLER")
    private String executorHandler;

    @TableField("EXECUTOR_PARAM")
    private String executorParam;

    @TableField("EXECUTOR_SHARDING_PARAM")
    private String executorShardingParam;

    @TableField("EXECUTOR_FAIL_RETRY_COUNT")
    private Long executorFailRetryCount;

    @TableField("TRIGGER_TIME")
    private LocalDateTime triggerTime;

    @TableField("TRIGGER_CODE")
    private Long triggerCode;

    @TableField("TRIGGER_MSG")
    private String triggerMsg;

    @TableField("HANDLE_TIME")
    private LocalDateTime handleTime;

    @TableField("HANDLE_CODE")
    private Long handleCode;

    @TableField("HANDLE_MSG")
    private String handleMsg;

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
    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public String getExecutorAddress() {
        return executorAddress;
    }

    public void setExecutorAddress(String executorAddress) {
        this.executorAddress = executorAddress;
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
    public String getExecutorShardingParam() {
        return executorShardingParam;
    }

    public void setExecutorShardingParam(String executorShardingParam) {
        this.executorShardingParam = executorShardingParam;
    }
    public Long getExecutorFailRetryCount() {
        return executorFailRetryCount;
    }

    public void setExecutorFailRetryCount(Long executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
    }
    public LocalDateTime getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(LocalDateTime triggerTime) {
        this.triggerTime = triggerTime;
    }
    public Long getTriggerCode() {
        return triggerCode;
    }

    public void setTriggerCode(Long triggerCode) {
        this.triggerCode = triggerCode;
    }
    public String getTriggerMsg() {
        return triggerMsg;
    }

    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }
    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }
    public Long getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(Long handleCode) {
        this.handleCode = handleCode;
    }
    public String getHandleMsg() {
        return handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public static final String ID = "ID";

    public static final String JOB_GROUP = "JOB_GROUP";

    public static final String JOB_ID = "JOB_ID";

    public static final String EXECUTOR_ADDRESS = "EXECUTOR_ADDRESS";

    public static final String EXECUTOR_HANDLER = "EXECUTOR_HANDLER";

    public static final String EXECUTOR_PARAM = "EXECUTOR_PARAM";

    public static final String EXECUTOR_SHARDING_PARAM = "EXECUTOR_SHARDING_PARAM";

    public static final String EXECUTOR_FAIL_RETRY_COUNT = "EXECUTOR_FAIL_RETRY_COUNT";

    public static final String TRIGGER_TIME = "TRIGGER_TIME";

    public static final String TRIGGER_CODE = "TRIGGER_CODE";

    public static final String TRIGGER_MSG = "TRIGGER_MSG";

    public static final String HANDLE_TIME = "HANDLE_TIME";

    public static final String HANDLE_CODE = "HANDLE_CODE";

    public static final String HANDLE_MSG = "HANDLE_MSG";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "QrtzTriggerLog{" +
        "id=" + id +
        ", jobGroup=" + jobGroup +
        ", jobId=" + jobId +
        ", executorAddress=" + executorAddress +
        ", executorHandler=" + executorHandler +
        ", executorParam=" + executorParam +
        ", executorShardingParam=" + executorShardingParam +
        ", executorFailRetryCount=" + executorFailRetryCount +
        ", triggerTime=" + triggerTime +
        ", triggerCode=" + triggerCode +
        ", triggerMsg=" + triggerMsg +
        ", handleTime=" + handleTime +
        ", handleCode=" + handleCode +
        ", handleMsg=" + handleMsg +
        "}";
    }
}
