<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function reset() {
			queryForm.reset();
		}

		function close(){
			parent.Ext.getCmp("${param.windowId}").close();
		}
	</script>
	<body>
		<d:form id="queryForm" labelWidth="80">
			<d:line columnWidth="1">
				<d:field name="employee_code" upper="true" anchor="85%" editor="textfield" prompt="工人编号">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field name="employee_name"  anchor="85%" editor="textfield" prompt="工人名称">
				</d:field>
			</d:line>
			<d:toolBar>
			    <d:formButton action="gc/employee.execute!add" title="保存" afterAction="close"></d:formButton>
			    <d:formButton action="" title="重置" beforeAction="reset"></d:formButton>
			</d:toolBar>
		</d:form>
	</body>
</html>
