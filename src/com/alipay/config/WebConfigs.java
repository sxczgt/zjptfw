package com.alipay.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebConfigs {
	private static Log log = LogFactory.getLog(WebConfigs.class);

	private static Configuration configs;
	private static String appId; // 商户appid
	private static String privateKey; // 商户RSA私钥 pkcs8格式
	private static String publicKey; // 商户RSA公钥
	private static String alipayPublicKey; // 支付宝公钥
	private static String requestUrl; // 请求网关地址
	private static String returnUrl; // 页面跳转同步通知页面路径
	private static String notifyUrl; // 服务器异步通知页面路径
	private static String alipay_partner; // 支付宝PartnerID
	private static String alipay_key; // 支付宝key
	private static String alipay_seller_email; // 支付宝账号
	private static String alipayVerifyUrl; // 验证签名地址
	

	public static void main(String[] args) {
		WebConfigs.init("alipay_web.properties");
	}

	public static synchronized void init(String filePath) {
		if (configs != null) {
			return;
		}
		try {
			configs = new PropertiesConfiguration(filePath);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		if (configs == null) {
			throw new IllegalStateException("can`t find file by path:" + filePath);
		}

		appId = configs.getString("appid");
		privateKey = configs.getString("rsa_private_key");
		publicKey = configs.getString("rsa_public_key");
		alipayPublicKey = configs.getString("alipay_public_key");

		requestUrl = configs.getString("request_url");
		returnUrl = configs.getString("return_url");
		notifyUrl = configs.getString("notify_url");
		
		alipay_partner = configs.getString("alipay_partner");
		alipay_key = configs.getString("alipay_key");
		alipay_seller_email = configs.getString("alipay_seller_email");
		alipayVerifyUrl = configs.getString("alipay_verify_url");

		log.info("配置文件名: " + filePath);
		log.info(description());
	}

	public static String description() {
		StringBuilder sb = new StringBuilder("WebConfigs{").append("\n");
		sb.append("AppID: ").append(appId).append("\n");

		sb.append(", 支付宝请求网关: ").append(requestUrl).append("\n");
		sb.append(", 同步通知地址: ").append(returnUrl).append("\n");
		sb.append(", 异步通知地址: ").append(notifyUrl).append("\n");

		sb.append(", 商户RSA私钥: ").append(getKeyDescription(privateKey)).append("\n");
		sb.append(", 商户RSA公钥: ").append(getKeyDescription(publicKey)).append("\n");
		sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(alipayPublicKey)).append("\n");

		sb.append("}");
		return sb.toString();
	}

	private static String getKeyDescription(String key) {
		int showLength = 6;
		if ((StringUtils.isNotEmpty(key)) && (key.length() > showLength)) {
			return key.substring(0, showLength) + "******" + key.substring(key.length() - showLength);
		}
		return null;
	}

	public static String getAppId() {
		return appId;
	}

	public static void setAppId(String appId) {
		WebConfigs.appId = appId;
	}

	public static String getPrivateKey() {
		return privateKey;
	}

	public static void setPrivateKey(String privateKey) {
		WebConfigs.privateKey = privateKey;
	}

	public static String getPublicKey() {
		return publicKey;
	}

	public static void setPublicKey(String publicKey) {
		WebConfigs.publicKey = publicKey;
	}

	public static String getAlipayPublicKey() {
		return alipayPublicKey;
	}

	public static void setAlipayPublicKey(String alipayPublicKey) {
		WebConfigs.alipayPublicKey = alipayPublicKey;
	}

	public static String getRequestUrl() {
		return requestUrl;
	}

	public static void setRequestUrl(String requestUrl) {
		WebConfigs.requestUrl = requestUrl;
	}

	public static String getReturnUrl() {
		return returnUrl;
	}

	public static void setReturnUrl(String returnUrl) {
		WebConfigs.returnUrl = returnUrl;
	}

	public static String getNotifyUrl() {
		return notifyUrl;
	}

	public static void setNotifyUrl(String notifyUrl) {
		WebConfigs.notifyUrl = notifyUrl;
	}

	public static String getAlipay_partner() {
		return alipay_partner;
	}

	public static void setAlipay_partner(String alipay_partner) {
		WebConfigs.alipay_partner = alipay_partner;
	}

	public static String getAlipay_key() {
		return alipay_key;
	}

	public static void setAlipay_key(String alipay_key) {
		WebConfigs.alipay_key = alipay_key;
	}

	public static String getAlipay_seller_email() {
		return alipay_seller_email;
	}

	public static void setAlipay_seller_email(String alipay_seller_email) {
		WebConfigs.alipay_seller_email = alipay_seller_email;
	}

	public static String getAlipayVerifyUrl() {
		return alipayVerifyUrl;
	}

	public static void setAlipayVerifyUrl(String alipayVerifyUrl) {
		WebConfigs.alipayVerifyUrl = alipayVerifyUrl;
	}

}
