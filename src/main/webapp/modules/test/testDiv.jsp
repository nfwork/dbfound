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

		function setDiv(){
			var value = "选择日期为：<font color='red'>" + form.getData().timefrom + " 到 " + form.getData().timeto +"</font>";
			document.getElementById("dateDiv").innerHTML = value;
		}
		
	</script>
	<body>
		<d:panel width="600" title="DIV测试" >
		
			<d:form id="form" labelWidth="80">
				<d:line columnWidth="0.5">
					<d:field name="user_code" upper="true" width="150" editor="textfield" prompt="用户编号" >
					   <d:event name="enter" handle="query"/>
					</d:field>
					<d:field name="user_name"  width="150" editor="textfield" prompt="i18n:用户名" >
					   <d:event name="enter" handle="query"/>
					</d:field>
				</d:line>
				<d:line columnWidth="0.5">
					<d:field name="timefrom" width="150" editor="datefield" prompt="创建日期从" >
						<d:event name="select" handle="setDiv"></d:event>
					</d:field>
					<d:field name="timeto" width="150" editor="datefield" prompt="创建日期到" >
						<d:event name="select" handle="setDiv"></d:event>
					</d:field>
				</d:line>
			</d:form>
		
			<d:div style="margin:5px">
				<div id="dateDiv">选择日期为：</div>
			</d:div>
		</d:panel>
	</body>
</html>
