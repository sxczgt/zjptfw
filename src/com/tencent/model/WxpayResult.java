package com.tencent.model;

public class WxpayResult {
	/** 返回的XML内容 */
	private String resultXML = "";

	/** 返回状态码，SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断 */
	private String return_code = "";
	/** 返回信息，如非空，为错误原因签名失败 */
	private String return_msg = "";

	/** 公众账号ID，微信支付分配的公众账号ID（企业号corpid即为此appId） */
	private String appid = "";
	/** 商户号，微信支付分配的商户号 */
	private String mch_id = "";
	/** 随机字符串，微信返回的随机字符串 */
	private String nonce_str = "";
	/** 签名，微信返回的签名 */
	private String sign = "";
	/** 业务结果，SUCCESS/FAIL */
	private String result_code = "";
	/** 错误代码 */
	private String err_code = "";
	/** 错误代码描述，错误返回的信息描述 */
	private String err_code_des = "";

	/****************************************** 统一下单 ********************************************/
	// https://api.mch.weixin.qq.com/pay/unifiedorder
	/** 设备号，调用接口提交的终端设备号 */
	private String device_info = "";
	/** 交易类型，调用接口提交的交易类型，取值如下：JSAPI，NATIVE，APP */
	private String trade_type = "";
	/** 预支付交易会话标识 ，微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时 */
	private String prepay_id = "";
	/** 二维码链接 */
	private String code_url = "";

	/****************************************** 查询订单 ********************************************/
	// https://api.mch.weixin.qq.com/pay/orderquery
	/** 用户标识，用户在商户appid下的唯一标识 */
	private String openid = "";
	/** 是否关注公众账号，用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效 */
	private String is_subscribe = "";
	/** 交易状态、SUCCESS—支付成功、REFUND—转入退款、NOTPAY—未支付、CLOSED—已关闭、REVOKED—已撤销（刷卡支付）、USERPAYING--用户支付中、PAYERROR--支付失败(其他原因，如银行返回失败) */
	private String trade_state = "";
	/** 付款银行，银行类型，采用字符串类型的银行标识 */
	private String bank_type = "";
	/** 标价金额，订单总金额，单位为分 */
	private int total_fee;
	/** 应结订单金额，应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。 */
	private int settlement_total_fee;
	/** 标价币种，货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY */
	private String fee_type = "";
	/** 现金支付金额，现金支付金额订单现金支付金额 */
	private int cash_fee;
	/** 现金支付币种，货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型 */
	private String cash_fee_type = "";
	// /** 代金券金额，代金券”金额<=订单金额，订单金额-“代金券”金额=现金支付金额 */
	// private int coupon_fee;
	// /** 代金券使用数量 */
	// private int coupon_count;
	// /** 代金券类型 CASH--充值代金券 、NO_CASH---非充值代金券、订单使用代金券时有返回（取值：CASH、NO_CASH */
	// private String coupon_type_$n = "";
	// /** 代金券ID，代金券ID, $n为下标，从0开始编号 */
	// private String coupon_id_$n = "";
	// /** 单个代金券支付金额，单个代金券支付金额, $n为下标，从0开始编号 */
	// private String coupon_fee_$n = "";
	/** 微信支付订单号 */
	private String transaction_id = "";
	/** 商户系统的订单号，与请求一致 */
	private String out_trade_no = "";
	/** 附加数据，原样返回 */
	private String attach = "";
	/** 支付完成时间，订单支付时间，格式为yyyyMMddHHmmss */
	private String time_end = "";
	/** 交易状态描述,对当前查询订单状态的描述和下一步操作的指引 */
	private String trade_state_desc = "";

	/****************************************** 关闭订单 ********************************************/
	// https://api.mch.weixin.qq.com/pay/closeorder
	/** 业务结果描述 */
	private String result_msg = "";

	/****************************************** 申请退款 ********************************************/
	// https://api.mch.weixin.qq.com/secapi/pay/refund
	/** 商户退款单号 */
	private String out_refund_no = "";
	/** 微信退款单号 */
	private String refund_id = "";
	/** 退款渠道 */
	private String refund_channel = "";
	/** 退款金额 */
	private int refund_fee;
	/** 应结退款金额 */
	private int settlement_refund_fee;
	/** 现金退款金额 */
	private int cash_refund_fee;

	/****************************************** 查询退款 ********************************************/
	// https://api.mch.weixin.qq.com/pay/refundquery
	/** 退款笔数 */
	private int refund_count;
	/** 商户退款单号 */
	private String out_refund_no_$n;
	/** 微信退款单号 */
	private String refund_id_$n;
	/** 退款渠道 */
	private String refund_channel_$n;
	/** 申请退款金额 */
	private int refund_fee_$n;
	/** 退款金额 */
	private int settlement_refund_fee_$n;
	/** 退款资金来源 */
	private int refund_account;
	/** 退款状态 */
	private String refund_status_$n;
	/** 退款入账账户 */
	private String refund_recv_accout_$n;

	/****************************************** 交易保障 ********************************************/
	// https://api.mch.weixin.qq.com/payitil/report

	/****************************************** 下载对账单 ********************************************/
	// https://api.mch.weixin.qq.com/pay/downloadbill

	/****************************************** 转换短链接 ********************************************/
	// https://api.mch.weixin.qq.com/tools/shorturl
	/** URL链接，转换后的URL */
	private String short_url = "";
	/** 支付跳转链接 mweb_url为拉起微信支付收银台的中间页面，可通过访问该url来拉起微信客户端，完成支付,mweb_url的有效期为5分钟。 */
	private String mweb_url = "";

	/****************************************** 获取RSA加密公钥API ********************************************/
	// https://fraud.mch.weixin.qq.com/risk/getpublickey
	/** 企业付款公钥 */
	private String pub_key;
	/** 微信企业付款单号 */
	private String payment_no;
	/****************************************** 查询企业付款银行卡API ********************************************/
	// https://api.mch.weixin.qq.com/mmpaysptrans/query_bank
	/** 商户企业付款单号 */
	private String partner_trade_no;
	/** 银行卡号，收款用户银行卡号(MD5加密) */
	private String bank_no_md5;
	/** 用户真实姓名，收款人真实姓名（MD5加密） */
	private String true_name_md5;
	/** 代付金额，代付订单金额RMB：分 */
	private int amount;
	/**
	 * 代付订单状态：<br>
	 * PROCESSING（处理中，如有明确失败，则返回额外失败原因；否则没有错误原因）<br>
	 * SUCCESS（付款成功）<br>
	 * FAILED（付款失败）<br>
	 * BANK_FAIL（银行退票，订单状态由付款成功流转至退票,退票时付款金额和手续费会自动退还）<br>
	 */
	private String status;
	/** 手续费金额，手续费订单金额 RMB：分 */
	private int cmms_amt;
	/** 创建时间，微信侧订单创建时间 */
	private String create_time;
	/** 成功付款时间，微信侧付款成功时间（但无法保证银行不会退票） */
	private String pay_succ_time;
	/** 失败原因 ，订单失败原因（如：余额不足） */
	private String reason;
	/**返回的沙箱密钥，用于测试签名*/
	private String sandbox_signkey;
	/**************************************************************************************************************/

	public String getMweb_url() {
		return mweb_url;
	}

	public void setMweb_url(String mweb_url) {
		this.mweb_url = mweb_url;
	}

	public String getResultXML() {
		return resultXML;
	}

	public void setResultXML(String resultXML) {
		this.resultXML = resultXML;
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

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
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

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getPrepay_id() {
		return prepay_id;
	}

	public void setPrepay_id(String prepay_id) {
		this.prepay_id = prepay_id;
	}

	public String getCode_url() {
		return code_url;
	}

	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public int getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}

	public int getSettlement_total_fee() {
		return settlement_total_fee;
	}

	public void setSettlement_total_fee(int settlement_total_fee) {
		this.settlement_total_fee = settlement_total_fee;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public int getCash_fee() {
		return cash_fee;
	}

	public void setCash_fee(int cash_fee) {
		this.cash_fee = cash_fee;
	}

	public String getCash_fee_type() {
		return cash_fee_type;
	}

	public void setCash_fee_type(String cash_fee_type) {
		this.cash_fee_type = cash_fee_type;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getTrade_state_desc() {
		return trade_state_desc;
	}

	public void setTrade_state_desc(String trade_state_desc) {
		this.trade_state_desc = trade_state_desc;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}

	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}

	public String getRefund_channel() {
		return refund_channel;
	}

	public void setRefund_channel(String refund_channel) {
		this.refund_channel = refund_channel;
	}

	public int getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(int refund_fee) {
		this.refund_fee = refund_fee;
	}

	public int getSettlement_refund_fee() {
		return settlement_refund_fee;
	}

	public void setSettlement_refund_fee(int settlement_refund_fee) {
		this.settlement_refund_fee = settlement_refund_fee;
	}

	public int getCash_refund_fee() {
		return cash_refund_fee;
	}

	public void setCash_refund_fee(int cash_refund_fee) {
		this.cash_refund_fee = cash_refund_fee;
	}

	public int getRefund_count() {
		return refund_count;
	}

	public void setRefund_count(int refund_count) {
		this.refund_count = refund_count;
	}

	public String getOut_refund_no_$n() {
		return out_refund_no_$n;
	}

	public void setOut_refund_no_$n(String out_refund_no_$n) {
		this.out_refund_no_$n = out_refund_no_$n;
	}

	public String getRefund_id_$n() {
		return refund_id_$n;
	}

	public void setRefund_id_$n(String refund_id_$n) {
		this.refund_id_$n = refund_id_$n;
	}

	public String getRefund_channel_$n() {
		return refund_channel_$n;
	}

	public void setRefund_channel_$n(String refund_channel_$n) {
		this.refund_channel_$n = refund_channel_$n;
	}

	public int getRefund_fee_$n() {
		return refund_fee_$n;
	}

	public void setRefund_fee_$n(int refund_fee_$n) {
		this.refund_fee_$n = refund_fee_$n;
	}

	public int getSettlement_refund_fee_$n() {
		return settlement_refund_fee_$n;
	}

	public void setSettlement_refund_fee_$n(int settlement_refund_fee_$n) {
		this.settlement_refund_fee_$n = settlement_refund_fee_$n;
	}

	public int getRefund_account() {
		return refund_account;
	}

	public void setRefund_account(int refund_account) {
		this.refund_account = refund_account;
	}

	public String getRefund_status_$n() {
		return refund_status_$n;
	}

	public void setRefund_status_$n(String refund_status_$n) {
		this.refund_status_$n = refund_status_$n;
	}

	public String getRefund_recv_accout_$n() {
		return refund_recv_accout_$n;
	}

	public void setRefund_recv_accout_$n(String refund_recv_accout_$n) {
		this.refund_recv_accout_$n = refund_recv_accout_$n;
	}

	public String getShort_url() {
		return short_url;
	}

	public void setShort_url(String short_url) {
		this.short_url = short_url;
	}

	public String getPub_key() {
		return pub_key;
	}

	public void setPub_key(String pub_key) {
		this.pub_key = pub_key;
	}

	public String getPayment_no() {
		return payment_no;
	}

	public void setPayment_no(String payment_no) {
		this.payment_no = payment_no;
	}

	public String getPartner_trade_no() {
		return partner_trade_no;
	}

	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}

	public String getBank_no_md5() {
		return bank_no_md5;
	}

	public void setBank_no_md5(String bank_no_md5) {
		this.bank_no_md5 = bank_no_md5;
	}

	public String getTrue_name_md5() {
		return true_name_md5;
	}

	public void setTrue_name_md5(String true_name_md5) {
		this.true_name_md5 = true_name_md5;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCmms_amt() {
		return cmms_amt;
	}

	public void setCmms_amt(int cmms_amt) {
		this.cmms_amt = cmms_amt;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getPay_succ_time() {
		return pay_succ_time;
	}

	public void setPay_succ_time(String pay_succ_time) {
		this.pay_succ_time = pay_succ_time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSandbox_signkey() {
		return sandbox_signkey;
	}

	public void setSandbox_signkey(String sandbox_signkey) {
		this.sandbox_signkey = sandbox_signkey;
	}

}
