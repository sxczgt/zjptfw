package com.unipay.model.xml.TX5W1006;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1006 E付通授权信息查询
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1006INFOBean {

	/***************** 请求参数 *****************/
	String GRANTNUM; // 授权号 varChar(20) F
	String BRANCHID; // 分行号 varChar(9) F 商户所属分行号

	/***************** 响应参数 *****************/
	String MER_CODE; // 商户代码 varChar(21) T
	String AUTH_DATE; // 授权日期 varChar(10) T
	String GRANT_NUM; // 授权号 varChar(20) T
	String CUSTOMER_NAME; // 客户姓名 varChar(40) T
	String SINGLE_TRAN_LIMIT; // 单笔交易限额 Decimal(16,2) T
	String DAY_TRAN_LIMIT; // 日累计交易限额 Decimal(16,2) T
	String GRANT_FLAG; // 预留字段 T 预留字段，暂返回为空

	/** 默认请求参数 */
	public void setDefaultRequest() {
	}

	public String getGRANTNUM() {
		return GRANTNUM;
	}

	@XmlElement(name = "GRANTNUM")
	public void setGRANTNUM(String gRANTNUM) {
		GRANTNUM = gRANTNUM;
	}

	public String getBRANCHID() {
		return BRANCHID;
	}

	@XmlElement(name = "BRANCHID")
	public void setBRANCHID(String bRANCHID) {
		BRANCHID = bRANCHID;
	}

	public String getMER_CODE() {
		return MER_CODE;
	}

	@XmlElement(name = "MER_CODE")
	public void setMER_CODE(String mER_CODE) {
		MER_CODE = mER_CODE;
	}

	public String getAUTH_DATE() {
		return AUTH_DATE;
	}

	@XmlElement(name = "AUTH_DATE")
	public void setAUTH_DATE(String aUTH_DATE) {
		AUTH_DATE = aUTH_DATE;
	}

	public String getGRANT_NUM() {
		return GRANT_NUM;
	}

	@XmlElement(name = "GRANT_NUM")
	public void setGRANT_NUM(String gRANT_NUM) {
		GRANT_NUM = gRANT_NUM;
	}

	public String getCUSTOMER_NAME() {
		return CUSTOMER_NAME;
	}

	@XmlElement(name = "CUSTOMER_NAME")
	public void setCUSTOMER_NAME(String cUSTOMER_NAME) {
		CUSTOMER_NAME = cUSTOMER_NAME;
	}

	public String getSINGLE_TRAN_LIMIT() {
		return SINGLE_TRAN_LIMIT;
	}

	@XmlElement(name = "SINGLE_TRAN_LIMIT")
	public void setSINGLE_TRAN_LIMIT(String sINGLE_TRAN_LIMIT) {
		SINGLE_TRAN_LIMIT = sINGLE_TRAN_LIMIT;
	}

	public String getDAY_TRAN_LIMIT() {
		return DAY_TRAN_LIMIT;
	}

	@XmlElement(name = "DAY_TRAN_LIMIT")
	public void setDAY_TRAN_LIMIT(String dAY_TRAN_LIMIT) {
		DAY_TRAN_LIMIT = dAY_TRAN_LIMIT;
	}

	public String getGRANT_FLAG() {
		return GRANT_FLAG;
	}

	@XmlElement(name = "GRANT_FLAG")
	public void setGRANT_FLAG(String gRANT_FLAG) {
		GRANT_FLAG = gRANT_FLAG;
	}

}
