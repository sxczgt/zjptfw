package com.unipay.model.xml.TX5W1007;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1007 外卡收单商户端mpi
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1007INFOBean {

	/***************** 请求参数 *****************/
	String CARD_LEN;// 卡号长度 varChar(2) F
	String ACCT_NO;// 卡号 varChar(19) F
	String AMT1;// 金额 varChar(14) F
	String EXPIRE_DATE;// 卡有效期 varChar(4) F 填入卡有效期YYMM
	String AGENT_BANK;// 受理行代码 varChar(11) F 接受人工电话授权的受理行
	String MERCHANT_NO;// 商户号 varChar(15) F “集团商户”的交易，上送“子商户编号”
	String CUNT_NO;// 柜台代码 varChar(9) F
	String CVV2;// 卡验证安全码 varChar(10) F
	String TERMINAL_FLOW;// 终端的流水号 varChar(6) F 前端生成的流水号(MPI返回)
	String TRADE_TIME;// 交易时间 varChar(12) F
	String TERMINAL_ID;// 终端号 varChar(8) F 如无，则填分行号的前8位
	String SECURE_LEV;// 安全级别 varChar(2) F
	String ORDER_NO;// 订单号 varChar(30) F
	String CAVV;// 持卡人认证信息 varChar(40) F
	String EXT1;// 附加域 varChar(5) T
	String ECSLI;// ECSLI值 varChar(3) T
	String CHECK_RESULT;// 验证场景 varChar(2) F
	String SIGN_INFO;// 签名信息 varChar(254) F
	String SIGNCERT;// 签名CA信息 varChar(254) F 建行客户端自动添加

	/***************** 响应参数 *****************/
	String AUTH_ID; // 授权码 varChar(6) F
	String REASONCODE; // 附加响应码 varChar(1) T 0发卡行代授权 1国际组织代授权 2系统内部拒绝

	/** 默认请求参数 */
	public void setDefaultRequest() {
	}

	public String getCARD_LEN() {
		return CARD_LEN;
	}
	@XmlElement(name = "CARD_LEN")
	public void setCARD_LEN(String cARD_LEN) {
		CARD_LEN = cARD_LEN;
	}

	public String getACCT_NO() {
		return ACCT_NO;
	}

	@XmlElement(name = "ACCT_NO")
	public void setACCT_NO(String aCCT_NO) {
		ACCT_NO = aCCT_NO;
	}

	public String getAMT1() {
		return AMT1;
	}

	@XmlElement(name = "AMT1")
	public void setAMT1(String aMT1) {
		AMT1 = aMT1;
	}

	public String getEXPIRE_DATE() {
		return EXPIRE_DATE;
	}

	@XmlElement(name = "EXPIRE_DATE")
	public void setEXPIRE_DATE(String eXPIRE_DATE) {
		EXPIRE_DATE = eXPIRE_DATE;
	}

	public String getAGENT_BANK() {
		return AGENT_BANK;
	}

	@XmlElement(name = "AGENT_BANK")
	public void setAGENT_BANK(String aGENT_BANK) {
		AGENT_BANK = aGENT_BANK;
	}

	public String getMERCHANT_NO() {
		return MERCHANT_NO;
	}

	@XmlElement(name = "MERCHANT_NO")
	public void setMERCHANT_NO(String mERCHANT_NO) {
		MERCHANT_NO = mERCHANT_NO;
	}

	public String getCUNT_NO() {
		return CUNT_NO;
	}

	@XmlElement(name = "CUNT_NO")
	public void setCUNT_NO(String cUNT_NO) {
		CUNT_NO = cUNT_NO;
	}

	public String getCVV2() {
		return CVV2;
	}

	@XmlElement(name = "CVV2")
	public void setCVV2(String cVV2) {
		CVV2 = cVV2;
	}

	public String getTERMINAL_FLOW() {
		return TERMINAL_FLOW;
	}

	@XmlElement(name = "TERMINAL_FLOW")
	public void setTERMINAL_FLOW(String tERMINAL_FLOW) {
		TERMINAL_FLOW = tERMINAL_FLOW;
	}

	public String getTRADE_TIME() {
		return TRADE_TIME;
	}

	@XmlElement(name = "TRADE_TIME")
	public void setTRADE_TIME(String tRADE_TIME) {
		TRADE_TIME = tRADE_TIME;
	}

	public String getTERMINAL_ID() {
		return TERMINAL_ID;
	}

	@XmlElement(name = "TERMINAL_ID")
	public void setTERMINAL_ID(String tERMINAL_ID) {
		TERMINAL_ID = tERMINAL_ID;
	}

	public String getSECURE_LEV() {
		return SECURE_LEV;
	}

	@XmlElement(name = "SECURE_LEV")
	public void setSECURE_LEV(String sECURE_LEV) {
		SECURE_LEV = sECURE_LEV;
	}

	public String getORDER_NO() {
		return ORDER_NO;
	}

	@XmlElement(name = "ORDER_NO")
	public void setORDER_NO(String oRDER_NO) {
		ORDER_NO = oRDER_NO;
	}

	public String getCAVV() {
		return CAVV;
	}

	@XmlElement(name = "CAVV")
	public void setCAVV(String cAVV) {
		CAVV = cAVV;
	}

	public String getEXT1() {
		return EXT1;
	}

	@XmlElement(name = "EXT1")
	public void setEXT1(String eXT1) {
		EXT1 = eXT1;
	}

	public String getECSLI() {
		return ECSLI;
	}

	@XmlElement(name = "ECSLI")
	public void setECSLI(String eCSLI) {
		ECSLI = eCSLI;
	}

	public String getCHECK_RESULT() {
		return CHECK_RESULT;
	}

	@XmlElement(name = "CHECK_RESULT")
	public void setCHECK_RESULT(String cHECK_RESULT) {
		CHECK_RESULT = cHECK_RESULT;
	}

	public String getSIGN_INFO() {
		return SIGN_INFO;
	}

	@XmlElement(name = "SIGN_INFO")
	public void setSIGN_INFO(String sIGN_INFO) {
		SIGN_INFO = sIGN_INFO;
	}

	public String getSIGNCERT() {
		return SIGNCERT;
	}

	@XmlElement(name = "SIGNCERT")
	public void setSIGNCERT(String sIGNCERT) {
		SIGNCERT = sIGNCERT;
	}

	public String getAUTH_ID() {
		return AUTH_ID;
	}

	@XmlElement(name = "AUTH_ID")
	public void setAUTH_ID(String aUTH_ID) {
		AUTH_ID = aUTH_ID;
	}

	public String getREASONCODE() {
		return REASONCODE;
	}

	@XmlElement(name = "REASONCODE")
	public void setREASONCODE(String rEASONCODE) {
		REASONCODE = rEASONCODE;
	}

}
