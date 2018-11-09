package com.unipay.model.xml.TX5W1003;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 5W1003 商户退款流水查询
 */
@XmlRootElement(name = "TX")
public class TX5W1003Bean extends BaseTXBean {
	private TX5W1003INFOBean TX_INFO;

	public TX5W1003Bean() {
		setTX_CODE("5W1003");
	}

	public TX5W1003INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX5W1003INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
