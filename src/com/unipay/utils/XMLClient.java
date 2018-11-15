package com.unipay.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.unipay.model.QueryOrder;
import com.unipay.model.QueryOrderResult;

import cn.tsinghua.sftp.util.DateUtils;

/**
 * HTTP Client, to send data of XML type to Server. This is a demonstration of how to use HTTP Client API
 */
public class XMLClient {
	private static final Log log = LogFactory.getLog(XMLClient.class);

	/**
	 * Xml转对象BeanClass
	 */
	public static QueryOrderResult xmlToResult(String xml) {
		QueryOrderResult result = new QueryOrderResult();
		try {
			Map<String, Object> map = resolve(xml);
			result = (QueryOrderResult) mapToObject(map, QueryOrderResult.class);
		} catch (Exception e) {
			log.error("[xmlToResult] Xml转支付结果失败：" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Map转对象BeanClass
	 */
	public static QueryOrder mapToOrder(Map<String, Object> map) {
		QueryOrder result = new QueryOrder();
		try {
			result = (QueryOrder) mapToObject(map, QueryOrder.class);
		} catch (Exception e) {
			log.error("[mapToOrder] Map转支付结果失败：" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Map转对象BeanClass
	 */
	public static QueryOrderResult mapToResult(Map<String, Object> map) {
		QueryOrderResult result = new QueryOrderResult();
		try {
			result = (QueryOrderResult) mapToObject(map, QueryOrderResult.class);
		} catch (Exception e) {
			log.error("[mapToResult] Map转支付结果失败：" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 将Object类型的值，转换成bean对象属性里对应的类型值
	 * 
	 * @param value Object对象值
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
	 * 解析XML
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> resolve(String xml) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(xml))
			return map;
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> eleList = root.elements();
			for (Element element : eleList) {
				if (element.getName().equals("QUERYORDER")) {
					List<Element> eleList2 = element.elements();
					for (Element element2 : eleList2) {
						map.put(element2.getName(), element2.getText());
					}
					continue;
				}
				map.put(element.getName(), element.getText());
			}
		} catch (Exception e) {
			log.error("[resolve] 支付结果解析失败：" + e.getMessage(), e);
		}
		return map;
	}

	/**
	 * 获取指定字段名称查找在class中的对应的Field对象(包括查找父类)
	 * 
	 * @param clazz 指定的class
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
	 * Main method
	 * 
	 * @param args
     */
	public static void main(String[] args) {
		String xml = "<xml><RETURN_CODE>交易返回码，成功时总为000000</RETURN_CODE><RETURN_MSG>交易返回提示信息，成功时为空</RETURN_MSG><CURPAGE>当前页</CURPAGE><PAGECOUNT>总页数</PAGECOUNT><TOTAL>总笔数</TOTAL><PAYAMOUNT>支付总金额</PAYAMOUNT><REFUNDAMOUNT>退款总金额</REFUNDAMOUNT><QUERYORDER>        <MERCHANTID>商户代码</MERCHANTID>        <BRANCHID>商户所在分行</BRANCHID>        <POSID>商户的POS号</POSID>        <ORDERID>订单号</ORDERID>        <ORDERDATE>支付/退款交易时间</ORDERDATE>s        <ACCDATE>记账日期</ACCDATE>        <AMOUNT>支付金额</AMOUNT>            <STATUSCODE>支付/退款状态码</STATUSCODE>        <STATUS>支付/退款状态</STATUS>        <REFUND>退款金额</REFUND>        <SIGN>签名串</SIGN></QUERYORDER></xml>";
		Map<String, Object> result = resolve(xml);
		System.out.println(result);
	}

}