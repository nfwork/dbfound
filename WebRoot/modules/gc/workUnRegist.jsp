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

	</script>
	<body>
	
	    <d:initProcedure>
	        <d:dataSet id="projectStore" queryName="combo" modelName="gc/project"/>
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="报工取消" labelWidth="80">
			<d:line columnWidth="0.33">
			    <d:field name="project_id" options="projectStore" valueField="project_id" displayField="project_name" anchor="85%" editor="combo" prompt="工程选择" >
			         <d:event name="select" handle="query"/>
			    </d:field>
			    <d:field editor="datefield" prompt="日期" name="work_date">
			         <d:event name="select" handle="query"/>
			    </d:field>
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="roleGrid"  title="工时列表" height="365" queryForm="queryForm" queryUrl="gc/workRegist.query!unRegist" autoQuery="true">
			<d:toolBar align="left">
			    <d:gridButton afterAction="query" icon="DBFoundUI/images/page_attach.png" action="gc/workRegist.execute!unRegiest" title="报工取消"/>
			</d:toolBar>
			<d:columns>
				<d:column name="employee_code" prompt="工人编号" width="130" />
				<d:column name="employee_name" prompt="工人名称" width="130" />
				<d:column name="project_name" prompt="工程名称" width="130" />
				<d:column name="work_date" prompt="日期" width="100" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
