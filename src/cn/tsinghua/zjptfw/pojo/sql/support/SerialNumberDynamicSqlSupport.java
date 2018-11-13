package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * 此表用于按天生成流水号数据，根据此表可知当天的各流水总量
 * table select sql:
   select * from ZJJS_SERIAL_NUMBER a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class SerialNumberDynamicSqlSupport {

     public static final SerialNumberDynamic table = new SerialNumberDynamic();
    /**
     * 日期
     */
    public static final SqlColumn<String> snDate = table.snDate;
    /**
     * 交易流水号
     */
    public static final SqlColumn<Long> snFlowNo = table.snFlowNo;
    /**
     * 交易订单号
     */
    public static final SqlColumn<Long> snTradeNo = table.snTradeNo;
    /**
     * 测试单号
     */
    public static final SqlColumn<Long> snNo = table.snNo;
    /**
     * 支付项目号
     */
    public static final SqlColumn<Long> snPaymentitemNo = table.snPaymentitemNo;


    public static final class SerialNumberDynamic extends SqlTable {


        public final SqlColumn<String> snDate = column("SN_DATE");


        public final SqlColumn<Long> snFlowNo = column("SN_FLOW_NO");


        public final SqlColumn<Long> snTradeNo = column("SN_TRADE_NO");


        public final SqlColumn<Long> snNo = column("SN_NO");


        public final SqlColumn<Long> snPaymentitemNo = column("SN_PAYMENTITEM_NO");

         public SerialNumberDynamic() {
            super("ZJJS_SERIAL_NUMBER");
        }
    }
}
