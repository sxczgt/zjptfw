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
   select * from ZJJS_PROVINCE a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_PROVINCE")
public class Province extends Model<Province> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SID", type = IdType.INPUT)
    private Long sid;

    @TableField("PROVINCEID")
    private String provinceid;

    @TableField("PROVINCE")
    private String province;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }
    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public static final String SID = "SID";

    public static final String PROVINCEID = "PROVINCEID";

    public static final String PROVINCE = "PROVINCE";

    @Override
    protected Serializable pkVal() {
        return this.sid;
    }

    @Override
    public String toString() {
        return "Province{" +
        "sid=" + sid +
        ", provinceid=" + provinceid +
        ", province=" + province +
        "}";
    }
}
