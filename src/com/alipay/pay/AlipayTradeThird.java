package com.alipay.pay;

import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.util.JsonUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.config.AlipayWebConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝支付服务(第三方)
 *
 * @author zhsh
 */
public class AlipayTradeThird {

    private static Log log = LogFactory.getLog(AlipayTradeThird.class);

    /**
     * 支付宝服务
     */
    private static AlipayClient alipayClient;

    static {
        alipayClient = new DefaultAlipayClient(AlipayWebConfig.URL, AlipayWebConfig.APPID, AlipayWebConfig.RSA_PRIVATE_KEY, AlipayWebConfig.FORMAT, AlipayWebConfig.CHARSET, AlipayWebConfig.ALIPAY_PUBLIC_KEY, AlipayWebConfig.sign_type);
    }

    /**
     * 统一收单交易支付接口（条码支付）
     *
     * @param paymentRecord
     * @return AlipayTradePayResponse
     */
    public AlipayTradePayResponse AlipayTradePay(PaymentRecord paymentRecord) {

        AlipayTradePayResponse response = null;

        DecimalFormat df = new DecimalFormat("###############0");
        // 支付超时，线下扫码交易定义为5分钟
        String t_timeout = paymentRecord.getTimeout();
        if (StringUtils.isEmpty(t_timeout))
            t_timeout = "5m";
        if (!t_timeout.toLowerCase().endsWith("m"))
            t_timeout = t_timeout + "m";
        // 创建条码支付请求builder，设置请求参数
        String outTradeNo = paymentRecord.getPartner() + paymentRecord.getOutTradeNo(); // 交易流水号=合作方号+交易流水号
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("out_trade_no", outTradeNo);
        params.put("scene", "bar_code");
        params.put("auth_code", paymentRecord.getAuthCode());
        params.put("product_code", "FACE_TO_FACE_PAYMENT");
        params.put("subject", paymentRecord.getTradeName());
        params.put("total_amount", paymentRecord.getTotalFee());
        params.put("body", paymentRecord.getSummary());
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(0);
        Map<String, Object> goods = new HashMap<String, Object>(0);
        goods.put("goods_id", paymentRecord.getItem());
        goods.put("goods_name", (paymentRecord.getSummary() == null || "".equals(paymentRecord.getSummary())) ? paymentRecord.getTradeName() : paymentRecord.getSummary());
        goods.put("quantity", 1);
        goods.put("price", Long.parseLong(df.format(paymentRecord.getTotalFee().doubleValue() * 100)));
        goods.put("body", paymentRecord.getSummary());
        list.add(goods);
        params.put("goods_detail", list);
        params.put("operator_id", paymentRecord.getItem());
        params.put("store_id", "0");// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        Map<String, Object> extend_params = new HashMap<String, Object>();
        extend_params.put("sys_service_provider_id", AlipayWebConfig.ALIPAY_PARTNER);
        params.put("extend_params", extend_params);
        params.put("timeout_express", t_timeout);
        request.setBizContent(JsonUtil.getJson(params));
        request.setNotifyUrl(AlipayWebConfig.NOTIFY_URL);
        try {
            log.debug("订单[" + outTradeNo + "]调起支付宝：" + request.getBizContent());
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("订单[" + outTradeNo + "]支付失败：" + e.getMessage(), e);
        }

        return response;
    }
}
