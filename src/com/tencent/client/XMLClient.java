package com.tencent.client;

import cn.tsinghua.sftp.bean.WxpayRequest;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.util.DateUtils;
import com.tencent.model.WxpayResult;
import com.tencent.model.builder.UnifiedOrderRequestBuilder;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.net.ssl.SSLContext;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * HTTP Client, to send data of XML type to Server. This is a demonstration of how to use HTTP Client API
 */
public class XMLClient {
    private static final Log log = LogFactory.getLog(XMLClient.class);

    /**
     * HTTP Client Object,used HttpClient Class before(version 3.x),but now the HttpClient is an interface
     */
    private CloseableHttpClient client;

    /**
     * Send a XML-Formed string to HTTP Server by post method
     *
     * @param url the request URL string
     * @param xml XML-Formed string ,will not check whether this string is XML-Formed or not
     */
    public WxpayResult sendPost(String url, String xml) throws IOException {
        log.debug("sendPost>>>>url=" + url + "\r\n" + xml);
        WxpayResult payResult = new WxpayResult();
        if (client == null) {
            client = HttpClients.createDefault();
        }
        HttpPost post = new HttpPost(url);
        StringEntity postEntity = new StringEntity(xml, "UTF-8");
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(postEntity);
        HttpResponse response = client.execute(post);
        payResult = getWxpayResult(payResult, response, false);
        return payResult;
    }

    /**
     * 根据Response状态判断生成WxpayResult结果
     *
     * @param payResult
     * @param response
     * @return
     * @throws IOException
     */
    private WxpayResult getWxpayResult(WxpayResult payResult, HttpResponse response, boolean useGzip) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            if (useGzip) {
                byte[] bytes = EntityUtils.toByteArray(entity);
                EntityUtils.consume(entity);
                payResult = resolveGzip(bytes);
            } else {
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                payResult = resolve(result);
            }
            //log.debug("sendPost>>>>payResult=" + payResult);
        }
        return payResult;
    }

    public WxpayResult sendPostSSL(String url, String xml, String mch_id) throws IOException {
        log.debug("sendPostSSL:" + url + "\r\n" + xml);
        WxpayResult payResult = new WxpayResult();
        CloseableHttpClient clientSSL = null;
        try {
            char[] keyPassword = mch_id.toCharArray();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            // 一个通道号对应一个证书
            String certFile = TysfConfig.getCertPath(mch_id + "_apiclient_cert.p12");
            // log.debug(">>>>>>>>PKCS12: " + certFile);
            keyStore.load(new FileInputStream(certFile), keyPassword);
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keyPassword).build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            clientSSL = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            // 发送并接收数据
            HttpPost post = new HttpPost(url);
            StringEntity postEntity = new StringEntity(xml, "UTF-8");
            post.addHeader("Content-Type", "text/xml");
            post.setEntity(postEntity);
            HttpResponse response = clientSSL.execute(post);
            payResult = getWxpayResult(payResult, response, false);
        } catch (Exception e) {
            payResult.setReturn_code("ERROR");
            payResult.setReturn_msg(e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            if (clientSSL != null)
                clientSSL.close();
        }
        return payResult;
    }

    /**
     * 提交xml到URL并返回GZIP压缩文件流
     *
     * @param url
     * @param xml
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public WxpayResult sendPostGzip(String url, String xml) throws ParseException, IOException {
        log.debug("sendPostGzip>>>>" + url + "\r\n" + xml);
        WxpayResult payResult = new WxpayResult();
        if (client == null) {
            client = HttpClients.createDefault();
        }
        HttpPost post = new HttpPost(url);
        StringEntity postEntity = new StringEntity(xml, "UTF-8");
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(postEntity);
        CloseableHttpResponse response = client.execute(post);
        payResult = getWxpayResult(payResult, response, true);
        return payResult;
    }

    /**
     * post发送请求数据
     *
     * @param urlStr
     */
    @Deprecated
    public WxpayResult sendPostBill(String urlStr, String xml) {
        WxpayResult payResult = new WxpayResult();

        BufferedReader br = null;
        HttpURLConnection con = null;
        OutputStreamWriter out = null;
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            // 发送POST请求必须设置如下两行
            con.setDoOutput(true); // POST方式
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            // 设置通用的请求属性
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 输出流，写数据
            out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(new String(xml.getBytes("UTF-8")));
            // flush输出流的缓冲
            out.flush();
            // 读取服务器的响应内容并显示 定义BufferedReader输入流来读取URL的响应
            br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                buffer.append(line); // 将读到的内容添加到 buffer 中
                buffer.append("\n"); // 添加换行符
            }
            String result = buffer.toString();
            // log.debug(">>>>>>>>>>>>sendPostBill:" + result);
            payResult = resolve(result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                    con.disconnect();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return payResult;
    }

    /**
     * 解析XML
     */
    public WxpayResult resolve(String xml) {
        WxpayResult result = new WxpayResult();
        try {
            if (StringUtils.isEmpty(xml)) {
                return result;
            }
            Document document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> eleList = root.elements();
            Map<String, Object> map = new HashMap<String, Object>();
            for (Element element : eleList) {
                map.put(element.getName(), element.getText());
            }
            result = (WxpayResult) mapToObject(map, WxpayResult.class);
        } catch (Exception e) {
            log.error("[resolve] 支付结果解析失败：" + e.getMessage(), e);
        }
        result.setResultXML(xml);
        return result;
    }

    /**
     * 解析GZip
     */
    public WxpayResult resolveGzip(byte[] bytes) {
        WxpayResult result = new WxpayResult();
        try {
            String xml = new String(bytes, "UTF-8");
            if (StringUtils.isEmpty(xml)) {
                return result;
            }
            //<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[No Bill Exist]]></return_msg><error_code><![CDATA[20002]]></error_code></xml>
            if (xml.startsWith("<xml>")&&xml.endsWith("</xml>")) {
                Document document = DocumentHelper.parseText(xml);
                Element root = document.getRootElement();
                List<Element> eleList = root.elements();
                Map<String, Object> map = new HashMap<>();
                for (Element element : eleList) {
                    map.put(element.getName(), element.getText());
                }
                result = (WxpayResult) mapToObject(map, WxpayResult.class);
                result.setResultXML(xml);
            } else {
                result.setReturn_code("SUCCESS");
                xml = uncompress(bytes);
                result.setResultXML(xml);
            }
        } catch (Exception e) {
            log.error("[resolveGzip] 支付结果解析失败：" + e.getMessage(), e);
        }
        return result;
    }

    // 解压缩
    public static String uncompress(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gzip = new GZIPInputStream(in);
        int n;
        byte[] buffer = new byte[256];
        while ((n = gzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
        return out.toString("UTF-8");
    }

    // 输入流转Map
    public static Map<String, Object> streamToMap(InputStream is) {
        Map<String, Object> map = new HashMap<>();
        try {
            StringBuffer sb = new StringBuffer();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            String str = sb.toString();
            // 获取post参数
            org.dom4j.Document document = DocumentHelper.parseText(str);
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            Iterator<Element> itr = root.elementIterator();
            if (itr != null) {
                while (itr.hasNext()) {
                    Element node = itr.next(); // 获取某个子节点对象
                    map.put(node.getName(), node.getText());
                }
            }
        } catch (Exception e) {
            log.error("[streamToMap] 输入流转Map失败：" + e.getMessage(), e);
        }
        return map;
    }

    /**
     * Map转对象BeanClass
     */
    public static WxpayResult mapToResult(Map<String, Object> map) {
        WxpayResult result = new WxpayResult();
        try {
            result = (WxpayResult) mapToObject(map, WxpayResult.class);
        } catch (Exception e) {
            log.error("[mapToResult] Map转支付结果失败：" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Map转对象BeanClass
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;
        Object obj = beanClass.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null && map.get(property.getName()) != null) {
                Field field = getClassField(beanClass, property.getName());
                Object value = map.get(property.getName());
                value = convertValType(value, field.getType());
                setter.invoke(obj, value);
            }
        }
        return obj;
    }

    /**
     * 将Object类型的值，转换成bean对象属性里对应的类型值
     *
     * @param value          Object对象值
     * @param fieldTypeClass 属性的类型
     * @return 转换后的值
     */
    private static Object convertValType(Object value, Class<?> fieldTypeClass) {
        Object retVal = null;
        try {
            if (Long.class.getName().equalsIgnoreCase(fieldTypeClass.getName())) {
                retVal = Long.parseLong(value.toString());
            } else if (Integer.class.getName().equals(fieldTypeClass.getName()) || int.class.getName().equals(fieldTypeClass.getName())) {
                retVal = Integer.parseInt(value.toString());
            } else if (Float.class.getName().equalsIgnoreCase(fieldTypeClass.getName())) {
                retVal = Float.parseFloat(value.toString());
            } else if (Double.class.getName().equalsIgnoreCase(fieldTypeClass.getName())) {
                retVal = Double.parseDouble(value.toString());
            } else if (BigDecimal.class.getName().equals(fieldTypeClass.getName())) {
                retVal = BigDecimal.valueOf(Double.parseDouble(value.toString()));
            } else if (Date.class.getName().equals(fieldTypeClass.getName())) {
                retVal = DateUtils.parseDate(value.toString(), "yyyyMMdd", "yyyyMMddHHmmss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss");
            } else {
                retVal = value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    /**
     * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
     *
     * @param clazz     指定的class
     * @param fieldName 字段名称
     * @return Field对象
     */
    private static Field getClassField(Class<?> clazz, String fieldName) {
        if (Object.class.getName().equals(clazz.getName())) {
            return null;
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {// 简单的递归一下
            return getClassField(superClass, fieldName);
        }
        return null;
    }

    /**
     * 支付结果转XML
     *
     * @param wxpayRequest
     * @return
     */
    public String getXMLString(WxpayRequest wxpayRequest) {
        UnifiedOrderRequestBuilder rb = new UnifiedOrderRequestBuilder();
        rb.setAttach(wxpayRequest.getAttach());
        rb.setBody(wxpayRequest.getBody());
        rb.setDetail(wxpayRequest.getDetail());
        rb.setDevice_info(wxpayRequest.getDevice_info());
        rb.setFee_type(wxpayRequest.getFee_type());
        rb.setGoods_tag(wxpayRequest.getGoods_tag());
        rb.setLimit_pay(wxpayRequest.getLimit_pay());
        rb.setNotify_url(wxpayRequest.getNotify_url());
        rb.setNonce_str(wxpayRequest.getNonce_str());
        rb.setOpenid(wxpayRequest.getOpenid());
        rb.setOut_trade_no(wxpayRequest.getOut_trade_no());
        rb.setProduct_id(wxpayRequest.getProduct_id());
        rb.setSign_type(wxpayRequest.getSign_type());
        rb.setSpbill_create_ip(wxpayRequest.getSpbill_create_ip());
        rb.setTime_expire(wxpayRequest.getTime_expire());
        rb.setTime_start(wxpayRequest.getTime_start());
        rb.setTotal_fee(wxpayRequest.getTotal_fee());
        rb.setTrade_type(wxpayRequest.getTrade_type());
        rb.setMch_id(wxpayRequest.getMch_id());
        return rb.buildXmlString();
    }

    /**
     * Main method
     *
     * @param args
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static void main(String[] args) throws ClientProtocolException, IOException {
        XMLClient client = new XMLClient();
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String xml = "";
        String mch_id = "1491596202";
        WxpayResult result = client.sendPostSSL(url, xml, mch_id);
        System.out.println(result);

//        String xml = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[签名错误]]></return_msg></xml>";
//        WxpayResult result = client.resolve(xml);
//        System.out.println(result.getReturn_code() + " - " + result.getReturn_msg());

//        StringBuffer sb = new StringBuffer();
//        sb.append("<xml>");
//        sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
//        sb.append("<return_msg><![CDATA[OK]]></return_msg>");
//        sb.append("<appid><![CDATA[wx2421b1c4370ec43b]]></appid>");
//        sb.append("<mch_id><![CDATA[10000100]]></mch_id>");
//        sb.append("<device_info><![CDATA[1000]]></device_info>");
//        sb.append("<nonce_str><![CDATA[TN55wO9Pba5yENl8]]></nonce_str>");
//        sb.append("<sign><![CDATA[BDF0099C15FF7BC6B1585FBB110AB635]]></sign>");
//        sb.append("<result_code><![CDATA[SUCCESS]]></result_code>");
//        sb.append("<openid><![CDATA[oUpF8uN95-Ptaags6E_roPHg7AG0]]></openid>");
//        sb.append("<is_subscribe><![CDATA[Y]]></is_subscribe>");
//        sb.append("<trade_type><![CDATA[MICROPAY]]></trade_type>");
//        sb.append("<bank_type><![CDATA[CCB_DEBIT]]></bank_type>");
//        sb.append("<total_fee>1</total_fee>");
//        sb.append("<fee_type><![CDATA[CNY]]></fee_type>");
//        sb.append("<transaction_id><![CDATA[1008450740201411110005820873]]></transaction_id>");
//        sb.append("<out_trade_no><![CDATA[1415757673]]></out_trade_no>");
//        sb.append("<attach><![CDATA[订单额外描述]]></attach>");
//        sb.append("<time_end><![CDATA[20141111170043]]></time_end>");
//        sb.append("<trade_state><![CDATA[SUCCESS]]></trade_state>");
//        sb.append("</xml>");
//        WxpayResult result = client.resolve(sb.toString());
//        System.out.println(result.getTrade_state());

//        // UnifiedOrder
//        sb = new StringBuffer();
//        sb.append("<xml>");
//        sb.append("<appid>wx2421b1c4370ec43b</appid>");
//        sb.append("<attach>支付测试</attach>");
//        sb.append("<body>JSAPI支付测试</body>");
//        sb.append("<mch_id>10000100</mch_id>");
//        sb.append("<detail><![CDATA[{ \"goods_detail\":[ { \"goods_id\":\"iphone6s_16G\", \"wxpay_goods_id\":\"1001\", \"goods_name\":\"iPhone6s 16G\", \"quantity\":1, \"price\":528800, \"goods_category\":\"123456\", \"body\":\"苹果手机\" }, { \"goods_id\":\"iphone6s_32G\", \"wxpay_goods_id\":\"1002\", \"goods_name\":\"iPhone6s 32G\", \"quantity\":1, \"price\":608800, \"goods_category\":\"123789\", \"body\":\"苹果手机\" } ] }]]></detail>");
//        sb.append("<nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>");
//        sb.append("<notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>");
//        sb.append("<openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid>");
//        sb.append("<out_trade_no>1415659990</out_trade_no>");
//        sb.append("<spbill_create_ip>14.23.150.211</spbill_create_ip>");
//        sb.append("<total_fee>1</total_fee>");
//        sb.append("<trade_type>JSAPI</trade_type>");
//        sb.append("<sign>0CB01533B8C1EF103065174F50BCA001</sign>");
//        sb.append("</xml>");
//        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//        WxpayResult result = client.sendPost(url, sb.toString());
//        System.out.println(result);
    }

}