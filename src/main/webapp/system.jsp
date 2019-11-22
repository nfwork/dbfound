<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.nfwork.dbfound.util.LogUtil"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
	function openLog() {
		Ext.Ajax.request( {
			url : 'sys/log.execute!openLog',
			params : null,
			success : function(response, action) {
				var obj = Ext.util.JSON.decode(response.responseText);
				location.href = "system.jsp";
			},
			failure : function() {
				Ext.Msg.alert('提示', '操作失败！');
			}
		});
	}

	function closeLog() {
		Ext.Ajax.request( {
			url : 'sys/log.execute!closeLog',
			params : null,
			success : function(response, action) {
				var obj = Ext.util.JSON.decode(response.responseText);
				location.href = "system.jsp";
			},
			failure : function() {
				Ext.Msg.alert('提示', '操作失败！');
			}
		});
	}
</script>
	<body>
	    <d:if test="${param.clear_flag=='true'}">
		   <d:execute modelName="access" executeName="clear_cache" ></d:execute>
		   <script type="text/javascript">
		      Ext.Msg.alert('提示', '操作成功！');
		   </script>
		</d:if>
		<d:if test="${param.model_clear_flag=='true'}">
		   <d:execute modelName="access" executeName="clear_model_cache" ></d:execute>
		   <script type="text/javascript">
		      Ext.Msg.alert('提示', '操作成功！');
		   </script>
		</d:if>
		
		<h1 style="margin-left: 12px; margin-top: 12px;">
			当前日志情况：
			<font color="red"> 
			         <%
					 	if (LogUtil.openLog)
					 		out.print("开启");
					 	else
					 		out.print("关闭");
					 %>
			  </font>
		</h1>
		<d:buttonGroup>
			<d:button title="开启日志" click="openLog" />
			<d:button title="关闭日志" click="closeLog" />
		</d:buttonGroup>
		<br/>
	</body>
</html>
