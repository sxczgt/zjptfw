package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;

/**
 * 企业付款到银行卡API {@link https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank}
 * 
 * @author Jack
 */
public class PayBankRequestBuilder2 extends RequestBuilder {
	/** [必填]微信支付分配的商户号 */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** [必填]商户企业付款单号，商户订单号，需保持唯一（只允许数字[0~9]或字母[A~Z]和[a~z]，最短8位，最长32位） */
	private String partner_trade_no = "";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";
	/** [必填]签名类型 String(32) 签名类型，默认为MD5，支持HMAC-SHA256和MD5。 */
	private String sign_type = "MD5";
	/** [必填]收款方银行卡号 收款方银行卡号（采用标准RSA算法，公钥由微信侧提供） */
	private String enc_bank_no = "";
	/** [必填]收款方用户名 收款方用户名（采用标准RSA算法，公钥由微信侧提供） */
	private String enc_true_name = "";
	/** [必填]收款方开户行 银行卡所在开户行编号 */
	private String bank_code = "";
	/** [必填]付款金额 付款金额：RMB分（支付总额，不含手续费） 注：大于0的整数 */
	private String amount = "";
	/** 付款说明 企业付款到银行卡付款说明,即订单备注（UTF8编码，允许100个字符以内） */
	private String desc = "";

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("partner_trade_no", partner_trade_no); // 商户企业付款单号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		param.put("enc_bank_no", enc_bank_no); // 收款方银行卡号
		param.put("enc_true_name", enc_true_name); // 收款方用户名
		param.put("bank_code", bank_code);// 收款方开户行
		param.put("amount", amount);// 付款金额
		param.put("desc", desc); // 付款说明
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
		sb.append("<enc_bank_no>" + enc_bank_no + "</enc_bank_no>");
		sb.append("<enc_true_name>" + enc_true_name + "</enc_true_name>");
		sb.append("<bank_code>" + bank_code + "</bank_code>");
		sb.append("<amount>" + amount + "</amount>");
		if (StringUtils.isNotEmpty(desc))
			sb.append("<desc>" + desc + "</desc>");
		sb.append("<sign>" + buildDigest() + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	/** 随机生成 商户企业付款单号 */
	public static String buildPartnerOutTradeNo() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
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

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getEnc_bank_no() {
		return enc_bank_no;
	}

	public void setEnc_bank_no(String enc_bank_no) {
		this.enc_bank_no = enc_bank_no;
	}

	public String getEnc_true_name() {
		return enc_true_name;
	}

	public void setEnc_true_name(String enc_true_name) {
		this.enc_true_name = enc_true_name;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
