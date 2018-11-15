package com.sxypay.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtil {
	static String UserAgent = "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2";

	/**
	 * GET方法获取页面内容
	 * 
	 * @param url
	 * @param charsetName
	 * @return
	 */
	public static String httpReader(String url, String charsetName) {
		System.out.println("GetPage:" + url);

		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);

		try {
			client.executeMethod(method);
			int status = method.getStatusCode();
			if (status == HttpStatus.SC_OK) {
				String result = method.getResponseBodyAsString();
				return result;
			} else {
				System.out.println("Method failed: " + method.getStatusLine());
			}
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			System.out.println("发生网络异常！");
			e.printStackTrace();
		} finally {
			// 释放连接
			if (method != null)
				method.releaseConnection();
			method = null;
			client = null;
		}
		return null;
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param charsetName
	 * @return
	 */
	public static String httpGet(String url, String charsetName) {
		System.out.println("GetPage:" + url);
		HttpClient httpClient = new HttpClient();
		// 设置header
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, UserAgent);
		GetMethod method = new GetMethod(url);
		try {
			int statusCode = httpClient.executeMethod(method);
			System.out.println("httpClientUtil::statusCode=" + statusCode);
			System.out.println(method.getStatusLine());
			String content = new String(method.getResponseBody(), charsetName);
			return content;
		} catch (Exception e) {
			System.out.println("time out");
			e.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
			method = null;
			httpClient = null;
		}
		return null;
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 * @param paramMap
	 * @param charsetName
	 * @return
	 */
	public static String httpPost(String url, Map<String, String> paramMap, String charsetName) {
		System.out.println("GetPage:" + url);
		if (url == null || url.trim().length() == 0 || paramMap == null || paramMap.isEmpty())
			return null;
		HttpClient httpClient = new HttpClient();
		// 设置header
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT, UserAgent);
		// 代理设置
		// httpClient.getHostConfiguration().setProxy("128.128.176.74", 808);
		PostMethod method = new PostMethod(url);
		Iterator<String> it = paramMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next() + "";
			method.addParameter(new NameValuePair(key, paramMap.get(key)));
		}

		try {
			int statusCode = httpClient.executeMethod(method);
			System.out.println("httpClientUtil::statusCode=" + statusCode);
			// System.out.println(method.getStatusLine());
			String content = new String(method.getResponseBody(), charsetName);
			return content;
		} catch (Exception e) {
			System.out.println("time out");
			e.printStackTrace();
		} finally {
			if (method != null)
				method.releaseConnection();
			method = null;
			httpClient = null;
		}
		return null;
	}

	public static String httpPost(String url, Map<String, String> paramMap) {
		// 编码：UTF-8
		if (url.toLowerCase().startsWith("https://")) {
			return httpsPost(url, paramMap, "UTF-8");
		} else {
			return httpPost(url, paramMap, "UTF-8");
		}
	}

	/**
	 * POST请求SSL类
	 * 
	 * @param url
	 * @param paramMap
	 * @param charsetName
	 * @return
	 */
	public static String httpsPost(String url, Map<String, String> paramMap, String charsetName) {
		org.apache.http.client.HttpClient httpClient = null;
		org.apache.http.client.methods.HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new HttpSSLClient();
			httpPost = new org.apache.http.client.methods.HttpPost(url);
			// 设置参数
			java.util.List<org.apache.http.NameValuePair> list = new java.util.ArrayList<org.apache.http.NameValuePair>();
			java.util.Iterator<java.util.Map.Entry<String, String>> iterator = paramMap.entrySet().iterator();
			while (iterator.hasNext()) {
				java.util.Map.Entry<String, String> elem = iterator.next();
				list.add(new org.apache.http.message.BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				org.apache.http.client.entity.UrlEncodedFormEntity entity = new org.apache.http.client.entity.UrlEncodedFormEntity(list, charsetName);
				httpPost.setEntity(entity);
			}
			org.apache.http.HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				org.apache.http.HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = org.apache.http.util.EntityUtils.toString(resEntity, charsetName);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static String getStreamReader(String url) {
		// String requestString = "我们要以流发送的数据...";
		String result = "";
		HttpURLConnection httpURLConnection;
		try {
			// 建立链接
			URL gatewayUrl = new URL(url);
			httpURLConnection = (HttpURLConnection) gatewayUrl.openConnection();
			// 设置连接属性
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
			// byte[] requestStringBytes = requestString.getBytes("utf-8");
			// 设置请求属性
			// httpURLConnection.setRequestProperty("Content-length", "" + requestStringBytes.length);
			httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
			// // 建立输出流，并写入数据
			// OutputStream outputStream = httpURLConnection.getOutputStream();
			// outputStream.write(requestStringBytes);
			// outputStream.close();
			// 获得响应状态
			int responseCode = httpURLConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {
				// 当正确响应时处理数据
				StringBuffer responseBuffer = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				// 处理响应流，必须与服务器响应流输出的编码一致
				responseReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "gb2312"));
				while ((readLine = responseReader.readLine()) != null) {
					responseBuffer.append(readLine).append("\n");
				}
				responseReader.close();
				result = responseBuffer.toString();
			}
		} catch (IOException e) {
			e.fillInStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		String content = HttpClientUtil.httpGet("https://zhifu.tsinghua.edu.cn/sfpt/requestBillAction.action?t_bill_date=20180329&t_item=100170&t_partner=2072&t_sign=2f72c534fa63628b9463ca642533a143", "UTF-8");
		System.out.println(content);
	}
}
