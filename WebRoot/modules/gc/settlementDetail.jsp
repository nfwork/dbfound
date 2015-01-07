<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>

	<body>
		<d:grid  title="工时明细列表" height="400" queryUrl="gc/settlement.query!detail?project_id=${param.project_id}&employee_id=${param.employee_id }" autoQuery="true">
			<d:toolBar>
			   <d:gridButton type="excel" />
			</d:toolBar>
			<d:columns>
			    <d:column name="project_name" prompt="工程名称" width="130" />
				<d:column name="employee_code" prompt="工人编号" width="130" />
				<d:column name="employee_name" prompt="工人名称" width="130" />
				<d:column name="price" prompt="工价（人/天）" width="100" />
				<d:column name="work_date" prompt="日期" width="100" />
			</d:columns>
		</d:grid>
	</body>
</html>
