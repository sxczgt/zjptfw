package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;

/**
 * 撤销订单 {@link https://api.mch.weixin.qq.com/secapi/pay/reverse}
 * 
 * @author Jack
 */
public class ReverseRequestBuilder extends RequestBuilder {
	/** [必填]微信支付分配的公众账号ID（企业号corpid即为此appId） */
	private String appid = WxpayConfig.appid;
	/** [必填]微信支付分配的商户号 */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** [二选一]微信订单号 String(28) 微信生成的订单号，在支付通知中有返回 */
	private String transaction_id = "";
	/** [二选一]商户订单号 String(32) 商户侧传给微信的订单号 */
	private String out_trade_no = "";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";
	/** [必填]签名类型 String(32) 签名类型，默认为MD5，支持HMAC-SHA256和MD5。 */
	private String sign_type = "MD5";

	public static void main(String[] args) {

	}

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", appid); // 公众账号ID，必填，微信支付分配的公众账号ID（企业号corpid即为此appId）
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("sign_type", sign_type);// 签名类型，签名类型，默认为MD5，支持HMAC-SHA256和MD5。
		param.put("transaction_id", transaction_id); // 微信订单号，二选一。商品描述，微信生成的订单号，在支付通知中有返回
		param.put("out_trade_no", out_trade_no);// 商户订单号，二选一。商户侧传给微信的订单号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		String prestr = createLinkString(paraFilter(param)) + "&key=" + key; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String digestString = MD5.MD5Encode(prestr).toUpperCase();
		// Md5 md5 = new Md5("");
		// md5.hmac_Md5("", key);
		// byte b[] = md5.getDigest();
		// String digestString = Md5.stringify(b);
		return digestString;
	}

	@Override
	public String buildXmlString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<appid>" + appid + "</appid>");
		sb.append("<mch_id>" + mch_id + "</mch_id>");
		sb.append("<nonce_str>" + nonce_str + "</nonce_str>");
		if (StringUtils.isNotEmpty(transaction_id))
			sb.append("<transaction_id>" + transaction_id + "</transaction_id>");
		if (StringUtils.isNotEmpty(out_trade_no))
			sb.append("<out_trade_no>" + out_trade_no + "</out_trade_no>");
		sb.append("<sign_type>" + sign_type + "</sign_type>");
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

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

}
