package com.unipay.model.xml.TX6W0111;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 6W0111 大文件下载
 */
@XmlRootElement(name = "TX_INFO")
public class TX6W0111INFOBean {

	/***************** 请求参数 *****************/
	String SOURCE; // 要下载的文件名称 varChar(128) F 要下载的文件名，如：CBB1172476199728
	String FILEPATH; // 文件路径 varChar(30) F 要下载文件的路径:merchant/shls,必须填该值
	String LOCAL_REMOTE; // 下载路径标志 varChar(1) F 一般0

	/***************** 响应参数 *****************/

	/** 默认请求参数 */
	public void setDefaultRequest() {
		setFILEPATH("merchant/shls"); // 文件路径
		setLOCAL_REMOTE("0");// 下载路径标志
	}

	public String getSOURCE() {
		return SOURCE;
	}

	@XmlElement(name = "SOURCE")
	public void setSOURCE(String sOURCE) {
		SOURCE = sOURCE;
	}

	public String getFILEPATH() {
		return FILEPATH;
	}

	@XmlElement(name = "FILEPATH")
	public void setFILEPATH(String fILEPATH) {
		FILEPATH = fILEPATH;
	}

	public String getLOCAL_REMOTE() {
		return LOCAL_REMOTE;
	}

	@XmlElement(name = "LOCAL_REMOTE")
	public void setLOCAL_REMOTE(String lOCAL_REMOTE) {
		LOCAL_REMOTE = lOCAL_REMOTE;
	}

}
