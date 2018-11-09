package com.unipay.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPClient {
	/** 服务器地址 */
	private static String DEFAULT_SERVER_IP = "166.111.14.107";
	/** 服务器端口 */
	private static int DEFAULT_SERVER_PORT = 8888;
	/** 连接超时时间 */
	private static int DEFAULT_TIMEOUT = 5 * 1000; // 超时时间10秒
	/** 默认字符集 */
	private static String DEFAULT_CHARSET = "GBK"; // UTF-8

	static {
		DEFAULT_SERVER_IP = "127.0.0.1";
		DEFAULT_SERVER_PORT = 8888;
		DEFAULT_TIMEOUT = 15 * 1000;
		DEFAULT_CHARSET = "GBK";
	}

	/**
	 * 发送报文
	 * 
	 * @param sendStr
	 * @return
	 */
	public static String sendMessage(String sendStr) {
		return sendMessage(sendStr, DEFAULT_TIMEOUT);
	}

	/**
	 * 发送报文
	 * 
	 * @param sendStr
	 * @param timeout
	 * @return
	 */
	public static String sendMessage(String sendStr, int timeout) {
		return sendMessage(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT, sendStr, timeout);
	}

	/**
	 * 发送报文
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param sendStr
	 * @return
	 */
	public static String sendMessage(String serverIp, int serverPort, String sendStr) {
		return sendMessage(serverIp, serverPort, sendStr, DEFAULT_TIMEOUT);
	}

	/**
	 * 发送报文
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @param sendStr
	 * @param timeout
	 * @return
	 */
	public static String sendMessage(String serverIp, int serverPort, String sendStr, int timeout) {
		StringBuffer recvBuf = new StringBuffer();
		// 客户端使用的TCP Socket
		Socket socket = null;
		try {
			// 连接服务器
			socket = new Socket(serverIp, serverPort);
			socket.setSoTimeout(timeout); // 超时时间（毫秒）
			// 发送请求内容
			OutputStream os = socket.getOutputStream();
			os.write(sendStr.getBytes());
			// 读取响应内容
			InputStream is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEFAULT_CHARSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				recvBuf.append(strRead);
			}
			reader.close();
		} catch (IOException e) {
			if ("Read timed out".equals(e.getMessage())) {
				System.out.println("连接服务器[" + DEFAULT_SERVER_IP + ":" + DEFAULT_SERVER_PORT + "]超时[" + DEFAULT_TIMEOUT / 1000 + "秒]已断开");
			} else {
				System.out.println("连接服务器[" + DEFAULT_SERVER_IP + ":" + DEFAULT_SERVER_PORT + "]异常错误：" + e.getMessage());
				e.printStackTrace();
				try {
					Thread.sleep(1); // 等待1毫秒以保证异常信息先打印完再执行后续逻辑
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return recvBuf.toString();
	}

	public static String queryOrderInfo(String mch_id, String out_trade_no) {
		// 请求序列号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String requestSN = sdf.format(new Date());
		// 发送报文
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"GB18030\"?>"); // standalone=\"yes\"
		sb.append("<TX>");
		sb.append("<REQUEST_SN>").append(requestSN).append("</REQUEST_SN>");// [必填]请求序列号
		sb.append("<CUST_ID>").append("105100000012072").append("</CUST_ID>"); // [必填]商户号
		sb.append("<USER_ID>").append("105100000012072-123").append("</USER_ID>"); // [必填]操作员号
		sb.append("<PASSWORD>").append("QhcwcCX123").append("</PASSWORD>"); // [必填]操作员号交易密码
		sb.append("<TX_CODE>").append("5W1002").append("</TX_CODE>"); // [必填]交易请求码
		sb.append("<LANGUAGE>").append("CN").append("</LANGUAGE>"); // [必填]CN
		sb.append("<TX_INFO>");
		sb.append("    <START>").append("</START>"); // 起始日期
		sb.append("    <STARTHOUR>").append("</STARTHOUR>"); // 开始小时
		sb.append("    <STARTMIN>").append("</STARTMIN>"); // 开始分钟
		sb.append("    <END>").append("</END>"); // 截止日期
		sb.append("    <ENDHOUR>").append("</ENDHOUR>"); // 结束小时
		sb.append("    <ENDMIN>").append("</ENDMIN>"); // 结束分钟
		sb.append("    <KIND>").append("1").append("</KIND>"); // [必填]流水类型：0:未结流水,1:已结流水
		sb.append("    <ORDER>").append(out_trade_no).append("</ORDER>"); // [必填]订单号
		sb.append("    <ACCOUNT>").append("</ACCOUNT>"); // 结算账户号
		sb.append("    <DEXCEL>").append("1").append("</DEXCEL>"); // [必填]文件类型：默认为“1”，1:不压缩,2.压缩成zip文件
		sb.append("    <MONEY>").append("</MONEY>"); // 金额，不支持以金额查询
		sb.append("    <NORDERBY>").append("1").append("</NORDERBY>"); // [必填]排序：1:交易日期,2:订单号
		sb.append("    <PAGE>").append("1").append("</PAGE>"); // [必填]当前页次
		sb.append("    <POS_CODE>").append("</POS_CODE>");// 柜台号
		sb.append("    <STATUS>").append("3").append("</STATUS>");// 流水状态：0:交易失败,1:交易成功,2:待银行确认(针对未结流水查询);3:全部
		sb.append("</TX_INFO>");
		sb.append("</TX>");

		System.out.println("服务器地址:" + DEFAULT_SERVER_IP + "，端口：" + DEFAULT_SERVER_PORT);
		String sendStr = sb.toString();
		System.out.println("发送内容:" + sendStr);
		String recvStr = TCPClient.sendMessage(sendStr);
		System.out.println("接收内容:" + recvStr);
		// <?xml version="1.0" encoding="GB18030"?><TX><REQUEST_SN></REQUEST_SN><RETURN_CODE>WLPT_Err1009</RETURN_CODE><RETURN_MSG>请求报文为空或者XML格式有误2</RETURN_MSG></TX>
		return recvStr;
	}

	public static void main(String[] args) {
		// queryOrderInfo("004161878", "2999TEST20180416144933");
		queryOrderInfo("004161897", "2999TEST20180416145013");
	}

}