<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			courceGrid.query();
		}
		function reset() {
			queryForm.form.reset();
		}

		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "Y", "Y-启用" ], [ "N", "N-冻结" ] ],
			fields : [ "status_code", "status_name" ]
		});

		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "branch_code")) {
				return false;
			} else {
				return true;
			}
		}

		function initDefaultValue(record,grid){
			record.set("enable_flag","Y");
		}
	</script>
	<body>
		<d:form id="queryForm" title="学科定义" labelWidth="90">
			<d:line columnWidth="0.33">
			    <d:field name="branch_code" upper="true" editor="textfield" prompt="学科编码" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="branch_name" editor="textfield" prompt="学科名称" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="status_code" editor="combo" options="statusStore" displayField="status_name" valueField="status_code"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="timefrom" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="courceGrid" title="学科列表" height="365" isCellEditable="isCellEditable" queryForm="queryForm" model="fnd/branch" autoQuery="true">
			<d:toolBar>
				<d:gridButton type="add" afterAction="initDefaultValue"/>
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="branch_code" upper="true" required="true" editor="textfield" prompt="学科编号" width="130" />
				<d:column name="branch_name" required="true" editor="textfield" prompt="学科名称" width="180" />
				<d:column name="enable_flag" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="状态" width="80" />
				<d:column name="create_date" prompt="创建日期" width="100" />
				<d:column name="last_update_date" prompt="最后修改日期" width="120" />
				<d:column name="last_update_user" prompt="最后经手人" width="140" />
			</d:columns>
		</d:grid>
	</body>
</html>
