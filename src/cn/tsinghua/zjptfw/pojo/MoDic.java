package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 字典列表
 * table select sql:
   select * from ZJJS_MO_DIC a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */

@Table(name="ZJJS_MO_DIC")
public class MoDic extends Model<MoDic> {


    /**
     * 字典标识
     */
    @TableId(value = "DIC", type = IdType.INPUT)
    private String dic;

    /**
     * 字典名称
     */
    @TableField("DICNAME")
    private String dicname;

    /**
     * 所在系统名称
     */
    @TableField("SYSTEM_NAME")
    private String systemName;

    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }
    public String getDicname() {
        return dicname;
    }

    public void setDicname(String dicname) {
        this.dicname = dicname;
    }
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Override
    protected Serializable pkVal() {
        return this.dic;
    }

    @Override
    public String toString() {
        return "MoDic{" +
        "dic=" + dic +
        ", dicname=" + dicname +
        ", systemName=" + systemName +
        "}";
    }
}
