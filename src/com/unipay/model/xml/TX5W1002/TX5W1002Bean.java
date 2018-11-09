package com.unipay.model.xml.TX5W1002;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 5W1002 商户支付流水查询
 */
@XmlRootElement(name = "TX")
public class TX5W1002Bean extends BaseTXBean {
	private TX5W1002INFOBean TX_INFO;

	public TX5W1002Bean() {
		setTX_CODE("5W1002");
	}

	public TX5W1002INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX5W1002INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
