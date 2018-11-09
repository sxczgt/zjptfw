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
   select * from ZJJS_QUARTZ_TASK a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QUARTZ_TASK")
public class QuartzTask extends Model<QuartzTask> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "TASK_ID", type = IdType.INPUT)
    private Double taskId;

    @TableField("TASK_NAME")
    private String taskName;

    @TableField("TASK_TYPE")
    private String taskType;

    @TableField("TASK_START")
    private LocalDateTime taskStart;

    @TableField("TASK_END")
    private LocalDateTime taskEnd;

    /**
     * 时间频率表达式
     */
    @TableField("TASK_FREQUENCY")
    private String taskFrequency;

    @TableField("TASK_FLAG")
    private String taskFlag;

    public Double getTaskId() {
        return taskId;
    }

    public void setTaskId(Double taskId) {
        this.taskId = taskId;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    public LocalDateTime getTaskStart() {
        return taskStart;
    }

    public void setTaskStart(LocalDateTime taskStart) {
        this.taskStart = taskStart;
    }
    public LocalDateTime getTaskEnd() {
        return taskEnd;
    }

    public void setTaskEnd(LocalDateTime taskEnd) {
        this.taskEnd = taskEnd;
    }
    public String getTaskFrequency() {
        return taskFrequency;
    }

    public void setTaskFrequency(String taskFrequency) {
        this.taskFrequency = taskFrequency;
    }
    public String getTaskFlag() {
        return taskFlag;
    }

    public void setTaskFlag(String taskFlag) {
        this.taskFlag = taskFlag;
    }

    public static final String TASK_ID = "TASK_ID";

    public static final String TASK_NAME = "TASK_NAME";

    public static final String TASK_TYPE = "TASK_TYPE";

    public static final String TASK_START = "TASK_START";

    public static final String TASK_END = "TASK_END";

    public static final String TASK_FREQUENCY = "TASK_FREQUENCY";

    public static final String TASK_FLAG = "TASK_FLAG";

    @Override
    protected Serializable pkVal() {
        return this.taskId;
    }

    @Override
    public String toString() {
        return "QuartzTask{" +
        "taskId=" + taskId +
        ", taskName=" + taskName +
        ", taskType=" + taskType +
        ", taskStart=" + taskStart +
        ", taskEnd=" + taskEnd +
        ", taskFrequency=" + taskFrequency +
        ", taskFlag=" + taskFlag +
        "}";
    }
}
