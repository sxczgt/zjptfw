package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 字典维护权限
 * table select sql:
   select * from ZJJS_MO_GRANT a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_MO_GRANT")
public class MoGrant extends Model<MoGrant> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private Long id;

    /**
     * mo_dic表中的dic字典代码
     */
    @TableField("DIC_CODE")
    private String dicCode;

    /**
     * 角色表ID
     */
    @TableField("ROLE_ID")
    private String roleId;

    /**
     * auth_dept中的dwbh单位编号
     */
    @TableField("DWBH")
    private String dwbh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getDicCode() {
        return dicCode;
    }

    public void setDicCode(String dicCode) {
        this.dicCode = dicCode;
    }
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getDwbh() {
        return dwbh;
    }

    public void setDwbh(String dwbh) {
        this.dwbh = dwbh;
    }

    public static final String ID = "ID";

    public static final String DIC_CODE = "DIC_CODE";

    public static final String ROLE_ID = "ROLE_ID";

    public static final String DWBH = "DWBH";

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "MoGrant{" +
        "id=" + id +
        ", dicCode=" + dicCode +
        ", roleId=" + roleId +
        ", dwbh=" + dwbh +
        "}";
    }
}
