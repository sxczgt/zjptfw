package com.unipay.trade;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.SpringContextUtil;
import com.base.TradeBase;
import com.unipay.builder.TradePayRequestBuilder;
import com.unipay.config.UnipayConfig;
import com.utils.ResultInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.Date;

public class UnipayTradeNew extends TradeBase {

    private static Log log = LogFactory.getLog(UnipayTradeNew.class);

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    /**
     * PC网站支付
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {
        // 支付超时时间
        String t_timeout = paymentRecord.getTimeout();
        if (!StringUtils.isEmpty(t_timeout)) {
            try {
                int minutes = 0;
                if (t_timeout.toLowerCase().endsWith("d")) {
                    String timeout = t_timeout.substring(0, t_timeout.length() - 2);
                    minutes = Integer.valueOf(timeout) * 60 * 12;
                }
                if (t_timeout.toLowerCase().endsWith("h")) {
                    String timeout = t_timeout.substring(0, t_timeout.length() - 2);
                    minutes = Integer.valueOf(timeout) * 60;
                }
                if (t_timeout.toLowerCase().endsWith("m")) {
                    String timeout = t_timeout.substring(0, t_timeout.length() - 2);
                    minutes = Integer.valueOf(timeout);
                }
                minutes += 15; // 建行要求增加15分钟的误差
                t_timeout = DateUtils.formatDate(DateUtils.addMinutes(new Date(), minutes), "yyyyMMddHHmmss");
            } catch (Exception e) {
                log.warn("解析支付超时参数[" + t_timeout + "]失败：" + e.getMessage(), e);
                t_timeout = "";
            }
        }
        paymentRecord.setState(PaymentState.SUBMITTED.getCode());// 流水状态
        paymentDao.receivedPayment(paymentRecord);

        String posid = UnipayConfig.getPosId(paymentRecord.getPaymentType()); // 柜台号
        String pubkey = UnipayConfig.getPublicKey30(paymentRecord.getPaymentType()); // 公钥后30位
        TradePayRequestBuilder builder = new TradePayRequestBuilder();
        builder.setPosid(posid);
        builder.setPub(pubkey);
        builder.setOrderid(paymentRecord.getPartner() + paymentRecord.getOutTradeNo());
        builder.setPayment(String.valueOf(paymentRecord.getTotalFee()));
        builder.setReginfo(paymentRecord.getTradeName());
        builder.setProinfo(paymentRecord.getSummary());
        builder.setRemark1(paymentRecord.getItem());
        builder.setRemark2("");
        builder.setTimeout(t_timeout);
        if (paymentRecord.getPaymentType().endsWith(PaymentType.JH.getCode())) {
            builder.setIssinscode("CCB"); // 建行
        }
        if (paymentRecord.getPaymentType().endsWith(PaymentType.YL.getCode())) {
            builder.setIssinscode("UnionPay"); // 银联
        }

        // 生成请求参数
        String url = builder.createRequestUrl();

        return info("OPERATE_SUCCESS",url);
    }

    @Override
    public ResultInfo TradePayQrcode(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayMicropay(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayApp(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayWap(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayPageFc(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayWapFc(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradeOrderQuery(String tradeNo) {
        return null;
    }

    @Override
    public ResultInfo TradeOrderCancel(String tradeNo) {
        return null;
    }

    @Override
    public ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        return null;
    }

    @Override
    public ResultInfo DownloadBillData(String mchId, String billDate, String billType) {
        return null;
    }
}
