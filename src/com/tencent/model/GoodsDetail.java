package com.tencent.model;

import com.tencent.utils.WxpayUtil;
import com.google.gson.annotations.SerializedName;

/**
 * 商品详情 String(6000)
 * @author Jack
 *
 */
public class GoodsDetail {
	/**
	 * 实例化商品详情
	 * 
	 * @param goodsId 必填 32 商品的编号
	 * @param goodsName 必填 256 商品名称
	 * @param price 必填 商品数量
	 * @param quantity 必填 商品单价，单位为分。注意：a、单品总金额应<=订单总金额total_fee，否则会导致下单失败。b、 单品单价，如果商户有优惠，需传输商户优惠后的单价
	 * @return
	 */
	public static GoodsDetail newInstance(String goodsId, String goodsName, long price, int quantity) {
		GoodsDetail info = new GoodsDetail();
		info.setGoodsId(goodsId);
		info.setGoodsName(goodsName);
		info.setPrice(price);
		info.setQuantity(quantity);
		return info;
	}

	/** 必填 32 商品的编号 */
	@SerializedName("goods_id")
	private String goodsId;

	/** 可选 32 微信支付定义的统一商品编号 */
	@SerializedName("wxpay_goods_id")
	private String wxpayGoodsId;

	/** 必填 256 商品名称 */
	@SerializedName("goods_name")
	private String goodsName;

	/** 必填 商品数量 */
	private int quantity;

	/** 必填 商品单价，单位为分。注意：a、单品总金额应<=订单总金额total_fee，否则会导致下单失败。b、 单品单价，如果商户有优惠，需传输商户优惠后的单价 */
	private String price;

	public String getWxpayGoodsId() {
		return this.wxpayGoodsId;
	}

	public String getGoodsId() {
		return this.goodsId;
	}

	public String getGoodsName() {
		return this.goodsName;
	}

	public String getPrice() {
		return this.price;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public GoodsDetail setWxpayGoodsId(String wxpayGoodsId) {
		this.wxpayGoodsId = wxpayGoodsId;
		return this;
	}

	public GoodsDetail setGoodsId(String goodsId) {
		this.goodsId = goodsId;
		return this;
	}

	public GoodsDetail setGoodsName(String goodsName) {
		this.goodsName = goodsName;
		return this;
	}

	public GoodsDetail setPrice(long price) {
		this.price = WxpayUtil.toAmount(price).toString();
		return this;
	}

	public GoodsDetail setQuantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	/*
	 * { "goods_id": "iphone6s_16G", "wxpay_goods_id": "1001", "goods_name": "iPhone6s 16G", "quantity": 1, "price": 528800, }
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"goods_id\": \"").append(this.goodsId).append("\",");
		sb.append("\"wxpay_goods_id\": \"").append(this.wxpayGoodsId).append("\",");
		sb.append("\"goods_name\":\"").append(this.goodsName).append("\",");
		sb.append("\"quantity\":\"").append(this.quantity).append("\",");
		sb.append("\"price\":\"").append(this.price).append("\",");
		sb.append('}');
		return sb.toString();
	}
}
