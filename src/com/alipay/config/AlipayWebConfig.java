package com.alipay.config;

/**
 * 类名：AlipayWebConfig<br>
 * 功能：支付宝 电脑网站支付 基础配置类<br>
 * 详细：设置帐户有关信息及返回路径<br>
 * 版本：3.3<br>
 * 日期：2012-08-10<br>
 */
public class AlipayWebConfig {
	static {
		WebConfigs.init("alipay_web.properties");
	}

	/** 调试用，创建TXT日志文件夹路径 */
	public static String log_path = "";

	/** 字符编码格式 目前支持 GBK 或 UTF-8 */
	public static String input_charset = "gbk";

	/** 签名方式 */
	// public static String sign_type = "MD5";
	public static String sign_type = "RSA2";

	/** 商户AppID */
	public static String APPID = WebConfigs.getAppId();
	/** 商户私钥 pkcs8格式的 */
	public static String RSA_PRIVATE_KEY = WebConfigs.getPrivateKey();
	/** 支付宝公钥 */
	public static String ALIPAY_PUBLIC_KEY = WebConfigs.getAlipayPublicKey();
	/** 异步通知地址 */
	public static String NOTIFY_URL = WebConfigs.getNotifyUrl();
	/** 同步通知地址 */
	public static String RETURN_URL = WebConfigs.getReturnUrl();
	/** 请求网关地址 */
	public static String URL = WebConfigs.getRequestUrl();// https://openapi.alipay.com/gateway.do
	/** 支付宝PartnerID */
	public static String ALIPAY_PARTNER = WebConfigs.getAlipay_partner();
	/** 支付宝Key */
	public static String ALIPAY_KEY = WebConfigs.getAlipay_key();
	/** 支付宝账号 */
	public static String ALIPAY_SELLER_EMAIL = WebConfigs.getAlipay_seller_email();
	public static String ALIPAY_VERIFY_URL = WebConfigs.getAlipayVerifyUrl();
	/** 编码格式，默认GBK */
	public static String CHARSET = "GBK"; // UTF-8
	/** 返回格式 */
	public static String FORMAT = "JSON";
}
