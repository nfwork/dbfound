<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary/> 
	</head>
	<body>
		<h1 style="margin-left:20px;margin-top:20px;" > <font color="red"> 您没有访问页面：${request_url}的权限</font></h1>
	</body>
</html>
