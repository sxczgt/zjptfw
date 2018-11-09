package com.unipay.model.xml.TX5W1005;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1005 商户流水文件下载
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1005INFOBean {

	/***************** 请求参数 *****************/
	String DATE; // 日期 varChar(8) F
	String KIND; // 流水类型 Char(1) F 0:未结流水,1:已结流水
	String FILETYPE; // 文件类型 Char(1) F 1：txt（默认），2：excel（一点接商户不支持excel文件格式下载）
	String TYPE; // 0：支付流水；1：退款流水
	String NORDERBY; // 排序 Char(1) T 1:交易日期,2:订单号
	String POS_CODE; // 柜台号 varChar(9) T不输入为全部
	String ORDER; // 订单号 varChar(30) T
	String STATUS; // 订单状态 Char(1) F 0：交易失败,1：交易成功,2：待银行确认(未结流水);3：全部(未结流水)
	String BILL_FLAG; // 对账单标志 Char(1) T 1：新对账单（且KIND为1时，新版对账单），0或空：旧对账单（默认）

	/***************** 响应参数 *****************/
	String FILE_NAME; // 文件名 varChar(60) T 通过6W0111交易进行文件下载
	String NOTICE; // 提示 varChar(200) T 提示信息

	/** 默认请求参数 */
	public void setDefaultRequest() {
		setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		setFILETYPE("1");// 文件类型：1：txt（默认），2：excel（一点接商户不支持excel文件格式下载）
		setNORDERBY("1"); // 排序：1:交易日期,2:订单号
		setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
	}

	public String getDATE() {
		return DATE;
	}

	@XmlElement(name = "DATE")
	public void setDATE(String dATE) {
		DATE = dATE;
	}

	public String getKIND() {
		return KIND;
	}

	@XmlElement(name = "KIND")
	public void setKIND(String kIND) {
		KIND = kIND;
	}

	public String getFILETYPE() {
		return FILETYPE;
	}

	@XmlElement(name = "FILETYPE")
	public void setFILETYPE(String fILETYPE) {
		FILETYPE = fILETYPE;
	}

	public String getTYPE() {
		return TYPE;
	}

	@XmlElement(name = "TYPE")
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getNORDERBY() {
		return NORDERBY;
	}

	@XmlElement(name = "NORDERBY")
	public void setNORDERBY(String nORDERBY) {
		NORDERBY = nORDERBY;
	}

	public String getPOS_CODE() {
		return POS_CODE;
	}

	@XmlElement(name = "POS_CODE")
	public void setPOS_CODE(String pOS_CODE) {
		POS_CODE = pOS_CODE;
	}

	public String getORDER() {
		return ORDER;
	}

	@XmlElement(name = "ORDER")
	public void setORDER(String oRDER) {
		ORDER = oRDER;
	}

	public String getSTATUS() {
		return STATUS;
	}

	@XmlElement(name = "STATUS")
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getBILL_FLAG() {
		return BILL_FLAG;
	}

	@XmlElement(name = "BILL_FLAG")
	public void setBILL_FLAG(String bILL_FLAG) {
		BILL_FLAG = bILL_FLAG;
	}

	public String getFILE_NAME() {
		return FILE_NAME;
	}

	@XmlElement(name = "FILE_NAME")
	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}

	public String getNOTICE() {
		return NOTICE;
	}

	@XmlElement(name = "NOTICE")
	public void setNOTICE(String nOTICE) {
		NOTICE = nOTICE;
	}

}
