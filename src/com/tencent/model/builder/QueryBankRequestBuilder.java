package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;

/**
 * 查询企业付款银行卡API {@link https://api.mch.weixin.qq.com/mmpaysptrans/query_bank}
 * 
 * @author Jack
 */
public class QueryBankRequestBuilder extends RequestBuilder {
	/** [必填]微信支付分配的商户号 */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** [必填]商户企业付款单号 */
	private String partner_trade_no = "";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("partner_trade_no", partner_trade_no); // 商户企业付款单号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		String prestr = createLinkString(paraFilter(param)) + "&key=" + key; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String digestString = MD5.MD5Encode(prestr).toUpperCase();
		return digestString;
	}

	@Override
	public String buildXmlString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<mch_id>" + mch_id + "</mch_id>");
		sb.append("<partner_trade_no>" + partner_trade_no + "</partner_trade_no>");
		sb.append("<nonce_str>" + nonce_str + "</nonce_str>");
		sb.append("<sign>" + buildDigest() + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getPartner_trade_no() {
		return partner_trade_no;
	}

	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
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

}
