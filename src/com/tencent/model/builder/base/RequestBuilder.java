package com.tencent.model.builder.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tencent.model.GoodsDetail;

import cn.tsinghua.sftp.util.StringUtil;

public abstract class RequestBuilder {
	/**
	 * 生成 商品详情
	 */
	public static String buildGoodsDetail(List<GoodsDetail> goodsDetailList) {
		String str = "";
		if (goodsDetailList != null && goodsDetailList.size() > 0) {
			str = "<![CDATA[{\"goods_detail\":[";
			for (GoodsDetail goodsDetail : goodsDetailList) {
				if (goodsDetailList.indexOf(goodsDetail) > 0)
					str += ",";
				str += goodsDetail.toString();
			}
			str += "]}]]>";
		}
		return str;
	}

	/**
	 * 除去数组中的空值和签名参数
	 * 
	 * @param sArray 签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {
		Map<String, String> result = new HashMap<String, String>();
		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {
		String prestr = "";
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}

	/** 生成签名 */
	public abstract String buildDigest();

	/** 生成XML */
	public abstract String buildXmlString();

	/** 元转分 */
	public static String yuanToFen(String yuan) {
		if (StringUtil.isEmpty(yuan)) {
			return "";
		}
		DecimalFormat a = new DecimalFormat("############");
		BigDecimal fen = new BigDecimal(yuan).multiply(new BigDecimal(100));
		return a.format(fen);
	}
}
