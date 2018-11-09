package com.unipay.builder;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.annotations.SerializedName;
import com.unipay.config.UnipayConfig;

import cn.tsinghua.sftp.util.StringUtil;

public class B2BPayRequestBuilder extends RequestBuilder {
	private static final Log log = LogFactory.getLog(B2BPayRequestBuilder.class);
	private BizContent bizContent = new BizContent();

	public BizContent getBizContent() {
		return this.bizContent;
	}

	/** 校验必填项 */
	public boolean validate() {
		if (StringUtils.isEmpty(this.bizContent.merchantid)) {
			throw new NullPointerException("merchantid should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.posid)) {
			throw new NullPointerException("posid should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.branchid)) {
			throw new NullPointerException("branchid should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.orderid)) {
			throw new NullPointerException("orderid should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.payment)) {
			throw new NullPointerException("payment should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.curcode)) {
			throw new NullPointerException("curcode should not be NULL!");
		}
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("B2BPayRequestBuilder{");
		sb.append("bizContent=").append(this.bizContent);
		sb.append(", super=").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	public B2BPayRequestBuilder() {
		this.bizContent.merchantid = UnipayConfig.MERCHANTID; // 商户代码
		this.bizContent.branchid = UnipayConfig.BRANCHID; // 分行代码
		this.bizContent.curcode = "01"; // 币种默认01－人民币
		this.bizContent.txcode = "690401"; // B2B 默认交易码
	}

	/**
	 * 生成签名
	 *
	 * @param key
	 * @param charset
	 * @return
	 */
	public String createSign() {
		StringBuilder sb = new StringBuilder();
		sb.append("MERCHANTID=" + StringUtil.setNullToSpace(this.bizContent.merchantid));
		sb.append("&POSID=" + StringUtil.setNullToSpace(this.bizContent.posid));
		sb.append("&BRANCHID=" + StringUtil.setNullToSpace(this.bizContent.branchid));
		sb.append("&ORDERID=" + StringUtil.setNullToSpace(this.bizContent.orderid));
		sb.append("&PAYMENT=" + StringUtil.setNullToSpace(this.bizContent.payment));
		sb.append("&CURCODE=" + StringUtil.setNullToSpace(this.bizContent.curcode));
		sb.append("&TXCODE=" + StringUtil.setNullToSpace(this.bizContent.txcode));
		sb.append("&REMARK1=" + StringUtil.setNullToSpace(this.bizContent.remark1));
		sb.append("&REMARK2=" + StringUtil.setNullToSpace(this.bizContent.remark2));
		if (!StringUtil.isEmpty(this.bizContent.timeout)) {
			sb.append("&TIMEOUT=" + StringUtil.setNullToSpace(this.bizContent.timeout));
		}
		String text = sb.toString();
		log.debug(">>>>createSign>>>>text=" + text);
		String sign = DigestUtils.md5Hex(getContentBytes(text, UnipayConfig.CHARSET));
		log.debug(">>>>createSign>>>>sign=" + sign);
		return sign;
	}

	public String createRequestUrl() {
		if (!validate()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MERCHANTID=" + StringUtil.setNullToSpace(this.bizContent.merchantid));
		sb.append("&POSID=" + StringUtil.setNullToSpace(this.bizContent.posid));
		sb.append("&BRANCHID=" + StringUtil.setNullToSpace(this.bizContent.branchid));
		sb.append("&ORDERID=" + StringUtil.setNullToSpace(this.bizContent.orderid));
		sb.append("&PAYMENT=" + StringUtil.setNullToSpace(this.bizContent.payment));
		sb.append("&CURCODE=" + StringUtil.setNullToSpace(this.bizContent.curcode));
		sb.append("&TXCODE=" + StringUtil.setNullToSpace(this.bizContent.txcode));
		sb.append("&REMARK1=" + StringUtil.setNullToSpace(this.bizContent.remark1));
		sb.append("&REMARK2=" + StringUtil.setNullToSpace(this.bizContent.remark2));
		if (!StringUtil.isEmpty(this.bizContent.timeout)) {
			sb.append("&TIMEOUT=" + StringUtil.setNullToSpace(this.bizContent.timeout));
		}
		String params = sb.toString();
		String url = UnipayConfig.B2B_URL + "?" + params + "&MAC=" + createSign();
		log.debug(">>>>createRequestUrl>>>>url=" + url);
		return url;
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	public String getMerchantid() {
		return this.bizContent.merchantid;
	}

	public B2BPayRequestBuilder setMerchantid(String merchantid) {
		this.bizContent.merchantid = merchantid;
		return this;
	}

	public String getPosid() {
		return this.bizContent.posid;
	}

	public B2BPayRequestBuilder setPosid(String posid) {
		this.bizContent.posid = posid;
		return this;
	}

	public String getBranchid() {
		return this.bizContent.branchid;
	}

	public B2BPayRequestBuilder setBranchid(String branchid) {
		this.bizContent.branchid = branchid;
		return this;
	}

	public String getOrderid() {
		return this.bizContent.orderid;
	}

	public B2BPayRequestBuilder setOrderid(String orderid) {
		this.bizContent.orderid = orderid;
		return this;
	}

	public String getPayment() {
		return this.bizContent.payment;
	}

	public B2BPayRequestBuilder setPayment(String payment) {
		this.bizContent.payment = payment;
		return this;
	}

	public String getCurcode() {
		return this.bizContent.curcode;
	}

	public B2BPayRequestBuilder setCurcode(String curcode) {
		this.bizContent.curcode = curcode;
		return this;
	}

	public String getRemark1() {
		return this.bizContent.remark1;
	}

	public B2BPayRequestBuilder setRemark1(String remark1) {
		this.bizContent.remark1 = remark1;
		return this;
	}

	public String getSubject() {
		return this.bizContent.remark2;
	}

	public B2BPayRequestBuilder setRemark2(String remark2) {
		this.bizContent.remark2 = remark2;
		return this;
	}

	public String getTxcode() {
		return this.bizContent.txcode;
	}

	public B2BPayRequestBuilder setTxcode(String txcode) {
		this.bizContent.txcode = txcode;
		return this;
	}

	public String getMac() {
		return this.bizContent.mac;
	}

	public B2BPayRequestBuilder setMac(String mac) {
		this.bizContent.mac = mac;
		return this;
	}

	public String getTimeout() {
		return this.bizContent.timeout;
	}

	public B2BPayRequestBuilder setTimeout(String timeout) {
		this.bizContent.timeout = timeout;
		return this;
	}

	public static class BizContent {
		/** 商户代码，必输项 */
		@SerializedName("merchantid")
		private String merchantid;

		/** 商户柜台代码，必输项 */
		@SerializedName("posid")
		private String posid;

		/** 分行代码，必输项 */
		@SerializedName("branchid")
		private String branchid;

		/** 定单号，必输项 */
		@SerializedName("orderid")
		private String orderid;

		/** 付款金额，必输项 */
		@SerializedName("payment")
		private String payment;

		/** 币种，必输项，缺省为01－人民币（只支持人民币支付） */
		@SerializedName("curcode")
		private String curcode = "01";

		/** 交易码，必输项 */
		@SerializedName("txcode")
		private String txcode;

		/** 备注1，非必输项 */
		@SerializedName("remark1")
		private String remark1;

		/** 备注2，非必输项 */
		@SerializedName("remark2")
		private String remark2;

		/** 订单超时时间，非必输项，格式：YYYYMMDDHHMMSS，银行系统时间>TIMEOUT时拒绝交易，若送空值则不判断超时。 */
		@SerializedName("timeout")
		private String timeout;

		/** MAC校验域，必输项 */
		@SerializedName("mac")
		private String mac;

		public BizContent() {
		}

		public String toString() {
			StringBuilder sb = new StringBuilder("BizContent{");
			sb.append("merchantid='").append(this.merchantid).append('\'');
			sb.append(", posid='").append(this.posid).append('\'');
			sb.append(", branchid='").append(this.branchid).append('\'');
			sb.append(", orderid='").append(this.orderid).append('\'');
			sb.append(", payment='").append(this.payment).append('\'');
			sb.append(", curcode='").append(this.curcode).append('\'');
			sb.append(", txcode='").append(this.txcode).append('\'');
			sb.append(", remark1='").append(this.remark1).append('\'');
			sb.append(", remark2='").append(this.remark2).append('\'');
			sb.append(", timeout='").append(this.timeout).append('\'');
			sb.append(", mac='").append(this.mac).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
