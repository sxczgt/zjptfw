package com.unipay.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import com.unipay.config.UnipayConfig;
import com.unipay.utils.RSASign;

import cn.tsinghua.sftp.util.StringUtil;

public class QueryOrder {
	/** 商户代码 */
	String merchantId;
	/** 商户所在分行 */
	String branchId;
	/** 商户的POS号 */
	String posId;
	/** 订单号 */
	String orderId;
	/** 支付/退款交易时间 */
	String orderDate;
	/** 记账日期 */
	String accDate;
	/** 支付金额 */
	BigDecimal amount;
	/** 支付/退款状态码 */
	String statusCode;
	/** 支付/退款状态 */
	String status;
	/** 退款金额 */
	BigDecimal refund;
	/** 签名串 */
	String sign;
	/** 支付流水支付类型 */
	String paymentType;

	public static void main(String[] args) {
		// <?xml version = "1.0" encoding="UTF-8" ?>
		// <DOCUMENT>
		// <RETURN_CODE>000000</RETURN_CODE>
		// <RETURN_MSG></RETURN_MSG>
		// <CURPAGE>1</CURPAGE>
		// <PAGECOUNT>1</PAGECOUNT>
		// <TOTAL>1</TOTAL>
		// <PAYAMOUNT>0.00</PAYAMOUNT>
		// <REFUNDAMOUNT>0.00</REFUNDAMOUNT>
		// <QUERYORDER>
		// <MERCHANTID>105100000012072</MERCHANTID>
		// <BRANCHID>110000000</BRANCHID>
		// <POSID>004161878</POSID>
		// <ORDERID>2999DEMO20171110144132</ORDERID>
		// <ORDERDATE>20171110144235</ORDERDATE>
		// <ACCDATE>20171110</ACCDATE>
		// <AMOUNT>0.01</AMOUNT>
		// <STATUSCODE>1</STATUSCODE>
		// <STATUS>成功</STATUS>
		// <REFUND>0.00</REFUND>
		// <SIGN>99584bd5e663ecba113edf45a6dfa536e79368734bdc2554ff9ef44a9b5bec680ca7752aae446681d6218b25372e1a7b470e31c960679535f97c38c9e45413b15aa02934bd1c9ebc8a0a48031459be7a9b8097e4653892d4dc16bd237f172a64056d7a3416ec97deb7d59d2bf700e232c031dc585de1563864c019a0288ab4d9</SIGN>
		// </QUERYORDER>
		// </DOCUMENT>
		QueryOrder q = new QueryOrder();
		q.setMerchantId("105100000012072");
		q.setBranchId("110000000");
		q.setPosId("004161878");
		q.setOrderId("2999DEMO20171110144132");
		q.setOrderDate("20171110144235");
		q.setAccDate("20171110");
		q.setAmount(new BigDecimal("0.01"));
		q.setStatusCode("1");
		q.setStatus("成功");
		q.setRefund(new BigDecimal("0.00"));
		q.setSign("99584bd5e663ecba113edf45a6dfa536e79368734bdc2554ff9ef44a9b5bec680ca7752aae446681d6218b25372e1a7b470e31c960679535f97c38c9e45413b15aa02934bd1c9ebc8a0a48031459be7a9b8097e4653892d4dc16bd237f172a64056d7a3416ec97deb7d59d2bf700e232c031dc585de1563864c019a0288ab4d9");
		q.setPaymentType("41");
		System.out.println("签名结果：" + q.checkSign());
	}

	/** 验证签名 */
	public boolean checkSign() {
		StringBuilder sb = new StringBuilder();
		sb.append("MERCHANTID=" + StringUtil.setNullToSpace(merchantId));
		sb.append("&BRANCHID=" + StringUtil.setNullToSpace(branchId));
		sb.append("&POSID=" + StringUtil.setNullToSpace(posId));
		sb.append("&ORDERID=" + StringUtil.setNullToSpace(orderId));
		sb.append("&ORDERDATE=" + StringUtil.setNullToSpace(orderDate));
		sb.append("&ACCDATE=" + StringUtil.setNullToSpace(accDate));
		sb.append("&AMOUNT=" + (amount.equals(0) ? "" : String.valueOf(amount)));
		sb.append("&STATUSCODE=" + StringUtil.setNullToSpace(statusCode));
		sb.append("&STATUS=" + StringUtil.setNullToSpace(status));
		sb.append("&REFUND=" + (refund.equals(0) ? "" : String.valueOf(refund)));
		String str = sb.toString();
		// 只有指定支付类型时才验签
		if (StringUtil.isNotEmpty(paymentType)) {
			// 获取验证Key
			String key = UnipayConfig.getPublicKey(paymentType);
			// 生成签名
			RSASign rsa = new RSASign();
			rsa.setPublicKey(key);
			if (rsa.verifySigature(sign, str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前类的Field名
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String getFieldName(String fieldName) {
		Field[] fields = QueryOrder.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			if (fieldName.equalsIgnoreCase(name)) {
				return name;
			}
		}
		return fieldName;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getAccDate() {
		return accDate;
	}

	public void setAccDate(String accDate) {
		this.accDate = accDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRefund() {
		return refund;
	}

	public void setRefund(BigDecimal refund) {
		this.refund = refund;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

}
