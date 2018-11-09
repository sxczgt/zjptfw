package cn.tsinghua.zjptfw.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * 此表用于按天生成流水号数据，根据此表可知当天的各流水总量
 * table select sql:
   select * from ZJJS_SERIAL_NUMBER a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-09
 */

@Table(name="ZJJS_SERIAL_NUMBER")
public class SerialNumber extends Model<SerialNumber> {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    @TableId(value = "SN_DATE", type = IdType.INPUT)
    private String snDate;

    /**
     * 交易流水号
     */
    @TableField("SN_FLOW_NO")
    private Long snFlowNo;

    /**
     * 交易订单号
     */
    @TableField("SN_TRADE_NO")
    private Long snTradeNo;

    /**
     * 测试单号
     */
    @TableField("SN_NO")
    private Long snNo;

    public String getSnDate() {
        return snDate;
    }

    public void setSnDate(String snDate) {
        this.snDate = snDate;
    }
    public Long getSnFlowNo() {
        return snFlowNo;
    }

    public void setSnFlowNo(Long snFlowNo) {
        this.snFlowNo = snFlowNo;
    }
    public Long getSnTradeNo() {
        return snTradeNo;
    }

    public void setSnTradeNo(Long snTradeNo) {
        this.snTradeNo = snTradeNo;
    }
    public Long getSnNo() {
        return snNo;
    }

    public void setSnNo(Long snNo) {
        this.snNo = snNo;
    }

    public static final String SN_DATE = "SN_DATE";

    public static final String SN_FLOW_NO = "SN_FLOW_NO";

    public static final String SN_TRADE_NO = "SN_TRADE_NO";

    public static final String SN_NO = "SN_NO";

    @Override
    protected Serializable pkVal() {
        return this.snDate;
    }

    @Override
    public String toString() {
        return "SerialNumber{" +
        "snDate=" + snDate +
        ", snFlowNo=" + snFlowNo +
        ", snTradeNo=" + snTradeNo +
        ", snNo=" + snNo +
        "}";
    }
}
