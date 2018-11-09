package com.tencent.trade;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.*;
import com.base.TradeBase;
import com.tencent.client.XMLClient;
import com.tencent.config.WxpayConfig;
import com.tencent.model.WxpayResult;
import com.tencent.model.builder.*;
import com.tencent.utils.WxpayUtil;
import com.utils.ResultInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * 微信支付服务（第三方接口）
 *
 * @author zhsh
 */
public class WxpayTradeNew extends TradeBase {

    private static Log log = LogFactory.getLog(WxpayTradeNew.class);

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    /**
     * PC网页支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {
        try {
            ResultInfo info = WxpayUnifiedOrder(paymentRecord);
            String code_url = "";
            if (info.getCode().equals("OPERATE_SUCCESS")) {  //成功
                code_url = info.getMessage();
            } else {
                return info("SYSTEM_ERROR", "获取微信二维码地址错误");
            }
            // 生成二维码
            String qrcode = QRCodeUtil.makeQrcode(code_url);
            if (StringUtils.isBlank(qrcode)) {
                return info("SYSTEM_ERROR", "生成微信二维码错误");
            } else {
                return info("OPERATE_SUCCESS", qrcode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", "获取微信二维码地址错误");
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
        //微信被扫也是提供二维码，所以直接使用网站支付接口
        return TradePayPage(paymentRecord);
    }

    /**
     * 商户主扫支付（收银台）
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayMicropay(PaymentRecord paymentRecord) {
        // APP和网页支付提交用户端IP，Native支付填调用微信支付API的机器IP。
        String spbill_create_ip = WxpayUtil.getRemoteAddr();
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = WxpayUtil.getLocalIp();
        }
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = "0.0.0.1";
        }

        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());

        // 商品描述，必填项
        String body = paymentRecord.getSummary();
        if (StringUtil.isEmpty(body)) {
            body = paymentRecord.getTradeName();
        }
        String outTradeNo = paymentRecord.getPartner() + paymentRecord.getOutTradeNo(); // 交易流水号=合作方号+交易流水号
        MicroPayRequestBuilder rb = new MicroPayRequestBuilder();
        rb.setMch_id(item.getThirdPartySubaccount());
        rb.setOut_trade_no(outTradeNo);
        rb.setTotal_fee(paymentRecord.getTotalFee().toPlainString());
        rb.setBody(body);
        rb.setAttach(paymentRecord.getReturnUrl()); // 使用附加数据字段保存同步返回地址
        rb.setSpbill_create_ip(spbill_create_ip); // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        rb.setAuth_code(paymentRecord.getAuthCode()); // 扫码支付授权码，设备读取用户微信中的条码或者二维码信息（注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头）

        // 获取返回信息
        XMLClient client = new XMLClient();
        try {
            String xmlStr = rb.buildXmlString();
            log.debug("订单[" + outTradeNo + "]调起微信：" + xmlStr);
            WxpayResult result = client.sendPost(WxpayConfig.micropay, xmlStr);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.SUCCESS.getCode());
                    Date gmt_payment = new Date();
                    try {
                        String time_end = result.getTime_end();
                        if (StringUtils.isNotEmpty(time_end)) {
                            gmt_payment = DateUtils.parseDate(time_end, "yyyyMMddHHmmss");
                        }
                    } catch (ParseException e) {
                        log.error("日期格式转换失败：" + result.getTime_end(), e);
                    }
                    paymentRecord.setGmtPayment(gmt_payment);
                    paymentDao.receivedPayment(paymentRecord);
                    log.debug("订单[" + outTradeNo + "]支付成功，(" + result.getResult_code() + ")" + result.getResult_msg());
                    return info("TRADE_SUCCESS", "支付成功");
                } else if ("USERPAYING".equalsIgnoreCase(result.getErr_code())) {
                    log.debug("订单[" + outTradeNo + "]支付处理中，(" + result.getErr_code() + ")" + result.getErr_code_des());
                    return info("WAIT_BUYER_PAY", "等待用户付款");
                } else {
                    log.debug("订单[" + outTradeNo + "]支付失败，(" + result.getErr_code() + ")" + result.getErr_code_des());
                    return info("UNKNOWN", "支付失败，(" + result.getErr_code() + ")" + result.getErr_code_des());
                }
            } else {
                log.debug("订单[" + outTradeNo + "]支付失败，" + result.getReturn_msg());
                return info("UNKNOWN", "支付失败，" + result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", "支付失败，" + e.getMessage());
        } catch (IOException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", "支付失败，" + e.getMessage());
        }
    }

    /**
     * 手机APP支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayApp(PaymentRecord paymentRecord) {
        try {
            ResultInfo info = WxpayUnifiedOrder(paymentRecord);
            String prepay_id = "";/** 预支付交易会话标识 ，微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时 */
            if (info.getCode().equals("OPERATE_SUCCESS")) {  //成功
                prepay_id = info.getMessage();
            } else {
                return info("SYSTEM_ERROR", "获取微信预支付交易会话标识错误");
            }

            return info("OPERATE_SUCCESS", prepay_id);
        } catch (Exception e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", "获取微信预支付交易会话标识错误");
        }
    }

    /**
     * 获取微信用户授权
     *
     * @param response
     * @param id
     */
    public void TradePayWxAuthorize(HttpServletResponse response, int id) throws IOException {
        String redirect_uri = URLEncoder.encode(WxpayConfig.redirect_uri, "UTF-8");
        String url = WxpayConfig.authorize + "?appid=" + WxpayConfig.appid + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_base&state=" + id + "#wechat_redirect";
        response.sendRedirect(url);
    }

    /**
     * 获取微信用户openId
     *
     * @param code
     * @return
     */
    public ResultInfo TradePayWxOpenId(String code) {
        // // https://api.weixin.qq.com/sns/oauth2/access_token?appid=<appid>&secret=<secret>&code=<code>&grant_type=authorization_code
        String url = WxpayConfig.access_token + "?appid=" + WxpayConfig.appid + "&secret=" + WxpayConfig.appsercret + "&code=" + code + "&grant_type=authorization_code";
        log.debug("WxpayTradeNew.url=" + url);
        String json = WxpayUtil.getURLContent(url);
        log.debug("WxpayTradeNew.json=" + json);
        // {"access_token":"WHChBVMHPvxSODuEFbxq0p82lf6EdEJT6wX1RZcIDsMOSu2Ubz0DYxkIKljpgFfOGAA3RcMPuSir5n6Nne2a3QD-W2i7ak9G7XHTUy6rNAs","expires_in":7200,"refresh_token":"rzi3VnbDfJ4GJdQXbBQplRz91swgb493F7G2jeD43i0mM0b86WT8xnNVmG24VIscp6RVMXL7akFvkekHR7ZuzznGbsJuJWui_AFPt991hzg","openid":"oysl2wVWfenvDUOHjdJ7d9VNy9hA","scope":"snsapi_base"}
        if (StringUtils.isEmpty(json)) {
            log.error("公众号返回信息错误" + "\r\n" + json);
            return info("SYSTEM_ERROR", "获取用户信息失败");
        }
        String msg;

        Map<String, Object> map = JsonUtil.parserToMap(json);
        if (!map.containsKey("openid")) {
            String errcode = map.get("errcode").toString();
            String errmsg = map.get("errmsg").toString();
            if (errmsg.startsWith("code been used") && "40163".equals(errcode)) {
                msg = "请重新申请支付";
            } else {
                msg = "获取预支付单号失败：" + errcode + "-" + errmsg;
            }
            log.error(msg + "\r\nWxpayTradeNew.url=" + url + "\r\nWxpayTradeNew.json=" + json);
            return info("SYSTEM_ERROR", msg);
        }
        return info("OPERATE_SUCCESS", map.get("openid").toString());
    }

    /**
     * 微信公众号支付（支付场景：trade.pay.jsapi）
     *
     * @param paymentRecord
     * @return
     */
    public ResultInfo TradePayJsapi(PaymentRecord paymentRecord) {
        try {
            ResultInfo info = WxpayUnifiedOrder(paymentRecord);
            String prepay_id = "";/** 预支付交易会话标识 ，微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时 */
            if (info.getCode().equals("OPERATE_SUCCESS")) {  //成功
                prepay_id = info.getMessage();
            } else {
                return info("SYSTEM_ERROR", "获取微信预支付交易会话标识错误");
            }

            return info("OPERATE_SUCCESS", prepay_id);
        } catch (Exception e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", "获取微信预支付交易会话标识错误");
        }
    }

    /**
     * 手机网页支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayWap(PaymentRecord paymentRecord) {
        try {
            ResultInfo info = WxpayUnifiedOrder(paymentRecord);
            String mweb_url = "";
            if (info.getCode().equals("OPERATE_SUCCESS")) {  //成功
                mweb_url = info.getMessage();
            } else {
                return info("SYSTEM_ERROR", "获取微信手机网站URL错误");
            }

            return info("OPERATE_SUCCESS", mweb_url);
        } catch (Exception e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", "获取微信手机网站URL错误");
        }
    }

    /**
     * PC网页外币支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPageFc(PaymentRecord paymentRecord) {
        return null;
    }

    /**
     * 手机网页外币支付
     *
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayWapFc(PaymentRecord paymentRecord) {
        return null;
    }

    /**
     * 订单查询
     *
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderQuery(String tradeNo) {
        PaymentRecord paymentRecord = this.paymentDao.getPaymentRecord(tradeNo);
        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        OrderQueryRequestBuilder rb = new OrderQueryRequestBuilder();
        rb.setMch_id(item.getThirdPartySubaccount());
        rb.setOut_trade_no(tradeNo);
        XMLClient client = new XMLClient();
        try {
            WxpayResult result = client.sendPost(WxpayConfig.orderquery, rb.buildXmlString());
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    String trade_state = result.getTrade_state();
                    if ("SUCCESS".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.SUCCESS.getCode());
                        Date gmt_payment = new Date();
                        try {
                            String time_end = result.getTime_end();
                            if (StringUtils.isNotEmpty(time_end)) {
                                gmt_payment = DateUtils.parseDate(time_end, "yyyyMMddHHmmss");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        paymentRecord.setGmtPayment(gmt_payment);
                        paymentDao.receivedPayment(paymentRecord);
                        return info("TRADE_SUCCESS", "支付成功");
                    } else if ("NOTPAY".equalsIgnoreCase(trade_state)) {
                        return info("TRADE_NOTPAY", "订单未支付");
                    } else if ("USERPAYING".equalsIgnoreCase(trade_state)) {
                        return info("WAIT_BUYER_PAY", "等待用户付款");
                    } else if ("CLOSED".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.CLOSED.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return info("TRADE_CLOSED", "订单已关闭");
                    } else if ("REVOKED".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.CANCEL.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return info("TRADE_CANCEL", "订单已撤销");
                    } else if ("REFUND".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.REFUND.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return info("TRADE_REFUND", "订单已退款");
                    } else if ("PAYERROR".equalsIgnoreCase(trade_state)) {
                        if (paymentRecord.getState().equals("1")) {
                            return info("TRADE_NOTPAY", "订单未支付");
                        }
                        paymentRecord.setState(PaymentState.FAIL.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return info("UNKNOWN", "支付失败");
                    } else {
                        return info("UNKNOWN", result.getTrade_state_desc());
                    }
                } else {
                    return info("UNKNOWN", result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return info("UNKNOWN", result.getReturn_msg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 订单撤销
     *
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderCancel(String tradeNo) {
        PaymentRecord paymentRecord = this.paymentDao.getPaymentRecord(tradeNo);
        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());

        // 撤销订单申请
        XMLClient client = new XMLClient();
        try {
            // 创建撤销请求builder，设置请求参数
            ReverseRequestBuilder rb = new ReverseRequestBuilder();
            rb.setMch_id(item.getThirdPartySubaccount());
            rb.setOut_trade_no(tradeNo);
            WxpayResult result = client.sendPostSSL(WxpayConfig.reverse, rb.buildXmlString(), item.getThirdPartySubaccount());
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                    paymentDao.receivedPayment(paymentRecord);
                    return info("OPERATE_SUCCESS", "订单已撤销");
                } else {
                    return info("UNKNOWN", result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return info("UNKNOWN", result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 订单退款
     *
     * @param tradeNo      订单号
     * @param refundNo     退款号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return
     */
    @Override
    public ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);
        // 查找支付项目
        PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
        String mchId = paymentItem.getThirdPartySubaccount();
        // 创建退款请求builder，设置请求参数
        RefundRequestBuilder rb = new RefundRequestBuilder();
        rb.setMch_id(mchId); // 微信支付分配的商户号
        rb.setOut_refund_no(refundNo); // 退款单号可保证唯一
        rb.setOut_trade_no(tradeNo);
        rb.setTotal_fee(paymentRecord.getTotalFee().toPlainString()); // 订单总金额，单位为分，只能为整数
        rb.setRefund_fee(refundAmount.toPlainString()); // 退款总金额，订单总金额，单位为分
        rb.setOp_user_id(paymentRecord.getPartner()); // 将合作方用作操作员

        XMLClient client = new XMLClient();
        WxpayResult result;
        try {
            result = client.sendPostSSL(WxpayConfig.refund, rb.buildXmlString(), mchId);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.REFUND.getCode());
                    paymentRecord.setRefundDate(new Date());
                    paymentRecord.setRefundDesc(refundReason);
                    paymentDao.receivedPayment(paymentRecord);
                    return info("OPERATE_SUCCESS", "退款申请提交成功");
                } else {
                    return info("UNKNOWN", result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return info("UNKNOWN", result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 下载对账单
     *
     * @param mchId    商户号
     * @param billDate 对账日期
     * @param billType 账单类型：ALL，返回当日所有订单信息，默认值、SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单
     * @return
     */
    @Override
    public ResultInfo DownloadBillData(String mchId, String billDate, String billType) {
        DownloadBillRequestBuilder rb = new DownloadBillRequestBuilder();
        rb.setMch_id(mchId);
        rb.setBill_date(billDate);
        rb.setBill_type(billType);
        String tar_type = "GZIP";
        rb.setTar_type(tar_type);
        XMLClient client = new XMLClient();
        try {
            WxpayResult result;
            if ("GZIP".equalsIgnoreCase(tar_type)) {
                result = client.sendPostGzip(WxpayConfig.downloadbill, rb.buildXmlString());
            } else {
                result = client.sendPost(WxpayConfig.downloadbill, rb.buildXmlString());
            }
            String content = result.getResultXML();
            // 保存微信对账文件到bptb目录
            try {
                String bptbPayFilename = billDate + "_" + mchId + "_" + DateUtils.formatDate(new Date(), "yyyyMMddHHmmss") + ".csv";
                String bptbPayFilePath = TysfConfig.getBptbPayFilePath(billDate) + bptbPayFilename;
                FileUtil.write(content, bptbPayFilePath, "GBK");
            } catch (Exception e) {
                log.error("微信下载对账文件保存失败！[" + mchId + "][" + billDate + "][" + billType + "][" + tar_type + "]");
            }
            if ("FAIL".equalsIgnoreCase(result.getReturn_code())) {
                return info("UNKNOWN", result.getReturn_msg());
            } else {    //下载成功
                return info("OPERATE_SUCCESS",content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 获取IP
     *
     * @return
     */
    private static String getIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String spbill_create_ip = WxpayUtil.getWapIp(request);
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = WxpayUtil.getRemoteAddr();
        }
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = "0.0.0.1";
        }
        return spbill_create_ip;
    }

    /**
     * 统一下单
     *
     * @param paymentRecord
     * @return
     */
    private ResultInfo WxpayUnifiedOrder(PaymentRecord paymentRecord) {
        log.info("WxpayUnifiedOrder t_partner=" + paymentRecord.getPartner() + ",t_out_trade_no=" + paymentRecord.getOutTradeNo());
        // APP和网页支付提交用户端IP，Native支付填调用微信支付API的机器IP。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String spbill_create_ip = getIP();
        System.out.println("ip:" + spbill_create_ip);
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        PaymentType payType = PaymentType.parse(item.getPaymentType());
        String t_summary = paymentRecord.getSummary();
        String t_name = paymentRecord.getTradeName();
        String t_partner = paymentRecord.getPartner();
        String t_out_trade_no = paymentRecord.getOutTradeNo();
        String t_total_fee = paymentRecord.getTotalFee().toPlainString();
        String t_timeout = paymentRecord.getTimeout();
        int id = paymentRecord.getId();
        // 超时时间
        int timeout = 120;
        if (StringUtils.isNotEmpty(t_timeout)) {
            if (t_timeout.toLowerCase().endsWith("m")) {
                timeout = Integer.parseInt(t_timeout.substring(0, t_timeout.length() - 1));
            } else {
                timeout = Integer.parseInt(t_timeout);
            }
        }
        // 商品描述(必填项)
        String body = StringUtil.isEmpty(t_summary) ? t_name : t_summary;
        if (StringUtil.isEmpty(body)) {
            body = t_out_trade_no;
        }
        Date createDate = paymentRecord.getCreateDate();
        String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
        UnifiedOrderRequestBuilder rb = new UnifiedOrderRequestBuilder();
        rb.setMch_id(item.getThirdPartySubaccount());
        rb.setOut_trade_no(outTradeNo);
        rb.setTotal_fee(t_total_fee);
        rb.setBody(body);
        rb.setNotify_url(WxpayConfig.notify_url);
        rb.setAttach(paymentRecord.getReturnUrl());// 使用附加数据字段保存同步返回地址
        rb.setSpbill_create_ip(spbill_create_ip);
        rb.setTime_start(WxpayUtil.fromDate(createDate));
        rb.setTime_expire(WxpayUtil.fromDate(DateUtils.addMinutes(createDate, timeout)));
        rb.setProduct_id(String.valueOf(id));
        //TODO  后期两套服务需要并行，所以http://zhifu.tsinghua.edu.cn 域名需要更改
        String info = "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://zhifu.tsinghua.edu.cn/\",\"wap_name\": \"清华大学资金结算服务平台\"}}";
        rb.setScene_info(info);
        if (payType.equals(PaymentType.WXSMFK)) {
            rb.setTrade_type("NATIVE");
        } else if (payType.equals(PaymentType.WXGZHZF)) {
            rb.setTrade_type("JSAPI");
            if (StringUtils.isBlank(paymentRecord.getOpenId())){
                //return info("INVALID_PARAMETER","openId不能为空");
            }
            rb.setOpenid(paymentRecord.getOpenId());    //
        } else if (payType.equals(PaymentType.WXAPPZF)) {
            rb.setTrade_type("APP");
            //TODO APP支付需要必传 APPID
        } else if (payType.equals(PaymentType.WXSJWZZF)) {
            rb.setTrade_type("MWEB");
        } else {
            log.error("您已选择" + payType.getName() + "，不支持更换支付方式！");
            return info("UNKNOWN", "您已选择" + payType.getName() + "，不支持更换支付方式！");
        }
        // 获取返回信息
        XMLClient client = new XMLClient();
        try {
            WxpayResult result = client.sendPost(WxpayConfig.unifiedorder, rb.buildXmlString());
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.SUBMITTED.getCode());
                    paymentDao.receivedPayment(paymentRecord);
                    if (payType.equals(PaymentType.WXSMFK)) {
                        return info("OPERATE_SUCCESS", result.getCode_url());
                    } else if (payType.equals(PaymentType.WXSJWZZF)) {
                        return info("OPERATE_SUCCESS", result.getMweb_url());
                    } else {
                        return info("OPERATE_SUCCESS", result.getPrepay_id());
                    }
                } else {
                    return info("UNKNOWN", result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return info("UNKNOWN", result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }


}
