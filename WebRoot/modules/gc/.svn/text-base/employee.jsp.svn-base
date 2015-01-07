<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			roleGrid.query();
		}
		
		function reset() {
			queryForm.reset();
		}
		
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "employee_code")) {
				return false;
			} else {
				return true;
			}
		}
	

	</script>
	<body>
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="employee_code" upper="true" anchor="85%" editor="textfield" prompt="工人编号">
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="employee_name"  anchor="85%" editor="textfield" prompt="工人名称">
				   <d:event name="enter" handle="query"/>
				</d:field>
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="roleGrid" title="工人列表" height="365" isCellEditable="isCellEditable" queryForm="queryForm" model="gc/employee" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add"/>
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="employee_code" upper="true" required="true" editor="textfield" prompt="工程编号" width="130" />
				<d:column name="employee_name" required="true" editor="textfield" prompt="工程名称" width="180" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
