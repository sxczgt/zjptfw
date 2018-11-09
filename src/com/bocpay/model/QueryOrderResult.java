package com.bocpay.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.bocpay.utils.XMLClient;

import cn.tsinghua.sftp.util.StringUtil;

public class QueryOrderResult {
	private static final Log log = LogFactory.getLog(QueryOrderResult.class);

	/** 交易返回码，成功时总为000000 */
	String returnCode;
	/** 交易返回提示信息，成功时为空 */
	String returnMsg;
	/** 当前页 */
	int curPage = 0;
	/** 总页数 */
	int pageCount = 0;
	/** 总笔数 */
	int total = 0;
	/** 支付总金额 */
	BigDecimal payAmount = BigDecimal.ZERO;
	/** 退款总金额 */
	BigDecimal refundAmount = BigDecimal.ZERO;
	/** 订单查询结果列表 */
	List<QueryOrder> orderList = new ArrayList<QueryOrder>();

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version = \"1.0\" encoding=\"UTF-8\" ?>").append("\n");
		sb.append("<DOCUMENT>").append("\n");
		sb.append("	<RETURN_CODE>000000</RETURN_CODE>").append("\n");
		sb.append("	<RETURN_MSG></RETURN_MSG>").append("\n");
		sb.append("	<CURPAGE>1</CURPAGE>").append("\n");
		sb.append("	<PAGECOUNT>1</PAGECOUNT>").append("\n");
		sb.append("	<TOTAL>1</TOTAL>").append("\n");
		sb.append("	<PAYAMOUNT>0.00</PAYAMOUNT>").append("\n");
		sb.append("	<REFUNDAMOUNT>0.00</REFUNDAMOUNT>").append("\n");
		sb.append("").append("\n");
		sb.append("	<QUERYORDER>").append("\n");
		sb.append("		<MERCHANTID>105100000012072</MERCHANTID>").append("\n");
		sb.append("		<BRANCHID>110000000</BRANCHID>").append("\n");
		sb.append("		<POSID>004161878</POSID>").append("\n");
		sb.append("		<ORDERID>2999DEMO20171110144132</ORDERID>").append("\n");
		sb.append("		<ORDERDATE>20171110144235</ORDERDATE>").append("\n");
		sb.append("		<ACCDATE>20171110</ACCDATE>").append("\n");
		sb.append("		<AMOUNT>0.01</AMOUNT>").append("\n");
		sb.append("		<STATUSCODE>1</STATUSCODE>").append("\n");
		sb.append("		<STATUS>成功</STATUS>").append("\n");
		sb.append("		<REFUND>0.00</REFUND>").append("\n");
		sb.append("		<SIGN>99584bd5e663ecba113edf45a6dfa536e79368734bdc2554ff9ef44a9b5bec680ca7752aae446681d6218b25372e1a7b470e31c960679535f97c38c9e45413b15aa02934bd1c9ebc8a0a48031459be7a9b8097e4653892d4dc16bd237f172a64056d7a3416ec97deb7d59d2bf700e232c031dc585de1563864c019a0288ab4d9</SIGN>").append("\n");
		sb.append("	</QUERYORDER>").append("\n");
		sb.append("").append("\n");
		sb.append("</DOCUMENT>").append("\n");

		String xml = sb.toString();
		log.debug(xml);
		QueryOrderResult r = xmlToResult(xml);
		log.debug(r);
		for (QueryOrder q : r.getOrderList()) {
			log.debug(q);
		}
	}

	/**
	 * XML转QueryOrderResult类
	 * 
	 * @param xml
	 * @return
	 */
	public static QueryOrderResult xmlToResult(String xml) {
		QueryOrderResult result = new QueryOrderResult();
		try {
			xml = xml.trim();
			if (StringUtil.isEmpty(xml)) {
				return result;
			}
			// XML转MAP
			Map<String, Object> map = resolveMap(xml);
			if (map.size() > 1) {
				// MAP转CLASS
				result = XMLClient.mapToResult(map);
			}
		} catch (Exception e) {
			log.error("银联查询结果解析失败：" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * XML转Map对象
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> resolveMap(String xml) {
		List<QueryOrder> orderList = new ArrayList<QueryOrder>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isEmpty(xml))
			return map;
		try {
			xml = xml.trim();
			if (xml.startsWith("<html>")) {
				log.error("[resolve] 支付结果解析失败：页面报错，请检查页面返回信息。");
				return map;
			}
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> eleList = root.elements();
			for (Element element : eleList) {
				if (element.getName().equals("QUERYORDER")) {
					Map<String, Object> orderMap = new HashMap<String, Object>();
					List<Element> elementList = element.elements();
					for (Element elementorder : elementList) {
						String fieldName = QueryOrder.getFieldName(StringUtil.lineToHump(elementorder.getName()));
						String fieldValue = elementorder.getText();
						orderMap.put(fieldName, fieldValue);
					}
					QueryOrder queryOrder = XMLClient.mapToOrder(orderMap);
					orderList.add(queryOrder);
					continue;
				}
				String fieldName = QueryOrderResult.getFieldName(StringUtil.lineToHump(element.getName()));
				String fieldValue = element.getText();
				map.put(fieldName, fieldValue);
			}
		} catch (Exception e) {
			log.error("[resolve] 支付结果解析失败：" + e.getMessage(), e);
		}
		map.put("orderList", orderList);
		return map;
	}

	/**
	 * 获取当前类的Field名
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String getFieldName(String fieldName) {
		Field[] fields = QueryOrderResult.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			if (fieldName.equalsIgnoreCase(name)) {
				return name;
			}
		}
		return fieldName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{").append("\n");
		sb.append("RETURN_CODE=").append(returnCode).append(",\n");
		sb.append("RETURN_MSG=").append(returnMsg).append(",\n");
		sb.append("CURPAGE=").append(curPage).append(",\n");
		sb.append("PAGECOUNT=").append(pageCount).append(",\n");
		sb.append("TOTAL=").append(total).append(",\n");
		sb.append("PAYAMOUNT=").append(payAmount).append(",\n");
		sb.append("REFUNDAMOUNT=").append(refundAmount).append(",\n");
		sb.append("ORDERLIST=").append(orderList).append("\n");
		sb.append("}");
		return sb.toString();

	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public List<QueryOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<QueryOrder> orderList) {
		this.orderList = orderList;
	}

}
