package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 数据字典表
 * table select sql:
   select * from ZJJS_MO_DICTIONARY a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */

@Table(name="ZJJS_MO_DICTIONARY")
public class MoDictionary extends Model<MoDictionary> {


    /**
     * 顺序号（主键）
     */
    @TableId(value = "XH", type = IdType.INPUT)
    private Long xh;

    /**
     * 字典项值(全称
     */
    @TableField("DFVALUE")
    private String dfvalue;

    /**
     * 字典
     */
    @TableField("DIC")
    private String dic;

    /**
     * 字典项
     */
    @TableField("DKEY")
    private String dkey;

    /**
     * 字典项对应值
     */
    @TableField("DVALUE")
    private String dvalue;

    /**
     * 备注
     */
    @TableField("MEMO")
    private String memo;

    public Long getXh() {
        return xh;
    }

    public void setXh(Long xh) {
        this.xh = xh;
    }
    public String getDfvalue() {
        return dfvalue;
    }

    public void setDfvalue(String dfvalue) {
        this.dfvalue = dfvalue;
    }
    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }
    public String getDkey() {
        return dkey;
    }

    public void setDkey(String dkey) {
        this.dkey = dkey;
    }
    public String getDvalue() {
        return dvalue;
    }

    public void setDvalue(String dvalue) {
        this.dvalue = dvalue;
    }
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    protected Serializable pkVal() {
        return this.xh;
    }

    @Override
    public String toString() {
        return "MoDictionary{" +
        "xh=" + xh +
        ", dfvalue=" + dfvalue +
        ", dic=" + dic +
        ", dkey=" + dkey +
        ", dvalue=" + dvalue +
        ", memo=" + memo +
        "}";
    }
}
