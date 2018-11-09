package com.unipay.model.xml.TX5W1004;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1004 商户单笔退款交易
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1004INFOBean {

	/***************** 请求参数 *****************/
	String MONEY; // 退款金额 varChar(100) F CN 
	String ORDER; // 订单号 varChar(30) F CN 
	String REFUND_CODE; // 退款流水号 varChar(15) T 可不填，商户可根据需要填写，退款流水号由商户的系统生成。 
	String SIGN_INFO; // 签名信息 varChar(254) T 
	String SIGNCERT; // 签名CA信息 varChar(254) T 客户采用socket连接时，建行客户端自动添加 

	/***************** 响应参数 *****************/
	String ORDER_NUM; //  订单号 varChar(30) F 交易响应码 
	String PAY_AMOUNT; // 支付金额 varChar(101) F 交易响应码 
	String AMOUNT; // 退款金额 varChar(101) F 交易响应码 

	/** 默认请求参数 */
	public void setDefaultRequest() {
		
	}

	public String getMONEY() {
		return MONEY;
	}
	@XmlElement(name = "MONEY")
	public void setMONEY(String mONEY) {
		MONEY = mONEY;
	}

	public String getORDER() {
		return ORDER;
	}

	@XmlElement(name = "ORDER")
	public void setORDER(String oRDER) {
		ORDER = oRDER;
	}

	public String getREFUND_CODE() {
		return REFUND_CODE;
	}

	@XmlElement(name = "REFUND_CODE")
	public void setREFUND_CODE(String rEFUND_CODE) {
		REFUND_CODE = rEFUND_CODE;
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

	public String getORDER_NUM() {
		return ORDER_NUM;
	}

	@XmlElement(name = "ORDER_NUM")
	public void setORDER_NUM(String oRDER_NUM) {
		ORDER_NUM = oRDER_NUM;
	}

	public String getPAY_AMOUNT() {
		return PAY_AMOUNT;
	}

	@XmlElement(name = "PAY_AMOUNT")
	public void setPAY_AMOUNT(String pAY_AMOUNT) {
		PAY_AMOUNT = pAY_AMOUNT;
	}

	public String getAMOUNT() {
		return AMOUNT;
	}

	@XmlElement(name = "AMOUNT")
	public void setAMOUNT(String aMOUNT) {
		AMOUNT = aMOUNT;
	}

}
