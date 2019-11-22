<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			functionGrid.query();
		}
		function reset() {
			queryForm.reset();
		}
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "function_code")) {
				return false;
			} else {
				return true;
			}
		}
	</script>
	<body>
	    <d:initProcedure>
	        <d:dataSet id="moduleStore" modelName="sys/module" queryName="combo" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="function_code" upper="true" anchor="85%" editor="textfield" prompt="功能编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="function_des"  anchor="85%" editor="textfield" prompt="功能描述" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="module_id" options="moduleStore" valueField="module_id" displayField="module_name" anchor="85%" editor="lovcombo" prompt="从属模块" />
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="timefrom" anchor="85%" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" anchor="85%" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="functionGrid" height="$D.getFullHeight('functionGrid')" selectFirstRow="false" title="功能列表" isCellEditable="isCellEditable" queryForm="queryForm" model="sys/function" autoQuery="true">
			<d:toolBar>
				<d:gridButton type="add"/>
				<d:gridButton type="save"/>
				<d:gridButton type="delete"/>
			</d:toolBar>
			<d:columns>
				<d:column name="function_code" sortable="true"  upper="true" required="true" editor="textfield" prompt="功能编号" width="100" />
				<d:column name="function_des" sortable="true" required="true" editor="textfield" prompt="功能描述" width="120" />
				<d:column name="image" hidden="true" editor="textfield" prompt="图标" width="100" />
				<d:column name="jsp_pager" required="true" editor="textfield" prompt="对应jsp页面" width="160" />
				<d:column name="priority" sortable="true" align="right" required="false" editor="numberfield" prompt="优先级" width="60" />
				<d:column name="function_module" required="true" options="moduleStore" valueField="module_id" displayField="module_name" width="130" editor="combo" prompt="从属模块" />
				<d:column name="create_date" align="center" prompt="创建日期" width="100" />
				<d:column name="last_update_user" align="center" prompt="最后经手人" width="100" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
