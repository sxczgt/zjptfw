package com.unipay.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLBuilder {
	private SAXBuilder builder = null;
	private Document doc = null;
	private Element root = null;
	private static Log trace = LogFactory.getLog("trace");

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GB2312\" standalone=\"yes\" ?>").append("\r\n");
		sb.append("<TX> ").append("\r\n");
		sb.append("  <REQUEST_SN>请求序列码</REQUEST_SN> ").append("\r\n");
		sb.append("  <CUST_ID>商户号</CUST_ID> ").append("\r\n");
		sb.append("  <TX_CODE>5W1001</TX_CODE> ").append("\r\n");
		sb.append("  <RETURN_CODE>返回码</RETURN_CODE> ").append("\r\n");
		sb.append("  <RETURN_MSG>返回码说明</RETURN_MSG> ").append("\r\n");
		sb.append("  <LANGUAGE>CN</LANGUAGE> ").append("\r\n");
		sb.append("  <TX_INFO> ").append("\r\n");
		// sb.append(" <REM1>备注1</REM1> ").append("\r\n");
		// sb.append(" <REM2>备注2</REM2> ").append("\r\n");
		sb.append("	<TPAGE>总页次</TPAGE>  ").append("\r\n");
		sb.append("	<CUR_PAGE>当前页次</CUR_PAGE>  ").append("\r\n");
		sb.append("	<LIST> ").append("\r\n");
		sb.append("	 <TRAN_DATE>交易日期</TRAN_DATE>  ").append("\r\n");
		sb.append("	 <REFUND_DATE>退款日期</REFUND_DATE>  ").append("\r\n");
		sb.append("	 <ORDER_NUMBER>订单号</ORDER_NUMBER>  ").append("\r\n");
		sb.append("	 <REFUND_ACCOUNT>退款账号</REFUND_ACCOUNT>  ").append("\r\n");
		sb.append("	 <PAY_AMOUNT>支付金额</PAY_AMOUNT>  ").append("\r\n");
		sb.append("	 <REFUNDEMENT_AMOUNT>退款金额</REFUNDEMENT_AMOUNT>  ").append("\r\n");
		sb.append("	 <POS_CODE>柜台号</POS_CODE>  ").append("\r\n");
		sb.append("	 <USERID>操作员</USERID>  ").append("\r\n");
		sb.append("	 <STATUS>订单状态</STATUS>  ").append("\r\n");
		sb.append("	 <REFUND_CODE>退款流水号</REFUND_CODE>  ").append("\r\n");
		sb.append("	</LIST> ").append("\r\n");
		sb.append("	<NOTICE>提示信息</NOTICE> ").append("\r\n");
		sb.append("  </TX_INFO>   ").append("\r\n");
		sb.append("</TX> ").append("\r\n");
		String xml = sb.toString();
		System.out.println(xml);
		try {
			Reader r = new StringReader(xml);
			XMLBuilder xb = new XMLBuilder(r);
			r.close();
			Element etx = xb.getFirstElement(xb.getRootElement(), "TX_INFO");
			Element el = xb.getFirstElement(etx, "LIST");
			String val = xb.getFirstElementValue(el, "STATUS");
			System.out.println(val);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XMLBuilder(InputStream inputStream) throws Exception {
		builder = new SAXBuilder();
		try {
			if (builder != null) {
				doc = builder.build(inputStream);
				setRootElement();
			} else {
				throw new JDOMException("xml文档有错，可能输入流有误");
			}
		} catch (JDOMException e) {
			throw new JDOMException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public XMLBuilder(Reader reader) throws Exception {
		builder = new SAXBuilder();
		try {
			if (builder != null) {
				doc = builder.build(reader);
				setRootElement();
			} else {
				throw new JDOMException("xml文档有错，可能传人Reader有误");
			}
		} catch (JDOMException e) {
			throw new JDOMException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public XMLBuilder(File file) throws Exception {
		builder = new SAXBuilder();
		try {
			if (builder != null) {
				doc = builder.build(file);
				setRootElement();
			} else {
				throw new JDOMException("xml文档有错，可能文件有误：");
			}
		} catch (JDOMException e) {
			throw new JDOMException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public XMLBuilder(String fileName) throws Exception {
		builder = new SAXBuilder();
		try {
			if (builder != null) {
				doc = builder.build(fileName);
				setRootElement();
			} else {
				throw new JDOMException("xml文档有错，可能传入XML文件路径有误：" + fileName);
			}
		} catch (JDOMException e) {
			throw new JDOMException(e.getMessage());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public XMLBuilder() {
	}

	public Document getDocment() {
		return doc;
	}

	public void setRootElement() {
		if (root == null) {
			root = doc.getRootElement();
		}
	}

	public Element getRootElement() {
		if (root == null) {
			root = doc.getRootElement();
		}
		return root;
	}

	public Element getFirstElement(Element parent, String elementName) {
		if (parent != null) {
			List<?> list = parent.getChildren(elementName);
			if ((list != null) && (list.size() > 0)) {
				return (Element) list.get(0);
			}
			return null;
		}
		return null;
	}

	public String getFirstElementValue(Element parent, String elementName) {
		List<?> list = parent.getChildren(elementName);
		Element e = null;
		if ((list != null) && (list.size() > 0)) {
			e = (Element) list.get(0);
		}
		if (e != null) {
			return e.getText();
		}
		return "";
	}

	public String getFirstElementValue(Object parent, String elementName) {
		List<?> list = ((Element) parent).getChildren(elementName);
		Element e = null;
		if ((list != null) && (list.size() > 0)) {
			e = (Element) list.get(0);
		}
		if (e != null) {
			return e.getText();
		}
		return "";
	}

	public void setElementValue(Element parent, String elementName, String newValue) {
		Element e = getFirstElement(parent, elementName);
		if (e != null) {
			e.setText(newValue);
		}
	}

	public void setElementCDATAValue(Element parent, String elementName, String newValue) {
		Element e = getFirstElement(parent, elementName);
		if (e != null) {
			e.setText("");
			e.addContent(new CDATA(newValue));
		}
	}

	public List<?> getElements(Element parent, String elementName) {
		List<?> list = parent.getChildren(elementName);
		return list;
	}

	public void deleteElement(Element parent, String elementName) {
		Element e = getFirstElement(parent, elementName);
		if (e != null) {
			parent.removeChild(elementName);
		}
	}

	public void addElement(Element parent, Element son) {
		parent.addContent(son);
	}

	public void addElement(Element parent, String sKey, String sValue) {
		parent.addContent(new Element(sKey).setText(sValue));
	}

	public String outDocumentToString(Document doc, String indent, String charset) {
		if (doc == null) {
			return "";
		}
		XMLOutputter out = new XMLOutputter();
		out.getFormat();
		Format format = Format.getPrettyFormat();
		format.setIndent(indent);
		format.setEncoding(charset);
		format.setExpandEmptyElements(true);
		out.setFormat(format);
		return out.outputString(doc);
	}

	public String outElementToString(Element e, String indent, String charset) {
		if (e == null) {
			return "";
		}
		XMLOutputter out = new XMLOutputter();
		out.getFormat();
		Format format = Format.getPrettyFormat();
		format.setIndent(indent);
		format.setEncoding(charset);
		format.setExpandEmptyElements(true);
		out.setFormat(format);
		return out.outputString(e);
	}

	public void outElementToFile_old(Element e, String indent, String charset, String fileName) throws Exception {
		XMLOutputter out = new XMLOutputter();
		out.getFormat();
		Format format = Format.getPrettyFormat();
		format.setIndent(indent);
		format.setEncoding(charset);
		format.setExpandEmptyElements(true);
		out.setFormat(format);
		out.output(e, new FileOutputStream(fileName));
	}

	public void outElementToFile(Element e, String indent, String charset, String fileName) throws Exception {
		XMLOutputter out = new XMLOutputter();
		out.getFormat();
		Format format = Format.getPrettyFormat();
		format.setIndent(indent);
		format.setEncoding(charset);
		format.setExpandEmptyElements(true);
		out.setFormat(format);
		out.output(e, new FileOutputStream(fileName));
	}

	public void outDocumentToFile(Document doc, String indent, String charset, String fileName) throws Exception {
		XMLOutputter out = new XMLOutputter();
		out.getFormat();
		Format format = Format.getPrettyFormat();
		format.setIndent(indent);
		format.setEncoding(charset);
		format.setExpandEmptyElements(true);
		out.setFormat(format);
		out.output(doc, new FileOutputStream(fileName));
	}

	public Element getFirstElementByNameAndValue(Element parent, String elementName, String keyValue) {
		List<?> list = getElements(parent, elementName);
		Element e = null;
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			e = (Element) iter.next();
			if (keyValue.equals(e.getText())) {
				break;
			}
		}
		return e;
	}

	public List<Map<String, String>> saveXmlnodeToMap(String str) {
		String textXml = "<?xml version='1.0' encoding='GB2312' standalone='yes' ?>" + str;
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		try {
			Reader r = new StringReader(textXml);
			XMLBuilder xml = new XMLBuilder(r);
			r.close();
			Element e = xml.getRootElement();
			List<?> l = e.getChildren("TIPS");
			Element ee = (Element) l.get(0);
			List<?> a = ee.getChildren("TIP");
			for (int j = 0; j < a.size(); j++) {
				Map<String, String> map = new HashMap<String, String>();
				Element eee = (Element) a.get(j);
				map.put("MSG_ID", getFirstElementValue(eee, "MSG_ID"));
				map.put("MSG_TIME", getFirstElementValue(eee, "MSG_TIME"));
				map.put("MSG_LEVEL", getFirstElementValue(eee, "MSG_LEVEL"));
				map.put("SUBJECT", getFirstElementValue(eee, "SUBJECT"));
				map.put("DETAIL", getFirstElementValue(eee, "DETAIL"));
				retList.add(map);
			}
		} catch (Exception localException) {
		}
		return retList;
	}

	public void saveListToXmlFile(List<Map<String, String>> list, String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			trace.error("tip.xml does not exist,will creat!");
			creatXmlFile(filename);
		}
		SAXBuilder builder = new SAXBuilder();
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String curr_time = df.format(new Date());
			Document document = builder.build(filename);
			Element root = document.getRootElement();
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				Element e = getExistElement(document, map.get("MSG_ID").toString());
				if (e != null) {
					e.getChild("REV_TIME").setText(curr_time);
					e.getChild("MSG_TIME").setText(map.get("MSG_TIME").toString());
					e.getChild("MSG_LEVEL").setText(map.get("MSG_LEVEL").toString());
					e.getChild("SUBJECT").setText(map.get("SUBJECT").toString());
					e.getChild("DETAIL").setText(map.get("DETAIL").toString());
				} else {
					Element element = new Element("TIP");
					Element e1 = new Element("MSG_ID");
					e1.setText(map.get("MSG_ID").toString());
					Element e2 = new Element("REV_TIME");
					e2.setText(curr_time);
					Element e3 = new Element("MSG_TIME");
					e3.setText(map.get("MSG_TIME").toString());
					Element e4 = new Element("MSG_LEVEL");
					e4.setText(map.get("MSG_LEVEL").toString());
					Element e5 = new Element("SUBJECT");
					e5.setText(map.get("SUBJECT").toString());
					Element e6 = new Element("DETAIL");
					e6.setText(map.get("DETAIL").toString());
					element.addContent(e1);
					element.addContent(e2);
					element.addContent(e3);
					element.addContent(e4);
					element.addContent(e5);
					element.addContent(e6);
					root.addContent(element);
				}
			}
			outDocumentToFile(document, "  ", "utf-8", filename);
		} catch (IOException eee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
		} catch (JDOMParseException ee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
		} catch (Exception e) {
			trace.error("catch exception in saveListToXmlFile():", e);
		}
	}

	public List<Map<String, String>> xmlFileToList(String filename) {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

		File file = new File(filename);
		if (!file.exists()) {
			trace.error("tip.xml does not exist!!");
			return retList;
		}
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(filename);
			Element root = document.getRootElement();
			List<?> list = root.getChildren();
			Iterator<?> it = list.iterator();
			while (it.hasNext()) {
				Element ee = (Element) it.next();
				Map<String, String> map = new HashMap<String, String>();
				map.put("REV_TIME", ee.getChildText("REV_TIME"));
				map.put("MSG_TIME", ee.getChildText("MSG_TIME"));
				map.put("MSG_LEVEL", ee.getChildText("MSG_LEVEL"));
				map.put("SUBJECT", ee.getChildText("SUBJECT"));
				map.put("DETAIL", ee.getChildText("DETAIL"));
				map.put("MSG_ID", ee.getChildText("MSG_ID"));
				retList.add(map);
			}
		} catch (IOException eee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
			retList.clear();
		} catch (JDOMParseException ee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
			retList.clear();
		} catch (Exception e) {
			trace.error("catch exception in xmlFileToList():", e);
		}
		return retList;
	}

	public void clearXmlTips() {
		String filename = "../tip/tip.xml";
		File file = new File(filename);
		if (!file.exists()) {
			trace.error("tip.xml does not exist!!");
			return;
		}
		SAXBuilder builder = new SAXBuilder();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		List<Map<String, String>> tipList = new ArrayList<Map<String, String>>();
		try {
			tipList = xmlFileToList(filename);
			Date d1 = df.parse(df.format(new Date()));
			Document document = builder.build(filename);
			Element root = document.getRootElement();
			List<?> list = root.getChildren();
			Iterator<?> it = list.iterator();
			List<Element> l = new ArrayList<Element>();
			while (it.hasNext()) {
				Element ee = (Element) it.next();
				String msg_time = ee.getChildText("MSG_TIME");
				Date d2 = df.parse(msg_time);
				long bb = (d1.getTime() - d2.getTime()) / 1000L;
				long day = bb / 86400L;
				if (day > 30L) {
					l.add(ee);
				}
			}
			for (int i = 0; i < l.size(); i++) {
				root.removeContent((Element) l.get(i));
			}
			outDocumentToFile(document, "  ", "utf-8", filename);
		} catch (IOException eee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
			saveListToXmlFile(tipList, filename);
		} catch (JDOMParseException ee) {
			trace.info("will create tip xml file!");
			creatXmlFile(filename);
			saveListToXmlFile(tipList, filename);
		} catch (Exception e) {
			trace.error("catch exception in clearXmlTips():", e);
		}
	}

	public void creatXmlFile(String filename) {
		Document doc = new Document();
		Element root = new Element("TIPS");
		doc.setRootElement(root);
		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
			outDocumentToFile(doc, "  ", "utf-8", filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Element getExistElement(Document doc, String id) {
		Element root = doc.getRootElement();
		List<?> list = root.getChildren();
		Iterator<?> it = list.iterator();
		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) it.next();
			if (id.equals(e.getChildText("MSG_ID"))) {
				return e;
			}
		}
		return null;
	}
}
