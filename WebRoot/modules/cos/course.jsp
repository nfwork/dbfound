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
			if (record.json && (name=="branch_id" || name=="class_id")) {
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
	    <d:initProcedure>
	    	<d:dataSet id="teacher_store" modelName="fnd/teacher" queryName="combo" />
	    	<d:dataSet id="class_ds" modelName="fnd/class" queryName="combo_all" />
	    	<d:dataSet id="branch_ds" modelName="fnd/branch" queryName="combo" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="课程开设" labelWidth="90">
			<d:line columnWidth="0.33">
			    <d:field name="class_id" options="class_ds" valueField ="class_id" displayField="class_name" editor="combo" prompt="班级">
			       <d:event name="select" handle="query"></d:event>
			    </d:field>
			    <d:field name="branch_id" options="branch_ds" valueField ="branch_id" displayField="branch_name" editor="combo" prompt="学科" >
			        <d:event name="select" handle="query"></d:event>
			    </d:field>
				<d:field name="teacher_id" options="teacher_store" valueField ="teacher_id" displayField="teacher_name" editor="combo" prompt="授课教师" >
				    <d:event name="select" handle="query"></d:event>
			    </d:field>
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
		
		<d:grid id="courceGrid" title="课程列表" height="365" autoQuery="true" isCellEditable="isCellEditable" queryForm="queryForm"  model="cos/course">
			<d:toolBar>
				<d:gridButton type="add" afterAction="initDefaultValue"/>
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="class_id" required="true" options="class_ds" valueField ="class_id" displayField="class_name" width="160" editor="combo" prompt="班级" />
			    <d:column name="branch_id" required="true" options="branch_ds" valueField ="branch_id" displayField="branch_name" width="170" editor="combo" prompt="学科" />
				<d:column name="teacher_id" options="teacher_store" valueField ="teacher_id" displayField="teacher_name" width="150" editor="combo" prompt="授课教师"  />
				<d:column name="enable_flag" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="状态" width="70" />
				<d:column name="create_date" prompt="创建日期" width="90" />
				<d:column name="last_update_date" prompt="最后修改日期" width="90" />
				<d:column name="last_update_user" prompt="最后经手人" width="100" />
			</d:columns>
		</d:grid>
	</body>
</html>
