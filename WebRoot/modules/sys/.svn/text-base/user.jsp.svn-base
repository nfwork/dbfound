<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			userGrid.query();
		}
		function reset() {
			queryForm.reset();
		}
	
		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "Y", "Y-启用" ], [ "N", "N-冻结" ] ],
			fields : [ "status_code", "status_name" ]
		});
	
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "user_code")) {
				return false;
			} else {
				return true;
			}
		}

		function initDefaultValue(record,grid){
			record.set("status","Y");
		}
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="roleStore" modelName="sys/role" queryName="combo" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="user_code" anchor="85%" editor="textfield" prompt="用户编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="user_name"  anchor="85%" editor="textfield" prompt="用户名" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="status_code" anchor="85%" editor="combo" options="statusStore" displayField="status_name" valueField="status_code"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="role_id" editor="combo" options="roleStore" displayField="role_name" valueField="role_id" prompt="角色" anchor="85%" />
				<d:field name="timefrom" anchor="85%" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" anchor="85%" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="userGrid" title="用户列表" selectFirstRow="false" height="$D.getFullHeight('userGrid')" model="sys/user" isCellEditable="isCellEditable" queryForm="queryForm" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add" afterAction="initDefaultValue"/>
				<d:gridButton type="save" />
			</d:toolBar>
			<d:columns>
				<d:column name="user_code" sortable="true" required="true" editor="textfield" prompt="用户编号" width="120" />
				<d:column name="user_name" sortable="true" required="true" editor="textfield" prompt="用户名" width="120" />
				<d:column name="password" required="true" editor="password" prompt="密码" width="120" />
				<d:column name="role_id" editor="combo" options="roleStore" displayField="role_name" valueField="role_id" prompt="角色" width="140" />
				<d:column name="status" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="状态" width="100" />
				<d:column name="create_date" prompt="创建日期" width="110" />
				<d:column name="last_update_user" prompt="最后经手人" width="110" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
