package com.ccbpay.trade;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SpringContextUtil;
import com.base.TradeBase;
import com.unipay.builder.B2BPayRequestBuilder;
import com.unipay.builder.TradeQueryRequestBuilder;
import com.unipay.config.UnipayConfig;
import com.unipay.model.QueryOrder;
import com.unipay.model.QueryOrderResult;
import com.unipay.utils.HttpClientUtil;
import com.utils.ResultInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

/**
 * 建行服务接口
 * @author zhsh
 */
public class CcbpayTrade extends TradeBase {

    private static Log log = LogFactory.getLog(CcbpayTrade.class);

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    /**
     * PC网站支付
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {
        String paymentType = paymentRecord.getPaymentType();
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

        String posid = UnipayConfig.getPosId(paymentType); // 柜台号
        B2BPayRequestBuilder builder = new B2BPayRequestBuilder();
        builder.setPosid(posid);
        builder.setOrderid(paymentRecord.getPartner() + paymentRecord.getOutTradeNo());
        builder.setPayment(String.valueOf(paymentRecord.getTotalFee()));
        builder.setRemark1(paymentRecord.getItem());
        builder.setRemark2("");
        builder.setTimeout(t_timeout);

        // 生成请求参数
        String url = builder.createRequestUrl();

        return info("OPERATE_SUCCESS", url);

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

    /**
     * 订单查询
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderQuery(String tradeNo) {
        PaymentRecord paymentRecord = this.paymentDao.getPaymentRecord(tradeNo);
        // 创建查询请求builder，设置请求参数
        String paymentType = paymentRecord.getPaymentType();
        String posId = UnipayConfig.getPosId(paymentType); // 柜台号
        TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder();
        builder.setPosid(posId);
        builder.setOrderid(tradeNo);
        builder.setType("0"); // 流水类型：0支付流水， 1退款流水 ———— 经测试这个参数必填但不影响查询到退款流水
        builder.setKind("0"); // 流水状态：0 未结算流水，1 已结算流水 ———— 经测试这个参数必填但不影响查询到已结算流水
        builder.setStatus("3"); // 交易状态：0失败，1成功，2不确定，3全部（已结算流水查询不支持全部）
        builder.setSel_type("3"); // 查询方式：1页面形式，2文件返回形式 (提供TXT和XML格式文件的下载)，3 XML页面形式
        builder.setPage("1"); // 查询页码

        // 生成请求参数
        String url = builder.createRequestUrl();
        //System.out.println("银联申请订单请求地址：" + url);
        String xml = HttpClientUtil.httpPost(url, new HashMap<>());
        //System.out.println("银联申请订单返回内容：\n" + xml);

        // 解析查询结果
        QueryOrderResult result = QueryOrderResult.xmlToResult(xml);
        if (result == null || result.getTotal() == 0 || result.getOrderList().size() == 0) {
            return info("UNKNOWN", "无数据");
        }

        String returnCode = result.getReturnCode();
        String returnMsg = result.getReturnMsg();

        // <?xml version = "1.0" encoding="UTF-8" ?>
        // <DOCUMENT>
        // <RETURN_CODE>0130Z110C054</RETURN_CODE>
        // <RETURN_MSG>密码不符，请重新输入[您输入的密码是：]</RETURN_MSG>
        // </DOCUMENT>
        if (!"000000".equals(returnCode)) {
            return info("UNKNOWN", returnCode + " - " + returnMsg);
        }

        QueryOrder queryOrder = result.getOrderList().get(0);
        if (queryOrder.checkSign()) {
            return info("UNKNOWN", "签名验证失败");
        }
        String orderId = queryOrder.getOrderId(); // 订单号
        String orderDate = queryOrder.getOrderDate(); // 支付/退款交易时间
        // String accDate = queryOrder.getAccDate(); // 记账日期
        BigDecimal amount = queryOrder.getAmount(); // 支付金额
        String statusCode = queryOrder.getStatusCode(); // 支付/退款状态码
        String status = queryOrder.getStatus(); // 支付/退款状态
        BigDecimal refund = queryOrder.getRefund(); // 退款金额 ，这里显示已退款金额可用于部分退款的判断

        if (!orderId.equals(paymentRecord.getPartner() + paymentRecord.getOutTradeNo())) {
            return info("UNKNOWN", "订单号不一致");
        }
        if (amount.doubleValue() != paymentRecord.getTotalFee().doubleValue()) {
            return info("UNKNOWN", "订单金额不一致");
        }

        if ("0".equals(statusCode)) {
            // 支付失败
            paymentRecord.setState(PaymentState.FAIL.getCode());
            paymentDao.receivedPayment(paymentRecord);
            return info("UNKNOWN", "订单支付失败");

        } else if ("1".equals(statusCode)) {
            // 支付成功
            Date gmtPayment = new Date();
            try {
                gmtPayment = DateUtils.parseDate(orderDate, "yyyyMMddHHmmss");
            } catch (ParseException e) {
                log.error("日期格式转换失败：" + orderDate + "！" + e.getMessage(), e);
            }
            if (gmtPayment == null) {
                gmtPayment = new Date();
            }
            paymentRecord.setState(PaymentState.SUCCESS.getCode());
            paymentRecord.setGmtPayment(gmtPayment); // 交易付款时间
            paymentDao.receivedPayment(paymentRecord);
            return info("TRADE_SUCCESS", "订单支付成功");

        } else if ("2".equals(statusCode) || "5".equals(statusCode)) {
            // 不确定交易，支付不确定
            return info("UNKNOWN", statusCode + " - " + status);

        } else if ("3".equals(statusCode) || "4".equals(statusCode)) {
            // 3 - 已部分退款，4 - 已全部退款
            Date refundDate = null;
            try {
                refundDate = DateUtils.parseDate(orderDate, "yyyyMMddHHmmss");
            } catch (ParseException e) {
                log.error("日期格式转换失败：" + orderDate + "！" + e.getMessage(), e);
            }
            if (refundDate == null) {
                refundDate = new Date();
            }
            paymentRecord.setState(PaymentState.REFUND.getCode());
            paymentRecord.setRefundDate(refundDate);
            if ("3".equals(statusCode)) {
                paymentRecord.setRefundDesc(paymentRecord.getRefundDesc() + "，已部分退款" + String.valueOf(refund.doubleValue()) + "元");
            }
            paymentDao.receivedPayment(paymentRecord);
            return info("UNKNOWN", "已退款");

        } else {
            return info("UNKNOWN", statusCode + " - " + status);
        }
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

    /**
     * 建行，银联下载对账单
     * @param paymentType 支付类型
     * @param orderDate 账单日期
     * @param billType 账单类型：0支付流水， 1退款流水
     * @param billPage 查询页码
     * @return
     */
    public ResultInfo DownloadBillData(String paymentType, String billDate, String billType,String billPage){
        log.debug("下载银联对账单：DownloadBill('" + paymentType + "','" + billDate + "','" + billType + "','" + billPage + "')");
        // 创建查询请求builder，设置请求参数
        String posId = UnipayConfig.getPosId(paymentType); // 柜台号
        TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder();
        builder.setPosid(posId); // 柜台号
        builder.setOrderdate(billDate); // 定单日期
        builder.setType(billType); // 流水类型：0支付流水， 1退款流水
        builder.setKind("1"); // 流水状态：0 未结算流水（当天查询），1 已结算流水（隔天查询）
        builder.setStatus("1"); // 交易状态：0失败，1成功，2不确定，3全部（已结算流水查询不支持全部）
        builder.setSel_type("3"); // 查询方式：1页面形式， 2文件返回形式 (提供TXT和XML格式文件的下载)，3 XML页面形式
        builder.setPage(billPage); // 要查询的页码

        // 生成请求参数
        String url = builder.createRequestUrl();
        // log.debug("银联对账单下载地址：" + url);

        String xml = HttpClientUtil.httpPost(url, new HashMap<>());
        if (xml.startsWith("<html>")) {
            log.debug("银联对账单下载失败，返回内容：\n" + xml);
            return info("UNKNOWN", "对账单下载失败");
        }
        return info("OPERATE_SUCCESS",xml);
    }
}
