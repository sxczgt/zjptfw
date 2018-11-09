package com.sxypay.builder;

import java.io.IOException;
import java.math.BigDecimal;

import com.capinfo.crypt.Md5;

/**
 * 首信易退款
 * 
 * @author xsy
 */
public class TradeRefundRequestBuilder {
	/** [必填]商户编号 */
	private String v_mid = "";
	/** [必填]订单编号 */
	private String v_oid = "";
	/** [必填]退款金额 */
	private BigDecimal v_refamount;
	/** [必填]退款原因 */
	private String v_refreason = ""; // GUID
	/** [必填]操作员编号 */
	private int v_refoprt = 0;
	/** [必填]数字指纹 */
	private String v_mac = "";
	/** 秘钥 */
	private String key = "";

	public String buildDigest() {
		Md5 md5 = new Md5("");
		String source = v_mid + v_oid + v_refamount + v_refoprt;
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
		sb.append("&v_refamount=" + v_refamount);
		sb.append("&v_refreason=" + v_refreason);
		sb.append("&v_refoprt=" + v_refoprt);
		sb.append("&v_mac=" + buildDigest());
		return sb.toString();
	}

	public String getV_oid() {
		return v_oid;
	}

	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}

	public BigDecimal getV_refamount() {
		return v_refamount;
	}

	public void setV_refamount(BigDecimal v_refamount) {
		this.v_refamount = v_refamount;
	}

	public String getV_refreason() {
		return v_refreason;
	}

	public void setV_refreason(String v_refreason) {
		this.v_refreason = v_refreason;
	}

	public int getV_refoprt() {
		return v_refoprt;
	}

	public void setV_refoprt(int v_refoprt) {
		this.v_refoprt = v_refoprt;
	}

	public String getV_mac() {
		return v_mac;
	}

	public void setV_mac(String v_mac) {
		this.v_mac = v_mac;
	}

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
