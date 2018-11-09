package com.tencent.model.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.config.WxpayConfig;
import com.tencent.model.builder.base.RequestBuilder;

import cn.tsinghua.sftp.util.MD5;
import cn.tsinghua.sftp.util.StringUtil;

/**
 * 提交刷卡支付 {@link https://api.mch.weixin.qq.com/pay/micropay}
 * 
 * @author Jack
 */
public class MicroPayRequestBuilder extends RequestBuilder {
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
	/** [必填]商品描述 String(128) */
	private String body = "";
	/** 商品详情 String(6000) 注意：a、单品总金额应<=订单总金额total_fee，否则会导致下单失败。b、 单品单价，如果商户有优惠，需传输商户优惠后的单价 */
	private String detail = "";
	/** 附加数据 String(127) 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。 */
	private String attach = "";
	/** [必填]商户订单号 String(32) 商户系统内部订单号，要求32个字符内、且在同一个商户号下唯一。 */
	private String out_trade_no = "";
	/** 标价币种 String(16) 符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型 */
	private String fee_type = "CNY";
	/** [必填]标价金额 Int 订单总金额，单位为分，详见支付金额 */
	private String total_fee;
	/** [必填]终端IP String(16) APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。 */
	private String spbill_create_ip = "";
	/** 商品标记 String(32) 商品标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 */
	private String goods_tag = "";
	/** 指定支付方式 String(32) 上传此参数no_credit--可限制用户不能使用信用卡支付 */
	private String limit_pay = "";
	/** [必填]授权码String(128)，扫码支付授权码，设备读取用户微信中的条码或者二维码信息（注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头） */
	private String auth_code = "";
	/** 场景信息 String(256) 该字段用于统一下单时上报场景信息，目前支持上报实际门店信息。 */
	private String scene_info = "";

	public static void main(String[] args) {
		MicroPayRequestBuilder b = new MicroPayRequestBuilder();
		b.setAppid("wx6b7a09a3fa6110cf");
		b.setAttach("http://demo.tsinghua.edu.cn/asynReceivedAction");
		b.setBody("test测试");
		b.setDevice_info("WEB");
		b.setFee_type("CNY");
		b.setMch_id("1449863802");
		b.setNonce_str("6756b90119544489a488398035dd05f9");
		b.setOut_trade_no("299920170419135918");
		b.setSpbill_create_ip("166.111.3.242");
		b.setTotal_fee("1");
		b.setAuth_code("12345678");
		String r = b.buildDigest();
		System.out.println(r);
	}

	@Override
	public String buildDigest() {
		String key = WxpayConfig.key; // 密钥

		Map<String, String> param = new HashMap<String, String>();
		param.put("appid", appid); // 公众账号ID，必填，微信支付分配的公众账号ID（企业号corpid即为此appId）
		param.put("mch_id", mch_id); // 商户号，必填，微信支付分配的商户号
		param.put("nonce_str", nonce_str); // 随机字符串，必填，随机字符串，长度要求在32位以内。
		param.put("device_info", device_info); // 设备号，自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
		param.put("sign_type", sign_type);// 签名类型，签名类型，默认为MD5，支持HMAC-SHA256和MD5。
		param.put("body", body); // 商品描述，必填
		param.put("detail", detail);// 商品详情
		param.put("attach", attach);// 附加数据，附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
		param.put("out_trade_no", out_trade_no);// 商户订单号，必填，商户系统内部订单号，要求32个字符内、且在同一个商户号下唯一。
		param.put("fee_type", fee_type);// 标价币种，符合ISO 4217标准的三位字母代码，默认人民币：CNY
		param.put("total_fee", yuanToFen(total_fee)); // 标价金额，必填，订单总金额，单位为分
		param.put("spbill_create_ip", spbill_create_ip); // 终端IP，必填，APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
		param.put("goods_tag", goods_tag); // 商品标记，使用代金券或立减优惠功能时需要的参数
		param.put("limit_pay", limit_pay); // 指定支付方式，上传此参数no_credit--可限制用户不能使用信用卡支付
		param.put("auth_code", auth_code); // 授权码，扫码支付授权码，设备读取用户微信中的条码或者二维码信息（注：用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头）
		param.put("scene_info", scene_info); // 场景信息，该字段用于统一下单时上报场景信息，目前支持上报实际门店信息。
		String prestr = createLinkString(paraFilter(param)) + "&key=" + key; // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		System.out.println(">>>>>>>buildDigest=" + prestr);
		String digestString = MD5.MD5Encode(prestr).toUpperCase();
		System.out.println(">>>>>>>digestString=" + digestString);
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
		if (StringUtils.isNotEmpty(body))
			sb.append("<body>" + body + "</body>");
		if (StringUtils.isNotEmpty(detail))
			sb.append("<detail><![CDATA[" + detail + "]]></detail>");
		if (StringUtils.isNotEmpty(attach))
			sb.append("<attach>" + attach + "</attach>");
		if (StringUtils.isNotEmpty(out_trade_no))
			sb.append("<out_trade_no>" + out_trade_no + "</out_trade_no>");
		if (StringUtils.isNotEmpty(fee_type))
			sb.append("<fee_type>" + fee_type + "</fee_type>");
		if (StringUtils.isNotEmpty(total_fee))
			sb.append("<total_fee>" + yuanToFen(total_fee) + "</total_fee>");
		if (StringUtils.isNotEmpty(spbill_create_ip))
			sb.append("<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>");
		if (StringUtils.isNotEmpty(goods_tag))
			sb.append("<goods_tag>" + goods_tag + "</goods_tag>");
		if (StringUtils.isNotEmpty(limit_pay))
			sb.append("<limit_pay>" + limit_pay + "</limit_pay>");
		if (StringUtils.isNotEmpty(auth_code))
			sb.append("<auth_code>" + auth_code + "</auth_code>");
		if (StringUtils.isNotEmpty(scene_info))
			sb.append("<scene_info>" + scene_info + "</scene_info>");
		sb.append("<sign_type>" + sign_type + "</sign_type>");
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = StringUtil.setNullToDefault(spbill_create_ip, "0.0.0.1");
	}

	public String getGoods_tag() {
		return goods_tag;
	}

	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getScene_info() {
		return scene_info;
	}

	public void setScene_info(String scene_info) {
		this.scene_info = scene_info;
	}

}
