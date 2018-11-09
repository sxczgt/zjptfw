package com.tencent.trade;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.*;
import cn.tsinghua.sftp.util.*;
import com.tencent.client.XMLClient;
import com.tencent.config.WxpayConfig;
import com.tencent.model.WxpayResult;
import com.tencent.model.builder.*;
import com.tencent.utils.RsaUtil;
import com.tencent.utils.WxpayUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 微信支付
 */
public class WxpayTrade {
    private static Log log = LogFactory.getLog(WxpayTrade.class);

    public static void main(String[] args) {
        WxpayTrade wt = new WxpayTrade();
        // String xml = wt.WxpayDownloadBill("1449863802", "20170517", "ALL", "GZIP");
        // System.out.println(xml);
        // String xml = wt.WxpayDownloadBill("1474084502", "20170629", "ALL", "GZIP"); // 网费-微信公众号
        String xml = wt.WxpayDownloadBill("1480179582", "20170629", "ALL", "GZIP"); // 网费-微信扫码
        try {
            FileUtils.writeStringToFile(new File("D:/1480179582.txt"), xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 生成签名
        String mch_id = "";
        PubKeyRequestBuilder rb = new PubKeyRequestBuilder();
        rb.setMch_id(mch_id);
        XMLClient client = new XMLClient();
        WxpayResult result;
        try {
            result = client.sendPostSSL(WxpayConfig.getpublickey, rb.buildXmlString(), mch_id);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    String certPath = "d://pem";
                    File f = new File(certPath);
                    if (!f.exists()) {
                        f.mkdir();
                    }
                    String filename_pem = certPath + "/" + mch_id + ".pem";// 秘钥文件
                    File file = new File(filename_pem);
                    if (!file.exists()) {// 如果秘钥文件不存在
                        FileUtil.write(result.getPub_key(), filename_pem, "GBK");
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    /**
     * 提交刷卡支付<br>
     * 收银员使用扫码设备读取微信用户刷卡授权码以后，二维码或条码信息传送至商户收银台，由商户收银台或者商户后台调用该接口发起支付<br>
     * 提醒1：提交支付请求后微信会同步返回支付结果。<br>
     * 当返回结果为“系统错误”时，商户系统等待5秒后调用【查询订单API】，查询支付实际交易结果；<br>
     * 当返回结果为“USERPAYING”时，商户系统可设置间隔时间(建议10秒)重新查询支付结果，直到支付成功或超时(建议30秒)；<br>
     * 提醒2：在调用查询接口返回后，如果交易状况不明晰，请调用【撤销订单API】，此时如果交易失败则关闭订单，该单不能再支付成功；<br>
     * 如果交易成功，则将扣款退回到用户账户。当撤销无返回或错误时，请再次调用。<br>
     * 注意：请勿扣款后立即调用【撤销订单API】,建议至少15秒后再调用。撤销订单API需要双向证书。<br>
     * URL地址：https://api.mch.weixin.qq.com/pay/micropay<br>
     *
     * @param t_partner            合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no       订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
     * @param t_name               订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
     * @param t_auth_code          扫码支付授权码，设备读取用户微信中的条码或者二维码信息（注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头）
     * @param t_total_fee          订单总金额，单位为元，不能超过1亿元
     * @param t_summary            订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
     * @param t_item               支付项目编号
     * @param t_subitem            子项目编号
     * @param t_fptt               发票抬头 多个发票，用|分割
     * @param t_username           缴款人姓名
     * @param t_user_id            缴款人证件编号
     * @param t_user_id_type       缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
     * @param t_return_url         页面跳转同步通知页面路径
     * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
     * @param t_datetime           当前时间
     * @param t_timeout            超时时间,支付超时定义为120分钟（不能带单位）
     * @param t_version            接口的版本号
     * @param t_charset            编码格式
     * @param t_sign               签名
     * @return
     */
    public String WxpayMicroPay(String t_partner, String t_out_trade_no, String t_name, String t_auth_code, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version, String t_charset, String t_sign) {
        log.debug("WxpayMicroPay [t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_name=" + t_name + ",t_auth_code=" + t_auth_code + ",t_total_fee=" + t_total_fee + ",t_summary=" + t_summary + ",t_item=" + t_item + ",t_subitem=" + t_subitem + ",t_fptt=" + t_fptt + ",t_username=" + t_username + ",t_user_id=" + t_user_id + ",t_user_id_type=" + t_user_id_type + ",t_return_url=" + t_return_url + ",t_extra_common_param=" + t_extra_common_param + ",t_datetime=" + t_datetime
                + ",t_timeout=" + t_timeout + ",t_version=" + t_version + ",t_charset=" + t_charset + ",t_sign=" + t_sign + "]");
        if (StringUtils.isEmpty(t_auth_code)) {
            log.error("支付授权码不能为空!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
            return JsonUtil.getJson(-100, "支付授权码不能为空");
        }

        // 支付授权码，用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头
        int auth_code_prefix = Integer.valueOf(t_auth_code.substring(0, 2));
        int auth_code_length = t_auth_code.length();
        if (auth_code_prefix < 10 || auth_code_prefix > 15 || auth_code_length != 18) {
            log.error("支付授权码错误!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
            return JsonUtil.getJson(-100, "支付授权码错误");
        }

        if (StringUtils.isNotEmpty(t_version)) {
            try {
                Float version = Float.parseFloat(t_version);
                if (version == 1.1) {
                    t_fptt = DecodeBase64(t_fptt);
                    t_username = DecodeBase64(t_username);
                    t_name = DecodeBase64(t_name);
                    t_summary = DecodeBase64(t_summary);
                    t_extra_common_param = DecodeBase64(t_extra_common_param);
                    log.info("Base64.decode " + t_fptt + " " + t_name + " " + t_extra_common_param);
                }
            } catch (Exception e) {
                log.error("版本号错误!!! " + e.getMessage() + " " + t_version, e);
                return JsonUtil.getJson(-1, "版本号错误，" + e.getMessage());
            }
        }

        // 如果指定了字符集，按指定字符集进行转换
        if (null != t_charset && t_charset.length() > 0) {
            try {
                t_fptt = TransformCharset(t_fptt, t_charset);
                t_username = TransformCharset(t_username, t_charset);
                t_name = TransformCharset(t_name, t_charset);
                t_summary = TransformCharset(t_summary, t_charset);
                t_extra_common_param = TransformCharset(t_extra_common_param, t_charset);
            } catch (UnsupportedEncodingException e) {
                log.error("字符编码转换错误!!! " + e.getMessage() + " " + t_charset, e);
                return JsonUtil.getJson(-1, "字符编码转换错误，" + e.getMessage());
            }
        }

        if (t_total_fee == null) {
            return JsonUtil.getJson(-2, "付款金额错误");
        }

        // 解析参数
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPartner(t_partner);
        paymentRecord.setOutTradeNo(t_out_trade_no);
        try {
            paymentRecord.setTotalFee(new BigDecimal(t_total_fee));
        } catch (Exception e2) {
            return JsonUtil.getJson(-2, "付款金额错误");
        }
        paymentRecord.setTradeName(t_name);
        paymentRecord.setSummary(t_summary);
        paymentRecord.setItem(t_item);
        // 保存超时时间时，不能带单位，否则处理超时数据会出错
        paymentRecord.setTimeout(t_timeout.toLowerCase().endsWith("m") ? t_timeout.substring(0, t_timeout.length() - 1) : t_timeout);

        if (null != t_subitem && t_subitem.trim().length() > 0)
            paymentRecord.setSubItem(t_subitem);
        else
            paymentRecord.setSubItem("0");

        paymentRecord.setState(PaymentState.UNDONE.getCode());
        paymentRecord.setCreateDate(new Date());
        paymentRecord.setReturnUrl(t_return_url);
        paymentRecord.setUserName(t_username);
        paymentRecord.setUserId(t_user_id);
        paymentRecord.setUserIdType(t_user_id_type);
        paymentRecord.setExtraCommonParam(t_extra_common_param);
        if (null == paymentRecord.getOutTradeNo() || paymentRecord.getOutTradeNo().trim().length() == 0) {
            return JsonUtil.getJson(-3, "订单号不能为空");
        }
        if (paymentRecord.getOutTradeNo().length() > 32) {
            return JsonUtil.getJson(-4, "订单号超长");
        }
        if (null == paymentRecord.getTradeName() || paymentRecord.getTradeName().trim().length() == 0) {
            return JsonUtil.getJson(-5, "订单名称不能为空");
        }
        if (paymentRecord.getTradeName().length() > 256) {
            return JsonUtil.getJson(-6, "订单名称超长");
        }
        if (null != paymentRecord.getUserName() && paymentRecord.getUserName().length() > 10) {
            return JsonUtil.getJson(-7, "缴款人姓名超长");
        }
        if (paymentRecord.getSubItem().length() > 4) {
            return JsonUtil.getJson(-8, "子项目号不能超过4位");
        }

        try {
            Date requestTime = DateUtils.parseDate(t_datetime, "yyyyMMddHHmmss");
            Date now = new Date();
            if (now.getTime() - requestTime.getTime() > 5 * 60 * 1000) {
                log.warn("请求超时 " + t_datetime + " " + requestTime.getTime() + " server time:" + " " + now + now.getTime() + " " + (now.getTime() - requestTime.getTime()));
            }
        } catch (ParseException e) {
            log.error("请求时间的格式错误!", e);
            return JsonUtil.getJson(-9, "请求时间的格式错误");
        }

        // 合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方未授权! " + paymentRecord);
            return JsonUtil.getJson(-10, "合作方未授权 " + paymentRecord.getPartner());
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_partner", t_partner);
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_name", t_name);
        signBulider.add("t_auth_code", t_auth_code);
        signBulider.add("t_total_fee", t_total_fee);
        signBulider.add("t_summary", t_summary);
        signBulider.add("t_item", t_item);
        signBulider.add("t_subitem", t_subitem);
        signBulider.add("t_fptt", t_fptt);
        signBulider.add("t_username", t_username);
        signBulider.add("t_user_id", t_user_id);
        signBulider.add("t_user_id_type", t_user_id_type);
        signBulider.add("t_return_url", t_return_url);
        signBulider.add("t_extra_common_param", t_extra_common_param);
        signBulider.add("t_datetime", t_datetime);
        signBulider.add("t_timeout", t_timeout);
        signBulider.add("t_version", t_version);
        signBulider.add("t_charset", t_charset);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_item + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-11, "签名验证失败");
            }
        }

        // 查找相同订单号的流水
        PaymentRecord oldPaymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null != oldPaymentRecord) {
            log.info("存在相同订单号的流水  " + oldPaymentRecord);
            if (oldPaymentRecord.getTotalFee().doubleValue() != Double.parseDouble(t_total_fee)) {
                log.error("存在相同订单号的流水且金额不一致!!!");
                return JsonUtil.getJson(-12, "存在相同订单号的流水且金额不一致");
            }
            if (!oldPaymentRecord.getItem().equals(t_item)) {
                log.error("存在相同订单号的流水且项目不一致!!!");
                return JsonUtil.getJson(-13, "存在相同订单号的流水且项目不一致");
            }
            if (!oldPaymentRecord.getUserName().equals(t_username)) {
                log.error("存在相同订单号的流水且姓名不一致!!!");
                return JsonUtil.getJson(-14, "存在相同订单号的流水且姓名不一致");
            }
            if (PaymentState.SUCCESS.getCode().equalsIgnoreCase(oldPaymentRecord.getState())) {
                log.info("此订单已完成缴费");
                return JsonUtil.getJson(-15, "此订单已完成缴费 ，订单编号:" + oldPaymentRecord.getOutTradeNo() + "，金额:" + oldPaymentRecord.getTotalFee());
            }
            log.info("此订单号已存在");
            return JsonUtil.getJson(-16, "此订单号已存在 ，订单编号:" + paymentRecord.getOutTradeNo() + "，金额:" + paymentRecord.getTotalFee());
        }

        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        if (null == item) {
            log.error("支付项目不存在!!! " + paymentRecord);
            return JsonUtil.getJson(-17, "支付项目不存在");
        }
        if (!"2".equals(item.getPaymentItemState())) {
            return JsonUtil.getJson(-18, "此项目未通过审核 " + item.getPaymentItemId());
        }
        String mchId = item.getThirdPartySubaccount();
        if (StringUtil.isEmpty(mchId)) {
            return JsonUtil.getJson(-25, "商户号不能为空 " + item.getPaymentItemId());
        }

        // 判断是否在允许的时间段内
        Date now = new Date();
        if (now.after(item.getEndDate())) {
            return JsonUtil.getJson(-19, "超过缴费时间");
        }
        if (now.before(item.getBeginDate())) {
            return JsonUtil.getJson(-20, "缴费尚未开始");
        }

        if (!paymentRecord.getPartner().trim().equals(item.getPartner())) {
            log.error(paymentRecord.getPartner() + "不允许使用" + item + "项目");
            return JsonUtil.getJson(-21, "此项目未授权");
        }

        // 确定支付方式
        PaymentType payType = PaymentType.parse(item.getPaymentType());
        if (null == payType) {
            log.error("未指定支付方式!!! " + item);
            return JsonUtil.getJson(-22, "未指定支付方式");
        }
        if (!payType.equals(PaymentType.WXSMFK) && !payType.equals(PaymentType.WXSMSK) && !payType.equals(PaymentType.WXAPPZF)) {
            log.error("您已选择" + payType.getName() + "，不支持更换支付方式！");
            return JsonUtil.getJson(-23, "您已选择" + payType.getName() + "，不支持更换支付方式！");
        }
        paymentRecord.setPaymentType(payType.getCode());
        // 设定币种
        paymentRecord.setMoneyType(item.getMoneyType());
        // 保存流水
        Invoice invoice = new Invoice();
        invoice.setPartner(paymentRecord.getPartner());
        invoice.setOutTradeNo(paymentRecord.getOutTradeNo());
        invoice.setFptt(t_fptt);
        invoice.setFpState("0");
        invoice.setMoney(paymentRecord.getTotalFee());
        int id = paymentDao.savePaymentAndFp(paymentRecord, invoice);
        if (id < 0) {
            log.error("保存流水失败!!!");
            return JsonUtil.getJson(-24, "保存流水失败");
        }

        // APP和网页支付提交用户端IP，Native支付填调用微信支付API的机器IP。
        String spbill_create_ip = WxpayUtil.getRemoteAddr();
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = WxpayUtil.getLocalIp();
        }
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = "0.0.0.1";
        }

        // 商品描述，必填项
        String body = t_summary;
        if (StringUtil.isEmpty(body)) {
            body = t_name;
        }
        String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
        MicroPayRequestBuilder rb = new MicroPayRequestBuilder();
        rb.setMch_id(mchId);
        rb.setOut_trade_no(outTradeNo);
        rb.setTotal_fee(t_total_fee);
        rb.setBody(body);
        rb.setAttach(paymentRecord.getReturnUrl()); // 使用附加数据字段保存同步返回地址
        rb.setSpbill_create_ip(spbill_create_ip); // APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        rb.setAuth_code(t_auth_code); // 扫码支付授权码，设备读取用户微信中的条码或者二维码信息（注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头）

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
                    return JsonUtil.getJson(0, "支付成功");
                } else if ("USERPAYING".equalsIgnoreCase(result.getErr_code())) {
                    log.debug("订单[" + outTradeNo + "]支付处理中，(" + result.getErr_code() + ")" + result.getErr_code_des());
                    return JsonUtil.getJson(43101, "支付处理中");
                } else {
                    log.debug("订单[" + outTradeNo + "]支付失败，(" + result.getErr_code() + ")" + result.getErr_code_des());
                    return JsonUtil.getJson(-1, "支付失败，(" + result.getErr_code() + ")" + result.getErr_code_des());
                }
            } else {
                log.debug("订单[" + outTradeNo + "]支付失败，" + result.getReturn_msg());
                return JsonUtil.getJson(-2, "支付失败，" + result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
            return JsonUtil.getJson(-3, "支付失败，" + e.getMessage());
        } catch (IOException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
            return JsonUtil.getJson(-4, "支付失败，" + e.getMessage());
        }
    }

    /**
     * 撤销订单<br>
     * 支付交易返回失败或支付系统超时，调用该接口撤销交易。<br>
     * 如果此订单用户支付失败，微信支付系统会将此订单关闭；如果用户支付成功，微信支付系统会将此订单资金退还给用户。<br>
     * <br>
     * 注意：7天以内的交易单可调用撤销，其他正常支付的单如需实现相同功能请调用申请退款API。<br>
     * 提交支付交易后调用【查询订单API】，没有明确的支付结果再调用【撤销订单API】。<br>
     * <br>
     * 调用支付接口后请勿立即调用撤销订单API，建议支付后至少15s后再调用撤销订单接口。<br>
     * URL地址：https://api.mch.weixin.qq.com/secapi/pay/reverse<br>
     * 请求需要双向证书。<br>
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
     * @param t_sign         签名
     * @return
     */
    public String WxpayReverse(String t_partner, String t_out_trade_no, String t_sign) {
        log.info("WxpayReverse t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (StringUtils.isEmpty(t_partner)) {
            return JsonUtil.getJson(-1, "合作方不能为空");
        }
        if (StringUtils.isEmpty(t_out_trade_no)) {
            return JsonUtil.getJson(-2, "订单号不能为空");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
            return JsonUtil.getJson(-3, "订单不存在");
        }
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方未授权！(" + paymentRecord.getPartner() + ")");
            return JsonUtil.getJson(-4, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_partner", t_partner);
        signBulider.add("t_out_trade_no", t_out_trade_no);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-5, "签名验证失败");
            }
        }

        // 查找支付项目
        PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
        String mchId = paymentItem.getThirdPartySubaccount();

        // 撤销订单申请
        String outTradeNo = t_partner + t_out_trade_no;
        XMLClient client = new XMLClient();
        try {
            // 创建撤销请求builder，设置请求参数
            ReverseRequestBuilder rb = new ReverseRequestBuilder();
            rb.setMch_id(mchId);
            rb.setOut_trade_no(outTradeNo);
            WxpayResult result = client.sendPostSSL(WxpayConfig.reverse, rb.buildXmlString(), mchId);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                    paymentDao.receivedPayment(paymentRecord);
                    return JsonUtil.getJson(0, "订单已撤销");
                } else {
                    return JsonUtil.getJson(-101, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-102, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-103, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-104, e.getMessage());
        }
    }

    /**
     * 统一下单<br>
     * 除被扫支付场景以外，商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再按扫码、JSAPI、APP等不同场景生成交易串调起支付。<br>
     * URL地址：https://api.mch.weixin.qq.com/pay/unifiedorder<br>
     *
     * @param t_partner            合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no       订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
     * @param t_name               订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
     * @param t_total_fee          订单总金额，单位为元，不能超过1亿元
     * @param t_summary            订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
     * @param t_item               支付项目编号
     * @param t_subitem            子项目编号
     * @param t_fptt               发票抬头 多个发票，用|分割
     * @param t_username           缴款人姓名
     * @param t_user_id            缴款人证件编号
     * @param t_user_id_type       缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
     * @param t_return_url         页面跳转同步通知页面路径
     * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
     * @param t_datetime           当前时间
     * @param t_timeout            超时时间,支付超时定义为120分钟（不能带单位）
     * @param t_version            接口的版本号
     * @param t_charset            编码格式
     * @param t_sign               签名
     * @return
     */
    public String WxpayUnifiedOrder(String t_partner, String t_out_trade_no, String t_name, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version, String t_charset, String t_sign) {
        log.debug("t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_name=" + t_name + ",t_total_fee=" + t_total_fee + ",t_summary=" + t_summary + ",t_item=" + t_item + ",t_subitem=" + t_subitem + ",t_fptt=" + t_fptt + ",t_username=" + t_username + ",t_user_id=" + t_user_id + ",t_user_id_type=" + t_user_id_type + ",t_return_url=" + t_return_url + ",t_extra_common_param=" + t_extra_common_param + ",t_datetime=" + t_datetime + ",t_timeout=" + t_timeout + ",t_version=" + t_version
                + ",t_charset=" + t_charset + ",t_sign=" + t_sign);

        if (StringUtils.isNotEmpty(t_version)) {
            try {
                Float version = Float.parseFloat(t_version);
                if (version == 1.1) {
                    t_fptt = DecodeBase64(t_fptt);
                    t_username = DecodeBase64(t_username);
                    t_name = DecodeBase64(t_name);
                    t_summary = DecodeBase64(t_summary);
                    t_extra_common_param = DecodeBase64(t_extra_common_param);
                    log.info("Base64.decode " + t_fptt + " " + t_name + " " + t_extra_common_param);
                }
            } catch (Exception e) {
                log.error("版本号错误!!! " + e.getMessage() + " " + t_version, e);
                return JsonUtil.getJson(-1, "版本号错误，" + e.getMessage());
            }
        }

        // 如果指定了字符集，按指定字符集进行转换
        if (null != t_charset && t_charset.length() > 0) {
            try {
                t_fptt = TransformCharset(t_fptt, t_charset);
                t_username = TransformCharset(t_username, t_charset);
                t_name = TransformCharset(t_name, t_charset);
                t_summary = TransformCharset(t_summary, t_charset);
                t_extra_common_param = TransformCharset(t_extra_common_param, t_charset);
            } catch (UnsupportedEncodingException e) {
                log.error("字符编码转换错误!!! " + e.getMessage() + " " + t_charset, e);
                return JsonUtil.getJson(-1, "字符编码转换错误，" + e.getMessage());
            }
        }

        if (t_total_fee == null) {
            return JsonUtil.getJson(-2, "付款金额错误");
        }

        // 解析参数
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPartner(t_partner);
        paymentRecord.setOutTradeNo(t_out_trade_no);
        try {
            paymentRecord.setTotalFee(new BigDecimal(t_total_fee));
        } catch (Exception e2) {
            return JsonUtil.getJson(-2, "付款金额错误");
        }
        paymentRecord.setTradeName(t_name);
        paymentRecord.setSummary(t_summary);
        paymentRecord.setItem(t_item);
        // 保存超时时间时，不能带单位，否则处理超时数据会出错
        paymentRecord.setTimeout(t_timeout.toLowerCase().endsWith("m") ? t_timeout.substring(0, t_timeout.length() - 1) : t_timeout);

        if (null != t_subitem && t_subitem.trim().length() > 0)
            paymentRecord.setSubItem(t_subitem);
        else
            paymentRecord.setSubItem("0");

        paymentRecord.setState(PaymentState.UNDONE.getCode());
        paymentRecord.setCreateDate(new Date());
        paymentRecord.setReturnUrl(t_return_url);
        paymentRecord.setUserName(t_username);
        paymentRecord.setUserId(t_user_id);
        paymentRecord.setUserIdType(t_user_id_type);
        paymentRecord.setExtraCommonParam(t_extra_common_param);
        if (null == paymentRecord.getOutTradeNo() || paymentRecord.getOutTradeNo().trim().length() == 0) {
            return JsonUtil.getJson(-3, "订单号不能为空");
        }
        if (paymentRecord.getOutTradeNo().length() > 32) {
            return JsonUtil.getJson(-4, "订单号超长");
        }
        if (null == paymentRecord.getTradeName() || paymentRecord.getTradeName().trim().length() == 0) {
            return JsonUtil.getJson(-5, "订单名称不能为空");
        }
        if (paymentRecord.getTradeName().length() > 256) {
            return JsonUtil.getJson(-6, "订单名称超长");
        }
        if (null != paymentRecord.getUserName() && paymentRecord.getUserName().length() > 10) {
            return JsonUtil.getJson(-7, "缴款人姓名超长");
        }
        if (paymentRecord.getSubItem().length() > 4) {
            return JsonUtil.getJson(-8, "子项目号不能超过4位");
        }

        try {
            Date requestTime = DateUtils.parseDate(t_datetime, "yyyyMMddHHmmss");
            Date now = new Date();
            if (now.getTime() - requestTime.getTime() > 5 * 60 * 1000) {
                log.warn("请求超时 " + t_datetime + " " + requestTime.getTime() + " server time:" + " " + now + now.getTime() + " " + (now.getTime() - requestTime.getTime()));
            }
        } catch (ParseException e) {
            log.error("请求时间的格式错误!", e);
            return JsonUtil.getJson(-9, "请求时间的格式错误");
        }

        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方未授权! " + paymentRecord);
            return JsonUtil.getJson(-10, "合作方未授权 " + paymentRecord.getPartner());
        }

        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_name", t_name);
        signBulider.add("t_total_fee", t_total_fee);
        signBulider.add("t_summary", t_summary);
        signBulider.add("t_partner", t_partner);
        signBulider.add("t_item", t_item);
        signBulider.add("t_subitem", t_subitem);
        signBulider.add("t_fptt", t_fptt);
        signBulider.add("t_username", t_username);
        signBulider.add("t_user_id", t_user_id);
        signBulider.add("t_user_id_type", t_user_id_type);
        signBulider.add("t_return_url", t_return_url);
        signBulider.add("t_extra_common_param", t_extra_common_param);
        signBulider.add("t_datetime", t_datetime);
        signBulider.add("t_timeout", t_timeout);
        signBulider.add("t_version", t_version);
        signBulider.add("t_charset", t_charset);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_item + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-11, "签名验证失败");
            }
        }

        // 查找相同订单号的流水
        PaymentRecord oldPaymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null != oldPaymentRecord) {
            log.info("存在相同订单号的流水  " + oldPaymentRecord);
            if (oldPaymentRecord.getTotalFee().doubleValue() != Double.parseDouble(t_total_fee)) {
                log.error("存在相同订单号的流水且金额不一致!!!");
                return JsonUtil.getJson(-12, "存在相同订单号的流水且金额不一致");
            }
            if (!oldPaymentRecord.getItem().equals(t_item)) {
                log.error("存在相同订单号的流水且项目不一致!!!");
                return JsonUtil.getJson(-13, "存在相同订单号的流水且项目不一致");
            }
            if (!oldPaymentRecord.getUserName().equals(t_username)) {
                log.error("存在相同订单号的流水且姓名不一致!!!");
                return JsonUtil.getJson(-14, "存在相同订单号的流水且姓名不一致");
            }
            if (PaymentState.SUCCESS.getCode().equalsIgnoreCase(oldPaymentRecord.getState())) {
                log.info("此订单已完成缴费");
                return JsonUtil.getJson(-15, "此订单已完成缴费 ，订单编号:" + oldPaymentRecord.getOutTradeNo() + "，金额:" + oldPaymentRecord.getTotalFee());
            }
            log.info("此订单号已存在");
            return JsonUtil.getJson(-16, "此订单号已存在 ，订单编号:" + paymentRecord.getOutTradeNo() + "，金额:" + paymentRecord.getTotalFee());
        }

        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        if (null == item) {
            log.error("支付项目不存在!!! " + paymentRecord);
            return JsonUtil.getJson(-17, "支付项目不存在");
        }
        if (!"2".equals(item.getPaymentItemState())) {
            return JsonUtil.getJson(-18, "此项目未通过审核 " + item.getPaymentItemId());
        }

        // 判断是否在允许的时间段内
        Date now = new Date();
        if (now.after(item.getEndDate())) {
            return JsonUtil.getJson(-19, "超过缴费时间");
        }
        if (now.before(item.getBeginDate())) {
            return JsonUtil.getJson(-20, "缴费尚未开始");
        }

        if (!paymentRecord.getPartner().trim().equals(item.getPartner())) {
            log.error(paymentRecord.getPartner() + "不允许使用" + item + "项目");
            return JsonUtil.getJson(-21, "此项目未授权");
        }

        // 确定支付方式
        PaymentType payType = PaymentType.parse(item.getPaymentType());
        if (null == payType) {
            log.error("未指定支付方式!!! " + item);
            return JsonUtil.getJson(-22, "未指定支付方式");
        }
        // !payType.equals(PaymentType.WXJS) &&
        if (!payType.equals(PaymentType.WXSMFK) && !payType.equals(PaymentType.WXAPPZF) && !payType.equals(PaymentType.WXSJWZZF)) {
            log.error("您已选择" + payType.getName() + "，不支持更换支付方式！");
            return JsonUtil.getJson(-23, "您已选择" + payType.getName() + "，不支持更换支付方式！");
        }
        paymentRecord.setPaymentType(payType.getCode());
        // 设定币种
        paymentRecord.setMoneyType(item.getMoneyType());
        // 保存流水
        Invoice invoice = new Invoice();
        invoice.setPartner(paymentRecord.getPartner());
        invoice.setOutTradeNo(paymentRecord.getOutTradeNo());
        invoice.setFptt(t_fptt);
        invoice.setFpState("0");
        invoice.setMoney(paymentRecord.getTotalFee());
        int id = paymentDao.savePaymentAndFp(paymentRecord, invoice);
        if (id < 0) {
            log.error("保存流水失败!!!");
            return JsonUtil.getJson(-24, "保存流水失败");
        }
        paymentRecord.setId(id);
        return WxpayUnifiedOrder(paymentRecord);
    }

    /**
     * 统一下单，只下单不校验
     *
     * @param paymentRecord
     * @return
     */
    public String WxpayUnifiedOrder(PaymentRecord paymentRecord) {
        log.info("WxpayUnifiedOrder t_partner=" + paymentRecord.getPartner() + ",t_out_trade_no=" + paymentRecord.getOutTradeNo());
        // APP和网页支付提交用户端IP，Native支付填调用微信支付API的机器IP。
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String spbill_create_ip = WxpayUtil.getWapIp(request);
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = WxpayUtil.getRemoteAddr();
        }
        if (StringUtil.isEmpty(spbill_create_ip)) {
            spbill_create_ip = "0.0.0.1";
        }
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
        Date timeStart = new Date();
        String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
        UnifiedOrderRequestBuilder rb = new UnifiedOrderRequestBuilder();
        rb.setMch_id(item.getThirdPartySubaccount());
        rb.setOut_trade_no(outTradeNo);
        rb.setTotal_fee(t_total_fee);
        rb.setBody(body);
        rb.setNotify_url(WxpayConfig.notify_url);
        rb.setAttach(paymentRecord.getReturnUrl());// 使用附加数据字段保存同步返回地址
        rb.setSpbill_create_ip(spbill_create_ip);
        rb.setTime_start(WxpayUtil.fromDate(timeStart));
        rb.setTime_expire(WxpayUtil.fromDate(DateUtils.addMinutes(timeStart, timeout)));
        rb.setProduct_id(String.valueOf(id));
        String info = "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"http://zhifu.tsinghua.edu.cn/\",\"wap_name\": \"清华大学资金结算服务平台\"}}";
        rb.setScene_info(info);
        if (payType.equals(PaymentType.WXSMFK)) {
            rb.setTrade_type("NATIVE");
        } else if (payType.equals(PaymentType.WXGZHZF)) {
            rb.setTrade_type("JSAPI");
        } else if (payType.equals(PaymentType.WXAPPZF)) {
            rb.setTrade_type("APP");
        } else if (payType.equals(PaymentType.WXSJWZZF)) {
            rb.setTrade_type("MWEB");
        } else {
            log.error("您已选择" + payType.getName() + "，不支持更换支付方式！");
            return JsonUtil.getJson(-23, "您已选择" + payType.getName() + "，不支持更换支付方式！");
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
                        return JsonUtil.getJson(0, result.getCode_url());
                    } else if (payType.equals(PaymentType.WXSJWZZF)) {
                        return JsonUtil.getJson(0, result.getMweb_url());
                    } else {
                        return JsonUtil.getJson(0, result.getPrepay_id());
                    }
                } else {
                    return JsonUtil.getJson(-1, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-2, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-3, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-4, e.getMessage());
        }
    }

    /**
     * 查询订单<br>
     * 该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。<br>
     * 需要调用查询接口的情况：<br>
     * ◆ 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；<br>
     * ◆ 调用支付接口后，返回系统错误或未知交易状态情况；<br>
     * ◆ 调用被扫支付API，返回USERPAYING的状态；<br>
     * ◆ 调用关单或撤销接口API之前，需确认支付状态；<br>
     * 接口链接 https://api.mch.weixin.qq.com/pay/orderquery<br>
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 商户订单号，通过此商户订单号查询当面付的交易状态(必填)
     */
    public String WxpayQueryOrder(String t_partner, String t_out_trade_no) {
        log.info("WxpayQueryOrder t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (null == t_out_trade_no || t_out_trade_no.trim().length() == 0) {
            return JsonUtil.getJson(-1, "订单号不能为空");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单" + t_out_trade_no + "不存在！");
            return JsonUtil.getJson(-2, "订单不存在");
        }
        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        // 根据支付状态返回："0,未完成","1,已提交","2,支付成功","3,支付失败","4,已关闭"，"5,已撤消"，"6,已退款"
        if (paymentRecord.getState().equals(PaymentState.CLOSED.getCode())) {
            return JsonUtil.getJson(-4, "订单已关闭");
        }
        if (paymentRecord.getState().equals(PaymentState.CANCEL.getCode())) {
            return JsonUtil.getJson(-5, "订单已撤消");
        }
        if (paymentRecord.getState().equals(PaymentState.REFUND.getCode())) {
            return JsonUtil.getJson(-6, "订单已退款");
        }
        // 创建查询请求builder，设置请求参数
        String mchId = item.getThirdPartySubaccount();
        String outTradeNo = t_partner + t_out_trade_no;
        OrderQueryRequestBuilder rb = new OrderQueryRequestBuilder();
        rb.setMch_id(mchId);
        rb.setOut_trade_no(outTradeNo);
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
                        return JsonUtil.getJson(0, "支付成功");
                    } else if ("NOTPAY".equalsIgnoreCase(trade_state)) {
                        return JsonUtil.getJson(1, "未支付");
                    } else if ("USERPAYING".equalsIgnoreCase(trade_state)) {
                        return JsonUtil.getJson(4, "支付处理中");
                    } else if ("CLOSED".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.CLOSED.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(-3, "已关闭");
                    } else if ("REVOKED".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.CANCEL.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(-4, "已撤销");
                    } else if ("REFUND".equalsIgnoreCase(trade_state)) {
                        paymentRecord.setState(PaymentState.REFUND.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(-5, "已退款");
                    } else if ("PAYERROR".equalsIgnoreCase(trade_state)) {
                        if (paymentRecord.getState().equals("1")) {
                            return JsonUtil.getJson(1, "未支付");
                        }
                        paymentRecord.setState(PaymentState.FAIL.getCode());
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(-6, "支付失败");
                    } else {
                        return JsonUtil.getJson(-1, result.getTrade_state_desc());
                    }
                } else {
                    return JsonUtil.getJson(-2, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-3, result.getReturn_msg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.getJson(-4, e.getMessage());
        }
    }

    /**
     * 关闭订单<br>
     * 以下情况需要调用关单接口：商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。<br>
     * 注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。<br>
     * 接口链接：https://api.mch.weixin.qq.com/pay/closeorder<br>
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 原支付请求的商户订单号不能为空
     * @param t_sign         签名
     * @return
     */
    public String WxpayCloseOrder(String t_partner, String t_out_trade_no, String t_sign) {
        log.info("WxpayCloseOrder t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (StringUtils.isEmpty(t_out_trade_no)) {
            return JsonUtil.getJson(-1, "原支付请求的商户订单号不能为空");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单" + t_out_trade_no + "不存在！");
            return JsonUtil.getJson(-2, "订单不存在");
        }
        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方(" + paymentRecord.getPartner() + ")未授权！");
            return JsonUtil.getJson(-3, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_partner", t_partner);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-4, "签名验证失败");
            }
        }

        String outTradeNo = t_partner + t_out_trade_no;
        CloseOrderRequestBuilder rb = new CloseOrderRequestBuilder();
        rb.setMch_id(item.getThirdPartySubaccount());
        rb.setOut_trade_no(outTradeNo);
        XMLClient client = new XMLClient();
        try {
            WxpayResult result = client.sendPost(WxpayConfig.closeorder, rb.buildXmlString());
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) { // SUCCESS
                    paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                    paymentDao.receivedPayment(paymentRecord);
                    return JsonUtil.getJson(0, "订单已撤销");
                } else { // FAIL
                    if ("ORDERPAID".equalsIgnoreCase(result.getErr_code())) {
                        return JsonUtil.getJson(-10, "订单已支付");
                    } else if ("ORDERREFUND".equalsIgnoreCase(result.getErr_code())) {
                        return JsonUtil.getJson(-11, "订单已退款");
                    } else {
                        return JsonUtil.getJson(-5, "[" + result.getErr_code() + "]" + result.getErr_code_des());
                    }
                }
            } else {
                return JsonUtil.getJson(-6, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-7, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-8, e.getMessage());
        }
    }

    /**
     * 申请退款：当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，微信支付将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。 <br>
     * 注意： <br>
     * 1、交易时间超过一年的订单无法提交退款；<br>
     * 2、微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额。<br>
     * 接口链接：https://api.mch.weixin.qq.com/secapi/pay/refund<br>
     * 问题：<br>
     * 1、需要双向证书
     *
     * @param t_partner       合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no  订单编号
     * @param t_refund_reason 退款原因
     * @param t_charset       字符集
     * @param t_version
     * @param t_sign          签名
     * @return
     */
    public String WxpayRefund(String t_partner, String t_out_trade_no, String t_refund_reason, String t_charset, String t_version, String t_sign) {
        log.info("WxpayRefund t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (StringUtils.isEmpty(t_partner)) {
            return JsonUtil.getJson(-1, "合作方不能为空");
        }
        if (StringUtils.isEmpty(t_out_trade_no)) {
            return JsonUtil.getJson(-2, "订单号不能为空");
        }
        if (StringUtils.isEmpty(t_refund_reason)) {
            return JsonUtil.getJson(-3, "退款原因不能为空");
        }
        if ("1.1".equalsIgnoreCase(t_version)) {
            t_refund_reason = DecodeBase64(t_refund_reason);
        }
        if (null != t_charset && t_charset.length() > 0) {
            try {
                t_refund_reason = TransformCharset(t_refund_reason, t_charset);
            } catch (UnsupportedEncodingException e) {
                log.error("字符编码转换错误！" + e.getMessage() + " " + t_charset, e);
                return JsonUtil.getJson(-4, "字符编码转换错误");
            }
        }
        if (t_refund_reason != null && t_refund_reason.length() > 256) {
            return JsonUtil.getJson(-5, "退款原因超长");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
            return JsonUtil.getJson(-6, "订单不存在");
        }
        // 付款金额
        String t_total_fee = paymentRecord.getTotalFee().toString();
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(t_partner);
        if (null == partner) {
            log.error("合作方未授权！t_partner=" + t_partner);
            return JsonUtil.getJson(-7, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_partner", t_partner);
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_refund_reason", t_refund_reason);
        signBulider.add("t_charset", t_charset);
        signBulider.add("t_version", t_version);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-8, "签名验证失败");
            }
        }

        // 非当天的订单不允许退款操作 add by dxj 20170703
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 系统当前时间
        Date currentDate = new Date();
        // 订单创建时间
        Date createDate = paymentRecord.getCreateDate();
        int compare = currentDate.compareTo(createDate);
        if (compare < 0) {
            log.error("订单创建时间不能大于系统当前时间！创建日期：" + sdf.format(createDate) + " 当前日期：" + sdf.format(currentDate));
            return JsonUtil.getJson(-9, "订单创建时间不能大于系统当前时间");
        }
        // 计算结束时间
        Calendar date = Calendar.getInstance();
        date.setTime(createDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
        Date endDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            endDate = df.parse(df.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        compare = currentDate.compareTo(endDate);
        if (compare >= 0) {
            log.warn("非当天订单不允许退款！创建日期：" + createDate + " 当前日期：" + currentDate);
            return JsonUtil.getJson(-10, "非当天订单不允许退款");
        }

        // 查找支付项目
        PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
        String mchId = paymentItem.getThirdPartySubaccount();
        // 创建退款请求builder，设置请求参数
        String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
        RefundRequestBuilder rb = new RefundRequestBuilder();
        rb.setMch_id(mchId); // 微信支付分配的商户号
        rb.setOut_refund_no(outTradeNo); // 用订单号作为退款单号可保证唯一
        rb.setOut_trade_no(outTradeNo);
        rb.setTotal_fee(t_total_fee); // 订单总金额，单位为分，只能为整数
        rb.setRefund_fee(t_total_fee); // 退款总金额，订单总金额，单位为分
        rb.setOp_user_id(t_partner); // 将合作方用作操作员

        XMLClient client = new XMLClient();
        WxpayResult result;
        try {
            result = client.sendPostSSL(WxpayConfig.refund, rb.buildXmlString(), mchId);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    paymentRecord.setState(PaymentState.REFUND.getCode());
                    paymentRecord.setRefundDate(new Date());
                    paymentRecord.setRefundDesc(t_refund_reason);
                    paymentDao.receivedPayment(paymentRecord);
                    return JsonUtil.getJson(0, "退款申请提交成功");
                } else {
                    return JsonUtil.getJson(-11, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-12, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-13, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-14, e.getMessage());
        }
    }

    /**
     * 交易保障<br>
     * 商户在调用微信支付提供的相关接口时，会得到微信支付返回的相关信息以及获得整个接口的响应时间。为提高整体的服务水平，协助商户一起提高服务质量，微信支付提供了相关接口调用耗时和返回信息的主动上报接口，微信支付可以根据商户侧上报的数据进一步优化网络部署，完善服务监控，和商户更好的协作为用户提供更好的业务体验。<br>
     * 接口地址 https://api.mch.weixin.qq.com/payitil/report<br>
     *
     * @param mch_id        商户号
     * @param interface_url 接口URL
     * @param execute_time  接口耗时情况，单位为毫秒
     * @param return_code   返回状态码
     * @param return_msg    返回信息
     * @param result_code   业务结果
     * @param err_code      错误代码
     * @param err_code_des  错误代码描述
     * @param out_trade_no  商户订单号
     * @param user_ip       发起接口调用时的机器IP
     * @return
     */
    public String WxpayReport(String mch_id, String interface_url, String out_trade_no, String execute_time, String return_code, String return_msg, String result_code, String err_code, String err_code_des, String user_ip) {
        String time = WxpayUtil.fromDate(new Date()); // 商户上报时间，系统时间，格式为yyyyMMddHHmmss
        ReportRequestBuilder rb = new ReportRequestBuilder();
        rb.setMch_id(mch_id);
        rb.setInterface_url(interface_url);
        rb.setExecute_time(execute_time);
        rb.setReturn_code(return_code);
        rb.setReturn_msg(return_msg);
        rb.setResult_code(result_code);
        rb.setErr_code(err_code);
        rb.setErr_code_des(err_code_des);
        rb.setOut_trade_no(out_trade_no);
        rb.setUser_ip(user_ip);
        rb.setTime(time);
        XMLClient client = new XMLClient();
        try {
            WxpayResult result = client.sendPost(WxpayConfig.report, rb.buildXmlString());
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    return JsonUtil.getJson(0, "SUCCESS");
                } else {
                    return JsonUtil.getJson(-1, "FAIL");
                }
            } else {
                return JsonUtil.getJson(-2, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-3, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-4, e.getMessage());
        }
    }

    /**
     * 下载对账单<br>
     * 商户可以通过该接口下载历史交易清单。比如掉单、系统错误等导致商户侧和微信侧数据不一致，通过对账单核对后可校正支付状态。<br>
     * 注意：<br>
     * 1、微信侧未成功下单的交易不会出现在对账单中。支付成功后撤销的交易会出现在对账单中，跟原支付单订单号一致，bill_type为REVOKED；<br>
     * 2、微信在次日9点启动生成前一天的对账单，建议商户10点后再获取；<br>
     * 3、对账单中涉及金额的字段单位为“元”。<br>
     * 4、对账单接口只能下载三个月以内的账单。<br>
     * 接口链接 https://api.mch.weixin.qq.com/pay/downloadbill<br>
     *
     * @param mch_id    商户号
     * @param bill_date 对账单日期：下载对账单的日期，格式：20140603
     * @param bill_type 账单类型：ALL，返回当日所有订单信息，默认值、SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单
     * @param tar_type  压缩账单：非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。
     * @return
     */
    public String WxpayDownloadBill(String mch_id, String bill_date, String bill_type, String tar_type) {
        DownloadBillRequestBuilder rb = new DownloadBillRequestBuilder();
        rb.setMch_id(mch_id);
        rb.setBill_date(bill_date);
        rb.setBill_type(bill_type);
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
                String bptbPayFilename = bill_date + "_" + mch_id + "_" + DateUtils.formatDate(new Date(), "yyyyMMddHHmmss") + ".csv";
                String bptbPayFilePath = TysfConfig.getBptbPayFilePath(bill_date) + bptbPayFilename;
                FileUtil.write(content, bptbPayFilePath, "GBK");
            } catch (Exception e) {
                log.error("微信下载对账文件保存失败！[" + mch_id + "][" + bill_date + "][" + bill_type + "][" + tar_type + "]");
            }
            if ("FAIL".equalsIgnoreCase(result.getReturn_code())) {
                return JsonUtil.getJson(-1, result.getReturn_msg());
            } else {
                return content;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-2, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-3, e.getMessage());
        }

        // 成功时，数据以文本表格的方式返回，第一行为表头，后面各行为对应的字段内容，字段内容跟查询订单或退款结果一致，具体字段说明可查阅相应接口。
        // 第一行为表头，根据请求下载的对账单类型不同而不同(由bill_type决定),目前有：
        //
        // 当日所有订单
        // 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额，退款类型，退款状态,商品名称,商户数据包,手续费,费率
        //
        // 当日成功支付的订单
        // 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,商品名称,商户数据包,手续费,费率
        //
        // 当日退款的订单
        // 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,退款申请时间,退款成功时间,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率
        //
        // 从第二行起，为数据记录，各参数以逗号分隔，参数前增加`符号，为标准键盘1左边键的字符，字段顺序与表头一致。
        // 倒数第二行为订单统计标题，最后一行为统计数据
        // 总交易单数，总交易额，总退款金额，总代金券或立减优惠退款金额，手续费总金额
        // 举例如下：
        // 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率
        // `2014-11-1016：33：45,`wx2421b1c4370ec43b,`10000100,`0,`1000,`1001690740201411100005734289,`1415640626,`085e9858e3ba5186aafcbaed1,`MICROPAY,`SUCCESS,`CFT,`CNY,`0.01,`0.0,`0,`0,`0,`0,`,`,`被扫支付测试,`订单额外描述,`0,`0.60%
        // `2014-11-1016：46：14,`wx2421b1c4370ec43b,`10000100,`0,`1000,`1002780740201411100005729794,`1415635270,`085e9858e90ca40c0b5aee463,`MICROPAY,`SUCCESS,`CFT,`CNY,`0.01,`0.0,`0,`0,`0,`0,`,`,`被扫支付测试,`订单额外描述,`0,`0.60%
        // 总交易单数,总交易额,总退款金额,总代金券或立减优惠退款金额,手续费总金额
        // `2,`0.02,`0.0,`0.0,`0
    }

    private String TransformCharset(String text, String charset) throws UnsupportedEncodingException {
        if (null == text)
            return null;
        byte[] bytes = text.getBytes(charset);
        return new String(bytes);
    }

    private String DecodeBase64(String text) {
        if (null == text) {
            return null;
        }
        return new String(Base64.decode(text));
    }

    public String GetErrorName(String errorCode) {
        if ("SUCCESS".equals(errorCode))
            return "订单交易成功";
        if ("FAILED".equals(errorCode))
            return "查询返回该订单支付失败或被关闭!!!";
        if ("SYSTEM_ERROR".equals(errorCode))
            return "系统错误，请重新发起请求";
        else
            return "不支持的交易状态，交易返回异常!!!";
    }

    public IPaymentDao getPaymentDao() {
        return paymentDao;
    }

    public void setPaymentDao(IPaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    /**
     * 撤销订单<br>
     * 此接口是对WxpayCloseOrder、WxpayRefund和WxpayReverse三个接口的合并处理<br>
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 原支付请求的商户订单号不能为空
     * @param t_sign         签名
     * @return
     */
    public String WxpayCancel(String t_partner, String t_out_trade_no, String t_sign) {
        log.info("WxpayCancel t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (StringUtils.isEmpty(t_out_trade_no)) {
            return JsonUtil.getJson(-1, "原支付请求的商户订单号不能为空");
        }

        // 执行查询订单，确定数据的状态是最新的
        String query_result = WxpayQueryOrder(t_partner, t_out_trade_no);
        log.info("WxpayCancel WxpayQueryOrder" + query_result);

        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单" + t_out_trade_no + "不存在！");
            return JsonUtil.getJson(-2, "订单不存在");
        }
        String totalFee = paymentRecord.getTotalFee().toString();
        // 查找支付项目
        PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
        String mchId = item.getThirdPartySubaccount();
        String paymentType = item.getPaymentType();
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方(" + paymentRecord.getPartner() + ")未授权！");
            return JsonUtil.getJson(-3, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_partner", t_partner);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-4, "签名验证失败");
            }
        }

        // 非当天的订单已经支付的不允许撤销操作 add by dxj 20180308
        if (paymentRecord.getState().equals(PaymentState.SUCCESS.getCode())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 系统当前时间
            Date currentDate = new Date();
            // 订单创建时间
            Date createDate = paymentRecord.getCreateDate();
            int compare = currentDate.compareTo(createDate);
            if (compare < 0) {
                log.error("订单创建时间不能大于系统当前时间！创建日期：" + sdf.format(createDate) + " 当前日期：" + sdf.format(currentDate));
                return JsonUtil.getJson(-9, "订单创建时间不能大于系统当前时间");
            }
            // 计算结束时间
            Calendar date = Calendar.getInstance();
            date.setTime(createDate);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
            Date endDate = null;
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                endDate = df.parse(df.format(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compare = currentDate.compareTo(endDate);
            if (compare >= 0) {
                log.warn("已支付成功的非当天订单不允许撤销！创建日期：" + createDate + " 当前日期：" + currentDate);
                return JsonUtil.getJson(-10, "已支付成功的非当天订单不允许撤销");
            }
        }

        // 创建关闭请求builder，设置请求参数
        String outTradeNo = t_partner + t_out_trade_no;
        XMLClient client = new XMLClient();
        try {
            if (paymentType.equals(PaymentType.WXSMSK.getCode())) { // 微信扫码收款
                // 撤销订单
                ReverseRequestBuilder rbReverse = new ReverseRequestBuilder();
                rbReverse.setMch_id(mchId);
                rbReverse.setOut_trade_no(outTradeNo);
                WxpayResult result = client.sendPostSSL(WxpayConfig.reverse, rbReverse.buildXmlString(), mchId);
                if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                    if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                        paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(0, "订单撤销成功");
                    } else {
                        return JsonUtil.getJson(-11, "[" + result.getErr_code() + "]" + result.getErr_code_des());
                    }
                } else {
                    return JsonUtil.getJson(-12, "订单撤销失败：" + result.getReturn_msg());
                }
            } else {
                CloseOrderRequestBuilder rbClose = new CloseOrderRequestBuilder();
                rbClose.setMch_id(mchId);
                rbClose.setOut_trade_no(outTradeNo);
                WxpayResult result = client.sendPost(WxpayConfig.closeorder, rbClose.buildXmlString());
                if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                    if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) { // SUCCESS
                        paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                        paymentDao.receivedPayment(paymentRecord);
                        return JsonUtil.getJson(0, "订单撤销成功");
                    } else { // FAIL
                        if ("ORDERPAID".equalsIgnoreCase(result.getErr_code())) {
                            // 如果已经支付，且当日订单直接退款，否则不允许撤销

                            // 当日订单直接退款
                            String refundReason = "订单撤销（订单已支付转退款申请）";
                            RefundRequestBuilder rbRefund = new RefundRequestBuilder();
                            rbRefund.setMch_id(mchId); // 微信支付分配的商户号
                            rbRefund.setOut_refund_no(outTradeNo); // 用订单号作为退款单号可保证唯一
                            rbRefund.setOut_trade_no(outTradeNo);
                            rbRefund.setTotal_fee(totalFee); // 订单总金额
                            rbRefund.setRefund_fee(totalFee); // 退款总金额
                            rbRefund.setOp_user_id(t_partner); // 将合作方用作操作员
                            result = client.sendPostSSL(WxpayConfig.refund, rbRefund.buildXmlString(), mchId);
                            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                                    paymentRecord.setState(PaymentState.REFUND.getCode());
                                    paymentRecord.setRefundDate(new Date());
                                    paymentRecord.setRefundDesc(refundReason);
                                    paymentDao.receivedPayment(paymentRecord);
                                    return JsonUtil.getJson(0, "退款申请提交成功");
                                } else {
                                    return JsonUtil.getJson(-21, "[" + result.getErr_code() + "]" + result.getErr_code_des());
                                }
                            } else {
                                return JsonUtil.getJson(-22, "订单已支付，退款申请失败：" + result.getReturn_msg());
                            }
                        } else if ("ORDERREFUND".equalsIgnoreCase(result.getErr_code())) {
                            return JsonUtil.getJson(0, "订单已退款");
                        } else {
                            return JsonUtil.getJson(-23, "[" + result.getErr_code() + "]" + result.getErr_code_des());
                        }
                    }
                } else {
                    return JsonUtil.getJson(-24, "订单关闭失败：" + result.getReturn_msg());
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-5, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-6, e.getMessage());
        }
    }

    /**
     * 企业付款到银行卡API <br>
     * 企业付款业务是基于微信支付商户平台的资金管理能力，为了协助商户方便地实现企业向银行卡付款，针对部分有开发能力的商户，提供通过API完成企业付款到银行卡的功能。 1、需要双向证书
     *
     * @param t_item      支付项目号
     * @param t_trade_no  商户企业付款单号
     * @param t_total_fee 付款金额（单位：元）
     * @param v_ymd       提款日期
     * @return
     */
    public String WxpayPayBank(String t_item, String t_trade_no, String t_total_fee, String v_ymd) {
        if (StringUtils.isEmpty(t_item)) {
            return JsonUtil.getJson(-1, "支付项目号不能为空");
        }
        if (StringUtils.isEmpty(t_trade_no)) {
            return JsonUtil.getJson(-2, "商户企业付款单号不能为空");
        }
        if (StringUtils.isEmpty(t_total_fee)) {
            return JsonUtil.getJson(-3, "付款金额不能为空");
        }
        //四舍五入保留2位小数
        //int amount = (int) (new BigDecimal(t_total_fee).doubleValue() * 100);
        int amount = (int) (new BigDecimal(t_total_fee).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
        if (amount < 0) {
            return JsonUtil.getJson(-4, "付款金额不能小于0");
        }
        PaymentItem paymentItem = paymentDao.getPaymentItem(t_item);
        if (null == paymentItem) {
            log.error("支付项目不存在！t_item=" + t_item);
            return JsonUtil.getJson(-5, "支付项目不存在");
        }
        String mchId = paymentItem.getThirdPartySubaccount(); // 获取商户号
        //String certPath = this.getClass().getResource("/").getPath() + "cert/";
        //String filename_der = certPath + "/" + mchId + ".der";// 秘钥加密文件
        String filename_der = TysfConfig.getCertPath(mchId + ".der");
        File file = new File(filename_der);
        if (!file.exists()) {// 如果秘钥文件不存在
            return JsonUtil.getJson(-6, "秘钥文件不存在");
        }
        // // 工商银行
        String enc_bank_no = paymentItem.getBankCode();
        if (StringUtils.isEmpty(enc_bank_no)) {
            return JsonUtil.getJson(-7, "项目收款银行不能为空！");
        }
        PaymentBank bank = paymentDao.getPaymentBank(enc_bank_no);
        String bank_code = bank.getBankNum();// 银行编号
        if (StringUtils.isEmpty(bank_code)) {
            return JsonUtil.getJson(-8, "银行编号不能为空，请联系管理员维护");
        }
        String enc_true_name = bank.getAcctName(); // 收款方用户名
        // 银行附言 对应银行回单摘要（格式为：999项目名称170201）
        String bank_note = paymentItem.getPartner().substring(1) + StringUtil.subString(paymentItem.getPaymentItemName(), 10) + StringUtil.right(v_ymd, 6);
        String desc = bank_note; // 付款说明
        try {
            enc_bank_no = RsaUtil.encode(filename_der, enc_bank_no);
            enc_true_name = RsaUtil.encode(filename_der, enc_true_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 付款
        PayBankRequestBuilder rb = new PayBankRequestBuilder();
        rb.setMch_id(mchId); // 微信支付分配的商户号
        rb.setPartner_trade_no(t_trade_no);
        rb.setAmount(amount); // 订单总金额，单位为分，只能为整数
        rb.setEnc_bank_no(enc_bank_no);// 收款方银行卡号
        rb.setEnc_true_name(enc_true_name);// 收款方用户名
        rb.setBank_code(bank_code);// 收款方开户行 银行卡所在开户行编号
        rb.setDesc(desc); // 付款说明
        // 下面为新接口参数
        rb.setAccount_type(Integer.valueOf(bank.getBankSign())); // 账户类型（1: 对公；2: 对私）
        if ("1".equals(bank.getBankSign()) && "1001".equals(bank_code)) {// 对公账户且是招行的时候，分行名称不能为空
            rb.setBank_branch_name(bank.getBankBranch());
        }
        rb.setBank_note(bank_note);// 银行附言
        try {
            XMLClient client = new XMLClient();
            WxpayResult result = client.sendPostSSL(WxpayConfig.pay_bank, rb.buildXmlString(), mchId);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    return JsonUtil.getJson(0, result.getPayment_no());
                } else {
                    return JsonUtil.getJson(-11, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-12, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-13, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-14, e.getMessage());
        }
    }

    /**
     * 查询企业付款银行卡API<br>
     * 用于对商户企业付款到银行卡操作进行结果查询，返回付款操作详细结果。
     *
     * @param t_item     支付项目号
     * @param t_trade_no 商户企业付款单号
     * @return
     */
    public String WxpayQueryBank(String t_item, String t_trade_no) {
        if (StringUtils.isEmpty(t_item)) {
            return JsonUtil.getJson(-1, "支付项目号不能为空");
        }
        if (StringUtils.isEmpty(t_trade_no)) {
            return JsonUtil.getJson(-2, "商户企业付款单号不能为空");
        }
        PaymentItem paymentItem = paymentDao.getPaymentItem(t_item);
        if (null == paymentItem) {
            log.error("支付项目不存在！t_item=" + t_item);
            return JsonUtil.getJson(-3, "支付项目不存在");
        }
        String mchId = paymentItem.getThirdPartySubaccount(); // 获取商户号
        // 查询
        QueryBankRequestBuilder rb = new QueryBankRequestBuilder();
        rb.setMch_id(mchId); // 微信支付分配的商户号
        rb.setPartner_trade_no(t_trade_no);
        try {
            XMLClient client = new XMLClient();
            WxpayResult result = client.sendPostSSL(WxpayConfig.query_bank, rb.buildXmlString(), mchId);
            if ("SUCCESS".equalsIgnoreCase(result.getReturn_code())) {
                if ("SUCCESS".equalsIgnoreCase(result.getResult_code())) {
                    return JsonUtil.getJson(0, result.getPayment_no());
                } else {
                    return JsonUtil.getJson(-11, result.getErr_code() + " - " + result.getErr_code_des());
                }
            } else {
                return JsonUtil.getJson(-12, result.getReturn_msg());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-13, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.getJson(-14, e.getMessage());
        }
    }
}
