package com.unipay.builder;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.annotations.SerializedName;
import com.unipay.config.UnipayConfig;

import cn.tsinghua.sftp.util.StringUtil;

public class TradePayRequestBuilder extends RequestBuilder {
	private static final Log log = LogFactory.getLog(TradePayRequestBuilder.class);
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
		StringBuilder sb = new StringBuilder("TradePayRequestBuilder{");
		sb.append("bizContent=").append(this.bizContent);
		sb.append(", super=").append(super.toString());
		sb.append('}');
		return sb.toString();
	}

	public TradePayRequestBuilder() {
		this.bizContent.merchantid = UnipayConfig.MERCHANTID; // 商户代码
		this.bizContent.branchid = UnipayConfig.BRANCHID; // 分行代码
		this.bizContent.curcode = "01"; // 币种默认01－人民币
		this.bizContent.txcode = "520100"; // 交易码由建行统一分配为520100
		this.bizContent.type = "1"; // 接口类型：0- 非钓鱼接口，1- 防钓鱼接口
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
		sb.append("&TYPE=" + StringUtil.setNullToSpace(this.bizContent.type));
		sb.append("&PUB=" + StringUtil.setNullToSpace(this.bizContent.pub));
		sb.append("&GATEWAY=" + StringUtil.setNullToSpace(this.bizContent.gateway));
		sb.append("&CLIENTIP=" + StringUtil.setNullToSpace(this.bizContent.clientip));
		sb.append("&REGINFO=" + StringUtil.escape(this.bizContent.reginfo));
		sb.append("&PROINFO=" + StringUtil.escape(this.bizContent.proinfo));
		sb.append("&REFERER=" + StringUtil.setNullToSpace(this.bizContent.referer));
		if (!StringUtil.isEmpty(this.bizContent.timeout)) {
			sb.append("&TIMEOUT=" + StringUtil.setNullToSpace(this.bizContent.timeout));
		}
		if (!StringUtil.isEmpty(this.bizContent.issinscode)) {
			sb.append("&ISSINSCODE=" + StringUtil.setNullToSpace(this.bizContent.issinscode));
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
		sb.append("&TYPE=" + StringUtil.setNullToSpace(this.bizContent.type));
		sb.append("&GATEWAY=" + StringUtil.setNullToSpace(this.bizContent.gateway));
		sb.append("&CLIENTIP=" + StringUtil.setNullToSpace(this.bizContent.clientip));
		sb.append("&REGINFO=" + StringUtil.escape(this.bizContent.reginfo));
		sb.append("&PROINFO=" + StringUtil.escape(this.bizContent.proinfo));
		sb.append("&REFERER=" + StringUtil.setNullToSpace(this.bizContent.referer));
		if (!StringUtil.isEmpty(this.bizContent.timeout)) {
			sb.append("&TIMEOUT=" + StringUtil.setNullToSpace(this.bizContent.timeout));
		}
		if (!StringUtil.isEmpty(this.bizContent.issinscode)) {
			sb.append("&ISSINSCODE=" + StringUtil.setNullToSpace(this.bizContent.issinscode));
		}
		String params = sb.toString();
		String url = UnipayConfig.URL + "?" + params + "&MAC=" + createSign();
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

	public TradePayRequestBuilder setMerchantid(String merchantid) {
		this.bizContent.merchantid = merchantid;
		return this;
	}

	public String getPosid() {
		return this.bizContent.posid;
	}

	public TradePayRequestBuilder setPosid(String posid) {
		this.bizContent.posid = posid;
		return this;
	}

	public String getBranchid() {
		return this.bizContent.branchid;
	}

	public TradePayRequestBuilder setBranchid(String branchid) {
		this.bizContent.branchid = branchid;
		return this;
	}

	public String getOrderid() {
		return this.bizContent.orderid;
	}

	public TradePayRequestBuilder setOrderid(String orderid) {
		this.bizContent.orderid = orderid;
		return this;
	}

	public String getPayment() {
		return this.bizContent.payment;
	}

	public TradePayRequestBuilder setPayment(String payment) {
		this.bizContent.payment = payment;
		return this;
	}

	public String getCurcode() {
		return this.bizContent.curcode;
	}

	public TradePayRequestBuilder setCurcode(String curcode) {
		this.bizContent.curcode = curcode;
		return this;
	}

	public String getRemark1() {
		return this.bizContent.remark1;
	}

	public TradePayRequestBuilder setRemark1(String remark1) {
		this.bizContent.remark1 = remark1;
		return this;
	}

	public String getSubject() {
		return this.bizContent.remark2;
	}

	public TradePayRequestBuilder setRemark2(String remark2) {
		this.bizContent.remark2 = remark2;
		return this;
	}

	public String getTxcode() {
		return this.bizContent.txcode;
	}

	public TradePayRequestBuilder setTxcode(String txcode) {
		this.bizContent.txcode = txcode;
		return this;
	}

	public String getMac() {
		return this.bizContent.mac;
	}

	public TradePayRequestBuilder setMac(String mac) {
		this.bizContent.mac = mac;
		return this;
	}

	public String getType() {
		return this.bizContent.type;
	}

	public TradePayRequestBuilder setType(String type) {
		this.bizContent.type = type;
		return this;
	}

	public String getPub() {
		return this.bizContent.pub;
	}

	public TradePayRequestBuilder setPub(String pub) {
		this.bizContent.pub = StringUtil.right(pub, 30);
		return this;
	}

	public String getGateway() {
		return this.bizContent.gateway;
	}

	public TradePayRequestBuilder setGateway(String gateway) {
		this.bizContent.gateway = gateway;
		return this;
	}

	public String getClientip() {
		return this.bizContent.clientip;
	}

	public TradePayRequestBuilder setClientip(String clientip) {
		this.bizContent.clientip = clientip;
		return this;
	}

	public String getReginfo() {
		return this.bizContent.reginfo;
	}

	public TradePayRequestBuilder setReginfo(String reginfo) {
		this.bizContent.reginfo = reginfo;
		return this;
	}

	public String getProinfo() {
		return this.bizContent.proinfo;
	}

	public TradePayRequestBuilder setProinfo(String proinfo) {
		this.bizContent.proinfo = proinfo;
		return this;
	}

	public String getReferer() {
		return this.bizContent.referer;
	}

	public TradePayRequestBuilder setReferer(String referer) {
		this.bizContent.referer = referer;
		return this;
	}

	public String getTimeout() {
		return this.bizContent.timeout;
	}

	public TradePayRequestBuilder setTimeout(String timeout) {
		this.bizContent.timeout = timeout;
		return this;
	}

	public String getIssinscode() {
		return this.bizContent.issinscode;
	}

	public TradePayRequestBuilder setIssinscode(String issinscode) {
		this.bizContent.issinscode = issinscode;
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

		/** 备注1，非必输项 */
		@SerializedName("remark1")
		private String remark1;

		/** 备注2，非必输项 */
		@SerializedName("remark2")
		private String remark2;

		/** 交易码，必输项 */
		@SerializedName("txcode")
		private String txcode;

		/** MAC校验域，必输项 */
		@SerializedName("mac")
		private String mac;

		/** 接口类型，必输项 */
		@SerializedName("type")
		private String type = "1";

		/** 公钥后30位，仅作为源串参加MD5摘要，不作为参数传递 */
		@SerializedName("pub")
		private String pub;

		/** 网关类型，必输项，参数送CCB只有建行，银联送空值 */
		@SerializedName("gateway")
		private String gateway;

		/** 客户端IP，非必输项 */
		@SerializedName("clientip")
		private String clientip;

		/** 客户注册信息，非必输项 */
		@SerializedName("reginfo")
		private String reginfo;

		/** 商品信息，非必输项，客户购买的商品，中文需使用escape编码 */
		@SerializedName("proinfo")
		private String proinfo;

		/** 商户URL，非必输项，商户送空值即可，银行从后台读取商户设置的一级域名 */
		@SerializedName("referer")
		private String referer;

		/** 订单超时时间，非必输项，格式：YYYYMMDDHHMMSS，银行系统时间>TIMEOUT时拒绝交易，若送空值则不判断超时。 */
		@SerializedName("timeout")
		private String timeout;

		/** 银行代码，非必输项，打开了“跨行付商户端模式”开关的商户方可上送该字段，若上送了该字段则直接跳转至该字段代表的银行界面 */
		@SerializedName("issinscode")
		private String issinscode;

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
			sb.append(", remark1='").append(this.remark1).append('\'');
			sb.append(", remark2='").append(this.remark2).append('\'');
			sb.append(", txcode='").append(this.txcode).append('\'');
			sb.append(", mac='").append(this.mac).append('\'');
			sb.append(", type='").append(this.type).append('\'');
			sb.append(", pub='").append(this.pub).append('\'');
			sb.append(", gateway='").append(this.gateway).append('\'');
			sb.append(", clientip='").append(this.clientip).append('\'');
			sb.append(", reginfo='").append(this.reginfo).append('\'');
			sb.append(", proinfo='").append(this.proinfo).append('\'');
			sb.append(", referer='").append(this.referer).append('\'');
			sb.append(", timeout='").append(this.timeout).append('\'');
			sb.append(", issinscode='").append(this.issinscode).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
