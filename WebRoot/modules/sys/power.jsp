<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		
		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "Y", "Y-启用" ], [ "N", "N-冻结" ] ],
			fields : [ "status_code", "status_name" ]
		});
		
		function query() {
			roleGrid.query();
		}

		function reset() {
			queryForm.reset();
		}
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="role_ds" modelName="sys/role" queryName="combo" />
	    	<d:dataSet id="moduleStore" modelName="sys/module" queryName="combo" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="权限分配" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="role_id" required="true" options="role_ds" valueField="role_id" displayField="role_name" width="200" editor="combo" prompt="角色选择">
				    <d:event name="select" handle="query"/>
				</d:field>
				<d:field name="module_id" options="moduleStore" valueField="module_id" displayField="module_name" width="200" editor="combo" prompt="模块" >
				    <d:event name="select" handle="query"/>
				</d:field>
			</d:line>
		</d:form>
        <d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		<d:grid id="roleGrid" queryUrl="sys/power.query" selectFirstRow="false" title="功能列表" queryForm="queryForm" height="$D.getFullHeight('roleGrid')" autoQuery="false" pagerSize="100">
			<d:toolBar>
			  <d:gridButton type="save" action="sys/power.execute!save"/>
			</d:toolBar>
			<d:columns> 
				<d:column name="function_code" prompt="功能编号" width="120" />
				<d:column name="function_des" prompt="功能名称" width="120" />
				<d:column name="module_code_name" prompt="所属模块" width="130" />
				<d:column name="enable_flag" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="是否启用" width="100" />
			    <d:column name="p_last_update_date" prompt="最后修改时间" width="120" />
				<d:column name="last_update_user" prompt="最后经手人" width="120" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
