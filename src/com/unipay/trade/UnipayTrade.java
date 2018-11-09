package com.unipay.trade;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unipay.builder.TradeQueryRequestBuilder;
import com.unipay.config.UnipayConfig;
import com.unipay.model.QueryOrder;
import com.unipay.model.QueryOrderResult;
import com.unipay.utils.HttpClientUtil;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SpringContextUtil;

public class UnipayTrade {
	private static Log log = LogFactory.getLog(UnipayTrade.class);

	public static void main(String[] args) {

	}

	/**
	 * 订单查询接口
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 商户订单号，通过此商户订单号查询当面付的交易状态(必填)
	 */
	public String TradeQuery(String t_partner, String t_out_trade_no) {
		log.info("TradeQuery t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
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

		// 创建查询请求builder，设置请求参数
		String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		String paymentType = paymentRecord.getPaymentType();
		String posId = UnipayConfig.getPosId(paymentType); // 柜台号
		TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder();
		builder.setPosid(posId);
		builder.setOrderid(outTradeNo);
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
			return JsonUtil.getJson(-11, "无数据");
		}

		String returnCode = result.getReturnCode();
		String returnMsg = result.getReturnMsg();

		// <?xml version = "1.0" encoding="UTF-8" ?>
		// <DOCUMENT>
		// <RETURN_CODE>0130Z110C054</RETURN_CODE>
		// <RETURN_MSG>密码不符，请重新输入[您输入的密码是：]</RETURN_MSG>
		// </DOCUMENT>
		if (!"000000".equals(returnCode)) {
			return JsonUtil.getJson(-12, returnCode + " - " + returnMsg);
		}

		// <?xml version = "1.0" encoding="UTF-8" ?>
		// <DOCUMENT>
		// <RETURN_CODE>000000</RETURN_CODE>
		// <RETURN_MSG></RETURN_MSG>
		// <CURPAGE>1</CURPAGE>
		// <PAGECOUNT>1</PAGECOUNT>
		// <TOTAL>1</TOTAL>
		// <PAYAMOUNT>0.00</PAYAMOUNT>
		// <REFUNDAMOUNT>0.00</REFUNDAMOUNT>
		// <QUERYORDER>
		// <MERCHANTID>105100000012072</MERCHANTID>
		// <BRANCHID>110000000</BRANCHID>
		// <POSID>004161878</POSID>
		// <ORDERID>2999DEMO20171110144132</ORDERID>
		// <ORDERDATE>20171110144235</ORDERDATE>
		// <ACCDATE>20171110</ACCDATE>
		// <AMOUNT>0.01</AMOUNT>
		// <STATUSCODE>1</STATUSCODE>
		// <STATUS>成功</STATUS>
		// <REFUND>0.00</REFUND>
		// <SIGN>99584bd5e663ecba113edf45a6dfa536e79368734bdc2554ff9ef44a9b5bec680ca7752aae446681d6218b25372e1a7b470e31c960679535f97c38c9e45413b15aa02934bd1c9ebc8a0a48031459be7a9b8097e4653892d4dc16bd237f172a64056d7a3416ec97deb7d59d2bf700e232c031dc585de1563864c019a0288ab4d9</SIGN>
		// </QUERYORDER>
		// </DOCUMENT>

		QueryOrder queryOrder = result.getOrderList().get(0);
		if (queryOrder.checkSign()) {
			return JsonUtil.getJson(-13, "签名验证失败");
		}
		String orderId = queryOrder.getOrderId(); // 订单号
		String orderDate = queryOrder.getOrderDate(); // 支付/退款交易时间
		// String accDate = queryOrder.getAccDate(); // 记账日期
		BigDecimal amount = queryOrder.getAmount(); // 支付金额
		String statusCode = queryOrder.getStatusCode(); // 支付/退款状态码
		String status = queryOrder.getStatus(); // 支付/退款状态
		BigDecimal refund = queryOrder.getRefund(); // 退款金额 ，这里显示已退款金额可用于部分退款的判断

		if (!orderId.equals(paymentRecord.getPartner() + paymentRecord.getOutTradeNo())) {
			return JsonUtil.getJson(-14, "订单号不一致");
		}
		if (amount.doubleValue() != paymentRecord.getTotalFee().doubleValue()) {
			return JsonUtil.getJson(-15, "订单金额不一致");
		}

		if ("0".equals(statusCode)) {
			// 支付失败
			paymentRecord.setState(PaymentState.FAIL.getCode());
			paymentDao.receivedPayment(paymentRecord);
			return JsonUtil.getJson(-3, "订单支付失败");

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
			return JsonUtil.getJson(0, "订单支付成功");

		} else if ("2".equals(statusCode) || "5".equals(statusCode)) {
			// 不确定交易，支付不确定
			return JsonUtil.getJson(-16, statusCode + " - " + status);

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
			return JsonUtil.getJson(-5, "已退款");

		} else {
			return JsonUtil.getJson(-19, statusCode + " - " + status);
		}
	}

	/**
	 * 下载银联对账单
	 * 
	 * @param paymentType 支付类型
	 * @param orderDate 账单日期
	 * @param billType 账单类型：0支付流水， 1退款流水
	 * @param billPage 查询页码
	 * @return 返回XML格式对账单内容
	 */
	public String DownloadBill(String paymentType, String orderDate, String billType, String billPage) {
		log.debug("下载银联对账单：DownloadBill('" + paymentType + "','" + orderDate + "','" + billType + "','" + billPage + "')");
		if (StringUtils.isEmpty(paymentType)) {
			return JsonUtil.getJson(-1, "支付类型不能为空");
		}
		if (null == orderDate || orderDate.trim().length() == 0) {
			return JsonUtil.getJson(-2, "对账日期不能为空");
		}
		if (null == billType || billType.trim().length() == 0) {
			return JsonUtil.getJson(-3, "账单类型不能为空");
		}
		if (null == billPage || billPage.trim().length() == 0 || Integer.parseInt(billPage) < 1) {
			return JsonUtil.getJson(-4, "查询页码不能小于1");
		}

		// 创建查询请求builder，设置请求参数
		String posId = UnipayConfig.getPosId(paymentType); // 柜台号
		TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder();
		builder.setPosid(posId); // 柜台号
		builder.setOrderdate(orderDate); // 定单日期
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
			return JsonUtil.getJson(-5, "对账单下载失败");
		}

		return xml;
	}

//	/**
//	 * 下单支付接口
//	 *
//	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
//	 * @param t_out_trade_no 订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
//	 * @param t_name 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
//	 * @param t_auth_code 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
//	 * @param t_total_fee 订单总金额，单位为元，不能超过1亿元
//	 * @param t_summary 订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//	 * @param t_item 支付项目编号
//	 * @param t_subitem 子项目编号
//	 * @param t_fptt 发票抬头 多个发票，用|分割
//	 * @param t_username 缴款人姓名
//	 * @param t_user_id 缴款人证件编号
//	 * @param t_user_id_type 缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
//	 * @param t_return_url 页面跳转同步通知页面路径
//	 * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
//	 * @param t_datetime 当前时间
//	 * @param t_timeout 超时时间,支付超时定义为120分钟（不能带单位）
//	 * @param t_version 接口的版本号
//	 * @param t_charset 编码格式
//	 * @param t_sign 签名
//	 * @return
//	 */
	// public String TradePay(String t_partner, String t_out_trade_no, String t_name, String t_auth_code, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version,
	// String t_charset, String t_sign) {
	// if (StringUtils.isEmpty(t_auth_code)) {
	// log.error("付款条码不能为空!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
	// return JsonUtil.getJson(-100, "付款条码不能为空");
	// }
	//
	// // 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
	// int auth_code_prefix = Integer.valueOf(t_auth_code.substring(0, 2));
	// int auth_code_length = t_auth_code.length();
	// if (auth_code_prefix < 25 || auth_code_prefix > 30 || auth_code_length < 16 || auth_code_length > 24) {
	// log.error("付款条码错误!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
	// return JsonUtil.getJson(-100, "付款条码错误");
	// }
	//
	// if (StringUtils.isNotEmpty(t_version)) {
	// try {
	// Float version = Float.parseFloat(t_version);
	// if (version == 1.1) {
	// t_fptt = DecodeBase64(t_fptt);
	// t_username = DecodeBase64(t_username);
	// t_name = DecodeBase64(t_name);
	// t_summary = DecodeBase64(t_summary);
	// t_extra_common_param = DecodeBase64(t_extra_common_param);
	// log.info("Base64.decode " + t_fptt + " " + t_name + " " + t_extra_common_param);
	// }
	// } catch (Exception e) {
	// log.error("版本号错误!!! " + e.getMessage() + " " + t_version, e);
	// return JsonUtil.getJson(-1, "版本号错误，" + e.getMessage());
	// }
	// }
	//
	// // 如果指定了字符集，按指定字符集进行转换
	// if (null != t_charset && t_charset.length() > 0) {
	// try {
	// t_fptt = TransformCharset(t_fptt, t_charset);
	// t_username = TransformCharset(t_username, t_charset);
	// t_name = TransformCharset(t_name, t_charset);
	// t_summary = TransformCharset(t_summary, t_charset);
	// t_extra_common_param = TransformCharset(t_extra_common_param, t_charset);
	// } catch (UnsupportedEncodingException e) {
	// log.error("字符编码转换错误!!! " + e.getMessage() + " " + t_charset, e);
	// return JsonUtil.getJson(-1, "字符编码转换错误，" + e.getMessage());
	// }
	// }
	//
	// if (t_total_fee == null) {
	// log.error("付款金额不能为空!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
	// return JsonUtil.getJson(-2, "付款金额错误");
	// }
	//
	// // 解析参数
	// PaymentRecord paymentRecord = new PaymentRecord();
	// paymentRecord.setPartner(t_partner);
	// paymentRecord.setOutTradeNo(t_out_trade_no);
	// try {
	// paymentRecord.setTotalFee(new BigDecimal(t_total_fee));
	// } catch (Exception e2) {
	// log.error("付款金额错误!!! t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no + ",t_total_fee=" + t_total_fee);
	// return JsonUtil.getJson(-2, "付款金额错误");
	// }
	//
	// paymentRecord.setTradeName(t_name);
	// paymentRecord.setSummary(t_summary);
	// paymentRecord.setItem(t_item);
	// // 保存超时时间时，不能带单位，否则处理超时数据会出错
	// paymentRecord.setTimeout(t_timeout.toLowerCase().endsWith("m") ? t_timeout.substring(0, t_timeout.length() - 1) : t_timeout);
	//
	// if (null != t_subitem && t_subitem.trim().length() > 0)
	// paymentRecord.setSubItem(t_subitem);
	// else
	// paymentRecord.setSubItem("0");
	//
	// paymentRecord.setState(PaymentState.UNDONE.getCode());
	// paymentRecord.setCreateDate(new Date());
	// paymentRecord.setReturnUrl(t_return_url);
	// paymentRecord.setUserName(t_username);
	// paymentRecord.setUserId(t_user_id);
	// paymentRecord.setUserIdType(t_user_id_type);
	// paymentRecord.setExtraCommonParam(t_extra_common_param);
	// if (null == paymentRecord.getOutTradeNo() || paymentRecord.getOutTradeNo().trim().length() == 0) {
	// return JsonUtil.getJson(-3, "订单号不能为空");
	// }
	// if (paymentRecord.getOutTradeNo().length() > 32) {
	// return JsonUtil.getJson(-4, "订单号超长");
	// }
	// if (null == paymentRecord.getTradeName() || paymentRecord.getTradeName().trim().length() == 0) {
	// return JsonUtil.getJson(-5, "订单名称不能为空");
	// }
	// if (paymentRecord.getTradeName().length() > 256) {
	// return JsonUtil.getJson(-6, "订单名称超长");
	// }
	// if (null != paymentRecord.getUserName() && paymentRecord.getUserName().length() > 10) {
	// return JsonUtil.getJson(-7, "缴款人姓名超长");
	// }
	// if (paymentRecord.getSubItem().length() > 4) {
	// return JsonUtil.getJson(-8, "子项目号不能超过4位");
	// }
	//
	// try {
	// Date requestTime = DateUtils.parseDate(t_datetime, "yyyyMMddHHmmss");
	// Date now = new Date();
	// if (now.getTime() - requestTime.getTime() > 5 * 60 * 1000) {
	// log.warn("请求超时 " + t_datetime + " " + requestTime.getTime() + " server time:" + " " + now + now.getTime() + " " + (now.getTime() - requestTime.getTime()));
	// }
	// } catch (ParseException e) {
	// log.error("请求时间的格式错误!", e);
	// return JsonUtil.getJson(-9, "请求时间的格式错误");
	// }
	//
	// // 读取合作方密钥
	// Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
	// if (null == partner) {
	// log.error("合作方未授权! " + paymentRecord);
	// return JsonUtil.getJson(-10, "合作方未授权 " + paymentRecord.getPartner());
	// }
	//
	// // 验证签名
	// String partnerKey = partner.getDecodeKey();
	// SignBuilder signBulider = new SignBuilder();
	// signBulider.add("t_partner", t_partner);
	// signBulider.add("t_out_trade_no", t_out_trade_no);
	// signBulider.add("t_name", t_name);
	// signBulider.add("t_auth_code", t_auth_code);
	// signBulider.add("t_total_fee", t_total_fee);
	// signBulider.add("t_summary", t_summary);
	// signBulider.add("t_item", t_item);
	// signBulider.add("t_subitem", t_subitem);
	// signBulider.add("t_fptt", t_fptt);
	// signBulider.add("t_username", t_username);
	// signBulider.add("t_user_id", t_user_id);
	// signBulider.add("t_user_id_type", t_user_id_type);
	// signBulider.add("t_return_url", t_return_url);
	// signBulider.add("t_extra_common_param", t_extra_common_param);
	// signBulider.add("t_datetime", t_datetime);
	// signBulider.add("t_timeout", t_timeout);
	// signBulider.add("t_version", t_version);
	// signBulider.add("t_charset", t_charset);
	// String rSign = signBulider.createSign(partnerKey, "GBK");
	// if (!rSign.equals(t_sign)) {
	// log.error("签名验证失败 [" + rSign + "] [" + t_sign + "] " + t_partner + ":" + t_item + ":" + t_out_trade_no);
	// if (TysfConfig.getCheckSign()) {
	// return JsonUtil.getJson(-11, "签名验证失败");
	// }
	// }
	//
	// // 查找相同订单号的流水
	// PaymentRecord oldPaymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
	// if (null != oldPaymentRecord) {
	// log.info("存在相同订单号的流水 " + oldPaymentRecord);
	// if (oldPaymentRecord.getTotalFee().doubleValue() != Double.parseDouble(t_total_fee)) {
	// log.error("存在相同订单号的流水且金额不一致!!!");
	// return JsonUtil.getJson(-12, "存在相同订单号的流水且金额不一致");
	// }
	// if (!oldPaymentRecord.getItem().equals(t_item)) {
	// log.error("存在相同订单号的流水且项目不一致!!!");
	// return JsonUtil.getJson(-13, "存在相同订单号的流水且项目不一致");
	// }
	// if (!oldPaymentRecord.getUserName().equals(t_username)) {
	// log.error("存在相同订单号的流水且姓名不一致!!!");
	// return JsonUtil.getJson(-14, "存在相同订单号的流水且姓名不一致");
	// }
	// if (PaymentState.SUCCESS.getCode().equalsIgnoreCase(oldPaymentRecord.getState())) {
	// log.info("此订单已完成缴费");
	// return JsonUtil.getJson(-15, "此订单已完成缴费 ，订单编号:" + oldPaymentRecord.getOutTradeNo() + "，金额:" + oldPaymentRecord.getTotalFee());
	// }
	// log.info("此订单号已存在");
	// return JsonUtil.getJson(-16, "此订单号已存在 ，订单编号:" + paymentRecord.getOutTradeNo() + "，金额:" + paymentRecord.getTotalFee());
	// }
	//
	// // 查找支付项目
	// PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
	// if (null == item) {
	// log.error("支付项目不存在!!! " + paymentRecord);
	// return JsonUtil.getJson(-17, "支付项目不存在");
	// }
	// if (!"2".equals(item.getPaymentItemState())) {
	// return JsonUtil.getJson(-18, "此项目未通过审核 " + item.getPaymentItemId());
	// }
	//
	// // 判断是否在允许的时间段内
	// Date now = new Date();
	// if (now.after(item.getEndDate())) {
	// return JsonUtil.getJson(-19, "超过缴费时间");
	// }
	// if (now.before(item.getBeginDate())) {
	// return JsonUtil.getJson(-20, "缴费尚未开始");
	// }
	//
	// if (!paymentRecord.getPartner().trim().equals(item.getPartner())) {
	// log.error(paymentRecord.getPartner() + "不允许使用" + item + "项目");
	// return JsonUtil.getJson(-21, "此项目未授权");
	// }
	//
	// // 确定支付方式
	// PaymentType payType = PaymentType.parse(item.getPaymentType());
	// if (null == payType) {
	// log.error("未指定支付方式!!! " + item);
	// return JsonUtil.getJson(-22, "未指定支付方式");
	// }
	// if (!payType.equals(PaymentType.ZFBSMFK) && !payType.equals(PaymentType.ZFBSMSK)) {
	// log.error("非二维码支付方式" + item);
	// return JsonUtil.getJson(-23, "非二维码支付方式");
	// }
	// paymentRecord.setPaymentType(payType.getCode());
	// // 设定币种
	// paymentRecord.setMoneyType(item.getMoneyType());
	// // 保存流水
	// Invoice invoice = new Invoice();
	// invoice.setPartner(paymentRecord.getPartner());
	// invoice.setOutTradeNo(paymentRecord.getOutTradeNo());
	// invoice.setFptt(t_fptt);
	// invoice.setFpState("0");
	// invoice.setMoney(paymentRecord.getTotalFee());
	// int id = paymentDao.savePaymentAndFp(paymentRecord, invoice);
	// if (id < 0) {
	// log.error("保存流水失败!!!");
	// return JsonUtil.getJson(-24, "保存流水失败");
	// }
	//
	// // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
	// // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
	// String sellerId = "";
	// DecimalFormat df = new DecimalFormat("###############0");
	//
	// // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
	// String storeId = "0";
	// List<GoodsDetail> goodsList = new ArrayList<GoodsDetail>();
	// // 创建一个商品信息，参数含义分别为：商品编码（国标）、商品名称、商品单价（单位为分）、商品数量，如果需要添加商品类别，详见GoodsDetail
	// GoodsDetail goods1 = GoodsDetail.newInstance(t_item, (t_summary == null || "".equals(t_summary)) ? t_name : t_summary, Long.parseLong(df.format(Double.parseDouble(t_total_fee) * 100)), 1);
	// goodsList.add(goods1);
	// // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
	// ExtendParams extendParams = new ExtendParams();
	// extendParams.setSysServiceProviderId(AlipayWebConfig.ALIPAY_PARTNER);
	//
	// // 支付超时
	// if (!StringUtils.isEmpty(t_timeout)) {
	// try {
	// int minutes = 0;
	// if (t_timeout.toLowerCase().endsWith("d")) {
	// String timeout = t_timeout.substring(0, t_timeout.length() - 2);
	// minutes = Integer.valueOf(timeout) * 60 * 12;
	// }
	// if (t_timeout.toLowerCase().endsWith("h")) {
	// String timeout = t_timeout.substring(0, t_timeout.length() - 2);
	// minutes = Integer.valueOf(timeout) * 60;
	// }
	// if (t_timeout.toLowerCase().endsWith("m")) {
	// String timeout = t_timeout.substring(0, t_timeout.length() - 2);
	// minutes = Integer.valueOf(timeout);
	// }
	// minutes += 15; // 建行要求增加15分钟的误差
	// t_timeout = DateUtils.formatDate(DateUtils.addMinutes(new Date(), minutes), "yyyyMMddHHmmss");
	// } catch (Exception e) {
	// log.warn("解析支付超时参数[" + t_timeout + "]失败：" + e.getMessage(), e);
	// t_timeout = "";
	// }
	// }
	//
	// // 创建条码支付请求builder，设置请求参数
	// String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
	//
	// TradePayRequestBuilder rb = new TradePayRequestBuilder();
	// rb.setOrderid(outTradeNo);
	// rb.setPayment(t_total_fee);
	// rb.setTimeout(t_timeout);
	// rb.setReginfo(t_name);
	// rb.setProinfo(t_summary);
	// rb.setRemark1(t_item);// 项目号
	// // builder.setSellerId(sellerId);
	// // builder.setStoreId(storeId);
	// // builder.setExtendParams(extendParams);
	// // builder.setGoodsDetailList(goodsList);
	// // // builder.setNotifyUrl(TysfConfig.getAlipayNotifyUrl()); // 异步通知地址为平台的 add by dxj 20170803
	// // String str = builder.toJsonString();
	// // log.debug(str);
	// //
	// // // 调用tradePay方法获取当面付应答
	// // String payInfo = "";
	// // AlipayF2FPayResult result = tradeService.tradePay(builder);
	// // AlipayTradePayResponse response = result.getResponse();
	// // switch (result.getTradeStatus()) {
	// // case SUCCESS:
	// // payInfo = "支付宝支付成功";
	// // paymentRecord.setState(PaymentState.SUCCESS.getCode());
	// // Date gmtPayment = response.getGmtPayment(); // 支付时间
	// // if (gmtPayment == null) {
	// // gmtPayment = new Date();
	// // }
	// // paymentRecord.setGmtPayment(gmtPayment);
	// // paymentDao.receivedPayment(paymentRecord);
	// // log.info(payInfo);
	// // return JsonUtil.getJson(0, payInfo);
	// //
	// // case FAILED:
	// // payInfo = "支付宝支付失败!!!";
	// // if (response != null) {
	// // payInfo += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // payInfo += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.error(payInfo);
	// // return JsonUtil.getJson(-25, payInfo);
	// //
	// // case UNKNOWN:
	// // payInfo = "系统异常，订单状态未知!!!";
	// // if (response != null) {
	// // payInfo += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // payInfo += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.error(payInfo);
	// // return JsonUtil.getJson(-26, payInfo);
	// //
	// // default:
	// // payInfo = "不支持的交易状态，交易返回异常!!!";
	// // if (response != null) {
	// // payInfo += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // payInfo += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.error(payInfo);
	// // return JsonUtil.getJson(-27, payInfo);
	// // }
	// return JsonUtil.getJson(-10000, "接口未实现");
	// }

//	/**
//	 * 订单预创建接口（扫码支付接口）
//	 *
//	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
//	 * @param t_out_trade_no 订单号，64个字符以内，只能包含字母、数字、下划线，需保证商户系统端不能重复
//	 * @param t_name 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
//	 * @param t_total_fee 订单总金额，单位为元，不能超过1亿元
//	 * @param t_summary 订单摘要，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//	 * @param t_item 支付项目编号
//	 * @param t_subitem 子项目编号
//	 * @param t_fptt 发票抬头 多个发票，用|分割
//	 * @param t_username 缴款人姓名
//	 * @param t_user_id 缴款人证件编号
//	 * @param t_user_id_type 缴款人证件类型 {0 身份证 1 军官证 2 护照 3 其他}
//	 * @param t_return_url 页面跳转同步通知页面路径
//	 * @param t_extra_common_param 公用回传参数 如果用户请求时传递了该参数，则返回给商户时会回传该参数
//	 * @param t_datetime 当前时间
//	 * @param t_timeout 超时时间,支付超时定义为120分钟（不能带单位）
//	 * @param t_version 接口的版本号
//	 * @param t_charset 编码格式
//	 * @param t_sign 签名
//	 * @return
//	 */
	// public String TradePrecreate(String t_partner, String t_out_trade_no, String t_name, String t_total_fee, String t_summary, String t_item, String t_subitem, String t_fptt, String t_username, String t_user_id, String t_user_id_type, String t_return_url, String t_extra_common_param, String t_datetime, String t_timeout, String t_version, String t_charset,
	// String t_sign) {
	// if (StringUtils.isNotEmpty(t_version)) {
	// try {
	// Float version = Float.parseFloat(t_version);
	// if (version == 1.1) {
	// t_fptt = DecodeBase64(t_fptt);
	// t_username = DecodeBase64(t_username);
	// t_name = DecodeBase64(t_name);
	// t_summary = DecodeBase64(t_summary);
	// t_extra_common_param = DecodeBase64(t_extra_common_param);
	// log.info("Base64.decode " + t_fptt + " " + t_name + " " + t_extra_common_param);
	// }
	// } catch (Exception e) {
	// log.error("版本号错误!!! " + e.getMessage() + " " + t_version, e);
	// return JsonUtil.getJson(-1, "版本号错误，" + e.getMessage());
	// }
	// }
	//
	// // 如果指定了字符集，按指定字符集进行转换
	// if (null != t_charset && t_charset.length() > 0) {
	// try {
	// t_fptt = TransformCharset(t_fptt, t_charset);
	// t_username = TransformCharset(t_username, t_charset);
	// t_name = TransformCharset(t_name, t_charset);
	// t_summary = TransformCharset(t_summary, t_charset);
	// t_extra_common_param = TransformCharset(t_extra_common_param, t_charset);
	// } catch (UnsupportedEncodingException e) {
	// log.error("字符编码转换错误!!! " + e.getMessage() + " " + t_charset, e);
	// return JsonUtil.getJson(-1, "字符编码转换错误，" + e.getMessage());
	// }
	// }
	//
	// if (t_total_fee == null) {
	// return JsonUtil.getJson(-2, "付款金额错误");
	// }
	//
	// // 解析参数
	// PaymentRecord paymentRecord = new PaymentRecord();
	// paymentRecord.setPartner(t_partner);
	// paymentRecord.setOutTradeNo(t_out_trade_no);
	// try {
	// paymentRecord.setTotalFee(new BigDecimal(t_total_fee));
	// } catch (Exception e2) {
	// return JsonUtil.getJson(-2, "付款金额错误");
	// }
	// paymentRecord.setTradeName(t_name);
	// paymentRecord.setSummary(t_summary);
	// paymentRecord.setItem(t_item);
	// // 保存超时时间时，不能带单位，否则处理超时数据会出错
	// paymentRecord.setTimeout(t_timeout.toLowerCase().endsWith("m") ? t_timeout.substring(0, t_timeout.length() - 1) : t_timeout);
	//
	// if (null != t_subitem && t_subitem.trim().length() > 0)
	// paymentRecord.setSubItem(t_subitem);
	// else
	// paymentRecord.setSubItem("0");
	//
	// paymentRecord.setState(PaymentState.UNDONE.getCode());
	// paymentRecord.setCreateDate(new Date());
	// paymentRecord.setReturnUrl(t_return_url);
	// paymentRecord.setUserName(t_username);
	// paymentRecord.setUserId(t_user_id);
	// paymentRecord.setUserIdType(t_user_id_type);
	// paymentRecord.setExtraCommonParam(t_extra_common_param);
	// if (null == paymentRecord.getOutTradeNo() || paymentRecord.getOutTradeNo().trim().length() == 0) {
	// return JsonUtil.getJson(-3, "订单号不能为空");
	// }
	// if (paymentRecord.getOutTradeNo().length() > 32) {
	// return JsonUtil.getJson(-4, "订单号超长");
	// }
	// if (null == paymentRecord.getTradeName() || paymentRecord.getTradeName().trim().length() == 0) {
	// return JsonUtil.getJson(-5, "订单名称不能为空");
	// }
	// if (paymentRecord.getTradeName().length() > 256) {
	// return JsonUtil.getJson(-6, "订单名称超长");
	// }
	// if (null != paymentRecord.getUserName() && paymentRecord.getUserName().length() > 10) {
	// return JsonUtil.getJson(-7, "缴款人姓名超长");
	// }
	// if (paymentRecord.getSubItem().length() > 4) {
	// return JsonUtil.getJson(-8, "子项目号不能超过4位");
	// }
	//
	// try {
	// Date requestTime = DateUtils.parseDate(t_datetime, "yyyyMMddHHmmss");
	// Date now = new Date();
	// if (now.getTime() - requestTime.getTime() > 5 * 60 * 1000) {
	// log.warn("请求超时 " + t_datetime + " " + requestTime.getTime() + " server time:" + " " + now + now.getTime() + " " + (now.getTime() - requestTime.getTime()));
	// }
	// } catch (ParseException e) {
	// log.error("请求时间的格式错误!", e);
	// return JsonUtil.getJson(-9, "请求时间的格式错误");
	// }
	//
	// // 读取合作方密钥
	// Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
	// if (null == partner) {
	// log.error("合作方未授权! " + paymentRecord);
	// return JsonUtil.getJson(-10, "合作方未授权 " + paymentRecord.getPartner());
	// }
	//
	// // 验证签名
	// String partnerKey = partner.getDecodeKey();
	// SignBuilder signBulider = new SignBuilder();
	// signBulider.add("t_out_trade_no", t_out_trade_no);
	// signBulider.add("t_name", t_name);
	// signBulider.add("t_total_fee", t_total_fee);
	// signBulider.add("t_summary", t_summary);
	// signBulider.add("t_partner", t_partner);
	// signBulider.add("t_item", t_item);
	// signBulider.add("t_subitem", t_subitem);
	// signBulider.add("t_fptt", t_fptt);
	// signBulider.add("t_username", t_username);
	// signBulider.add("t_user_id", t_user_id);
	// signBulider.add("t_user_id_type", t_user_id_type);
	// signBulider.add("t_return_url", t_return_url);
	// signBulider.add("t_extra_common_param", t_extra_common_param);
	// signBulider.add("t_datetime", t_datetime);
	// signBulider.add("t_timeout", t_timeout);
	// signBulider.add("t_version", t_version);
	// signBulider.add("t_charset", t_charset);
	// String rSign = signBulider.createSign(partnerKey, "GBK");
	// if (!rSign.equals(t_sign)) {
	// log.error("签名验证失败 [" + rSign + "] [" + t_sign + "] " + t_partner + ":" + t_item + ":" + t_out_trade_no);
	// if (TysfConfig.getCheckSign()) {
	// return JsonUtil.getJson(-11, "签名验证失败");
	// }
	// }
	//
	// // 查找相同订单号的流水
	// PaymentRecord oldPaymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
	// if (null != oldPaymentRecord) {
	// log.info("存在相同订单号的流水 " + oldPaymentRecord);
	// if (oldPaymentRecord.getTotalFee().doubleValue() != Double.parseDouble(t_total_fee)) {
	// log.error("存在相同订单号的流水且金额不一致!!!");
	// return JsonUtil.getJson(-12, "存在相同订单号的流水且金额不一致");
	// }
	// if (!oldPaymentRecord.getItem().equals(t_item)) {
	// log.error("存在相同订单号的流水且项目不一致!!!");
	// return JsonUtil.getJson(-13, "存在相同订单号的流水且项目不一致");
	// }
	// if (!oldPaymentRecord.getUserName().equals(t_username)) {
	// log.error("存在相同订单号的流水且姓名不一致!!!");
	// return JsonUtil.getJson(-14, "存在相同订单号的流水且姓名不一致");
	// }
	// if (PaymentState.SUCCESS.getCode().equalsIgnoreCase(oldPaymentRecord.getState())) {
	// log.info("此订单已完成缴费");
	// return JsonUtil.getJson(-15, "此订单已完成缴费 ，订单编号:" + oldPaymentRecord.getOutTradeNo() + "，金额:" + oldPaymentRecord.getTotalFee());
	// }
	// log.info("此订单号已存在");
	// return JsonUtil.getJson(-16, "此订单号已存在 ，订单编号:" + paymentRecord.getOutTradeNo() + "，金额:" + paymentRecord.getTotalFee());
	// }
	//
	// // 查找支付项目
	// PaymentItem item = paymentDao.getPaymentItem(paymentRecord.getItem());
	// if (null == item) {
	// log.error("支付项目不存在!!! " + paymentRecord);
	// return JsonUtil.getJson(-17, "支付项目不存在");
	// }
	// if (!"2".equals(item.getPaymentItemState())) {
	// return JsonUtil.getJson(-18, "此项目未通过审核 " + item.getPaymentItemId());
	// }
	//
	// // 判断是否在允许的时间段内
	// Date now = new Date();
	// if (now.after(item.getEndDate())) {
	// return JsonUtil.getJson(-19, "超过缴费时间");
	// }
	// if (now.before(item.getBeginDate())) {
	// return JsonUtil.getJson(-20, "缴费尚未开始");
	// }
	//
	// if (!paymentRecord.getPartner().trim().equals(item.getPartner())) {
	// log.error(paymentRecord.getPartner() + "不允许使用" + item + "项目");
	// return JsonUtil.getJson(-21, "此项目未授权");
	// }
	//
	// // 确定支付方式
	// PaymentType payType = PaymentType.parse(item.getPaymentType());
	// if (null == payType) {
	// log.error("未指定支付方式!!! " + item);
	// return JsonUtil.getJson(-22, "未指定支付方式");
	// }
	// if (!payType.equals(PaymentType.ZFBSMFK)) {
	// log.error("非二维码支付方式" + item);
	// return JsonUtil.getJson(-23, "非二维码支付方式");
	// }
	// paymentRecord.setPaymentType(payType.getCode());
	// // 设定币种
	// paymentRecord.setMoneyType(item.getMoneyType());
	// // 保存流水
	// Invoice invoice = new Invoice();
	// invoice.setPartner(paymentRecord.getPartner());
	// invoice.setOutTradeNo(paymentRecord.getOutTradeNo());
	// invoice.setFptt(t_fptt);
	// invoice.setFpState("0");
	// invoice.setMoney(paymentRecord.getTotalFee());
	// int id = paymentDao.savePaymentAndFp(paymentRecord, invoice);
	// if (id < 0) {
	// log.error("保存流水失败!!!");
	// return JsonUtil.getJson(-24, "保存流水失败");
	// }
	//
	// // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
	// // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
	// String sellerId = "";
	// DecimalFormat df = new DecimalFormat("###############0");
	// // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
	// String storeId = "0";
	// List<GoodsDetail> goodsList = new ArrayList<GoodsDetail>();
	// // 创建一个商品信息，参数含义分别为：商品编码（国标）、商品名称、商品单价（单位为分）、商品数量，如果需要添加商品类别，详见GoodsDetail
	// GoodsDetail goods1 = GoodsDetail.newInstance(t_item, (t_summary == null || "".equals(t_summary)) ? t_name : t_summary, Long.parseLong(df.format(Double.parseDouble(t_total_fee) * 100)), 1);
	// goodsList.add(goods1);
	// // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
	// ExtendParams extendParams = new ExtendParams();
	// extendParams.setSysServiceProviderId(AlipayWebConfig.ALIPAY_PARTNER);
	//
	// // 发起订单
	// String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
	// // AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder();
	// // builder.setSubject(t_name);
	// // builder.setTotalAmount(t_total_fee);
	// // // builder.setOutTradeNo(t_out_trade_no);
	// // builder.setOutTradeNo(outTradeNo);
	// // // builder.setUndiscountableAmount(undiscountableAmount);
	// // builder.setSellerId(sellerId);
	// // builder.setBody(t_summary);
	// // builder.setOperatorId(t_item);// 项目号
	// // builder.setStoreId(storeId);
	// // builder.setExtendParams(extendParams);
	// // String timeout_express = t_timeout.toLowerCase().endsWith("m") ? t_timeout : t_timeout + "m";
	// // builder.setTimeoutExpress(timeout_express);
	// // builder.setGoodsDetailList(goodsList);
	// // // builder.setNotifyUrl(TysfConfig.getAlipayNotifyUrl()); // 异步通知地址为平台的 add by dxj 20170803
	// // String str = builder.toJsonString();
	// // log.debug(str);
	// //
	// // // 获取返回信息
	// // String error = "";
	// // AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
	// // AlipayTradePrecreateResponse response = result.getResponse();
	// // switch (result.getTradeStatus()) {
	// // case SUCCESS:
	// // log.info("支付宝预下单成功: )");
	// // paymentRecord.setState(PaymentState.SUBMITED.getCode());
	// // paymentDao.receivedPayment(paymentRecord);
	// // if (log.isDebugEnabled()) {
	// // DumpResponse(response);
	// // }
	// // String qrCode = response.getQrCode();
	// // return JsonUtil.getJson(0, qrCode);
	// //
	// // case FAILED:
	// // error = "支付宝预下单失败";
	// // if (response != null) {
	// // error += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // error += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.warn(error);
	// // return JsonUtil.getJson(-25, error);
	// //
	// // case UNKNOWN:
	// // error = "系统异常，预下单状态未知";
	// // if (response != null) {
	// // error += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // error += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.warn(error);
	// // return JsonUtil.getJson(-26, error);
	// //
	// // default:
	// // error = "系统异常，不支持的交易状态";
	// // if (response != null) {
	// // error += String.format("，(%s) %s", response.getCode(), response.getMsg());
	// // if (StringUtils.isNotEmpty(response.getSubCode())) {
	// // error += String.format("，[%s - %s]", response.getSubCode(), response.getSubMsg());
	// // }
	// // }
	// // log.warn(error);
	// // return JsonUtil.getJson(-27, error);
	// // }
	// return JsonUtil.getJson(-10000, "功能未实现");
	// }

//	/**
//	 * 订单撤销接口<br>
//	 *
//	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
//	 * @param t_out_trade_no 原支付请求的商户订单号不能为空
//	 * @param t_sign 签名
//	 * @return
//	 */
	// public String TradeCancel(String t_partner, String t_out_trade_no, String t_sign) {
	// if (StringUtils.isEmpty(t_partner)) {
	// return JsonUtil.getJson(-1, "合作方不能为空");
	// }
	// if (StringUtils.isEmpty(t_out_trade_no)) {
	// return JsonUtil.getJson(-2, "订单号不能为空");
	// }
	// PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
	// if (null == paymentRecord) {
	// log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
	// return JsonUtil.getJson(-3, "订单不存在");
	// }
	// // 读取合作方密钥
	// Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
	// if (null == partner) {
	// log.error("合作方未授权！(" + paymentRecord.getPartner() + ")");
	// return JsonUtil.getJson(-4, "合作方未授权 ");
	// }
	// // 验证签名
	// String partnerKey = partner.getDecodeKey();
	// SignBuilder signBulider = new SignBuilder();
	// signBulider.add("t_out_trade_no", t_out_trade_no);
	// signBulider.add("t_partner", t_partner);
	// String rSign = signBulider.createSign(partnerKey, "GBK");
	// if (!rSign.equals(t_sign)) {
	// log.error("签名验证失败 [" + rSign + "] [" + t_sign + "] " + t_partner + ":" + t_out_trade_no);
	// if (TysfConfig.getCheckSign()) {
	// return JsonUtil.getJson(-5, "签名验证失败");
	// }
	// }
	//
	// String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
	// // AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
	// // // String bizContent = String.format("{\"out_trade_no\":\"%s\",\"trade_no\":\"%s\"}", t_out_trade_no, "");
	// // String bizContent = String.format("{\"out_trade_no\":\"%s\",\"trade_no\":\"%s\"}", outTradeNo, "");
	// // request.setBizContent(bizContent);
	// // try {
	// // AlipayTradeCancelResponse response = alipayClient.execute(request);
	// // if (response.isSuccess()) {
	// // paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
	// // paymentDao.receivedPayment(paymentRecord);
	// // return JsonUtil.getJson(0, "订单撤销成功");
	// // } else {
	// // String msg = response.getMsg();
	// // String subMsg = response.getSubMsg();
	// // return JsonUtil.getJson(-101, StringUtils.isNotEmpty(subMsg) ? subMsg : msg);
	// // }
	// // } catch (AlipayApiException e) {
	// // log.error("订单撤销失败！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no, e);
	// // return JsonUtil.getJson(-102, e.getErrCode() + "-" + e.getErrMsg());
	// // }
	// return JsonUtil.getJson(-10000, "订单撤销功能未实现");
	// }

//	/**
//	 * 订单退款接口
//	 *
//	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
//	 * @param t_out_trade_no 订单编号
//	 * @param t_refund_reason 退款原因
//	 * @param t_charset 字符集
//	 * @param t_version
//	 * @param t_sign 签名
//	 * @return
//	 */
	// public String TradeRefund(String t_partner, String t_out_trade_no, String t_refund_reason, String t_charset, String t_version, String t_sign) {
	// if (StringUtils.isEmpty(t_partner)) {
	// return JsonUtil.getJson(-1, "合作方不能为空");
	// }
	// if (StringUtils.isEmpty(t_out_trade_no)) {
	// return JsonUtil.getJson(-2, "订单号不能为空");
	// }
	// if (StringUtils.isEmpty(t_refund_reason)) {
	// return JsonUtil.getJson(-3, "退款原因不能为空");
	// }
	// if ("1.1".equalsIgnoreCase(t_version)) {
	// t_refund_reason = DecodeBase64(t_refund_reason);
	// }
	// if (null != t_charset && t_charset.length() > 0) {
	// try {
	// t_refund_reason = TransformCharset(t_refund_reason, t_charset);
	// } catch (UnsupportedEncodingException e) {
	// log.error("字符编码转换错误！" + e.getMessage() + " " + t_charset, e);
	// return JsonUtil.getJson(-4, "字符编码转换错误");
	// }
	// }
	// if (t_refund_reason != null && t_refund_reason.length() > 256) {
	// return JsonUtil.getJson(-5, "退款原因超长");
	// }
	// PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
	// if (null == paymentRecord) {
	// log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
	// return JsonUtil.getJson(-7, "订单不存在");
	// }
	// // 退款金额，该金额不能大于订单金额,单位为元，支持两位小数
	// String t_total_fee = paymentRecord.getTotalFee().toString();
	// // 读取合作方密钥
	// Partner partner = paymentDao.getPartner(t_partner);
	// if (null == partner) {
	// log.error("合作方未授权！t_partner=" + t_partner);
	// return JsonUtil.getJson(-8, "合作方未授权 ");
	// }
	// // 验证签名
	// String partnerKey = partner.getDecodeKey();
	// SignBuilder signBulider = new SignBuilder();
	// signBulider.add("t_partner", t_partner);
	// signBulider.add("t_out_trade_no", t_out_trade_no);
	// signBulider.add("t_refund_reason", t_refund_reason);
	// signBulider.add("t_charset", t_charset);
	// signBulider.add("t_version", t_version);
	// String rSign = signBulider.createSign(partnerKey, "GBK");
	// if (!rSign.equals(t_sign)) {
	// log.error("签名验证失败 [" + rSign + "] [" + t_sign + "] " + t_partner + ":" + t_out_trade_no);
	// if (TysfConfig.getCheckSign()) {
	// return JsonUtil.getJson(-9, "签名验证失败");
	// }
	// }
	//
	// // 非当天的订单不允许退款操作 add by dxj 20170703
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// // 系统当前时间
	// Date currentDate = new Date();
	// // 订单创建时间
	// Date createDate = paymentRecord.getCreateDate();
	// log.debug("订单创建日期：" + sdf.format(createDate) + "，系统当前日期：" + sdf.format(currentDate));
	// int compare = currentDate.compareTo(createDate);
	// if (compare < 0) {
	// log.error("订单创建时间不能大于系统当前时间！创建日期：" + sdf.format(createDate) + " 当前日期：" + sdf.format(currentDate));
	// return JsonUtil.getJson(-10, "订单创建时间不能大于系统当前时间");
	// }
	// // 计算结束时间
	// Calendar date = Calendar.getInstance();
	// date.setTime(createDate);
	// date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
	// Date endDate = null;
	// try {
	// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	// endDate = df.parse(df.format(date.getTime()));
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// compare = currentDate.compareTo(endDate);
	// if (compare >= 0) {
	// log.warn("非当天订单不允许退款！创建日期：" + createDate + " 当前日期：" + currentDate);
	// return JsonUtil.getJson(-11, "非当天订单不允许退款");
	// }
	//
	// // 创建退款请求builder，设置请求参数
	// String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
	// // AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder();
	// // builder.setOutTradeNo(outTradeNo);
	// // builder.setRefundAmount(t_total_fee);
	// // builder.setRefundReason(t_refund_reason);
	// // builder.setOutRequestNo("");
	// // builder.setStoreId(Configs.getAppid());
	// // AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
	// // switch (result.getTradeStatus()) {
	// // case SUCCESS:
	// // paymentRecord.setState(PaymentState.REFUND.getCode());
	// // paymentRecord.setRefundDate(new Date());
	// // paymentRecord.setRefundDesc(t_refund_reason);
	// // paymentDao.receivedPayment(paymentRecord);
	// // return JsonUtil.getJson(0, "退款成功");
	// // case FAILED:
	// // return JsonUtil.getJson(-10, "支付宝退款失败!!!");
	// // case UNKNOWN:
	// // return JsonUtil.getJson(-11, "系统异常，订单退款状态未知!!!");
	// // default:
	// // return JsonUtil.getJson(-12, "不支持的交易状态，交易返回异常!!!");
	// // }
	// return JsonUtil.getJson(-10000, "功能未实现");
	// }

	// private String TransformCharset(String text, String charset) throws UnsupportedEncodingException {
	// if (null == text)
	// return null;
	// byte[] bytes = text.getBytes(charset);
	// return new String(bytes);
	// }
	//
	// private String DecodeBase64(String text) {
	// if (null == text) {
	// return null;
	// }
	// return new String(Base64.decode(text));
	// }

	private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

	public IPaymentDao getPaymentDao() {
		return paymentDao;
	}

	public void setPaymentDao(IPaymentDao paymentDao) {
		this.paymentDao = paymentDao;
	}
}
