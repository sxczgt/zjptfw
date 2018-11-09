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
public class PayBankRequestBuilder extends RequestBuilder {
	/** [必填]微信支付分配的商户号String(32) */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** [必填]商户订单号，需保持唯一（只允许数字[0~9]或字母[A~Z]和[a~z]，最短8位，最长32位） */
	private String partner_trade_no = "";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";
	/** [必填]签名类型 String(32) 签名类型，默认为MD5，支持HMAC-SHA256和MD5。 */
	private String sign_type = "MD5";
	/** [必填]收款方银行卡号 String(64)（采用标准RSA算法，公钥由微信侧提供） */
	private String enc_bank_no = "";
	/** [必填]收款方用户名 String(64)（采用标准RSA算法，公钥由微信侧提供） */
	private String enc_true_name = "";
	/** [必填]收款方开户行 String(64)银行卡所在开户行编号 */
	private String bank_code = "";
	/** 分支行名称 String(128)，银行卡的开户分行、支行名称，是否需要填看附录1 */
	private String bank_branch_name = "";
	/** 省份,银行卡的开户支行省份编号，是否需要填看附录1。 */
	private int branch_area_code = 1; // 1 = 北京
	/** 城市,银行卡的开户支行城市编号，是否需要填看附录1。 */
	private int branch_city_code = 10; // 10 = 北京
	/** [必填]账户类型（对公/私）,入款账户的类型（1: 对公；2: 对私） */
	private int account_type = 1;
	/** [必填]付款金额 ：RMB分（支付总额，不含手续费） 注：大于0的整数 */
	private int amount = 0;
	/** 付款说明，企业付款到银行卡付款说明,即订单备注（UTF8编码，允许100个字符以内） */
	private String desc = "";
	/** 银行附言,银行操作说明（支持UTF8编码，允许20个字符以内）， 注：实际到账附言以银行为准，微信支付仅负责提交，不代表最终的到账结果样式 */
	private String bank_note = "";

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
		if (StringUtils.isNotEmpty(bank_branch_name)) {
			param.put("bank_branch_name", bank_branch_name);// 分支行名称,银行卡的开户分行、支行名称
		}
		if (branch_area_code > 0) {
			param.put("branch_area_code", String.valueOf(branch_area_code)); // 省份,银行卡的开户支行省份编号
		}
		if (branch_city_code > 0) {
			param.put("branch_city_code", String.valueOf(branch_city_code)); // 城市,银行卡的开户支行城市编号
		}
		if (account_type > 0) {
			param.put("account_type", String.valueOf(account_type));// 账户类型（对公/私）,入款账户的类型（1: 对公；2: 对私）
		}
		param.put("amount", String.valueOf(amount));// 付款金额,RMB分（支付总额，不含手续费）, 注：大于0的整数
		if (StringUtils.isNotEmpty(desc)) {
			param.put("desc", desc); // 付款说明
		}
		if (StringUtils.isNotEmpty(bank_note)) {
			param.put("bank_note", bank_note); // 银行附言,银行操作说明（支持UTF8编码，允许20个字符以内）
		}
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
		if (StringUtils.isNotEmpty(bank_branch_name)) {
			sb.append("<bank_branch_name>" + bank_branch_name + "</bank_branch_name>");
		}
		if (branch_area_code > 0) {
			sb.append("<branch_area_code>" + branch_area_code + "</branch_area_code>");
		}
		if (branch_city_code > 0) {
			sb.append("<branch_city_code>" + branch_city_code + "</branch_city_code>");
		}
		if (account_type > 0) {
			sb.append("<account_type>" + account_type + "</account_type>");
		}
		sb.append("<amount>" + amount + "</amount>");
		if (StringUtils.isNotEmpty(desc)) {
			sb.append("<desc>" + desc + "</desc>");
		}
		if (StringUtils.isNotEmpty(bank_note)) {
			sb.append("<bank_note>" + bank_note + "</bank_note>");
		}
		sb.append("<sign>" + buildDigest() + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	/** 随机生成 商户企业付款单号 */
	public static String buildPartnerOutTradeNo() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}

	public String getBank_branch_name() {
		return bank_branch_name;
	}

	public void setBank_branch_name(String bank_branch_name) {
		this.bank_branch_name = bank_branch_name;
	}

	public int getBranch_area_code() {
		return branch_area_code;
	}

	public void setBranch_area_code(int branch_area_code) {
		this.branch_area_code = branch_area_code;
	}

	public int getBranch_city_code() {
		return branch_city_code;
	}

	public void setBranch_city_code(int branch_city_code) {
		this.branch_city_code = branch_city_code;
	}

	public int getAccount_type() {
		return account_type;
	}

	public void setAccount_type(int account_type) {
		this.account_type = account_type;
	}

	public String getBank_note() {
		return bank_note;
	}

	public void setBank_note(String bank_note) {
		this.bank_note = bank_note;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
