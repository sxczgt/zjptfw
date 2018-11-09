package com.unipay.model.xml.TX5W1001;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 5W1001 B2C外联平台启动连接交易
 */
@XmlRootElement(name = "TX_INFO")
public class TX5W1001INFOBean {

	/***************** 请求参数 *****************/
	String REM1; // 备注1 varChar(32)
	String REM2; // 备注2 varChar(32)

	/***************** 响应参数 同请求参数 *****************/

	public String getREM1() {
		return REM1;
	}

	@XmlElement(name = "REM1")
	public void setREM1(String rEM1) {
		REM1 = rEM1;
	}

	public String getREM2() {
		return REM2;
	}

	@XmlElement(name = "REM2")
	public void setREM2(String rEM2) {
		REM2 = rEM2;
	}

}