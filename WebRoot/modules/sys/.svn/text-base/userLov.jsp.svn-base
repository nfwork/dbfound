<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			userGrid.query();
		}
		function commit() {
			var json = userGrid.getSelectionModel().getSelected().json;
			var result = {user_name:json.user_name,user_code:json.user_code,user_id:json.user_id};
			parent.Ext.getCmp("${param.windowId}").commit(result);
		}

		function clear() {
			var result = {user_name:'',user_id:null,user_code:''};
			parent.Ext.getCmp("${param.windowId}").commit(result);
		}
	</script>
	<body>
		<d:form title="查询条件" id="queryForm" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="user_code" width="180" editor="textfield" prompt="用户编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="user_name"  width="180" editor="textfield" prompt="用户名" >
				   <d:event name="enter" handle="query"/>
				</d:field>
			</d:line>
		</d:form>
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="确定" click="commit" />
			<d:button title="清除" click="clear" />
		</d:buttonGroup>
		<d:grid title="用户列表" selectFirstRow="false" id="userGrid" singleSelect="true" queryForm="queryForm" height="300" model="sys/user" autoQuery="true" pagerSize="10">
			<d:columns>
				<d:column name="user_code" prompt="用户编号" width="120" />
				<d:column name="user_name" prompt="用户名" width="120" />
			</d:columns>
			<d:events>
				<d:event name="rowdblclick" handle="commit"></d:event>
			</d:events>
		</d:grid>
	</body>
</html>
