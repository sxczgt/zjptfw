package com.unipay.model.xml.TX6W0111;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.unipay.model.xml.BaseTXBean;

/**
 * 6W0111 大文件下载
 */
@XmlRootElement(name = "TX")
public class TX6W0111Bean extends BaseTXBean {
	private TX6W0111INFOBean TX_INFO;

	public TX6W0111INFOBean getTX_INFO() {
		return TX_INFO;
	}

	@XmlElement(name = "TX_INFO")
	public void setTX_INFO(TX6W0111INFOBean TX_INFO) {
		this.TX_INFO = TX_INFO;
	}

}
