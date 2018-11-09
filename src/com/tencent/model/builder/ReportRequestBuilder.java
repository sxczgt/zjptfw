package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;

/**
 * 交易保障 {@link https://api.mch.weixin.qq.com/payitil/report}
 * 
 * @author Jack
 */
public class ReportRequestBuilder extends RequestBuilder {
	/** [必填]微信支付分配的公众账号ID（企业号corpid即为此appId） */
	private String appid = WxpayConfig.appid;
	/** [必填]微信支付分配的商户号 */
	private String mch_id = ""; // WxpayConfig.mch_id
	/** 设备号 String(32) 自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" */
	private String device_info = "WEB";
	/** [必填]随机字符串 String(32) 随机字符串，长度要求在32位以内。推荐随机数生成算法 */
	private String nonce_str = java.util.UUID.randomUUID().toString().replace("-", ""); // GUID
	/** [必填]签名 String(32) 通过签名算法计算得出的签名值，详见签名生成算法 */
	private String sign = "";
	/** 签名类型 String(32) 签名类型，默认为MD5，支持HMAC-SHA256和MD5。 */
	private String sign_type = "MD5";
	/** [必填]接口URL String(127) 报对应的接口的完整URL，类似： https://api.mch.weixin.qq.com/pay/unifiedorder。 */
	private String interface_url = "";
	/** [必填]接口耗时 Int 接口耗时情况，单位为毫秒 */
	private String execute_time = "";
	/** [必填]返回状态码 String(16) SUCCESS/FAIL此字段是通信标识，非交易标识，交易是否成功需要查看trade_state来判断 */
	private String return_code = "";
	/** 返回信息 String(128) 返回信息，如非空，为错误原因：签名失败、参数格式校验错误 */
	private String return_msg = "";
	/** [必填]业务结果 String(16) SUCCESS/FAIL */
	private String result_code = "";
	/** 错误代码 String(32) ORDERNOTEXIST—订单不存在、SYSTEMERROR—系统错误 */
	private String err_code = "";
	/** 错误代码描述 String(128) 结果信息描述 */
	private String err_code_des = "";
	/** 商户订单号 String(32) 商户系统内部的订单号,商户可以在上报时提供相关商户订单号方便微信支付更好的提高服务质量。 */
	private String out_trade_no = "";
	/** [必填]访问接口IP String(16) 发起接口调用时的机器IP */
	private String user_ip = "";
	/** 商户上报时间 String(14) 系统时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则 */
	private String time = "";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", appid); // 公众账号ID，必填，微信支付分配的公众账号ID（企业号corpid即为此appId）
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		param.put("device_info", device_info); // 设备号，自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		// param.put("sign_type", sign_type);// 签名类型，签名类型，默认为MD5，支持HMAC-SHA256和MD5。
		param.put("interface_url", interface_url); // 接口URL，必填
		param.put("execute_time", execute_time);// 接口耗时情况，单位为毫秒
		param.put("return_code", return_code);// 返回状态码
		param.put("return_msg", return_msg);// 返回信息，如非空，为错误原因：签名失败、参数格式校验错误
		param.put("result_code", result_code); // 业务结果
		param.put("err_code", err_code); // 错误代码
		param.put("err_code_des", err_code_des); // 错误代码描述
		param.put("out_trade_no", out_trade_no);// 商户订单号，必填，商户系统内部订单号，要求32个字符内、且在同一个商户号下唯一。
		param.put("user_ip", user_ip); // 访问接口IP
		param.put("time", time); // 商户上报时间
		String prestr = createLinkString(paraFilter(param)) + "&key=" + key; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String digestString = MD5.MD5Encode(prestr).toUpperCase();
		// Md5 md5 = new Md5("");
		// md5.hmac_Md5("", key);
		// byte b[] = md5.getDigest();
		// String digestString = Md5.stringify(b);
		return digestString;
	}

	@Override
	public String buildXmlString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<appid>" + appid + "</appid>");
		sb.append("<mch_id>" + mch_id + "</mch_id>");
		sb.append("<nonce_str>" + nonce_str + "</nonce_str>");
		if (StringUtils.isNotEmpty(device_info))
			sb.append("<device_info>" + device_info + "</device_info>");
		if (StringUtils.isNotEmpty(interface_url))
			sb.append("<interface_url>" + interface_url + "</interface_url>");
		if (StringUtils.isNotEmpty(execute_time))
			sb.append("<execute_time>" + execute_time + "</execute_time>");
		if (StringUtils.isNotEmpty(return_code))
			sb.append("<return_code>" + return_code + "</return_code>");
		if (StringUtils.isNotEmpty(return_msg))
			sb.append("<return_msg>" + return_msg + "</return_msg>");
		if (StringUtils.isNotEmpty(result_code))
			sb.append("<result_code>" + result_code + "</result_code>");
		if (StringUtils.isNotEmpty(err_code))
			sb.append("<err_code>" + err_code + "</err_code>");
		if (StringUtils.isNotEmpty(err_code_des))
			sb.append("<err_code_des>" + err_code_des + "</err_code_des>");
		if (StringUtils.isNotEmpty(out_trade_no))
			sb.append("<out_trade_no>" + out_trade_no + "</out_trade_no>");
		if (StringUtils.isNotEmpty(user_ip))
			sb.append("<user_ip>" + user_ip + "</user_ip>");
		if (StringUtils.isNotEmpty(time))
			sb.append("<time>" + time + "</time>");
		// sb.append("<sign_type>" + sign_type + "</sign_type>");
		sb.append("<sign>" + buildDigest() + "</sign>");
		sb.append("</xml>");
		return sb.toString();
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getInterface_url() {
		return interface_url;
	}

	public void setInterface_url(String interface_url) {
		this.interface_url = interface_url;
	}

	public String getExecute_time() {
		return execute_time;
	}

	public void setExecute_time(String execute_time) {
		this.execute_time = execute_time;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_code_des() {
		return err_code_des;
	}

	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getUser_ip() {
		return user_ip;
	}

	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
