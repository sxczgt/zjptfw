package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 部门信息表
 * table select sql:
   select * from ZJJS_PARTNER a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */

@Table(name="ZJJS_PARTNER")
public class Partner extends Model<Partner> {


    /**
     * 部门ID
     */
    @TableId(value = "PARTNER_ID", type = IdType.INPUT)
    private String partnerId;

    /**
     * 部门中文名
     */
    @TableField("PARTNER_NAME")
    private String partnerName;

    /**
     * 部门英文名
     */
    @TableField("PARTNER_ENAME")
    private String partnerEname;

    /**
     * 部门显示名
     */
    @TableField("PARTNER_VNAME")
    private String partnerVname;

    /**
     * 启用标志
     */
    @TableField("ENABLE_FLAG")
    private String enableFlag;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
    public String getPartnerEname() {
        return partnerEname;
    }

    public void setPartnerEname(String partnerEname) {
        this.partnerEname = partnerEname;
    }
    public String getPartnerVname() {
        return partnerVname;
    }

    public void setPartnerVname(String partnerVname) {
        this.partnerVname = partnerVname;
    }
    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.partnerId;
    }

    @Override
    public String toString() {
        return "Partner{" +
        "partnerId=" + partnerId +
        ", partnerName=" + partnerName +
        ", partnerEname=" + partnerEname +
        ", partnerVname=" + partnerVname +
        ", enableFlag=" + enableFlag +
        "}";
    }
}
