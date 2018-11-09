package com.sxypay.trade;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.capinfo.crypt.Md5;
import com.sxypay.builder.TradeRefundQueryRequestBuilder;
import com.sxypay.builder.TradeRefundRequestBuilder;
import com.sxypay.utils.HttpClientUtil;

import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.PaymentType;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.Partner;
import cn.tsinghua.sftp.pojo.PaymentBank;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.Base64;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SignBuilder;
import cn.tsinghua.sftp.util.SpringContextUtil;
import cn.tsinghua.sftp.util.SxyzfUtil;

/**
 * 支付宝当面付2.0服务
 * 
 * @author Created by liuyangkly on 15/8/9.
 */
public class SxypayTrade {
	private static Log log = LogFactory.getLog(SxypayTrade.class);

	private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

	/**
	 * 统一收单线下交易查询
	 * 
	 * @param t_partner 合作方编号，由财务处统一编码，分配给合作方
	 * @param t_out_trade_no 商户订单号，通过此商户订单号查询当面付的交易状态(必填)
	 */
	public String SxypayTradeQuery(String t_partner, String t_out_trade_no) {
		log.info("SxypayTradeQuery t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
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

		// // 创建查询请求builder，设置请求参数
		// String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
		// AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		// request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"\"" + " }");
		// try {
		//
		// } catch (Exception e) {
		// return JsonUtil.getJson(-7, "系统异常" + e.getMessage());
		// }
		return JsonUtil.getJson(-100, "功能未实现");
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
	public String SxypayTradeRefund(String t_partner, String t_out_trade_no, String t_refund_reason, String t_charset, String t_version, String t_sign) {
		log.info("SxypayTradeRefund t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
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
			e.printStackTrace();
		}
		compare = currentDate.compareTo(endDate);
		if (compare >= 0) {
			log.warn("非当天订单不允许退款！创建日期：" + createDate + " 当前日期：" + currentDate);
			// return JsonUtil.getJson(-11, "非当天订单不允许退款");
		}
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
		rb.setV_refamount(paymentRecord.getTotalFee());
		rb.setV_refreason(t_refund_reason);
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
				return JsonUtil.getJson(new Integer(status.getData().toString()), statusdesc.getData().toString());
			}
			paymentRecord.setState(PaymentState.REFUND.getCode());
			paymentRecord.setRefundDate(new Date());
			paymentRecord.setRefundDesc(t_refund_reason);
			paymentDao.receivedPayment(paymentRecord);
			return JsonUtil.getJson(0, "退款成功");
		} catch (Exception e) {
			return JsonUtil.getJson(-102, e.getMessage());
		}
	}

	public String SxypayRefundQuery(String t_partner, String t_out_trade_no, String t_sign) {
		log.info("SxypayRefundQuery t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
		if (StringUtils.isEmpty(t_partner)) {
			return JsonUtil.getJson(-1, "合作方不能为空");
		}
		if (StringUtils.isEmpty(t_out_trade_no)) {
			return JsonUtil.getJson(-2, "订单号不能为空");
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
		String rSign = signBulider.createSign(partnerKey, "GBK");
		if (!rSign.equals(t_sign)) {
			log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
			if (TysfConfig.IsCheckSign()) {
				return JsonUtil.getJson(-9, "签名验证失败");
			}
		}
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
				return JsonUtil.getJson(new Integer(status.getData().toString()), statusdesc.getData().toString());
			}
			Element body = docu.element("messagebody");
			Element refund = body.element("refund");
			Element refid = refund.element("refid");// 退款流水号
			Element refstatus = refund.element("refstatus");// 退款处理状态
			log.info("status success!!! " + refid.getData() + "\n" + refstatus.getData() + " \n" + urlStr);
			return JsonUtil.getJson(0, refid.getData().toString());
		} catch (Exception e) {
			log.error("首信易退款查询" + e.getMessage(), e);
			return JsonUtil.getJson(-102, e.getMessage());
		}
	}

	/**
	 * 首信易支付批量代付后台提交接口
	 * 
	 * @param v_ymd
	 * @return
	 */
	public String SxypayTradeB2C(String v_ymd) {
		// 1.查询当天流水 查询项目关联通道号
		String b2c_submit_url = TysfConfig.getSxyzfB2CSubmitUrl();
		String msg = "";// 返回信息
		List<Map<String, Object>> list = paymentDao.queryPaymentRecordItem(v_ymd);
		for (Map<String, Object> map : list) {
			try {
				String v_mid = map.get("third_party_subaccount").toString();
				String total_fee = map.get("total_fee").toString();
				String payment_type = map.get("payment_type").toString();
				String item = map.get("item").toString();
				Object b = map.get("bank_code");
				if (b == null) {
					log.error(item + "银行卡号为空.");
					continue;
				}
				String bank_code = map.get("bank_code").toString();// 银行账号
				String ymd = new SimpleDateFormat("yyyyMMdd").format(new Date());
				String pch = v_mid + ymd;// 商户编号-日期(yyyymmdd)-顺序号
				StringBuffer sb = new StringBuffer();
				sb.append("1|" + total_fee + "|" + pch + "$");
				// 客户标识为收方客户在商户端的编码信息，如商户端用户编号等，长度不超过 120 位字符
				String bs = item + v_mid + pch + java.util.UUID.randomUUID().toString().replace("-", "");
				// sb.append("收方帐号 1 | 收方帐户名 1 | 收方开户行 1 |收方省份 1 | 收方城市 1 | 付款金额 1 | 客户标识 1|联行号 1");
				PaymentBank bank = paymentDao.getPaymentBank(bank_code);
				if (bank == null) {
					log.error("付款银行表未登记该银行卡号" + bank_code);
					continue;
				}
				sb.append("" + bank_code + "|" + bank.getAcctName() + "|" + bank.getBankName() + "|" + bank.getBankProvince() + "|" + bank.getBankCity() + "|" + total_fee + "|" + bs + "|102100099996");
				String key = "";
				if (PaymentType.SXYWK.getCode().equals(payment_type)) {// 外卡
					key = TysfConfig.getSxyzfwkKey();
				} else {
					key = TysfConfig.getSxyzfKey();
				}
				String v_data = sb.toString();
				String v_mac = hmac_md5(v_mid + URLEncoder.encode(v_data, "utf-8"), key);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("v_mid", v_mid);
				paramMap.put("v_data", v_data);
				paramMap.put("v_version", "1.0");
				paramMap.put("v_mac", v_mac);
				String content = HttpClientUtil.httpPost(b2c_submit_url, paramMap);
				System.out.println(content);
				// SAXReader saxReader = new SAXReader();
				// sb = new StringBuffer().append("?").append("v_mid=" + v_mid).append("&v_data=" + v_data).append("&v_version=1.0").append("&v_mac=" + v_mac);
				// String urlStr = b2c_submit_url + sb.toString();
				// Document document = saxReader.read(urlStr);
				Document document = DocumentHelper.parseText(content);
				Element head = document.getRootElement();
				// 解析报文头
				Element status = head.element("status");
				Element statusdesc = head.element("statusdesc");
				if (!"0".equals(status.getData())) {
					log.warn("status error!!! " + status.getData() + "\n" + statusdesc.getData() + " \n" + b2c_submit_url + " \n" + item);
					msg += item + "支付批量代付失败" + status.getData() + "\n" + statusdesc.getData() + "\n";
					break;
				}
				msg += item + "支付批量代付成功" + "\n";
				// log.warn("首信易支付批量代付成功!!! " + status.getData() + "\n" + statusdesc.getData() + " \n" + urlStr + " \n" + item);
			} catch (Exception e) {
				return JsonUtil.getJson(-102, e.getMessage());
			}
		}
		return JsonUtil.getJson(0, msg);
	}

	/**
	 * 生成md5
	 * 
	 * @param source
	 * @param key
	 * @return
	 */
	public String hmac_md5(String source, String key) {
		Md5 md5 = new Md5("");
		String digestString = "";
		try {
			md5.hmac_Md5(source, key);
			byte b[] = md5.getDigest();
			digestString = Md5.stringify(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return digestString;
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

}
