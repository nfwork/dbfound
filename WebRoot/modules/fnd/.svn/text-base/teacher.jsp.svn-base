<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			teacherGrid.query();
		}
		function reset() {
			queryForm.form.reset();
		}
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "teacher_code")) {
				return false;
			} else {
				return true;
			}
		}
		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "Y", "Y-启用" ], [ "N", "N-冻结" ] ],
			fields : [ "status_code", "status_name" ]
		});

		function initDefaultValue(record,grid){
			record.set("enable_flag","Y");
		}
	</script>
	<body>
		<d:form id="queryForm" title="教师定义" labelWidth="90">
			<d:line columnWidth="0.33">
				<d:field name="teacher_code" upper="true" editor="textfield" prompt="教师编号">
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="teacher_name" editor="textfield" prompt="教师姓名">
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
		<d:grid id="teacherGrid" title="学生列表" height="365" isCellEditable="isCellEditable"  queryForm="queryForm" model="fnd/teacher" autoQuery="true">
			<d:toolBar>
				<d:gridButton type="add" afterAction="initDefaultValue" />
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="teacher_code" upper="true" required="true" editor="textfield" prompt="教师编号" width="120" />
				<d:column name="teacher_name" required="true" editor="textfield" prompt="教师姓名" width="120" />
				<d:column name="telphone_num"  editor="textfield" prompt="电话" width="120" />
				<d:column name="email"  editor="textfield" prompt="邮箱" width="120" />
				<d:column name="enable_flag" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="状态" width="70" />
				<d:column name="create_date" prompt="创建时间" width="100" />
				<d:column name="last_update_date" prompt="最后修改时间" width="100" />
				<d:column name="last_update_user" prompt="最后经手人" width="100" />
			</d:columns>
		</d:grid>
	</body>
	<script type="text/javascript">
	    //function hello(){alert(1)}
		//Ext.getCmp('teacherGrid').getSelectionModel().on('click',hello);
	</script>
</html>
