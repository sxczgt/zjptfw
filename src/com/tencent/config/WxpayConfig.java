package com.tencent.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 类名：WxpayConfig<br>
 * 功能：基础配置类<br>
 * 详细：设置帐户有关信息及返回路径<br>
 * 版本：0.1<br>
 * 日期：2016-12-16
 */
public class WxpayConfig {
    private static Log log = LogFactory.getLog(WxpayConfig.class);
    /**
     * 微信支付分配的公众账号ID（企业号corpid即为此appId）
     */
    public static String appid;
    /**
     * 微信秘钥
     */
    public static String appsercret;
    /**
     * 商户的私钥
     */
    public static String key;
    // /** 商户号 ，因为每个通道都是独立的商户，所以这个值需要在支付项目表里取*/
    // public static String mch_id;

    /**
     * 获取token或openid地址
     */
    public static String access_token;
    /**
     * 身份认证地址
     */
    public static String authorize;
    /**
     * 统一下单地址
     */
    public static String unifiedorder;
    /**
     * 转换短链接地址
     */
    public static String shorturl;
    /**
     * 查询订单地址
     */
    public static String orderquery;
    /**
     * 关闭订单地址
     */
    public static String closeorder;
    /**
     * 申请退款地址
     */
    public static String refund;
    /**
     * 查询退款地址
     */
    public static String refundquery;
    /**
     * 撤销订单地址
     */
    public static String reverse;
    /**
     * 提交刷卡支付地址
     */
    public static String micropay;
    /**
     * 交易保障地址
     */
    public static String report;
    /**
     * 下载对账单地址
     */
    public static String downloadbill;
    /**
     * 异步通知地址
     */
    public static String notify_url;
    /**
     * 公众号支付跳转地址
     **/
    public static String redirect_uri;

    /**
     * 获取RSA加密公钥API
     */
    public static String getpublickey;
    /**
     * 企业付款到银行卡API
     */
    public static String pay_bank;
    /**
     * 查询企业付款银行卡API
     */
    public static String query_bank;

    /**
     * 字符编码格式 目前支持 gbk 或 utf-8
     */
    public static String input_charset = "gbk";
    /**
     * 签名方式 不需修改
     */
    public static String sign_type = "MD5";

    private static PropertiesConfiguration configs;

    static {
        //读取配置信息
        init("tencent.properties");
    }

    public static void init(String filePath) {
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

        appid = configs.getString("appid");
        appsercret = configs.getString("appsercret");
        key = configs.getString("key");
        access_token = configs.getString("access_token");
        authorize = configs.getString("authorize");
        unifiedorder = configs.getString("unifiedorder");
        shorturl = configs.getString("shorturl");
        orderquery = configs.getString("orderquery");
        closeorder = configs.getString("closeorder");
        refund = configs.getString("refund");
        refundquery = configs.getString("refundquery");
        reverse = configs.getString("reverse");
        micropay = configs.getString("micropay");
        report = configs.getString("report");
        downloadbill = configs.getString("downloadbill");
        notify_url = configs.getString("notify_url");
        redirect_uri = configs.getString("redirect_uri");
        getpublickey = configs.getString("getpublickey");
        pay_bank = configs.getString("pay_bank");
        query_bank = configs.getString("query_bank");
        log.info("初始化WxpayConfig：" + description());
    }

    private static String description() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("appid=" + appid);
        sb.append(",appsercret=" + appsercret);
        sb.append(",key=" + key);
        sb.append(",access_token=" + access_token);
        sb.append(",authorize=" + authorize);
        sb.append(",unifiedorder=" + unifiedorder);
        sb.append(",shorturl=" + shorturl);
        sb.append(",orderquery=" + orderquery);
        sb.append(",closeorder=" + closeorder);
        sb.append(",refund=" + refund);
        sb.append(",refundquery=" + refundquery);
        sb.append(",reverse=" + reverse);
        sb.append(",micropay=" + micropay);
        sb.append(",report=" + report);
        sb.append(",downloadbill=" + downloadbill);
        sb.append(",notify_url=" + notify_url);
        sb.append(",redirect_uri=" + redirect_uri);
        sb.append(",input_charset=" + input_charset);
        sb.append(",getpublickey=" + getpublickey);
        sb.append(",pay_bank=" + pay_bank);
        sb.append(",query_bank=" + query_bank);
        sb.append(",sign_type=" + sign_type);
        return sb.toString();
    }
}
