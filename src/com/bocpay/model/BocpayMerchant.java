package com.bocpay.model;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 中行商户对象<br>
 *
 * @author Jack
 */
public class BocpayMerchant {
    /**
     * 支付地址
     */
    private String vpcpayUrl = "https://migs.mastercard.com.au/vpcpay";
    /**
     * 查询、关闭、退款等地址
     */
    private String vpcdpsUrl = "https://migs.mastercard.com.au/vpcdps";
    /**
     * 商户密钥
     */
    private String secureSecret;
    /**
     * 商户代码
     */
    private String merchantID;
    /**
     * 商户访问代码
     */
    private String accessCode;
    /**
     * 查询、关闭、退款等账号
     */
    private String user;
    /**
     * 查询、关闭、退款等密码
     */
    private String password;
    /**
     * 字符集
     */
    private String charset = "ISO-8859-1";
    /**
     * 同步返回地址
     */
    private String returnUrl;

    public String toString() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("merchantID", merchantID);
        map.put("accessCode", accessCode);
        map.put("user", user);
        map.put("password", password);
        map.put("secureSecret", secureSecret);
        map.put("vpcpayUrl", vpcpayUrl);
        map.put("vpcdpsUrl", vpcdpsUrl);
        map.put("returnUrl", returnUrl);
        map.put("charset", charset);
        return map.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof BocpayMerchant) {
            BocpayMerchant bm = (BocpayMerchant) obj;
            return (merchantID.equals(bm.merchantID));
        }
        return super.equals(obj);
    }

    public int hashCode() {
        //BocpayMerchant bm = (BocpayMerchant) this;
        return merchantID.hashCode();
    }

    public String getVpcpayUrl() {
        return vpcpayUrl;
    }

    public void setVpcpayUrl(String vpcpayUrl) {
        this.vpcpayUrl = vpcpayUrl;
    }

    public String getVpcdpsUrl() {
        return vpcdpsUrl;
    }

    public void setVpcdpsUrl(String vpcdpsUrl) {
        this.vpcdpsUrl = vpcdpsUrl;
    }

    public String getSecureSecret() {
        return secureSecret;
    }

    public void setSecureSecret(String secureSecret) {
        this.secureSecret = secureSecret;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * 解析JSON为商户对象
     */
    public static BocpayMerchant parse(String bocJson) {
        return JSON.parseObject(bocJson, BocpayMerchant.class);
    }

    /**
     * 将商户对象转JSON
     */
    public static String toJson(BocpayMerchant bm) {
        return JSON.toJSONString(bm);
    }

    public static void main(String[] args) {
        // 生成JSON
        BocpayMerchant bm = new BocpayMerchant();
        bm.setMerchantID("110082202460"); // 商户账号
        bm.setAccessCode("41EA4FC0"); // 商户访问代码
        bm.setUser("tstrans"); // 查询、关闭、退款等账号
        bm.setPassword("TsTrs2018"); // 查询、关闭、退款等密码
        bm.setSecureSecret("B659B3A5C8A2443EDD3FCB6C15AA2EE4"); // 商户密钥
        bm.setVpcdpsUrl("https://migs.mastercard.com.au/vpcdps"); // 查询、关闭、退款等地址
        bm.setVpcpayUrl("https://migs.mastercard.com.au/vpcpay"); // 支付地址
        bm.setReturnUrl("http://166.111.7.67:28076/sfpt/bocpayReceivedAction.action"); // 同步返回地址
        String json = toJson(bm);
        System.out.println(json);
        bm = parse(json);
        System.out.println(bm);
    }
}
