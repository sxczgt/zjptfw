package com.unipay.model.xml;

/**
 * 外联平台 报文头
 */
public abstract class BaseTXBean {
	String REQUEST_SN; // 请求序列码
	String CUST_ID; // 商户号
	String USER_ID; // 操作员号
	String PASSWORD; // 密码
	String TX_CODE; // 交易码
	String LANGUAGE; // 语言
	String RETURN_CODE; // 返回代码
	String RETURN_MSG; // 返回信息

	public BaseTXBean() {
		setREQUEST_SN(String.valueOf(System.currentTimeMillis()));
		setCUST_ID("105100000012072");
		setUSER_ID("105100000012072-123");
		setPASSWORD("QhcwcCX123");
		setLANGUAGE("CN");
	}

	public String getREQUEST_SN() {
		return REQUEST_SN;
	}

	public void setREQUEST_SN(String rEQUEST_SN) {
		REQUEST_SN = rEQUEST_SN;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getTX_CODE() {
		return TX_CODE;
	}

	public void setTX_CODE(String tX_CODE) {
		TX_CODE = tX_CODE;
	}

	public String getLANGUAGE() {
		return LANGUAGE;
	}

	public void setLANGUAGE(String lANGUAGE) {
		LANGUAGE = lANGUAGE;
	}

	public String getRETURN_CODE() {
		return RETURN_CODE;
	}

	public void setRETURN_CODE(String rETURN_CODE) {
		RETURN_CODE = rETURN_CODE;
	}

	public String getRETURN_MSG() {
		return RETURN_MSG;
	}

	public void setRETURN_MSG(String rETURN_MSG) {
		RETURN_MSG = rETURN_MSG;
	}

}
