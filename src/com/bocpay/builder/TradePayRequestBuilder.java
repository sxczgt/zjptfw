package com.bocpay.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocpay.config.BocpayConfig;
import com.bocpay.model.BocpayMerchant;
import com.google.gson.annotations.SerializedName;

import cn.tsinghua.sftp.util.StringUtil;

public class TradePayRequestBuilder extends RequestBuilder {
	private static final Log log = LogFactory.getLog(TradePayRequestBuilder.class);
	private BizContent bizContent = new BizContent();

	public BizContent getBizContent() {
		return this.bizContent;
	}

	/** 校验必填项 */
	public boolean validate() {
		if (StringUtils.isEmpty(this.bizContent.vpc_AccessCode)) {
			log.error("vpc_AccessCode should not be NULL!");
			throw new NullPointerException("vpc_AccessCode should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.vpc_MerchTxnRef)) {
			log.error("vpc_MerchTxnRef should not be NULL!");
			throw new NullPointerException("vpc_MerchTxnRef should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.vpc_Merchant)) {
			log.error("vpc_Merchant should not be NULL!");
			throw new NullPointerException("vpc_Merchant should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.vpc_Amount)) {
			log.error("vpc_Amount should not be NULL!");
			throw new NullPointerException("vpc_Amount should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.vpc_Locale)) {
			log.error("vpc_Locale should not be NULL!");
			throw new NullPointerException("vpc_Locale should not be NULL!");
		}
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("TradePayRequestBuilder{");
		sb.append("bizContent=").append(this.bizContent);
		sb.append(", super=").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	public TradePayRequestBuilder(String merchant_id) {
		this.bizContent.vpc_Version = "1"; // VPC版本
		this.bizContent.vpc_Command = "pay"; // 命令类型
		this.bizContent.vpc_Merchant = merchant_id; // 商户ID
		BocpayMerchant bm = BocpayConfig.getInstance().getMerchant(merchant_id);
		if (bm == null) {
			log.error("商户编号[" + merchant_id + "]的配置信息不存在！");
			return;
		}
		this.bizContent.vpc_AccessCode = bm.getAccessCode(); // 商户访问代码
		this.bizContent.vpc_ReturnURL = BocpayConfig.getInstance().getReturnUrl(); // 同步返回地址
		this.bizContent.vpc_Locale = "en_US"; // 显示语种：zh_CN、en_US
		this.bizContent.vpc_Currency = "CNY"; // 币种
	}

	/**
	 * 生成map
	 * 
	 * @return
	 */
	public Map<String, Object> getBuildMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("vpc_Version", StringUtil.setNullToSpace(this.bizContent.vpc_Version));
		map.put("vpc_Command", StringUtil.setNullToSpace(this.bizContent.vpc_Command));
		map.put("vpc_AccessCode", StringUtil.setNullToSpace(this.bizContent.vpc_AccessCode));
		map.put("vpc_MerchTxnRef", StringUtil.setNullToSpace(this.bizContent.vpc_MerchTxnRef));
		map.put("vpc_Merchant", StringUtil.setNullToSpace(this.bizContent.vpc_Merchant));
		map.put("vpc_OrderInfo", StringUtil.setNullToSpace(this.bizContent.vpc_OrderInfo));
		map.put("vpc_Amount", StringUtil.setNullToSpace(this.bizContent.vpc_Amount));
		map.put("vpc_ReturnURL", StringUtil.setNullToSpace(this.bizContent.vpc_ReturnURL));
		map.put("vpc_Locale", StringUtil.setNullToSpace(this.bizContent.vpc_Locale));
		map.put("vpc_Currency", StringUtil.setNullToSpace(this.bizContent.vpc_Currency));
		return map;
	}

	public String getVpc_Version() {
		return this.bizContent.vpc_Version;
	}

	public TradePayRequestBuilder setVpc_Version(String vpc_Version) {
		this.bizContent.vpc_Version = vpc_Version;
		return this;
	}

	public String getVpc_Command() {
		return this.bizContent.vpc_Command;
	}

	public TradePayRequestBuilder setVpc_Command(String vpc_Command) {
		this.bizContent.vpc_Command = vpc_Command;
		return this;
	}

	public String getVpc_AccessCode() {
		return this.bizContent.vpc_AccessCode;
	}

	public TradePayRequestBuilder setVpc_AccessCode(String vpc_AccessCode) {
		this.bizContent.vpc_AccessCode = vpc_AccessCode;
		return this;
	}

	public String getVpc_MerchTxnRef() {
		return this.bizContent.vpc_MerchTxnRef;
	}

	public TradePayRequestBuilder setVpc_MerchTxnRef(String vpc_MerchTxnRef) {
		this.bizContent.vpc_MerchTxnRef = vpc_MerchTxnRef;
		return this;
	}

	public String getVpc_Merchant() {
		return this.bizContent.vpc_Merchant;
	}

	public TradePayRequestBuilder setVpc_Merchant(String vpc_Merchant) {
		this.bizContent.vpc_Merchant = vpc_Merchant;
		return this;
	}

	public String getVpc_OrderInfo() {
		return this.bizContent.vpc_OrderInfo;
	}

	public TradePayRequestBuilder setVpc_OrderInfo(String vpc_OrderInfo) {
		this.bizContent.vpc_OrderInfo = vpc_OrderInfo;
		return this;
	}

	public String getVpc_Amount() {
		return this.bizContent.vpc_Amount;
	}

	public TradePayRequestBuilder setVpc_Amount(String vpc_Amount) {
		this.bizContent.vpc_Amount = vpc_Amount;
		return this;
	}

	public String getVpc_ReturnURL() {
		return this.bizContent.vpc_ReturnURL;
	}

	public TradePayRequestBuilder setVpc_ReturnURL(String vpc_ReturnURL) {
		this.bizContent.vpc_ReturnURL = vpc_ReturnURL;
		return this;
	}

	public String getVpc_Locale() {
		return this.bizContent.vpc_Locale;
	}

	public TradePayRequestBuilder setVpc_Locale(String vpc_Locale) {
		this.bizContent.vpc_Locale = vpc_Locale;
		return this;
	}

	public String getVpc_Currency() {
		return this.bizContent.vpc_Currency;
	}

	public TradePayRequestBuilder setVpc_Currency(String vpc_Currency) {
		this.bizContent.vpc_Currency = vpc_Currency;
		return this;
	}

	public static class BizContent {
		/** VPC版本 */
		@SerializedName("vpc_Version")
		private String vpc_Version;

		/** 命令类型 */
		@SerializedName("vpc_Command")
		private String vpc_Command;

		/** 商户访问代码 */
		@SerializedName("vpc_AccessCode")
		private String vpc_AccessCode;

		/** 订单号 */
		@SerializedName("vpc_MerchTxnRef")
		private String vpc_MerchTxnRef;

		/** 商户ID */
		@SerializedName("vpc_Merchant")
		private String vpc_Merchant;

		/** 订单描述，使用项目号 */
		@SerializedName("vpc_OrderInfo")
		private String vpc_OrderInfo;

		/** 订单金额（单位：分） */
		@SerializedName("vpc_Amount")
		private String vpc_Amount;

		/** 同步返回地址 */
		@SerializedName("vpc_ReturnURL")
		private String vpc_ReturnURL;

		/** 显示语种（需要设计语种选择列表），英文：en_AU，中文：zh_CN */
		@SerializedName("vpc_Locale")
		private String vpc_Locale;

		/** 币种，美元：USD、人民币：CNY、港币：HKD */
		@SerializedName("vpc_Currency")
		private String vpc_Currency;

		public BizContent() {
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("BizContent{");
			sb.append("vpc_Version='").append(this.vpc_Version).append('\'');
			sb.append(", vpc_Command='").append(this.vpc_Command).append('\'');
			sb.append(", vpc_AccessCode='").append(this.vpc_AccessCode).append('\'');
			sb.append(", vpc_MerchTxnRef='").append(this.vpc_MerchTxnRef).append('\'');
			sb.append(", vpc_Merchant='").append(this.vpc_Merchant).append('\'');
			sb.append(", vpc_OrderInfo='").append(this.vpc_OrderInfo).append('\'');
			sb.append(", vpc_Amount='").append(this.vpc_Amount).append('\'');
			sb.append(", vpc_ReturnURL='").append(this.vpc_ReturnURL).append('\'');
			sb.append(", vpc_Locale='").append(this.vpc_Locale).append('\'');
			sb.append(", vpc_Currency='").append(this.vpc_Currency).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
