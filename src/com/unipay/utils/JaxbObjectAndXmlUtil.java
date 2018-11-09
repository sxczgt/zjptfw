package com.unipay.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unipay.model.xml.TX5W1001.TX5W1001Bean;
import com.unipay.model.xml.TX5W1001.TX5W1001INFOBean;
import com.unipay.model.xml.TX5W1002.TX5W1002Bean;
import com.unipay.model.xml.TX5W1002.TX5W1002INFOBean;
import com.unipay.model.xml.TX5W1003.TX5W1003Bean;
import com.unipay.model.xml.TX5W1003.TX5W1003INFOBean;

/**
 * Jaxb2.0 处理Xml与Object转换
 * 
 * @Title: JaxbObjectAndXmlUtil
 * @author zhsh
 * @date 2018年5月18日
 */
public class JaxbObjectAndXmlUtil {
	private static Log log = LogFactory.getLog(JaxbObjectAndXmlUtil.class);

	/**
	 * @param xmlStr 字符串
	 * @param c 对象Class类型
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xml2Object(String xmlStr, Class<T> c) {
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
			return t;
		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param object 对象
	 * @return 返回xmlStr
	 */
	public static String object2Xml(Object object) {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshal = context.createMarshaller();
			marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
			marshal.setProperty(Marshaller.JAXB_ENCODING, "utf-8");// 编码格式,默认为utf-8
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xml头信息
			marshal.setProperty("jaxb.encoding", "GB2312");
			marshal.marshal(object, writer);
			return new String(writer.getBuffer());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public static void main(String[] args) {
		// // 5W1001 商户连接交易
		// test5W1001();

		// // 5W1002 商户支付流水查询(按日期区间)
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			test5W1002(sdf.parse("2018-04-16 12:34:56"), sdf.parse("2018-05-16 12:34:56"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// // 5W1002 商户支付流水查询(按订单号)
		// test5W1002("2999TEST20180416144933");

		// // 5W1003 商户退款流水查询(按日期区间)
		// try {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		// test5W1003(sdf.parse("2018-04-16 01:23:45.678"), sdf.parse("2018-05-17 01:23:45.678"));
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		// 5W1003 商户退款流水查询(按订单号)
		// test5W1003("2999TEST20180416144933");

	}

	public static void test5W1001() {
		TX5W1001INFOBean txInfo = new TX5W1001INFOBean();
		txInfo.setREM1("");
		txInfo.setREM2("");
		TX5W1001Bean txBean = new TX5W1001Bean();
		txBean.setREQUEST_SN("1528076555599");
		txBean.setTX_INFO(txInfo);
		// 收发消息
		String sendStr = JaxbObjectAndXmlUtil.object2Xml(txBean);
		System.out.println("请求内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
		// if (StringUtil.isNotEmpty(recvStr)) {
		// txBean = JaxbObjectAndXmlUtil.xml2Object(recvStr, TX5W1001Bean.class);
		// JSONObject json = JSONObject.fromObject(txBean);
		// System.out.println(json.toString());
		// }
	}

	public static void test5W1002(Date start, Date end) {
		TX5W1002INFOBean txInfo = new TX5W1002INFOBean();
		// 按日期查询
		txInfo.setSTART(new SimpleDateFormat("yyyyMMdd").format(start));
		txInfo.setSTARTHOUR(new SimpleDateFormat("H").format(start));
		txInfo.setSTARTMIN(new SimpleDateFormat("m").format(start));
		txInfo.setEND(new SimpleDateFormat("yyyyMMdd").format(end));
		txInfo.setENDHOUR(new SimpleDateFormat("H").format(end));
		txInfo.setENDMIN(new SimpleDateFormat("m").format(end));
		// // 按订单号查询
		// txInfo.setORDER("2999TEST20180416144933"); // 订单号：按订单号查询时，时间段不起作用
		txInfo.setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		txInfo.setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		TX5W1002Bean txBean = new TX5W1002Bean();
		txBean.setTX_INFO(txInfo);
		// 收发消息
		String sendStr = JaxbObjectAndXmlUtil.object2Xml(txBean);
		System.out.println("请求内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
	}

	public static void test5W1002(String outTradeNo) {
		TX5W1002INFOBean txInfo = new TX5W1002INFOBean();
		// // 按日期查询
		// txInfo.setSTART("20180416");
		// txInfo.setSTARTHOUR("0");
		// txInfo.setSTARTMIN("0");
		// txInfo.setEND("20180517");
		// txInfo.setENDHOUR("0");
		// txInfo.setENDMIN("0");
		// 按订单号查询
		txInfo.setORDER(outTradeNo); // 订单号：按订单号查询时，时间段不起作用
		txInfo.setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		txInfo.setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		TX5W1002Bean txBean = new TX5W1002Bean();
		txBean.setTX_INFO(txInfo);
		// 收发消息
		String sendStr = JaxbObjectAndXmlUtil.object2Xml(txBean);
		System.out.println("请求内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
	}

	public static void test5W1003(Date start, Date end) {
		TX5W1003INFOBean txInfo = new TX5W1003INFOBean();
		// 按日期查询
		txInfo.setSTART(new SimpleDateFormat("yyyyMMdd").format(start));
		txInfo.setSTARTHOUR(new SimpleDateFormat("H").format(start));
		txInfo.setSTARTMIN(new SimpleDateFormat("m").format(start));
		txInfo.setEND(new SimpleDateFormat("yyyyMMdd").format(end));
		txInfo.setENDHOUR(new SimpleDateFormat("H").format(end));
		txInfo.setENDMIN(new SimpleDateFormat("m").format(end));
		// 按订单号查询
		// txInfo.setORDER("2999TEST20180416144933"); // 订单号：按订单号查询时，时间段不起作用
		txInfo.setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		txInfo.setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		TX5W1003Bean txBean = new TX5W1003Bean();
		txBean.setTX_INFO(txInfo);
		// 收发消息
		String sendStr = JaxbObjectAndXmlUtil.object2Xml(txBean);
		System.out.println("请求内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
	}

	public static void test5W1003(String outTradeNo) {
		TX5W1003INFOBean txInfo = new TX5W1003INFOBean();
		// 按订单号查询
		txInfo.setORDER("2999TEST20180416144933"); // 订单号：按订单号查询时，时间段不起作用
		txInfo.setKIND("1"); // 流水类型：0:未结流水,1:已结流水
		txInfo.setSTATUS("3");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		TX5W1003Bean txBean = new TX5W1003Bean();
		txBean.setTX_INFO(txInfo);
		// 收发消息
		String sendStr = JaxbObjectAndXmlUtil.object2Xml(txBean);
		System.out.println("请求内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
	}

}
