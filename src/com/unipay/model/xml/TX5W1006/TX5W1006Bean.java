package com.unipay.model.xml.TX5W1006;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 5W1006 E付通授权信息查询
 */
@XmlRootElement(name = "TX")
public class TX5W1006Bean extends BaseTXBean {
	private TX5W1006INFOBean TX_INFO;

	public TX5W1006INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX5W1006INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
