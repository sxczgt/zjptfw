package com.tencent.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import cn.tsinghua.sftp.util.CsvFileUtil;
import cn.tsinghua.sftp.util.TysfPropertiesUtil;

public class WxpayUtil {

	public static void main(String[] args) {
		visitUrl("https://mp.weixin.qq.com/cgi-bin/login?lang=zh_CN");
	}

	/**
	 * 是否以调试环境运行系统
	 * 
	 * @return true为调试环境，false为正式环境
	 * @author 杜小江
	 */
	public static boolean isDebug() {
		boolean isDebug = TysfPropertiesUtil.getInstance().getBooleanProperty("debug", false);
		return isDebug;
	}

	public static boolean isEmpty(Object object) {
		if (object instanceof String) {
			return StringUtils.isEmpty((String) object);
		}
		return (object == null);
	}

	public static <T> boolean isListEmpty(List<T> list) {
		return (!(isListNotEmpty(list)));
	}

	public static <T> boolean isListNotEmpty(List<T> list) {
		return ((list != null) && (list.size() > 0));
	}

	public static boolean isNotEmpty(Object object) {
		return (!(isEmpty(object)));
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String toAmountString(long amount) {
		return toAmount(amount).toString();
	}

	public static BigDecimal toAmount(long amount) {
		return new BigDecimal(amount).divide(new BigDecimal(100));
	}

	public static String getLocalIp() {
		Enumeration<?> allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				System.out.println(netInterface.getName());
				Enumeration<?> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getWapIp(HttpServletRequest request) {
		// String client_ip = "";
		// if (request.getHeader("x-forwarded-for") == null) {
		// client_ip = request.getRemoteAddr();
		// } else {
		// client_ip = request.getHeader("x-forwarded-for");
		// }
		String ip = request.getHeader("x-real-ip");
		if (StringUtils.isEmpty(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip)) {
			ip = request.getRemoteAddr();
		}
		if (StringUtils.indexOf(ip, "0:0") != -1) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * 获取客户端IP地址
	 */
	public static String getRemoteAddr() {
		try {
			MessageContext mc = MessageContext.getCurrentContext();
			if (mc != null) {
				HttpServletRequest request = (HttpServletRequest) mc.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
				return request.getRemoteAddr();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * gmt_payment字段格式化处理
	 * 
	 * @param str
	 * @return
	 */
	public static Date toDate(String str) {
		Date date = null;
		if (StringUtils.isNotEmpty(str)) {
			try {
				date = new SimpleDateFormat("yyyyMMddHHmmss").parse(str);
			} catch (ParseException e) {
				// e.printStackTrace();
				System.out.println("将字符串[" + str + "]转换为日期失败！");
			}
		}
		return date;
	}

	public static String fromDate(Date date) {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
	}

	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 拆分Wxpay对账文件内容<br>
	 * <br>
	 * 1. 第一行为表头，根据请求下载的对账单类型不同而不同(由bill_type决定),目前有：<br>
	 * <b>ALL - 当日所有订单</b><br>
	 * 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额，退款类型，退款状态,商品名称,商户数据包,手续费,费率<br>
	 * <b>SUCCESS - 当日成功支付的订单</b><br>
	 * 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,商品名称,商户数据包,手续费,费率<br>
	 * <b>REFUND - 当日退款的订单</b><br>
	 * 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,退款申请时间,退款成功时间,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率<br>
	 * <br>
	 * 2. 从第二行起，为数据记录，各参数以逗号分隔，参数前增加`符号，为标准键盘1左边键的字符，字段顺序与表头一致。<br>
	 * <br>
	 * 3. 倒数第二行为订单统计标题，最后一行为统计数据<br>
	 * 总交易单数，总交易额，总退款金额，总代金券或立减优惠退款金额，手续费总金额<br>
	 * <br>
	 * <b>举例如下：</b><br>
	 * 交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率<br>
	 * `2014-11-1016：33：45,`wx2421b1c4370ec43b,`10000100,`0,`1000,`1001690740201411100005734289,`1415640626,`085e9858e3ba5186aafcbaed1,`MICROPAY,`SUCCESS,`CFT,`CNY,`0.01,`0.0,`0,`0,`0,`0,`,`,`被扫支付测试,`订单额外描述,`0,`0.60%<br>
	 * `2014-11-1016：46：14,`wx2421b1c4370ec43b,`10000100,`0,`1000,`1002780740201411100005729794,`1415635270,`085e9858e90ca40c0b5aee463,`MICROPAY,`SUCCESS,`CFT,`CNY,`0.01,`0.0,`0,`0,`0,`0,`,`,`被扫支付测试,`订单额外描述,`0,`0.60%<br>
	 * 总交易单数,总交易额,总退款金额,总代金券或立减优惠退款金额,手续费总金额<br>
	 * `2,`0.02,`0.0,`0.0,`0<br>
	 */
	public static List<Map<String, String>> splitCsvWxpay(String fileContent) {
		List<Map<String, String>> reulst = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		try {
			List<String[]> list = CsvFileUtil.readCsvString(fileContent);
			int size = list.size();
			// 数据行
			String[] headers = list.get(0);
			for (int i = 1; i < size - 2; i++) {
				map = new HashMap<String, String>();
				String[] values = list.get(i);
				for (int j = 0; j < values.length; j++) {
					map.put(headers[j], values[j]);
				}
				reulst.add(map);
			}
			// 汇总行
			map = new HashMap<String, String>();
			String[] sumarys = list.get(size - 2);
			String[] values = list.get(size - 1);
			for (int j = 0; j < values.length; j++) {
				map.put(sumarys[j], values[j]);
			}
			reulst.add(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reulst;
	}

	/**
	 * 拆分Alipay对账文件内容<br>
	 * 
	 * @param filepath csv文件绝对路径
	 * @return
	 */
	public static List<Map<String, String>> splitCsvAlipay(String filepath) {
		File file = new File(filepath);
		String line = "";
		List<Map<String, String>> reulst = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		try {
			@SuppressWarnings("resource")
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
			List<String[]> list = new ArrayList<String[]>();
			while ((line = bReader.readLine()) != null) {
				if (line.trim().startsWith("#")) {
					continue;
				}
				if (line.trim() != "") {
					String[] pills = line.trim().split(",");
					list.add(pills);
				}
			}
			int size = list.size();
			//
			String[] headers = list.get(0);
			for (int i = 1; i < size; i++) {
				map = new HashMap<String, String>();
				String[] values = list.get(i);
				for (int j = 0; j < values.length; j++) {
					map.put(headers[j], values[j].trim().replaceAll("\t", ""));
				}
				reulst.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reulst;
	}

	/**
	 * 程序中访问http数据接口
	 */
	public static String getURLContent(String urlStr) {
		/** 网络的url地址 */
		URL url = null;
		/**//** 输入流 */
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlStr);
			in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			String str = null;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		String result = sb.toString();
		return result;
	}

	public static void visitUrl(String urlStr) {
		try {
			System.setProperty("jsse.enableSNIExtension", "false");

			// 创造httpclient实例
			HttpClient client = new HttpClient();
			client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); // 设置cookie管理策略
			client.getParams().setParameter("http.protocol.single-cookie-header", true);

			PostMethod post = new PostMethod();
			// 模拟浏览器
			post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
			// 这个必须设置 否则无法登录 还是尽量完全模拟浏览器的行为
			post.setRequestHeader("Referer", "https://mp.weixin.qq.com");
			// 登录请求提交地址
			post.setURI(new URI(urlStr, false));

			// 构造请求参数
			int responseCode = client.executeMethod(post);
			System.out.println("[visitUrl]" + urlStr + ">>>>ResponseCode=" + responseCode + ">>>>ResponseBody=" + post.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
