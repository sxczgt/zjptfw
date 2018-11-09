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

public class TradeRefundRequestBuilder extends RequestBuilder {
	private static final Log log = LogFactory.getLog(TradeRefundRequestBuilder.class);
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
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("TradeCancelRequestBuilder{");
		sb.append("bizContent=").append(this.bizContent);
		sb.append(", super=").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	public TradeRefundRequestBuilder(String merchant_id) {
		this.bizContent.vpc_Version = "1"; // VPC版本
		this.bizContent.vpc_Command = "refund"; // 命令类型
		this.bizContent.vpc_Merchant = merchant_id; // 商户ID
		BocpayMerchant bm = BocpayConfig.getInstance().getMerchant(merchant_id);
		this.bizContent.vpc_AccessCode = bm.getAccessCode(); // 商户访问代码
		this.bizContent.vpc_User = bm.getUser(); // 账号
		this.bizContent.vpc_Password = bm.getPassword(); // 密码
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
		map.put("vpc_User", StringUtil.setNullToSpace(this.bizContent.vpc_User));
		map.put("vpc_Password", StringUtil.setNullToSpace(this.bizContent.vpc_Password));
		map.put("vpc_TransNo", StringUtil.setNullToSpace(this.bizContent.vpc_TransNo));
		map.put("vpc_Amount", StringUtil.setNullToSpace(this.bizContent.vpc_Amount));
		return map;
	}

	public String getVpc_Version() {
		return this.bizContent.vpc_Version;
	}

	public TradeRefundRequestBuilder setVpc_Version(String vpc_Version) {
		this.bizContent.vpc_Version = vpc_Version;
		return this;
	}

	public String getVpc_Command() {
		return this.bizContent.vpc_Command;
	}

	public TradeRefundRequestBuilder setVpc_Command(String vpc_Command) {
		this.bizContent.vpc_Command = vpc_Command;
		return this;
	}

	public String getVpc_AccessCode() {
		return this.bizContent.vpc_AccessCode;
	}

	public TradeRefundRequestBuilder setVpc_AccessCode(String vpc_AccessCode) {
		this.bizContent.vpc_AccessCode = vpc_AccessCode;
		return this;
	}

	public String getVpc_MerchTxnRef() {
		return this.bizContent.vpc_MerchTxnRef;
	}

	public TradeRefundRequestBuilder setVpc_MerchTxnRef(String vpc_MerchTxnRef) {
		this.bizContent.vpc_MerchTxnRef = vpc_MerchTxnRef;
		return this;
	}

	public String getVpc_Merchant() {
		return this.bizContent.vpc_Merchant;
	}

	public TradeRefundRequestBuilder setVpc_Merchant(String vpc_Merchant) {
		this.bizContent.vpc_Merchant = vpc_Merchant;
		return this;
	}

	public String getVpc_User() {
		return this.bizContent.vpc_User;
	}

	public TradeRefundRequestBuilder setVpc_User(String vpc_User) {
		this.bizContent.vpc_User = vpc_User;
		return this;
	}

	public String getVpc_Password() {
		return this.bizContent.vpc_Password;
	}

	public TradeRefundRequestBuilder setVpc_Password(String vpc_Password) {
		this.bizContent.vpc_Password = vpc_Password;
		return this;
	}

	public String getVpc_TransNo() {
		return this.bizContent.vpc_TransNo;
	}

	public TradeRefundRequestBuilder setVpc_TransNo(String vpc_TransNo) {
		this.bizContent.vpc_TransNo = vpc_TransNo;
		return this;
	}

	public String getVpc_Amount() {
		return this.bizContent.vpc_Amount;
	}

	public TradeRefundRequestBuilder setVpc_Amount(String vpc_Amount) {
		this.bizContent.vpc_Amount = vpc_Amount;
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

		/** 用户 */
		@SerializedName("vpc_User")
		private String vpc_User;
		/** 密码 */
		@SerializedName("vpc_Password")
		private String vpc_Password;
		/**
		 * 订单查询类型， 交易查询：空值-2/3-Party Transaction， 退款/完成查询：ama-Refund/Capture Transaction
		 */
		@SerializedName("vpc_TransNo")
		private String vpc_TransNo;
		/**
		 * 退款金额
		 */
		@SerializedName("vpc_Amount")
		private String vpc_Amount;

		public BizContent() {
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("BizContent{");
			sb.append("vpc_Version='").append(this.vpc_Version).append('\'');
			sb.append(", vpc_Command='").append(this.vpc_Command).append('\'');
			sb.append(", vpc_AccessCode='").append(this.vpc_AccessCode).append('\'');
			sb.append(", vpc_MerchTxnRef='").append(this.vpc_MerchTxnRef).append('\'');
			sb.append(", vpc_Merchant='").append(this.vpc_Merchant).append('\'');
			sb.append(", vpc_User='").append(this.vpc_User).append('\'');
			sb.append(", vpc_Password='").append(this.vpc_Password).append('\'');
			sb.append(", vpc_TransNo='").append(this.vpc_TransNo).append('\'');
			sb.append(", vpc_Amount='").append(this.vpc_Amount).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
