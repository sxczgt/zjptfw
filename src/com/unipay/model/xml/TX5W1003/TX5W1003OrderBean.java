package com.unipay.model.xml.TX5W1003;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LIST")
public class TX5W1003OrderBean {
	List<String> TRAN_DATE; // 交易日期 varChar(32) T
	List<String> REFUND_DATE; // 退款日期 varChar(32) T
	List<String> ORDER_NUMBER; // 订单号 varChar(30) T
	List<String> REFUND_ACCOUNT; // 退款账号 T
	List<String> PAY_AMOUNT; // 支付金额 Decimal(16,2) T
	List<String> REFUNDEMENT_AMOUNT; // 退款金额 Decimal(16,2) T
	List<String> POS_CODE; // 柜台号 varChar(9) T
	List<String> USERID; // 操作员 varChar(21) T
	List<String> STATUS; // 订单状态 char(1) T 0:失败,1:成功,2:待银行确认,5:待银行确认
	List<String> ORDER_STATUS; // 订单状态 Char(1) T 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认
	List<String> REFUND_CODE; // 退款流水号 Char(1) varChar(15) T 商户退款时上送的退款流水号，无上送则不展示

	public List<String> getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(List<String> tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
	}

	public List<String> getREFUND_DATE() {
		return REFUND_DATE;
	}

	public void setREFUND_DATE(List<String> rEFUND_DATE) {
		REFUND_DATE = rEFUND_DATE;
	}

	public List<String> getORDER_NUMBER() {
		return ORDER_NUMBER;
	}

	public void setORDER_NUMBER(List<String> oRDER_NUMBER) {
		ORDER_NUMBER = oRDER_NUMBER;
	}

	public List<String> getREFUND_ACCOUNT() {
		return REFUND_ACCOUNT;
	}

	public void setREFUND_ACCOUNT(List<String> rEFUND_ACCOUNT) {
		REFUND_ACCOUNT = rEFUND_ACCOUNT;
	}

	public List<String> getPAY_AMOUNT() {
		return PAY_AMOUNT;
	}

	public void setPAY_AMOUNT(List<String> pAY_AMOUNT) {
		PAY_AMOUNT = pAY_AMOUNT;
	}

	public List<String> getREFUNDEMENT_AMOUNT() {
		return REFUNDEMENT_AMOUNT;
	}

	public void setREFUNDEMENT_AMOUNT(List<String> rEFUNDEMENT_AMOUNT) {
		REFUNDEMENT_AMOUNT = rEFUNDEMENT_AMOUNT;
	}

	public List<String> getPOS_CODE() {
		return POS_CODE;
	}

	public void setPOS_CODE(List<String> pOS_CODE) {
		POS_CODE = pOS_CODE;
	}

	public List<String> getUSERID() {
		return USERID;
	}

	public void setUSERID(List<String> uSERID) {
		USERID = uSERID;
	}

	public List<String> getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(List<String> sTATUS) {
		STATUS = sTATUS;
	}

	public List<String> getORDER_STATUS() {
		return ORDER_STATUS;
	}

	public void setORDER_STATUS(List<String> oRDER_STATUS) {
		ORDER_STATUS = oRDER_STATUS;
	}

	public List<String> getREFUND_CODE() {
		return REFUND_CODE;
	}

	public void setREFUND_CODE(List<String> rEFUND_CODE) {
		REFUND_CODE = rEFUND_CODE;
	}

}
