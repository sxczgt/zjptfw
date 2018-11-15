package com.alipay.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.config.AlipayWebConfig;
import com.alipay.sign.MD5;

/* *
 *类名：AlipayNotify
 *功能：支付宝通知处理类
 *详细：处理支付宝各接口通知返回
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考

 *************************注意*************************
 *调试通知返回时，可查看或改写log日志的写入TXT里的数据，来检查通知返回是否正常
 */
public class AlipayNotify {
	private static final Log log = LogFactory.getLog(AlipayNotify.class);
	
	private static final String HTTPS_VERIFY_URL = AlipayWebConfig.ALIPAY_VERIFY_URL;

	public static void main(String[] args){
//		/{buyer_id=2088902064640845, trade_no=2017050221001004840233697831, use_coupon=N, notify_time=2017-08-03 18:55:45, subject=Tsinghua Gymnasium Management and Booking System, sign_type=MD5, is_total_fee_adjust=N, notify_type=trade_status_sync, gmt_close=2017-08-03 18:41:55, out_trade_no=20721493721660611c8017521n8C65, gmt_payment=2017-05-02 18:41:33, trade_status=TRADE_FINISHED, discount=0.00, sign=9b0011924c170630c57dba89f71e4079, gmt_create=2017-05-02 18:41:15, buyer_email=18811368673, price=10.00, total_fee=10.00, seller_id=2088011993891540, quantity=1, seller_email=sfptjf@mail.tsinghua.edu.cn, notify_id=aa991aff63788cdc7cf609a3f7c585cmhe, payment_type=1}
	}
	
	/**
	 * 验证消息是否是支付宝发出的合法消息
	 * @param params 通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verify(Map<String, String> params) {

		// 判断responsetTxt是否为true，isSign是否为true
		// responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
		// isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
		String responseTxt = "true";
		if (params.get("notify_id") != null) {
			String notify_id = params.get("notify_id");
			responseTxt = verifyResponse(notify_id);
		}
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}
		boolean isSign = getSignVeryfy(params, sign);

		// 写日志记录
		String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
		log.debug(sWord);
		if (isSign && responseTxt.equals("true")) {
			return true;
		} else {
			log.error("verify fail!!! " + sWord);
			return false;
		}
	}

	/**
	 * 根据反馈回来的信息，生成签名结果
	 * @param Params 通知返回来的参数数组
	 * @param sign 比对的签名结果
	 * @return 生成的签名结果
	 */
	private static boolean getSignVeryfy(Map<String, String> Params, String sign) {
		// 过滤空值、sign与sign_type参数
		Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
		// 获取待签名字符串
		String preSignStr = AlipayCore.createLinkString(sParaNew);
		// 获得签名验证结果
		boolean isSign = false;
		// log.info("AlipayConfig.key="+AlipayConfig.key);
		if (AlipayWebConfig.sign_type.equals("MD5")) {
			isSign = MD5.verify(preSignStr, sign, AlipayWebConfig.ALIPAY_KEY, AlipayWebConfig.input_charset);
		}
		return isSign;
	}

	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * @param notify_id 通知校验ID
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true 返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String verifyResponse(String notify_id) {
		// 获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		log.info("获取远程服务器ATN结果,验证返回URL " + notify_id);

		String partner = AlipayWebConfig.ALIPAY_PARTNER;
		String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;

		return checkUrl(veryfy_url);
	}

	/**
	 * 获取远程服务器ATN结果
	 * @param urlvalue 指定URL路径地址
	 * @return 服务器ATN结果 验证结果集： invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 true 返回正确信息 false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String checkUrl(String urlvalue) {
		String inputLine = "";

		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			inputLine = in.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" checkUrl " + urlvalue + " " + e.getLocalizedMessage());
			System.err.println(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()) + " checkUrl " + urlvalue + " " + e.getLocalizedMessage());
			inputLine = "";
		}

		return inputLine;
	}
}
