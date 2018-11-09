package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 
 * table select sql:
   select * from ZJJS_QRTZ_TRIGGER_GROUP a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_QRTZ_TRIGGER_GROUP")
public class QrtzTriggerGroup extends Model<QrtzTriggerGroup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    @TableField("APP_NAME")
    private String appName;

    @TableField("TITLE")
    private Long title;

    @TableField("ORDERXH")
    private Long orderxh;

    @TableField("ADDRESS_TYPE")
    private Integer addressType;

    @TableField("ADDRESS_LIST")
    private String addressList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    public Long getTitle() {
        return title;
    }

    public void setTitle(Long title) {
        this.title = title;
    }
    public Long getOrderxh() {
        return orderxh;
    }

    public void setOrderxh(Long orderxh) {
        this.orderxh = orderxh;
    }
    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }
    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public static final String ID = "ID";

    public static final String APP_NAME = "APP_NAME";

    public static final String TITLE = "TITLE";

    public static final String ORDERXH = "ORDERXH";

    public static final String ADDRESS_TYPE = "ADDRESS_TYPE";

    public static final String ADDRESS_LIST = "ADDRESS_LIST";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "QrtzTriggerGroup{" +
        "id=" + id +
        ", appName=" + appName +
        ", title=" + title +
        ", orderxh=" + orderxh +
        ", addressType=" + addressType +
        ", addressList=" + addressList +
        "}";
    }
}
