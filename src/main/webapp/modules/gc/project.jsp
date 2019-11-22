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
			if (record.json && ( name == "project_code")) {
				return false;
			} else {
				return true;
			}
		}
	</script>
	<body>
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="project_code" upper="true" anchor="85%" editor="textfield" prompt="工程编号">
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="project_name"  anchor="85%" editor="textfield" prompt="工程名称">
				   <d:event name="enter" handle="query"/>
				</d:field>
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="roleGrid" title="工程列表" height="365" isCellEditable="isCellEditable" queryForm="queryForm" model="gc/project" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add"/>
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="project_code" upper="true" required="true" editor="textfield" prompt="工程编号" width="130" />
				<d:column name="project_name" required="true" editor="textfield" prompt="工程名称" width="180" />
			    <d:column name="price" required="true" editor="numberfield" prompt="工价（人/天）" width="100" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
