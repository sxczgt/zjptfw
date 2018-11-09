<%@ page import="cn.tsinghua.sftp.util.XssEncoder"%>
<%@ page import="java.io.UnsupportedEncodingException"%>
<%@ page language="java" contentType="text/html; charset=gbk" pageEncoding="GBK"%>
<%@ include file="tag.jspf"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String msg = XssEncoder.xssEncode(String.valueOf(request.getAttribute("msg")));
	msg = msg.replace("\r\n","<br>");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>清华大学资金结算服务平台</title>
<meta charset="gbk" />
<meta name="renderer" content="webkit"> 
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<style type="text/css">
.denied {
	background-image: url(images/lock.png);
	display: block;
	margin: 20px;
	padding: 15px 10px 15px 100px;
	background-position: 20px 15px;
	background-repeat: no-repeat;
	border: 1px dashed;
	border-color: #999;
	height: auto;
	background-color: #f5f5f5;
}
.name {
	font-size: 28px;
	font-family: "Microsoft YaHei", "黑体";
	color: #666666;
	line-height: 40px;
}
.info {
	font-size: 14px;
	color: #444;
	line-height: 18px;
}
body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
}
</style>
</head>
<body>
<div class="denied">
	<span class="name"><%=msg%></span>
	<input type="hidden" id="msg" name="msg" value="<%=msg%>">
	<br/><br/>
	<span class="info">请联系管理员</span>
</div>
</body>
</html>
