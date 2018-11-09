package com.unipay.model.xml.TX5W1005;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 5W1005 商户流水文件下载
 */
@XmlRootElement(name = "TX")
public class TX5W1005Bean extends BaseTXBean {
	private TX5W1005INFOBean TX_INFO;

	public TX5W1005INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX5W1005INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
