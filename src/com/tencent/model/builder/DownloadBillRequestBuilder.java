package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;

/**
 * 下载对账单 {@link https://api.mch.weixin.qq.com/pay/downloadbill}
 * 
 * @author Jack
 */
public class DownloadBillRequestBuilder extends RequestBuilder {
	/** [必填]微信支付分配的公众账号ID（企业号corpid即为此appId） */
	private String appid = WxpayConfig.appid;
	/** [必填]微信支付分配的商户号 */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** 设备号 String(32) 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" */
	private String device_info = "WEB";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";
	/** 签名类型 String(32) 签名类型，默认为MD5，支持HMAC-SHA256和MD5。 */
	private String sign_type = "MD5";
	/** [必填]对账单日期 String(8) 下载对账单的日期，格式：20140603 */
	private String bill_date = "";
	/** [必填]账单类型 String(8) ALL，返回当日所有订单信息，默认值SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单 */
	private String bill_type = "";
	/** 压缩账单 String(8) 非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。 */
	private String tar_type = "";

	public static void main(String[] args) {

	}

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", appid); // 公众账号ID，必填，微信支付分配的公众账号ID（企业号corpid即为此appId）
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		param.put("device_info", device_info); // 设备号，自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		// param.put("sign_type", sign_type);// 签名类型，签名类型，默认为MD5，支持HMAC-SHA256和MD5。
		param.put("bill_date", bill_date); // 对账单日期
		param.put("bill_type", bill_type);// 账单类型
		param.put("tar_type", tar_type);// 压缩账单
		String prestr = createLinkString(paraFilter(param)); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String digestString = MD5.MD5Encode(prestr + "&key=" + key).toUpperCase();
		return digestString;
	}

	@Override
	public String buildXmlString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<appid>" + appid + "</appid>");
		sb.append("<mch_id>" + mch_id + "</mch_id>");
		sb.append("<nonce_str>" + nonce_str + "</nonce_str>");
		if (StringUtils.isNotEmpty(device_info))
			sb.append("<device_info>" + device_info + "</device_info>");
		if (StringUtils.isNotEmpty(bill_date))
			sb.append("<bill_date>" + bill_date + "</bill_date>");
		if (StringUtils.isNotEmpty(bill_type))
			sb.append("<bill_type>" + bill_type + "</bill_type>");
		if (StringUtils.isNotEmpty(tar_type))
			sb.append("<tar_type>" + tar_type + "</tar_type>");
		// sb.append("<sign_type>" + sign_type + "</sign_type>");
		sb.append("<sign>" + buildDigest() + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getBill_date() {
		return bill_date;
	}

	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}

	public String getBill_type() {
		return bill_type;
	}

	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}

	public String getTar_type() {
		return tar_type;
	}

	public void setTar_type(String tar_type) {
		this.tar_type = tar_type;
	}

}
