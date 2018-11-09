package com.unipay.builder;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.annotations.SerializedName;
import com.unipay.config.UnipayConfig;

import cn.tsinghua.sftp.util.StringUtil;

public class TradeQueryRequestBuilder extends RequestBuilder {
	private static final Log log = LogFactory.getLog(TradeQueryRequestBuilder.class);
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
		if (StringUtils.isEmpty(this.bizContent.type)) {
			throw new NullPointerException("type should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.orderid) && StringUtils.isEmpty(this.bizContent.orderdate)) {
			throw new NullPointerException("orderdate or orderid should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.qupwd)) {
			throw new NullPointerException("qupwd should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.txcode)) {
			throw new NullPointerException("txcode should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.kind)) {
			throw new NullPointerException("kind should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.status)) {
			throw new NullPointerException("status should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.sel_type)) {
			throw new NullPointerException("sel_type should not be NULL!");
		}
		if (StringUtils.isEmpty(this.bizContent.page)) {
			throw new NullPointerException("page should not be NULL!");
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

	public TradeQueryRequestBuilder() {
		this.bizContent.merchantid = UnipayConfig.MERCHANTID; // 商户代码
		this.bizContent.branchid = UnipayConfig.BRANCHID; // 分行代码
		this.bizContent.txcode = "410408"; // 交易码TXCODE=410408，这个参数的值是固定的，不可以修改
		this.bizContent.operator = "123";
		this.bizContent.qupwd = UnipayConfig.QUPWD;
		// this.bizContent.type = "0"; // 0支付流水 1退款流水
		// this.bizContent.sel_type = "3";
		this.bizContent.page = "1";
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
		sb.append("&BRANCHID=" + StringUtil.setNullToSpace(this.bizContent.branchid));
		sb.append("&POSID=" + StringUtil.setNullToSpace(this.bizContent.posid));
		sb.append("&ORDERDATE=" + StringUtil.setNullToSpace(this.bizContent.orderdate));
		sb.append("&BEGORDERTIME=" + StringUtil.setNullToSpace(this.bizContent.begordertime));
		sb.append("&ENDORDERTIME=" + StringUtil.setNullToSpace(this.bizContent.endordertime));
		sb.append("&ORDERID=" + StringUtil.setNullToSpace(this.bizContent.orderid));
		sb.append("&QUPWD=");
		sb.append("&TXCODE=" + StringUtil.setNullToSpace(this.bizContent.txcode));
		sb.append("&TYPE=" + StringUtil.setNullToSpace(this.bizContent.type));
		sb.append("&KIND=" + StringUtil.setNullToSpace(this.bizContent.kind));
		sb.append("&STATUS=" + StringUtil.setNullToSpace(this.bizContent.status));
		sb.append("&SEL_TYPE=" + StringUtil.setNullToSpace(this.bizContent.sel_type));
		sb.append("&PAGE=" + StringUtil.setNullToSpace(this.bizContent.page));
		sb.append("&OPERATOR=" + StringUtil.setNullToSpace(this.bizContent.operator));
		sb.append("&CHANNEL=" + StringUtil.setNullToSpace(this.bizContent.channel));
		String text = sb.toString();
		String sign = DigestUtils.md5Hex(getContentBytes(text, UnipayConfig.CHARSET));
		log.debug(">>>>TradeQueryRequestBuilder.createSign>>>>" + text + "&MAC=" + sign);
		return sign;
	}

	public String createRequestUrl() {
		if (!validate()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MERCHANTID=" + StringUtil.setNullToSpace(this.bizContent.merchantid));
		sb.append("&BRANCHID=" + StringUtil.setNullToSpace(this.bizContent.branchid));
		sb.append("&POSID=" + StringUtil.setNullToSpace(this.bizContent.posid));
		sb.append("&ORDERDATE=" + StringUtil.setNullToSpace(this.bizContent.orderdate));
		sb.append("&BEGORDERTIME=" + StringUtil.setNullToSpace(this.bizContent.begordertime));
		sb.append("&ENDORDERTIME=" + StringUtil.setNullToSpace(this.bizContent.endordertime));
		sb.append("&ORDERID=" + StringUtil.setNullToSpace(this.bizContent.orderid));
		sb.append("&QUPWD=" + StringUtil.setNullToSpace(this.bizContent.qupwd));
		sb.append("&TXCODE=" + StringUtil.setNullToSpace(this.bizContent.txcode));
		sb.append("&TYPE=" + StringUtil.setNullToSpace(this.bizContent.type));
		sb.append("&KIND=" + StringUtil.setNullToSpace(this.bizContent.kind));
		sb.append("&STATUS=" + StringUtil.setNullToSpace(this.bizContent.status));
		sb.append("&SEL_TYPE=" + StringUtil.setNullToSpace(this.bizContent.sel_type));
		sb.append("&PAGE=" + StringUtil.setNullToSpace(this.bizContent.page));
		sb.append("&OPERATOR=" + StringUtil.setNullToSpace(this.bizContent.operator));
		sb.append("&CHANNEL=" + StringUtil.setNullToSpace(this.bizContent.channel));
		String params = sb.toString();
		String url = UnipayConfig.URL + "?" + params + "&MAC=" + createSign();
		log.debug(">>>>TradeQueryRequestBuilder.createRequestUrl>>>>" + url);
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

	public TradeQueryRequestBuilder setMerchantid(String merchantid) {
		this.bizContent.merchantid = merchantid;
		return this;
	}

	public String getPosid() {
		return this.bizContent.posid;
	}

	public TradeQueryRequestBuilder setPosid(String posid) {
		this.bizContent.posid = posid;
		return this;
	}

	public String getBranchid() {
		return this.bizContent.branchid;
	}

	public TradeQueryRequestBuilder setBranchid(String branchid) {
		this.bizContent.branchid = branchid;
		return this;
	}

	public String getOrderid() {
		return this.bizContent.orderid;
	}

	public TradeQueryRequestBuilder setOrderid(String orderid) {
		this.bizContent.orderid = orderid;
		return this;
	}

	public String getOrderdate() {
		return this.bizContent.orderdate;
	}

	public TradeQueryRequestBuilder setOrderdate(String orderdate) {
		this.bizContent.orderdate = orderdate;
		return this;
	}

	public String getBegordertime() {
		return this.bizContent.begordertime;
	}

	public TradeQueryRequestBuilder setBegordertime(String begordertime) {
		this.bizContent.begordertime = begordertime;
		return this;
	}

	public String getEndordertime() {
		return this.bizContent.endordertime;
	}

	public TradeQueryRequestBuilder setEndordertime(String endordertime) {
		this.bizContent.endordertime = endordertime;
		return this;
	}

	public String getQupwd() {
		return this.bizContent.qupwd;
	}

	public TradeQueryRequestBuilder setQupwd(String qupwd) {
		this.bizContent.qupwd = qupwd;
		return this;
	}

	public String getType() {
		return this.bizContent.type;
	}

	public TradeQueryRequestBuilder setType(String type) {
		this.bizContent.type = type;
		return this;
	}

	public String getTxcode() {
		return this.bizContent.txcode;
	}

	public TradeQueryRequestBuilder setTxcode(String txcode) {
		this.bizContent.txcode = txcode;
		return this;
	}

	public String getKind() {
		return this.bizContent.kind;
	}

	public TradeQueryRequestBuilder setKind(String kind) {
		this.bizContent.kind = kind;
		return this;
	}

	public String getStatus() {
		return this.bizContent.status;
	}

	public TradeQueryRequestBuilder setStatus(String status) {
		this.bizContent.status = status;
		return this;
	}

	public String getSel_type() {
		return this.bizContent.sel_type;
	}

	public TradeQueryRequestBuilder setSel_type(String sel_type) {
		this.bizContent.sel_type = sel_type;
		return this;
	}

	public String getPage() {
		return this.bizContent.page;
	}

	public TradeQueryRequestBuilder setPage(String page) {
		this.bizContent.page = page;
		return this;
	}

	public String getOperator() {
		return this.bizContent.operator;
	}

	public TradeQueryRequestBuilder setOperator(String operator) {
		this.bizContent.operator = operator;
		return this;
	}

	public String getChannel() {
		return this.bizContent.channel;
	}

	public TradeQueryRequestBuilder setChannel(String channel) {
		this.bizContent.channel = channel;
		return this;
	}

	public String getMac() {
		return this.bizContent.mac;
	}

	public TradeQueryRequestBuilder setMac(String mac) {
		this.bizContent.mac = mac;
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

		/** 定单号，非必输项，ORDERID元素必须有,但值可为空 ORDERDATE与ORDERID必须有一个输入 */
		@SerializedName("orderid")
		private String orderid;

		/** 定单日期，非必输项，OPERATOR元素必须有,但值可为空 ORDERDATE与ORDERID必须有一个输入 */
		@SerializedName("orderdate")
		private String orderdate;

		/** 定单开始时间，非必输项 ，begordertime元素必须有,但值可为空 */
		@SerializedName("begordertime")
		private String begordertime;

		/** 定单截止时间，非必输项，endordertime元素必须有,但值可为空 */
		@SerializedName("endordertime")
		private String endordertime;

		/** 查询密码，必输项 必输项,主管或操作员的登录密码 */
		@SerializedName("qupwd")
		private String qupwd;

		/** 流水类型，0支付流水 1退款流水 必输项 */
		@SerializedName("type")
		private String type;

		/** 交易码，必输项 */
		@SerializedName("txcode")
		private String txcode;

		/** 流水状态，必输项 */
		@SerializedName("kind")
		private String kind;

		/** 交易状态，必输项 */
		@SerializedName("status")
		private String status;

		/** 查询方式，必输项 */
		@SerializedName("sel_type")
		private String sel_type;

		/** 页码，必输项 */
		@SerializedName("page")
		private String page;

		/** 操作员 */
		@SerializedName("operator")
		private String operator;

		/** 预留字段，非必输项 */
		@SerializedName("channel")
		private String channel;

		/** 校验信息 */
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
			sb.append(", orderdate='").append(this.orderdate).append('\'');
			sb.append(", begordertime='").append(this.begordertime).append('\'');
			sb.append(", endordertime='").append(this.endordertime).append('\'');
			sb.append(", qupwd='").append(this.qupwd).append('\'');
			sb.append(", type='").append(this.type).append('\'');
			sb.append(", txcode='").append(this.txcode).append('\'');
			sb.append(", kind='").append(this.kind).append('\'');
			sb.append(", status='").append(this.status).append('\'');
			sb.append(", sel_type='").append(this.sel_type).append('\'');
			sb.append(", page='").append(this.page).append('\'');
			sb.append(", operator='").append(this.operator).append('\'');
			sb.append(", channel='").append(this.channel).append('\'');
			sb.append(", mac='").append(this.mac).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
