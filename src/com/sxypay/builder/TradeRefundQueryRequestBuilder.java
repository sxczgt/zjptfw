package com.sxypay.builder;

import java.io.IOException;

import com.capinfo.crypt.Md5;

/**
 * 首信易退款查询 
 * 
 * @author xsy
 */
public class TradeRefundQueryRequestBuilder {
	/** [必填]商户编号 */
	private String v_mid = "";
	/** [必填]订单编号 */
	private String v_oid = "";
	/** [必填]版本号 */
	private String v_version = "1.1";
	/** [必填]数字指纹 */
	private String v_mac = "";
	private String key = "";

	public String buildDigest() {
		Md5 md5 = new Md5("");
		String source = v_mid + v_oid;
		String digestString = "";
		try {
			md5.hmac_Md5(source, key);
			byte b[] = md5.getDigest();
			digestString = Md5.stringify(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return digestString;
	}

	// <?xml version="1.0" encoding="gb2312"?>
	// <refmessage>
	// <status>[v_status]</status>
	// <statusdesc>[v_desc]</statusdesc>
	// <mid>[v_mid]</mid>
	// <oid>[v_oid]</oid>
	// <refamount>[v_refamount]</refamount>
	// <refoprt>[v_refoprt]</refoprt>
	// <refid>[v_refid]</refid>
	// <sign>[v_sign]</sign>
	// </refmessage>
	public String buildString() {
		StringBuffer sb = new StringBuffer();
		sb.append("?");
		sb.append("v_mid=" + v_mid);
		sb.append("&v_oid=" + v_oid);
		sb.append("&v_mac=" + buildDigest());
		sb.append("&v_version=" + v_version);
		return sb.toString();
	}

	public String getV_oid() {
		return v_oid;
	}

	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getV_version() {
		return v_version;
	}

	public void setV_version(String v_version) {
		this.v_version = v_version;
	}

	public String getV_mac() {
		return v_mac;
	}

	public void setV_mac(String v_mac) {
		this.v_mac = v_mac;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
