package com.alipay.trade;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.config.AlipayWebConfig;

import cn.tsinghua.sftp.config.MoneyType;
import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.Invoice;
import cn.tsinghua.sftp.pojo.Partner;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.Base64;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SignBuilder;
import cn.tsinghua.sftp.util.SpringContextUtil;

/**
 * 支付宝当面付2.0服务
 * 
 * @author Created by liuyangkly on 15/8/9.
 */
public class AlipayTrade {
	private static Log log = LogFactory.getLog(AlipayTrade.class);

	/** 支付宝服务 */
	private static AlipayClient alipayClient;

	static {
		alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);
	}

	private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

	/**、
	 * 统一收单交易支付接口（条码支付）
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
	 * @param t_name 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
	 * @param t_auth_code 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
	 * @param t_total_fee 订单总金额，单位为元，不能超过1亿元
	 * @param t_summary 订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
	 * @param t_item 支付项目编号
	 * @param t_subitem 子项目编号
	 * @param t_fptt 发票抬头 多个发票，用|分割
	 * @param t_username 缴款人姓名
	 * @param t_user_id 缴款人证件编号
	 * @param t_user_id_type 缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
	 * @param t_return_url 页面跳转同步通知页面路径
	 * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
	 * @param t_datetime 当前时间
	 * @param t_timeout 超时时间,支付超时定义为120分钟（不能带单位）
	 * @param t_version 接口的版本号
	 * @param t_charset 编码格式
	 * @param t_sign 签名
	 * @return
	 */
	public String AlipayTradePay(String t_partner, String t_out_trade_no, String t_name, String t_auth_code, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version, String t_charset, String t_sign) {
		log.info("AlipayTradePay t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_name=" + t_name + ",t_auth_code=" + t_auth_code + ",t_item=" + t_item);
		if (StringUtils.isEmpty(t_auth_code)) {
			log.error("付款条码不能为空!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
			return JsonUtil.getJson(-100, "付款条码不能为空");
		}

		// 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
		int auth_code_prefix = Integer.valueOf(t_auth_code.substring(0, 2));
		int auth_code_length = t_auth_code.length();
		if (auth_code_prefix < 25 || auth_code_prefix > 30 || auth_code_length < 16 || auth_code_length > 24) {
			log.error("付款条码错误!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
			return JsonUtil.getJson(-100, "付款条码错误");
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
			log.error("付款金额不能为空!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
			return JsonUtil.getJson(-2, "付款金额错误");
		}

		// 解析参数
		PaymentRecord paymentRecord = new PaymentRecord();
		paymentRecord.setPartner(t_partner);
		paymentRecord.setOutTradeNo(t_out_trade_no);
		try {
			paymentRecord.setTotalFee(new BigDecimal(t_total_fee));
		} catch (Exception e2) {
			log.error("付款金额错误!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_total_fee=" + t_total_fee);
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
		if (!payType.equals(PaymentType.ZFBSMFK) && !payType.equals(PaymentType.ZFBSMSK)) {
			log.error("您已选择" + payType.getName() + "，不支持更换支付方式！");
			return JsonUtil.getJson(-23, "您已选择" + payType.getName() + "，不支持更换支付方式！");
		}
		paymentRecord.setPaymentType(payType.getCode());
		// 设定币种
		paymentRecord.setMoneyType(item.getMoneyType());
		paymentRecord.setAuthCode(t_auth_code);
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
		DecimalFormat df = new DecimalFormat("###############0");
		// 支付超时，线下扫码交易定义为5分钟
		if (StringUtils.isEmpty(t_timeout))
			t_timeout = "5m";
		if (!t_timeout.toLowerCase().endsWith("m"))
			t_timeout = t_timeout + "m";
		// 创建条码支付请求builder，设置请求参数
		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		AlipayTradePayRequest request = new AlipayTradePayRequest();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("out_trade_no", outTradeNo);
		params.put("scene", "bar_code");
		params.put("auth_code", t_auth_code);
		params.put("product_code", "FACE_TO_FACE_PAYMENT");
		params.put("subject", t_name);
		// params.put("buyer_id", "");
		// params.put("seller_id", "");// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号),如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		params.put("total_amount", new BigDecimal(t_total_fee));
		// params.put("discountable_amount", 0);
		params.put("body", t_summary);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> goods = new HashMap<String, Object>();
		goods.put("goods_id", t_item);
		goods.put("goods_name", (t_summary == null || "".equals(t_summary)) ? t_name : t_summary);
		goods.put("quantity", 1);
		goods.put("price", Long.parseLong(df.format(Double.parseDouble(t_total_fee) * 100)));
		goods.put("body", t_summary);
		// goods.put("show_url", "");
		list.add(goods);
		params.put("goods_detail", list);
		params.put("operator_id", t_item);
		params.put("store_id", "0");// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		// params.put("terminal_id", "");
		Map<String, Object> extend_params = new HashMap<String, Object>();
		extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
		params.put("extend_params", extend_params);
		params.put("timeout_express", t_timeout);
		request.setBizContent(JsonUtil.getJson(params));
		request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);
		try {
			log.debug("订单[" + outTradeNo + "]调起支付宝：" + request.getBizContent());
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
				return JsonUtil.getJson(0, "支付成功");
			} else if (response.isSuccess() && "10003".equals(response.getCode())) {
				log.debug("订单[" + outTradeNo + "]支付处理中" + String.format("，(%s) %s", response.getCode(), response.getMsg()));
				return JsonUtil.getJson(43101, "支付处理中");
			} else {
				String payInfo = "支付失败";
				if (response != null) {
					payInfo += String.format("，(%s) %s", response.getCode(), response.getMsg());
					if (StringUtils.isNotEmpty(response.getSubCode())) {
						payInfo += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
					}
				}
				log.debug("订单[" + outTradeNo + "]" + payInfo);
				return JsonUtil.getJson(-25, payInfo);
			}
		} catch (AlipayApiException e) {
			log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
			return JsonUtil.getJson(-26, e.getErrCode() + e.getErrMsg());
		}

	}

	/**
	 * 统一收单线下交易预创建（扫码支付）
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
	 * @param t_name 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
	 * @param t_total_fee 订单总金额，单位为元，不能超过1亿元
	 * @param t_summary 订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
	 * @param t_item 支付项目编号
	 * @param t_subitem 子项目编号
	 * @param t_fptt 发票抬头 多个发票，用|分割
	 * @param t_username 缴款人姓名
	 * @param t_user_id 缴款人证件编号
	 * @param t_user_id_type 缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
	 * @param t_return_url 页面跳转同步通知页面路径
	 * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
	 * @param t_datetime 当前时间
	 * @param t_timeout 超时时间,支付超时定义为120分钟（不能带单位）
	 * @param t_version 接口的版本号
	 * @param t_charset 编码格式
	 * @param t_sign 签名
	 * @return
	 */
	public String AlipayTradePrecreate(String t_partner, String t_out_trade_no, String t_name, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version, String t_charset, String t_sign) {
		log.info("AlipayTradePrecreate t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_name=" + t_name + ",t_total_fee=" + t_total_fee + ",t_item=" + t_item);
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
		if (!payType.equals(PaymentType.ZFBSMFK)) {
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

		// (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		// 发起订单
		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		String timeout_express = t_timeout.toLowerCase().endsWith("m") ? t_timeout : t_timeout + "m";

		// 获取返回信息
		String error = "";
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("out_trade_no", outTradeNo);
		params.put("seller_id", "");// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号),如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
		params.put("total_amount", new BigDecimal(t_total_fee));
		params.put("subject", t_name);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> goods = new HashMap<String, Object>();
		goods.put("goods_id", t_item);
		goods.put("goods_name", (t_summary == null || "".equals(t_summary)) ? t_name : t_summary);
		goods.put("quantity", 1);
		goods.put("price", new BigDecimal(t_total_fee));
		goods.put("body", t_summary);
		list.add(goods);
		params.put("goods_detail", list);
		params.put("body", t_summary);
		params.put("operator_id", t_item);// 对账时用到
		params.put("store_id", "0");// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
		Map<String, Object> extend_params = new HashMap<String, Object>();
		extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
		params.put("extend_params", extend_params);
		params.put("timeout_express", timeout_express);
		// params.put("business_params", "");
		String bizcontent = JsonUtil.getJson(params);
		System.out.println("bizcontent>>" + bizcontent);
		request.setBizContent(bizcontent);
		request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);
		try {
			AlipayTradePrecreateResponse response = alipayClient.execute(request);
			if (response.isSuccess()) {
				log.info("支付宝预下单成功: )");
				paymentRecord.setState(PaymentState.SUBMITTED.getCode());
				paymentDao.receivedPayment(paymentRecord);
				if (log.isDebugEnabled()) {
					DumpResponse(response);
				}
				String qrCode = response.getQrCode();
				return JsonUtil.getJson(0, qrCode);
			} else {
				error = "支付宝预下单失败";
				if (response != null) {
					error += String.format("，(%s) %s", response.getCode(), response.getMsg());
					if (StringUtils.isNotEmpty(response.getSubCode())) {
						error += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
					}
				}
				log.warn(error);
				return JsonUtil.getJson(-1, error);
			}
		} catch (AlipayApiException e) {
			error = "支付宝预下单失败";
			error += String.format("，(%s) %s", e.getErrCode(), e.getErrMsg());
			log.error(error, e);
			return JsonUtil.getJson(-1, error);
		}
	}

	/**
	 * 统一收单线下交易查询
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 商户订单号，通过此商户订单号查询当面付的交易状态(必填)
	 */
	public String AlipayTradeQuery(String t_partner, String t_out_trade_no) {
		log.info("AlipayTradeQuery t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
		if (null == t_out_trade_no || t_out_trade_no.trim().length() == 0) {
			return JsonUtil.getJson(-1, "订单号不能为空");
		}
		PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
		if (null == paymentRecord) {
			log.error("订单" + t_out_trade_no + "不存在！");
			return JsonUtil.getJson(-2, "订单不存在");
		}
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
		// 只有未完成或已提交状态的订单才判断超时问题，否则可能把已支付成功的订单退掉了
		if (paymentRecord.getState().equals(PaymentState.UNDONE.getCode()) || paymentRecord.getState().equals(PaymentState.SUBMITTED.getCode())) {
			String t_timeout = paymentRecord.getTimeout();
			if (t_timeout != null) {
				if (t_timeout.toLowerCase().endsWith("m"))
					t_timeout = t_timeout.substring(0, t_timeout.length() - 1);
				if (!DateUtils.getMinuteAfter(paymentRecord.getCreateDate(), Integer.parseInt(t_timeout)).after(new Date())) {
					// 读取合作方密钥
					Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
					if (null == partner) {
						log.error("合作方(" + paymentRecord.getPartner() + ")未授权！");
						return JsonUtil.getJson(-9, "合作方未授权 ");
					}
					// 生成签名
					String partnerKey = partner.getDecodeKey();
					SignBuilder signBulider = new SignBuilder();
					signBulider.add("t_out_trade_no", t_out_trade_no);
					signBulider.add("t_partner", t_partner);
					String t_sign = signBulider.createSign(partnerKey, "GBK");
					// 撤消订单
					AlipayTradeCancel(t_partner, t_out_trade_no, t_sign);
					return JsonUtil.getJson(-10, "订单支付超时");
				}
			}
		}

		// 创建查询请求builder，设置请求参数
		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"\"" + "  }");
		try {
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
					// throw new SfptException("金额不一致!!! " + total_fee + " " + paymentRecord);
					return JsonUtil.getJson(-6, "金额不一致");
				}
				// (trade_status)交易状态：
				// WAIT_BUYER_PAY（交易创建，等待买家付款）
				// TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
				// TRADE_SUCCESS（交易支付成功）
				// TRADE_FINISHED（交易结束，不可退款）
				if (!"TRADE_SUCCESS".equalsIgnoreCase(trade_status)) {// 如果支付不成功就返回
					try {
						if ("TRADE_CLOSED".equals(trade_status) && paymentRecord.getState().equals(PaymentState.REFUND.getCode())) {
							return JsonUtil.getJson(-1, "订单已退款");
						} else if ("TRADE_CLOSED".equals(trade_status)) {
							paymentRecord.setState(PaymentState.CLOSED.getCode());
							paymentDao.receivedPayment(paymentRecord);
							return JsonUtil.getJson(-2, "订单已关闭");
						} else if ("TRADE_CANCEL".equals(trade_status)) { // 哪儿来的???
							paymentRecord.setState(PaymentState.CANCEL.getCode());
							paymentDao.receivedPayment(paymentRecord);
							return JsonUtil.getJson(-3, "订单已撤销");
						} else if ("WAIT_BUYER_PAY".equals(trade_status)) {
							paymentRecord.setState(PaymentState.SUBMITTED.getCode());
							paymentDao.receivedPayment(paymentRecord);
							return JsonUtil.getJson(4, "支付处理中");
						} else if ("TRADE_FINISHED".equals(trade_status)) {
							return JsonUtil.getJson(-5, "交易结束，不可退款");
						}
					} catch (Exception e2) {
						log.debug("交易状态码[" + trade_status + "]转换失败！" + e2.getMessage(), e2);
					}
					return JsonUtil.getJson(-7, "未支付成功(" + trade_status + ")");
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
					return JsonUtil.getJson(0, "支付成功");
				} else {
					log.error("更新支付记录失败! " + updateRownum);
					// throw new SfptException("更新支付记录失败! " + updateRownum);
					return JsonUtil.getJson(-10, "支付记录更新失败");
				}
			} else {
				String subcode = response.getSubCode();
				if ("ACQ.TRADE_NOT_EXIST".equals(subcode) && paymentRecord.getState().equals(PaymentState.SUCCESS.getCode())) {
					paymentRecord.setState(PaymentState.UNDONE.getCode());
					paymentDao.receivedPayment(paymentRecord);
				}
				// 错误编码转名称
				String errorname = paymentDao.getSysParamsValue("alipay_error_code", subcode);
				return JsonUtil.getJson(-1, errorname);
			}
		} catch (AlipayApiException e) {
			return JsonUtil.getJson(-7, "系统异常" + e.getErrCode() + e.getErrMsg());
		}
	}

	/**
	 * 统一收单交易撤销接口<br>
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 原支付请求的商户订单号不能为空
	 * @param t_sign 签名
	 * @return
	 */
	public String AlipayTradeCancel(String t_partner, String t_out_trade_no, String t_sign) {
		log.info("AlipayTradeCancel t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
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
		signBulider.add("t_out_trade_no", t_out_trade_no);
		signBulider.add("t_partner", t_partner);
		String rSign = signBulider.createSign(partnerKey, "GBK");
		if (!rSign.equals(t_sign)) {
			log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
			if (TysfConfig.IsCheckSign()) {
				return JsonUtil.getJson(-5, "签名验证失败");
			}
		}

		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
		// String bizContent = String.format("{\"out_trade_no\":\"%s\",\"trade_no\":\"%s\"}", t_out_trade_no, "");
		String bizContent = String.format("{\"out_trade_no\":\"%s\",\"trade_no\":\"%s\"}", outTradeNo, "");
		request.setBizContent(bizContent);
		try {
			AlipayTradeCancelResponse response = alipayClient.execute(request);
			if (log.isDebugEnabled()) {
				DumpResponse(response);
			}
			if (response.isSuccess() && "10000".equals(response.getCode())) {
				paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
				paymentDao.receivedPayment(paymentRecord);
				return JsonUtil.getJson(0, "订单撤销成功");
			} else {
				String msg = response.getMsg();
				String subMsg = response.getSubMsg();
				return JsonUtil.getJson(-101, StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
			}
		} catch (AlipayApiException e) {
			log.error("订单撤销失败！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no, e);
			return JsonUtil.getJson(-102, e.getErrCode() + "-" + e.getErrMsg());
		}
	}

	/**
	 * 统一收单交易退款接口
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 订单编号
	 * @param t_refund_reason 退款原因
	 * @param t_charset 字符集
	 * @param t_version
	 * @param t_sign 签名
	 * @return
	 */
	public String AlipayTradeRefund(String t_partner, String t_out_trade_no, String t_refund_reason, String t_charset, String t_version, String t_sign) {
		log.info("AlipayTradeRefund t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
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
			return JsonUtil.getJson(-7, "订单不存在");
		}
		// 退款金额，该金额不能大于订单金额,单位为元，支持两位小数
		// 读取合作方密钥
		Partner partner = paymentDao.getPartner(t_partner);
		if (null == partner) {
			log.error("合作方未授权！t_partner=" + t_partner);
			return JsonUtil.getJson(-8, "合作方未授权 ");
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
				return JsonUtil.getJson(-9, "签名验证失败");
			}
		}

		// 非当天的订单不允许退款操作 add by dxj 20170703
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 系统当前时间
		Date currentDate = new Date();
		// 订单创建时间
		Date createDate = paymentRecord.getCreateDate();
		log.debug("订单创建日期：" + sdf.format(createDate) + "，系统当前日期：" + sdf.format(currentDate));
		int compare = currentDate.compareTo(createDate);
		if (compare < 0) {
			log.error("订单创建时间不能大于系统当前时间！创建日期：" + sdf.format(createDate) + " 当前日期：" + sdf.format(currentDate));
			return JsonUtil.getJson(-10, "订单创建时间不能大于系统当前时间");
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
			log.error("日期格式转换失败!", e);
		}
		compare = currentDate.compareTo(endDate);
		if (compare >= 0) {
			log.warn("非当天订单不允许退款！创建日期：" + createDate + " 当前日期：" + currentDate);
			return JsonUtil.getJson(-11, "非当天订单不允许退款");
		}

		// 创建退款请求builder，设置请求参数
		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("out_trade_no", outTradeNo);
		params.put("trade_no", "");
		// params.put("out_request_no", outTradeNo + "02");
		params.put("refund_amount", paymentRecord.getTotalFee());
		params.put("refund_reason", "正常退款");
		request.setBizContent(JsonUtil.getJson(params));
		try {
			AlipayTradeRefundResponse response = alipayClient.execute(request);
			if (response.isSuccess() && "10000".equals(response.getCode())) {
				paymentRecord.setState(PaymentState.REFUND.getCode());
				paymentRecord.setRefundDate(new Date());
				paymentRecord.setRefundDesc(t_refund_reason);
				paymentDao.receivedPayment(paymentRecord);
				return JsonUtil.getJson(0, "退款成功");
			} else {
				String msg = response.getMsg();
				String subMsg = response.getSubMsg();
				return JsonUtil.getJson(-101, StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
			}
		} catch (AlipayApiException e) {
			return JsonUtil.getJson(-102, e.getErrCode() + e.getErrMsg());
		}
	}

	/**
	 * 统一收单交易退款查询接口
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 订单号
	 * @param t_out_request_no 退款单号
	 * @param t_sign 签名
	 * @return
	 */
	public String AlipayTradeRefundQuery(String t_partner, String t_out_trade_no, String t_out_request_no, String t_sign) {
		log.info("AlipayTradeRefund t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_out_request_no=" + t_out_request_no);
		if (StringUtils.isEmpty(t_partner)) {
			return JsonUtil.getJson(-1, "合作方不能为空");
		}
		if (StringUtils.isEmpty(t_out_trade_no)) {
			return JsonUtil.getJson(-2, "订单号不能为空");
		}
		if (StringUtils.isEmpty(t_out_request_no)) {
			return JsonUtil.getJson(-3, "退款单号不能为空");
		}
		PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
		if (null == paymentRecord) {
			log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
			return JsonUtil.getJson(-7, "订单不存在");
		}
		// 退款金额，该金额不能大于订单金额,单位为元，支持两位小数
		// 读取合作方密钥
		Partner partner = paymentDao.getPartner(t_partner);
		if (null == partner) {
			log.error("合作方未授权！t_partner=" + t_partner);
			return JsonUtil.getJson(-8, "合作方未授权 ");
		}
		// 验证签名
		String partnerKey = partner.getDecodeKey();
		SignBuilder signBulider = new SignBuilder();
		signBulider.add("t_partner", t_partner);
		signBulider.add("t_out_trade_no", t_out_trade_no);
		signBulider.add("t_out_request_no", t_out_request_no);
		String rSign = signBulider.createSign(partnerKey, "GBK");
		if (!rSign.equals(t_sign)) {
			log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no + ":" + t_out_request_no);
			if (TysfConfig.IsCheckSign()) {
				return JsonUtil.getJson(-9, "签名验证失败");
			}
		}
		// 创建退款请求builder，设置请求参数
		String out_trade_no = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		String out_request_no = t_partner + t_out_request_no; //
		AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
		AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
		model.setOutTradeNo(out_trade_no);
		model.setTradeNo("");
		model.setOutRequestNo(out_request_no);
		request.setBizModel(model);
		try {
			AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
			if (response.isSuccess() && "10000".equals(response.getCode())) {
				// 商户可使用该接口查询自已通过alipay.trade.refund提交的退款请求是否执行成功。
				// 该接口的返回码10000，仅代表本次查询操作成功，不代表退款成功。如果该接口返回了查询数据，则代表退款成功，
				// 如果没有查询到则代表未退款成功，可以调用退款接口进行重试。重试时请务必保证退款请求号一致。
				String refundAmount = response.getRefundAmount();
				String totalAmount = response.getTotalAmount();
				if (Double.parseDouble(totalAmount) == paymentRecord.getTotalFee().doubleValue()) {
					// 注意：等退款记录分开后，此处需要改为退款记录的金额 *********************************
					if (Double.parseDouble(refundAmount) == paymentRecord.getTotalFee().doubleValue()) {
						return JsonUtil.getJson(0, "退款成功");
					} else {
						return JsonUtil.getJson(-10, "退款失败，退款金额不一致");
					}
				} else {
					return JsonUtil.getJson(-11, "退款失败，定单金额不一致");
				}
			} else {
				String msg = response.getMsg();
				String subMsg = response.getSubMsg();
				return JsonUtil.getJson(-101, StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
			}
		} catch (AlipayApiException e) {
			return JsonUtil.getJson(-102, e.getErrCode() + e.getErrMsg());
		}
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

	/**
	 * 下载对支付宝账单
	 * 
	 * @param bill_type 账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型：trade、signcustomer；trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单；
	 * @param bill_date 账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM。
	 * @return 账单下载地址链接，获取连接后30秒后未下载，链接地址失效。
	 */
	public String queryAlipayBill(String bill_type, String bill_date) {
		log.debug("下载支付宝对账单：queryAlipayBill('" + bill_type + "','" + bill_date + "')");
		if (StringUtils.isEmpty(bill_type)) {
			bill_type = "trade";
		}
		AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
		request.setBizContent("{" + "\"bill_type\":\"" + bill_type + "\"," + "\"bill_date\":\"" + bill_date + "\"" + "  }");
		try {
			AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
			if (log.isInfoEnabled()) {
				DumpResponse(response);
			}
			if (response.isSuccess()) {
				String url = response.getBillDownloadUrl();
				return JsonUtil.getJson(0, url);
			} else {
				return JsonUtil.getJson(-1, "[" + response.getCode() + "]" + response.getMsg() + ", [" + response.getSubCode() + "]" + response.getSubMsg());
			}
		} catch (AlipayApiException e) {
			log.error(e.getMessage(), e);
			return JsonUtil.getJson(-2, e.getMessage());
		}
	}

	/**
	 * 打印Response信息
	 * 
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

	public static void main(String[] arg) {
		// AlipayTrade t = new AlipayTrade();
		// t.AlipayTradeQuery("2254", "ZZBK03CSS20170625133333");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("err", 0);
		map.put("msg", "Tips");
		if (!map.get("err").equals(0)) {
			log.warn("Err: " + map);
			return;
		}
		log.debug("Success");
	}

}
