package com.alipay.trade;


import cn.tsinghua.sftp.config.MoneyType;
import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SpringContextUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.alipay.config.AlipayWebConfig;
import com.base.TradeBase;
import com.utils.ResultInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 支付宝服务（第三方）
 *
 * @author zhsh
 */
public class AlipayTradeNew extends TradeBase {

    private static Log log = LogFactory.getLog(AlipayTradeNew.class);

    /**
     * 支付宝服务
     */
    private AlipayClient alipayClient;


    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    /**
     * PC网页支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {

        if (StringUtils.isBlank(paymentRecord.getOutTradeNo())){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        if (paymentRecord.getTotalFee() == null || paymentRecord.getTotalFee().compareTo(BigDecimal.ZERO) == -1){
            return info("INVALID_PARAMETER","付款金额不允许为空/付款金额不允许小于0");
        }
        if (StringUtils.isBlank(paymentRecord.getTradeName())){
            return info("INVALID_PARAMETER","订单标题不允许为空");
        }

        String t_timeout = paymentRecord.getTimeout();
        // 支付超时，线下扫码交易定义为5分钟
        if (StringUtils.isEmpty(t_timeout))
            t_timeout = "5m";
        if (!t_timeout.toLowerCase().endsWith("m"))
            t_timeout = t_timeout + "m";
        paymentRecord.setState(PaymentState.SUBMITTED.getCode());// 流水状态
        paymentDao.receivedPayment(paymentRecord);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();// 创建API对应的request
        alipayRequest.setReturnUrl(AlipayWebConfig.RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);// 在公共参数中设置回跳和通知地址
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("out_trade_no", paymentRecord.getPartner() + paymentRecord.getOutTradeNo());
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");// 销售产品码，与支付宝签约的产品码名称。 注：目前仅支持FAST_INSTANT_TRADE_PAY
        params.put("total_amount", paymentRecord.getTotalFee());
        params.put("subject", paymentRecord.getTradeName());
        params.put("body", paymentRecord.getSummary());
        Map<String, Object> extend_params = new HashMap<String, Object>(0);
        extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
        params.put("extend_params", extend_params);
        params.put("timeout_express", t_timeout);
        alipayRequest.setBizContent(JsonUtil.getJson(params));// 填充业务参数
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest); // 调用SDK生成表单
            if (response.isSuccess()) {
                return info("WAIT_BUYER_PAY", response.getBody());
            } else {
                return info("SYSTEM_ERROR", response.getSubMsg());
            }

        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            return info("SYSTEM_ERROR", "系统错误");
        }
    }

    /**
     * 商户被扫支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayQrcode(PaymentRecord paymentRecord) {

        if (StringUtils.isBlank(paymentRecord.getOutTradeNo())){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        if (paymentRecord.getTotalFee() == null || paymentRecord.getTotalFee().compareTo(BigDecimal.ZERO) == -1){
            return info("INVALID_PARAMETER","付款金额不允许为空/付款金额不允许小于0");
        }
        if (StringUtils.isBlank(paymentRecord.getTradeName())){
            return info("INVALID_PARAMETER","订单标题不允许为空");
        }

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        // 发起订单
        String outTradeNo = paymentRecord.getPartner() + paymentRecord.getOutTradeNo(); // 交易流水号=合作方号+交易流水号
        String t_timeout = paymentRecord.getTimeout();
        // 支付超时，线下扫码交易定义为5分钟
        if (StringUtils.isEmpty(t_timeout))
            t_timeout = "5m";

        String timeout_express = t_timeout.toLowerCase().endsWith("m") ? t_timeout : t_timeout + "m";

        // 获取返回信息
        String error = "";
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        Map<String, Object> params = new HashMap<String, Object>(0);
        params.put("out_trade_no", outTradeNo);
        params.put("seller_id", "");// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号),如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        params.put("total_amount", paymentRecord.getTotalFee());
        params.put("subject", paymentRecord.getTradeName());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(0);
        Map<String, Object> goods = new HashMap<String, Object>(0);
        goods.put("goods_id", paymentRecord.getItem());
        goods.put("goods_name", (paymentRecord.getSummary() == null || "".equals(paymentRecord.getSummary())) ? paymentRecord.getTradeName() : paymentRecord.getSummary());
        goods.put("quantity", 1);
        goods.put("price", paymentRecord.getTotalFee());
        goods.put("body", paymentRecord.getSummary());
        list.add(goods);
        params.put("goods_detail", list);
        params.put("body", paymentRecord.getSummary());
        params.put("operator_id", paymentRecord.getItem());// 对账时用到
        params.put("store_id", "0");// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        Map<String, Object> extend_params = new HashMap<String, Object>(0);
        extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
        params.put("extend_params", extend_params);
        params.put("timeout_express", timeout_express);
        String bizcontent = JsonUtil.getJson(params);
        System.out.println("bizcontent>>" + bizcontent);
        request.setBizContent(bizcontent);
        request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("支付宝预下单成功: )");
                paymentRecord.setState(PaymentState.SUBMITTED.getCode());
                paymentDao.receivedPayment(paymentRecord);
                if (log.isDebugEnabled()) {
                    DumpResponse(response);
                }
                String qrCode = response.getQrCode();
                return info("WAIT_BUYER_PAY", qrCode);
            } else {
                error = "支付宝预下单失败";
                if (response != null) {
                    error += String.format("，(%s) %s", response.getCode(), response.getMsg());
                    if (StringUtils.isNotEmpty(response.getSubCode())) {
                        error += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
                    }
                }
                log.warn(error);
                return info("SYSTEM_ERROR", error);
            }
        } catch (AlipayApiException e) {
            error = "支付宝预下单失败";
            error += String.format("，(%s) %s", e.getErrCode(), e.getErrMsg());
            log.error(error, e);
            return info("SYSTEM_ERROR", error);
        }

    }

    /**
     * 商户主扫支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayMicropay(PaymentRecord paymentRecord) {
        if (StringUtils.isBlank(paymentRecord.getOutTradeNo())){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        if (paymentRecord.getTotalFee() == null || paymentRecord.getTotalFee().compareTo(BigDecimal.ZERO) == -1){
            return info("INVALID_PARAMETER","付款金额不允许为空/付款金额不允许小于0");
        }
        if (StringUtils.isBlank(paymentRecord.getTradeName())){
            return info("INVALID_PARAMETER","订单标题不允许为空");
        }

        DecimalFormat df = new DecimalFormat("###############0");
        // 支付超时，线下扫码交易定义为5分钟
        String t_timeout = paymentRecord.getTimeout();
        if (StringUtils.isEmpty(t_timeout))
            t_timeout = "5m";
        if (!t_timeout.toLowerCase().endsWith("m"))
            t_timeout = t_timeout + "m";
        // 创建条码支付请求builder，设置请求参数
        String outTradeNo = paymentRecord.getPartner() + paymentRecord.getOutTradeNo(); // 交易流水号=合作方号+交易流水号
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        Map<String, Object> params = new HashMap<String, Object>(0);
        params.put("out_trade_no", outTradeNo);
        params.put("scene", "bar_code");
        params.put("auth_code", paymentRecord.getAuthCode());
        params.put("product_code", "FACE_TO_FACE_PAYMENT");
        params.put("subject", paymentRecord.getTradeName());
        // params.put("buyer_id", "");
        // params.put("seller_id", "");// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号),如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        params.put("total_amount", paymentRecord.getTotalFee());
        // params.put("discountable_amount", 0);
        params.put("body", paymentRecord.getSummary());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(0);
        Map<String, Object> goods = new HashMap<String, Object>();
        goods.put("goods_id", paymentRecord.getItem());
        goods.put("goods_name", (paymentRecord.getSummary() == null || "".equals(paymentRecord.getSummary())) ? paymentRecord.getTradeName() : paymentRecord.getSummary());
        goods.put("quantity", 1);
        goods.put("price", Long.parseLong(df.format(paymentRecord.getTotalFee().doubleValue() * 100)));
        goods.put("body", paymentRecord.getSummary());
        // goods.put("show_url", "");
        list.add(goods);
        params.put("goods_detail", list);
        params.put("operator_id", paymentRecord.getItem());
        params.put("store_id", "0");// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        // params.put("terminal_id", "");
        Map<String, Object> extend_params = new HashMap<String, Object>(0);
        extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
        params.put("extend_params", extend_params);
        params.put("timeout_express", t_timeout);
        request.setBizContent(JsonUtil.getJson(params));
        request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);
        try {
            log.debug("订单[" + outTradeNo + "]调起支付宝：" + request.getBizContent());
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradePayResponse response = alipayClient.execute(request);
            if (response.isSuccess() && "10000".equals(response.getCode())) {
                paymentRecord.setState(PaymentState.SUCCESS.getCode());
                Date gmtPayment = response.getGmtPayment(); // 支付时间
                if (gmtPayment == null) {
                    gmtPayment = new Date();
                }
                paymentRecord.setGmtPayment(gmtPayment);
                paymentDao.receivedPayment(paymentRecord);
                log.debug("订单[" + outTradeNo + "]支付成功" + String.format("，(%s) %s", response.getCode(), response.getMsg()));
                return info("TRADE_SUCCESS","支付成功");
            } else if (response.isSuccess() && "10003".equals(response.getCode())) {
                log.debug("订单[" + outTradeNo + "]支付处理中" + String.format("，(%s) %s", response.getCode(), response.getMsg()));
                return info("WAIT_BUYER_PAY","等待用户付款");
            } else {
                String payInfo = "支付失败";
                if (response != null) {
                    payInfo += String.format("，(%s) %s", response.getCode(), response.getMsg());
                    if (StringUtils.isNotEmpty(response.getSubCode())) {
                        payInfo += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
                    }
                }
                log.debug("订单[" + outTradeNo + "]" + payInfo);
                return info("SYSTEM_ERROR", payInfo);
            }
        } catch (AlipayApiException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getErrCode() + e.getErrMsg());
        }
    }

    @Override
    public ResultInfo TradePayApp(PaymentRecord paymentRecord) {
        return null;
    }

    /**
     * 手机网站支付
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayWap(PaymentRecord paymentRecord) {
        if (StringUtils.isBlank(paymentRecord.getOutTradeNo())){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        if (paymentRecord.getTotalFee() == null || paymentRecord.getTotalFee().compareTo(BigDecimal.ZERO) == -1){
            return info("INVALID_PARAMETER","付款金额不允许为空/付款金额不允许小于0");
        }
        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();
        // 封装请求支付信息
        String outTradeNo = paymentRecord.getPartner() + paymentRecord.getOutTradeNo();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);// 商户订单号，商户网站订单系统中唯一订单号，必填
        model.setSubject(paymentRecord.getTradeName());// 订单名称，必填
        model.setTotalAmount(paymentRecord.getTotalFee().toPlainString()); // 付款金额，必填
        model.setBody(paymentRecord.getSummary());// 商品描述，可空
        model.setTimeoutExpress(paymentRecord.getTimeout());// 超时时间 可空，该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点。注：若为空，则默认为15d。
        model.setProductCode("QUICK_WAP_PAY");// 销售产品码 必填
        alipay_request.setBizModel(model);
        alipay_request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);// 设置异步通知地址
        alipay_request.setReturnUrl(AlipayWebConfig.RETURN_URL);// 设置同步地址

        //更新状态
        paymentRecord.setState(PaymentState.SUBMITTED.getCode());// 流水状态
        paymentDao.receivedPayment(paymentRecord);

        // form表单生产
        try {
            // 调用SDK生成表单
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipay_request);
            if (response.isSuccess()) {
                return info("WAIT_BUYER_PAY", response.getBody());
            } else {
                return info("SYSTEM_ERROR", response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付申请异常错误，请稍候重试！", e);
            return info("SYSTEM_ERROR", "系统错误");
        }
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
     * 查询订单
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderQuery(String tradeNo) {
        if (StringUtils.isBlank(tradeNo)){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" + "\"out_trade_no\":\"" + tradeNo + "\"," + "\"trade_no\":\"\"" + "  }");
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                BigDecimal total_fee = BigDecimal.valueOf(Double.parseDouble(response.getTotalAmount()));
                Date gmt_payment = response.getSendPayDate();
                String trade_status = response.getTradeStatus();
                if (gmt_payment == null) {
                    // log.error("缴费时间不能为空!!! " + total_fee + " " + paymentRecord);
                    // return JsonUtil.getJson(-5, "缴费时间不能为空");
                    log.warn("支付宝缴费时间返回空!!! " + " " + paymentRecord + "\n" + response.getBody());
                }
                // 支付成功，更新订单状态
                if (paymentRecord.getTotalFee().doubleValue() != total_fee.doubleValue()) {
                    log.error("金额不一致!!! " + total_fee + "  " + paymentRecord);
                    return info("SYSTEM_ERROR", "金额不一致");
                }
                // (trade_status)交易状态：
                // WAIT_BUYER_PAY（交易创建，等待买家付款）
                // TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                // TRADE_SUCCESS（交易支付成功）
                // TRADE_FINISHED（交易结束，不可退款）
                if (!"TRADE_SUCCESS".equalsIgnoreCase(trade_status)) {// 如果支付不成功就返回
                    try {
                        if ("TRADE_CLOSED".equals(trade_status) && paymentRecord.getState().equals(PaymentState.REFUND.getCode())) {
                            return info("TRADE_REFUND", "订单已退款");
                        } else if ("TRADE_CLOSED".equals(trade_status)) {
                            paymentRecord.setState(PaymentState.CLOSED.getCode());
                            paymentDao.receivedPayment(paymentRecord);
                            return info("TRADE_CLOSED", "订单已关闭");
                        } else if ("TRADE_CANCEL".equals(trade_status)) { // 哪儿来的???
                            paymentRecord.setState(PaymentState.CANCEL.getCode());
                            paymentDao.receivedPayment(paymentRecord);
                            return info("TRADE_CANCEL", "订单已撤销");
                        } else if ("WAIT_BUYER_PAY".equals(trade_status)) {
                            paymentRecord.setState(PaymentState.SUBMITTED.getCode());
                            paymentDao.receivedPayment(paymentRecord);
                            return info("WAIT_BUYER_PAY", "等待用户付款");
                        } else if ("TRADE_FINISHED".equals(trade_status)) {
                            return info("TRADE_FINISHED", "订单已完结");
                        }
                    } catch (Exception e2) {
                        log.debug("交易状态码[" + trade_status + "]转换失败！" + e2.getMessage(), e2);
                    }
                    return info("SYSTEM_ERROR", "未支付成功(" + trade_status + ")");
                }
                paymentRecord.setMoneyType(MoneyType.RMB.getCode());
                paymentRecord.setState(PaymentState.SUCCESS.getCode());
                if (null == gmt_payment) {
                    gmt_payment = new Date();
                }
                paymentRecord.setGmtPayment(gmt_payment);
                int updateRownum = paymentDao.receivedPayment(paymentRecord);
                if (updateRownum == 1) {
                    log.info("更新支付记录成功");
                    return info("TRADE_SUCCESS", "支付成功");
                } else {
                    log.error("更新支付记录失败! " + updateRownum);
                    return info("SYSTEM_ERROR", "支付记录更新失败");
                }
            } else {
                String subcode = response.getSubCode();
                if ("ACQ.TRADE_NOT_EXIST".equals(subcode) && paymentRecord.getState().equals(PaymentState.SUCCESS.getCode())) {
                    paymentRecord.setState(PaymentState.UNDONE.getCode());
                    paymentDao.receivedPayment(paymentRecord);
                }
                // 错误编码转名称
                String errorname = paymentDao.getSysParamsValue("alipay_error_code", subcode);
                return info("SYSTEM_ERROR", errorname);
            }
        }catch (AlipayApiException e){
            log.error("支付宝查询订单接口异常");
            return info("SYSTEM_ERROR","系统错误");
        }

    }

    /**
     * 撤销订单
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderCancel(String tradeNo) {
        if (StringUtils.isBlank(tradeNo)){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        String bizContent = String.format("{\"out_trade_no\":\"%s\",\"trade_no\":\"%s\"}", tradeNo, "");
        request.setBizContent(bizContent);
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradeCancelResponse response = alipayClient.execute(request);
            if (log.isDebugEnabled()) {
                DumpResponse(response);
            }
            if (response.isSuccess()) {
                paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                paymentDao.receivedPayment(paymentRecord);
                return info("OPERATE_SUCCESS", "订单撤销成功");
            } else {
                String msg = response.getMsg();
                String subMsg = response.getSubMsg();
                return info("UNKNOWN", StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
            }
        } catch (AlipayApiException e) {
            log.error("订单撤销失败！trade_no=" + tradeNo, e);
            return info("SYSTEM_ERROR", e.getErrCode() + "-" + e.getErrMsg());
        }
    }

    /**
     * 订单退款
     * @param tradeNo      订单号
     * @param refundNo     退款号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return
     */
    @Override
    public ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        if (StringUtils.isBlank(tradeNo)){
            return info("INVALID_PARAMETER","订单号不允许为空");
        }
        if (StringUtils.isBlank(refundNo)){
            return info("INVALID_PARAMETER","退款号不允许为空");
        }
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) == -1){
            return info("INVALID_PARAMETER","退款金额不允许为空/退款金额不允许小于0");
        }
        if (StringUtils.isBlank(refundReason)){
            return info("INVALID_PARAMETER","退款原因不允许为空");
        }

        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        Map<String, Object> params = new HashMap<String, Object>(0);
        params.put("out_trade_no", tradeNo);
        params.put("trade_no", "");
        params.put("out_request_no", refundNo);
        params.put("refund_amount", refundAmount);
        params.put("refund_reason", refundReason);
        request.setBizContent(JsonUtil.getJson(params));
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);

            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess() && "10000".equals(response.getCode())) {
                paymentRecord.setState(PaymentState.REFUND.getCode());
                paymentRecord.setRefundDate(new Date());
                paymentRecord.setRefundDesc(refundReason);
                paymentDao.receivedPayment(paymentRecord);
                return info("OPERATE_SUCCESS", "退款申请提交成功");
            } else {
                String msg = response.getMsg();
                String subMsg = response.getSubMsg();
                String code = response.getCode();
                String subCode = response.getSubCode();
                return info(StringUtils.isNotEmpty(subCode) ? subCode : code, StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
            }
        } catch (AlipayApiException e) {
            return info("SYSTEM_ERROR", e.getErrCode() + e.getErrMsg());
        }
    }

    /**
     * 下载对账单
     * @param mchId    商户号
     * @param billDate 对账日期
     * @param billType 账单类型：ALL，返回当日所有订单信息，默认值、SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单
     * @return
     */
    @Override
    public ResultInfo DownloadBillData(String mchId, String billDate, String billType) {
        if (StringUtils.isBlank(mchId)){
            return info("INVALID_PARAMETER","商户号不允许为空");
        }
        if (StringUtils.isBlank(billDate)){
            return info("INVALID_PARAMETER","对账日期不允许为空");
        }
        log.debug("下载支付宝对账单：DownloadBillData('" + billType + "','" + billDate + "')");
        if (StringUtils.isEmpty(billType)) {
            billType = "trade";
        }
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        request.setBizContent("{" + "\"bill_type\":\"" + billType + "\"," + "\"bill_date\":\"" + billDate + "\"" + "  }");
        try {
            alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, mchId, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            if (log.isInfoEnabled()) {
                DumpResponse(response);
            }
            if (response.isSuccess()) {
                String url = response.getBillDownloadUrl();
                return info("OPERATE_SUCCESS", url);
            } else {
                return info("UNKNOWN", "[" + response.getCode() + "]" + response.getMsg() + ", [" + response.getSubCode() + "]" + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }


    /**
     * 打印信息
     * @param response
     */
    private void DumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(), response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
