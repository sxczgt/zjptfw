package com.unipay.model.xml.TX5W1001;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 5W1001 B2C外联平台启动连接交易
 */
@XmlRootElement(name = "TX")
public class TX5W1001Bean extends BaseTXBean {
	private TX5W1001INFOBean TX_INFO;

	public TX5W1001Bean() {
		setTX_CODE("5W1001");
	}

	public TX5W1001INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX5W1001INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
