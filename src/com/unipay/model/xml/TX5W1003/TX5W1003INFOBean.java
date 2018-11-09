package com.unipay.model.xml.TX5W1003;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1003 商户退款流水查询
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1003INFOBean {

	/***************** 请求参数 *****************/
	String START; // 起始日期 varChar(8) T
	String STARTHOUR; // 开始小时 varChar(2) T
	String STARTMIN; // 开始分钟 varChar(2) T
	String END; // 截止日期 varChar(8) T
	String ENDHOUR; // 结束小时varChar(2) T
	String ENDMIN; // 结束分钟varChar(2) T
	String KIND; // 流水类型 Char(1) F 0:未结流水,1:已结流水
	String ORDER; // 订单号 varChar(30) F 按订单号查询时，时间段不起作用
	String ACCOUNT; // 结算账户号 varChar(30) T 暂不用
	String MONEY; // 金额 Decimal(16,2) T 不支持以金额查询
	String NORDERBY; // 排序 Char(1) F 1:交易日期,2:订单号
	String PAGE; // 当前页次 Int F
	String POS_CODE; // 柜台号 varChar(9) T
	String STATUS; // 流水状态 Char(1) F 0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部

	/***************** 响应参数 *****************/
	String CUR_PAGE; // 当前页次 Int T
	String TPAGE; // 总页次 Int T
	String NOTICE; // 提示 varChar(200) T 提示信息
	List<TX5W1003OrderBean> LIST; // 订单列表

	/** 默认请求参数 */
	public void setDefaultRequest() {
		setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		setNORDERBY("1"); // 排序：1:交易日期,2:订单号
		setPAGE("1"); // 当前页次
		setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
	}

	public String getSTART() {
		return START;
	}

	@XmlElement(name = "START")
	public void setSTART(String sTART) {
		START = sTART;
	}

	public String getSTARTHOUR() {
		return STARTHOUR;
	}

	@XmlElement(name = "STARTHOUR")
	public void setSTARTHOUR(String sTARTHOUR) {
		STARTHOUR = sTARTHOUR;
	}

	public String getSTARTMIN() {
		return STARTMIN;
	}

	@XmlElement(name = "STARTMIN")
	public void setSTARTMIN(String sTARTMIN) {
		STARTMIN = sTARTMIN;
	}

	public String getEND() {
		return END;
	}

	@XmlElement(name = "END")
	public void setEND(String eND) {
		END = eND;
	}

	public String getENDHOUR() {
		return ENDHOUR;
	}

	@XmlElement(name = "ENDHOUR")
	public void setENDHOUR(String eNDHOUR) {
		ENDHOUR = eNDHOUR;
	}

	public String getENDMIN() {
		return ENDMIN;
	}

	@XmlElement(name = "ENDMIN")
	public void setENDMIN(String eNDMIN) {
		ENDMIN = eNDMIN;
	}

	public String getKIND() {
		return KIND;
	}

	@XmlElement(name = "KIND")
	public void setKIND(String kIND) {
		KIND = kIND;
	}

	public String getORDER() {
		return ORDER;
	}

	@XmlElement(name = "ORDER")
	public void setORDER(String oRDER) {
		ORDER = oRDER;
	}

	public String getACCOUNT() {
		return ACCOUNT;
	}

	@XmlElement(name = "ACCOUNT")
	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public String getMONEY() {
		return MONEY;
	}

	@XmlElement(name = "MONEY")
	public void setMONEY(String mONEY) {
		MONEY = mONEY;
	}

	public String getNORDERBY() {
		return NORDERBY;
	}

	@XmlElement(name = "NORDERBY")
	public void setNORDERBY(String nORDERBY) {
		NORDERBY = nORDERBY;
	}

	public String getPAGE() {
		return PAGE;
	}

	@XmlElement(name = "PAGE")
	public void setPAGE(String pAGE) {
		PAGE = pAGE;
	}

	public String getPOS_CODE() {
		return POS_CODE;
	}

	@XmlElement(name = "POS_CODE")
	public void setPOS_CODE(String pOS_CODE) {
		POS_CODE = pOS_CODE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	@XmlElement(name = "STATUS")
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getCUR_PAGE() {
		return CUR_PAGE;
	}

	@XmlElement(name = "CUR_PAGE")
	public void setCUR_PAGE(String cUR_PAGE) {
		CUR_PAGE = cUR_PAGE;
	}

	public String getTPAGE() {
		return TPAGE;
	}

	@XmlElement(name = "TPAGE")
	public void setTPAGE(String tPAGE) {
		TPAGE = tPAGE;
	}

	public String getNOTICE() {
		return NOTICE;
	}

	@XmlElement(name = "NOTICE")
	public void setNOTICE(String nOTICE) {
		NOTICE = nOTICE;
	}

	public List<TX5W1003OrderBean> getLIST() {
		return LIST;
	}

	@XmlElement(name = "LIST")
	public void setLIST(List<TX5W1003OrderBean> LIST) {
		this.LIST = LIST;
	}

}
