package com.bocpay.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;

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

	/** 获取当前类的Field名 */
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
