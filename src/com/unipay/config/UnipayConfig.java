package com.unipay.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unipay.utils.RSASign;

import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.SpringContextUtil;
import cn.tsinghua.sftp.util.StringUtil;

/**
 * 银联配置类<br>
 * 
 * @author Jack
 */
public class UnipayConfig {
	private static Log log = LogFactory.getLog(UnipayConfig.class);
	/** 银联支付接口地址 */
	public static String URL = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain";
	/** 商户代码 */
	public static String MERCHANTID = "105100000012072";
	/** 分行代码 */
	public static String BRANCHID = "110000000";
	/** 字符集 */
	public static String CHARSET = "utf-8";
	/** 查询密码 */
	public static String QUPWD = "";

	/** 商户柜台 */
	private static Map<String, Object> posIds;
	/** 公钥 */
	private static Map<String, Object> pubKeys;
	/** B2B支付接口地址*/
	public static String B2B_URL="https://b2bpay.ccb.com/NCCB/NECV5B2BPayMainPlat";

	/**
	 * 根据支付类型获取柜台号
	 * 
	 * @param paymentType 支付类型
	 */
	public static String getPosId(String paymentType) {
		String key = "POSID" + paymentType;
		if (posIds.containsKey(key)) {
			return posIds.get(key).toString();
		}
		return "";
	}

	/**
	 * 根据支付类型获取公钥
	 * 
	 * @param paymentType 支付类型
	 */
	public static String getPublicKey(String paymentType) {
		String key = "PUBKEY" + paymentType;
		if (pubKeys.containsKey(key)) {
			return pubKeys.get(key).toString();
		}
		return "";
	}

	/**
	 * 根据支付类型获取公钥后30位
	 * 
	 * @param paymentType 支付类型
	 */
	public static String getPublicKey30(String paymentType) {
		return StringUtil.right(getPublicKey(paymentType), 30); // 公钥后30位
	}

	static {
		init();
	}

	/** 加载配置信息 */
	public static void init() {
		log.warn("正在加载UnipayConfig配置信息：" + DateUtils.getDateTime());
		IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");
		if (paymentDao == null) {
			return;
		}
		// 银联支付接口地址
		URL = paymentDao.getSysParamsValue("UnionPay", "URL", "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain");
		// 商户代码
		MERCHANTID = paymentDao.getSysParamsValue("UnionPay", "MERCHANTID", "");
		// 分行代码
		BRANCHID = paymentDao.getSysParamsValue("UnionPay", "BRANCHID", "");
		// 商户柜台
		posIds = paymentDao.getSysParamsMapValue("UnionPay", "POSID%");
		// 公钥
		pubKeys = paymentDao.getSysParamsMapValue("UnionPay", "PUBKEY%");
		// 查询密码
		QUPWD = paymentDao.getSysParamsValue("UnionPay", "QUPWD", "QhcwcCX123");
		//B2B支付接口地址
		B2B_URL = paymentDao.getSysParamsValue("UnionPay", "B2BPAY", "https://b2bpay.ccb.com/NCCB/NECV5B2BPayMainPlat");
		System.out.println("初始化UnipayConfig：" + description());
	}

	/** 显示描述信息 */
	private static String description() {
		StringBuilder sb = new StringBuilder();
		sb.append("URL=" + URL).append("\n");
		sb.append(",MERCHANTID=" + MERCHANTID).append("\n");
		sb.append(",BRANCHID=" + BRANCHID).append("\n");
		sb.append(",B2B_URL=" + B2B_URL).append("\n");
		// 打印POSID和PUBKEY
		List<String> keys = new ArrayList<String>(posIds.keySet());
		Collections.sort(keys);
		for (String key1 : keys) {
			sb.append("," + key1 + "=" + posIds.get(key1)).append("\n");
			String key2 = "PUBKEY" + key1.replace("POSID", "");
			sb.append("," + key2 + "=" + getKeyDescription(pubKeys.get(key2).toString())).append("\n");
		}
		return sb.toString();
	}

	/** 截取显示Key值 */
	private static String getKeyDescription(String key) {
		int showLength = 6;
		if ((StringUtils.isNotEmpty(key)) && (key.length() > showLength)) {
			return key.substring(0, showLength) + "******" + key.substring(key.length() - showLength);
		}
		return null;
	}

	/**
	 * 验证签名
	 * 
	 * @param map 参数值
	 * @param key 公钥
	 * @param sign 签名
	 * @return
	 */
	public static boolean checkSign(Map<String, String> params, String key, String sign) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("POSID=%s", params.get("POSID")));
		sb.append(String.format("&BRANCHID=%s", params.get("BRANCHID")));
		sb.append(String.format("&ORDERID=%s", params.get("ORDERID")));
		sb.append(String.format("&PAYMENT=%s", params.get("PAYMENT")));
		sb.append(String.format("&CURCODE=%s", params.get("CURCODE")));
		sb.append(String.format("&REMARK1=%s", params.get("REMARK1")));
		sb.append(String.format("&REMARK2=%s", params.get("REMARK2")));
		if (params.containsKey("ACC_TYPE")) {
			sb.append(String.format("&ACC_TYPE=%s", params.get("ACC_TYPE")));
		}
		sb.append(String.format("&SUCCESS=%s", params.get("SUCCESS")));
		sb.append(String.format("&TYPE=%s", params.get("TYPE")));
		sb.append(String.format("&REFERER=%s", params.get("REFERER")));
		sb.append(String.format("&CLIENTIP=%s", params.get("CLIENTIP")));
		if (params.containsKey("ACCDATE")) {
			sb.append(String.format("&ACCDATE=%s", params.get("ACCDATE")));
		}
		if (params.containsKey("USRMSG")) {
			sb.append(String.format("&USRMSG=%s", params.get("USRMSG")));
		}
		if (params.containsKey("INSTALLNUM")) {
			sb.append(String.format("&INSTALLNUM=%s", params.get("INSTALLNUM")));
		}
		if (params.containsKey("ERRMSG")) {
			sb.append(String.format("&ERRMSG=%s", params.get("ERRMSG")));
		}
		if (params.containsKey("USRINFO")) {
			sb.append(String.format("&USRINFO=%s", params.get("USRINFO")));
		}
		if (params.containsKey("DISCOUNT")) {
			sb.append(String.format("&DISCOUNT=%s", params.get("DISCOUNT")));
		}
		String str = sb.toString();
		// 生成签名
		RSASign rsa = new RSASign();
		rsa.setPublicKey(key);
        return rsa.verifySigature(sign, str);
	}

	public static void main(String[] args) {
		String sign = "5ce7dd0d98a65734b6391ded5e969b8fb84fd243aab283653d480de6992b101367aede13bfcff1ea16e726151318cce5e3dca040a2bc0d9705d333168eb17331dd7f0e9961ad102801e4aae576022909e57e763a2d9ded898dd0ebceaa441e39801e2a3f2a58f4e54d6ba56fa5f3e67425c95eb3e09a1122a74c1037f9d9f428";
		Map<String, String> params = new HashMap<String, String>();
		params.put("REMARK1", "100323");
		params.put("CLIENTIP", "101.6.16.156");
		params.put("SUCCESS", "N");
		params.put("REMARK2", "");
		params.put("BRANCHID", "110000000");
		params.put("ACC_TYPE", "12");
		params.put("CURCODE", "01");
		params.put("ORDERID", "DEMO20171103094857");
		params.put("POSID", "004161897");
		params.put("REFERER", "http://101.6.16.156:8089/sfpt/requestPayAction!payment.action");
		params.put("PAYMENT", "0.01");
		params.put("TYPE", "1");

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("POSID=%s", params.get("POSID")));
		sb.append(String.format("&BRANCHID=%s", params.get("BRANCHID")));
		sb.append(String.format("&ORDERID=%s", params.get("ORDERID")));
		sb.append(String.format("&PAYMENT=%s", params.get("PAYMENT")));
		sb.append(String.format("&CURCODE=%s", params.get("CURCODE")));
		sb.append(String.format("&REMARK1=%s", params.get("REMARK1")));
		sb.append(String.format("&REMARK2=%s", params.get("REMARK2")));
		if (params.containsKey("ACC_TYPE")) {
			sb.append(String.format("&ACC_TYPE=%s", params.get("ACC_TYPE")));
		}
		sb.append(String.format("&SUCCESS=%s", params.get("SUCCESS")));
		sb.append(String.format("&TYPE=%s", params.get("TYPE")));
		sb.append(String.format("&REFERER=%s", params.get("REFERER")));
		sb.append(String.format("&CLIENTIP=%s", params.get("CLIENTIP")));
		if (params.containsKey("ACCDATE")) {
			sb.append(String.format("&ACCDATE=%s", params.get("ACCDATE")));
		}
		if (params.containsKey("USRMSG")) {
			sb.append(String.format("&USRMSG=%s", params.get("USRMSG")));
		}
		if (params.containsKey("INSTALLNUM")) {
			sb.append(String.format("&INSTALLNUM=%s", params.get("INSTALLNUM")));
		}
		if (params.containsKey("ERRMSG")) {
			sb.append(String.format("&ERRMSG=%s", params.get("ERRMSG")));
		}
		if (params.containsKey("USRINFO")) {
			sb.append(String.format("&USRINFO=%s", params.get("USRINFO")));
		}
		if (params.containsKey("DISCOUNT")) {
			sb.append(String.format("&DISCOUNT=%s", params.get("DISCOUNT")));
		}
		String strSrc = sb.toString();
		System.out.println(strSrc);

		// 生成签名
		String strPubKey = "30819c300d06092a864886f70d010101050003818a003081860281807795b65f56bc8926bd139a62441dbf5e3d267b68534c9855d93aae98f19bff7accf7f710e44965000408cee2eb1b74789a42b54432370164e4806e48b33b8d55c16369491f30c5c1d8ee13ee9e02259d5fce3303ea91c44087876be7e911b2f2a3d0cf2ccf4935ca2afaf27bbd019a65fd4bc8c90b88fe45acd50e9eb3fabf37020111";
		RSASign rsa = new RSASign();
		rsa.setPublicKey(strPubKey);
		if (rsa.verifySigature(sign, strSrc)) {
			System.out.println("银联同步通知，签名验证成功。");
		} else {
			System.out.println("银联同步通知错误，签名验证错误！\n" + sign);
		}
	}
}
