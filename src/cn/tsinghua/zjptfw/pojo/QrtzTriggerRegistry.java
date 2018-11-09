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
   select * from ZJJS_QRTZ_TRIGGER_REGISTRY a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QRTZ_TRIGGER_REGISTRY")
public class QrtzTriggerRegistry extends Model<QrtzTriggerRegistry> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("REGISTRY_GROUP")
    private String registryGroup;

    @TableField("REGISTRY_KEY")
    private String registryKey;

    @TableField("REGISTRY_VALUE")
    private String registryValue;

    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }
    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }
    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public static final String ID = "ID";

    public static final String REGISTRY_GROUP = "REGISTRY_GROUP";

    public static final String REGISTRY_KEY = "REGISTRY_KEY";

    public static final String REGISTRY_VALUE = "REGISTRY_VALUE";

    public static final String UPDATE_TIME = "UPDATE_TIME";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "QrtzTriggerRegistry{" +
        "id=" + id +
        ", registryGroup=" + registryGroup +
        ", registryKey=" + registryKey +
        ", registryValue=" + registryValue +
        ", updateTime=" + updateTime +
        "}";
    }
}
