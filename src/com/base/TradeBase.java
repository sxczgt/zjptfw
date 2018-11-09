package com.base;

import cn.tsinghua.sftp.pojo.PaymentRecord;
import com.utils.ResultInfo;

import java.math.BigDecimal;

/**
 * 支付接口类
 *
 * @author zhsh
 */
public abstract class TradeBase {

    /**
     * PC网页支付(支付场景:trade.pay.page)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayPage(PaymentRecord paymentRecord);

    /**
     * 商户被扫支付（自助机）(支付场景:trade.pay.qrcode)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayQrcode(PaymentRecord paymentRecord);

    /**
     * 商户主扫支付（收银台）(支付场景:trade.pay.micropay)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayMicropay(PaymentRecord paymentRecord);

    /**
     * 手机APP支付(支付场景:trade.pay.app)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayApp(PaymentRecord paymentRecord);

    /**
     * 手机网页支付(支付场景:trade.pay.wap)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayWap(PaymentRecord paymentRecord);

    /**
     * PC网页外币支付(支付场景:trade.pay.page.fc)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayPageFc(PaymentRecord paymentRecord);

    /**
     * 手机网页外币支付(支付场景:trade.pay.wap.fc)
     *
     * @param paymentRecord
     * @return
     */
    public abstract ResultInfo TradePayWapFc(PaymentRecord paymentRecord);

    /**
     * 订单查询(trade.order.query)
     *
     * @param tradeNo 订单号
     * @return
     */
    public abstract ResultInfo TradeOrderQuery(String tradeNo);

    /**
     * 订单撤销(trade.order.cancel)
     *
     * @param tradeNo 订单号
     * @return
     */
    public abstract ResultInfo TradeOrderCancel(String tradeNo);

    /**
     * 订单退款(trade.order.refund)
     *
     * @param tradeNo      订单号
     * @param refundNo     退款号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return
     */
    public abstract ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason);

    /**
     * 下载对账单(download.bill.data)
     *
     * @param mchId    商户号
     * @param billDate 对账日期
     * @param billType 账单类型：ALL，返回当日所有订单信息，默认值、SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单
     * @return
     */
    public abstract ResultInfo DownloadBillData(String mchId, String billDate, String billType);


    protected static ResultInfo info(String code, String msg) {
        return ResultInfo.info(code, msg);
    }

}
