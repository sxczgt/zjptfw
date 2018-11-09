package com.alipay.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ReaderXmlForDom4j {
	private static final Log log = LogFactory.getLog(ReaderXmlForDom4j.class);

	/**
	 * 解析支付宝批量付款结果
	 * 
	 * @param protocolXML
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parse(String protocolXML) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Document document = DocumentHelper.parseText(protocolXML);
			Element root = document.getRootElement();
			Element element = root.element("is_success");
			map.put("is_success", element.getText());
			if (element.getText().equals("T")) {
				Iterator<Element> it = root.element("request").elementIterator();
				while (it.hasNext()) {
					// 获取某个子节点对象
					Element e = it.next();
					// 对子节点进行遍历
					listNodes(e, map);
				}
				Element e = root.element("response");
				Element e1 = e.element("alipay_results");
				Element e2 = e1.element("alipay_result");
				Element e3 = e2.element("detailList");
				Iterator<Element> itr = e3.elementIterator();
				while (itr.hasNext()) {
					// 获取某个子节点对象
					Element node = itr.next();
					Map<String, String> mapDetail = new HashMap<String, String>();
					Element userSerialNo = node.element("userSerialNo");
					Element status = node.element("status");
					Element instructionId = node.element("instructionId");
					Element refundMark = node.element("refundMark");
					Element dealMemo = node.element("dealMemo");
					mapDetail.put(status.getName(), status.getText());
					mapDetail.put(instructionId.getName(), instructionId.getText());
					mapDetail.put(refundMark.getName(), refundMark.getText());
					mapDetail.put(dealMemo.getName(), dealMemo.getText());
					map.put("Recordid" + userSerialNo.getText(), mapDetail);
				}
			} else {
				map.put("error", root.element("error").getText());
			}
			// System.out.println(map);
		} catch (Exception e) {
			log.error("解析支付宝批量付款结果失败：" + e.getMessage(), e);
		}
		return map;
	}

	/**
	 * 解析支付宝订单查询结果(未引用)
	 * 
	 * @param protocolXML
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseForQuery(String protocolXML) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Document document = DocumentHelper.parseText(protocolXML);
			Element root = document.getRootElement();
			Element element = root.element("is_success");
			map.put("is_success", element.getText());
			if (element.getText().equals("T")) {
				Iterator<Element> it = root.element("request").elementIterator();
				while (it.hasNext()) {
					// 获取某个子节点对象
					Element e = it.next();
					// 对子节点进行遍历
					listNodes(e, map);
				}
				Element e = root.element("response");
				Element e1 = e.element("trade");
				Iterator<Element> itr = e1.elementIterator();
				while (itr.hasNext()) {
					// 获取某个子节点对象
					Element node = itr.next();
					map.put(node.getName(), node.getText());
				}
			} else {
				map.put("error", root.element("error").getText());
			}
			// System.out.println(map);
		} catch (Exception e) {
			log.error("解析支付宝订单查询结果失败：" + e.getMessage(), e);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static void listNodes(Element node, Map<String, Object> map) {
		// 获取当前节点的所有属性节点
		List<Attribute> list = node.attributes();
		// 遍历属性节点
		for (Attribute attr : list) {
			map.put(attr.getText(), node.getText());
		}
		// 当前节点下面子节点迭代器
		Iterator<Element> it = node.elementIterator();
		// 遍历
		while (it.hasNext()) {
			// 获取某个子节点对象
			Element e = it.next();
			// 对子节点进行遍历
			listNodes(e, map);
		}
	}

	public static void main(String[] arr) {
		try {
			Document document;
			org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
			document = reader.read(new java.io.File("F:\\123.xml"));
			parse(document.asXML());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}