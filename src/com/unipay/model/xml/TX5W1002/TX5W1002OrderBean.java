package com.unipay.model.xml.TX5W1002;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LIST")
public class TX5W1002OrderBean {
	String TRAN_DATE; // 交易日期 varChar(32) T
	String ACC_DATE; // 记账日期 varChar(32) T
	String ORDER; // 订单号 varChar(30) T
	String ACCOUNT; // 付款方账号 varChar(30) T
	String PAYMENT_MONEY; // 支付金额 Decimal(16,2) T
	String REFUND_MONEY; // 退款金额 Decimal(16,2) T
	String POS_ID; // 柜台号 varChar(9) T
	String REM1; // 备注1 varChar(32) T
	String REM2; // 备注2 varChar(32) T
	String ORDER_STATUS; // 订单状态 Char(1) T 0:失败,1:成功,2:待银行确认,3:已部分退款,4:已全额退款,5:待银行确认

	public String getTRAN_DATE() {
		return TRAN_DATE;
	}

	public void setTRAN_DATE(String tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
	}

	public String getACC_DATE() {
		return ACC_DATE;
	}

	public void setACC_DATE(String aCC_DATE) {
		ACC_DATE = aCC_DATE;
	}

	public String getORDER() {
		return ORDER;
	}

	public void setORDER(String oRDER) {
		ORDER = oRDER;
	}

	public String getACCOUNT() {
		return ACCOUNT;
	}

	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public String getPAYMENT_MONEY() {
		return PAYMENT_MONEY;
	}

	public void setPAYMENT_MONEY(String pAYMENT_MONEY) {
		PAYMENT_MONEY = pAYMENT_MONEY;
	}

	public String getREFUND_MONEY() {
		return REFUND_MONEY;
	}

	public void setREFUND_MONEY(String rEFUND_MONEY) {
		REFUND_MONEY = rEFUND_MONEY;
	}

	public String getPOS_ID() {
		return POS_ID;
	}

	public void setPOS_ID(String pOS_ID) {
		POS_ID = pOS_ID;
	}

	public String getREM1() {
		return REM1;
	}

	public void setREM1(String rEM1) {
		REM1 = rEM1;
	}

	public String getREM2() {
		return REM2;
	}

	public void setREM2(String rEM2) {
		REM2 = rEM2;
	}

	public String getORDER_STATUS() {
		return ORDER_STATUS;
	}

	public void setORDER_STATUS(String oRDER_STATUS) {
		ORDER_STATUS = oRDER_STATUS;
	}

}
