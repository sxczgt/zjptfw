package com.sxypay.trade;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.Partner;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SignBuilder;
import cn.tsinghua.sftp.util.SpringContextUtil;
import cn.tsinghua.sftp.util.SxyzfUtil;
import com.base.TradeBase;
import com.sxypay.builder.TradeRefundQueryRequestBuilder;
import com.sxypay.builder.TradeRefundRequestBuilder;
import com.sxypay.utils.HttpClientUtil;
import com.utils.ResultInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 首信易服务接口
 * @author zhsh
 */
public class SxypayTradeNew extends TradeBase {
    private static Log log = LogFactory.getLog(SxypayTradeNew.class);

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {
        return null;
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

    /**
     * 首信易退款接口
     * @param tradeNo      订单号
     * @param refundNo     退款号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return
     */
    @Override
    public ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);
        // 退款金额，该金额不能大于订单金额,单位为元，支持两位小数
        String key = "";
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        if (PaymentType.SXYWK.getCode().equals(item.getPaymentType())) {// 外卡
            key = TysfConfig.getSxyzfwkKey();
        } else {
            key = TysfConfig.getSxyzfKey();
        }
        String v_mid = item.getThirdPartySubaccount();// 商户编号
        String v_oid = SxyzfUtil.buildOid(paymentRecord, item.getThirdPartySubaccount()); // 订单编号
        TradeRefundRequestBuilder rb = new TradeRefundRequestBuilder();
        rb.setV_mid(v_mid);
        rb.setV_oid(v_oid);
        rb.setV_refamount(refundAmount);
        rb.setV_refreason(refundReason);
        rb.setV_refoprt(30595);// 操作员编号
        rb.setKey(key);
        try {
            String urlStr = TysfConfig.getSxyzfRefAckUrl();
            SAXReader saxReader = new SAXReader();
            String url = urlStr + rb.buildString();
            Document document = saxReader.read(url);
            Element head = document.getRootElement();
            Element status = head.element("status");
            Element statusdesc = head.element("statusdesc");
            if (!"0".equals(status.getData())) {
                log.warn("status error!!! " + status.getData() + "\n" + statusdesc.getData() + " \n" + urlStr + " \n" + paymentRecord);
                return info("UNKNOWN", statusdesc.getData().toString());
            }
            paymentRecord.setState(PaymentState.REFUND.getCode());
            paymentRecord.setRefundDate(new Date());
            paymentRecord.setRefundDesc(refundReason);
            paymentDao.receivedPayment(paymentRecord);
            return info("OPERATE_SUCCESS", "退款成功");
        } catch (Exception e) {
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    @Override
    public ResultInfo DownloadBillData(String mchId, String billDate, String billType) {
        return null;
    }


    /**
     * 退款查询接口
     * @param tradeNo
     * @return 退款成功-REFUND_SUCCESS
     */
    public ResultInfo TradeOrderRefundQuery(String tradeNo){

        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);

        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        try {
            String key = "";
            if (PaymentType.SXYWK.getCode().equals(item.getPaymentType())) {// 外卡
                key = TysfConfig.getSxyzfwkKey();
            } else {
                key = TysfConfig.getSxyzfKey();
            }
            String v_mid = item.getThirdPartySubaccount();// 商户编号
            String v_oid = SxyzfUtil.buildOid(paymentRecord, item.getThirdPartySubaccount()); // 订单编号
            TradeRefundQueryRequestBuilder rb = new TradeRefundQueryRequestBuilder();
            rb.setV_mid(v_mid);
            rb.setV_oid(v_oid);
            rb.setKey(key);
            String urlStr = TysfConfig.getSxyzfRefAckQueryUrl();
            // SAXReader saxReader = new SAXReader();
            String url = urlStr + rb.buildString();
            String content = HttpClientUtil.getStreamReader(url);
            Document document = DocumentHelper.parseText(content);
            Element docu = document.getRootElement();
            Element head = docu.element("messagehead");
            Element status = head.element("status");
            Element statusdesc = head.element("statusdesc");
            if (!"0".equals(status.getData())) {
                log.warn("status error!!! " + status.getData() + "\n" + statusdesc.getData() + " \n" + urlStr);
                return info("UNKNOWN", statusdesc.getData().toString());
            }
            Element body = docu.element("messagebody");
            Element refund = body.element("refund");
            Element refid = refund.element("refid");// 退款流水号
            Element refstatus = refund.element("refstatus");// 退款处理状态
            log.info("status success!!! " + refid.getData() + "\n" + refstatus.getData() + " \n" + urlStr);
            return info("REFUND_SUCCESS", refid.getData().toString());
        } catch (Exception e) {
            log.error("首信易退款查询" + e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }
}
