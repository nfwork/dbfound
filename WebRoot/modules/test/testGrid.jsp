<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
		<script type="text/javascript" src="DBFoundUI/plugin/ColumnHeaderGroup.js" charset='utf-8'></script>
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
			if (record.json && (name == "role_id" || name == "user_code")) {
				return false;
			} else {
				return true;
			}
		}
		function commit(json){
			userGrid.initCurrentRecordData(json);
		}

		var continentGroupRow = [ 
      		    {colspan: 2},
      			{header: '基本信息', colspan: 5, align: 'center'},
      	     	{header: '操作信息', colspan: 2, align: 'center'}
		] ;
		             			 
   		var group = new Ext.ux.grid.ColumnHeaderGroup({
   		        rows: [continentGroupRow]
   		});
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="roleStore" modelName="sys/role" queryName="combo" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="复合表头测试" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="user_code" upper="true" width="150" editor="textfield" prompt="用户编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="user_name"  width="150" editor="textfield" prompt="i18n:用户名" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="status_code" width="150" editor="combo" options="statusStore" displayField="status_name" valueField="status_code"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="role_id" editor="combo" options="roleStore" displayField="role_name" valueField="role_id" prompt="角色" width="150" />
				<d:field name="timefrom" width="150" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" width="150" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="userGrid" title="用户列表" height="370" isCellEditable="isCellEditable" queryForm="queryForm" plugins="group" model="sys/user" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add"/>
				<d:gridButton type="save"/>
			</d:toolBar>
			<d:columns>
				<d:column name="user_code" upper="true" required="true" editor="lov" lovHeight="500" lovWidth="550" lovUrl="sys/userLov.jsp?lov=1" prompt="用户编号" width="120">
				   <d:event name="commit" handle="commit"></d:event>
				</d:column>
				<d:column name="user_name" required="true" editor="textfield" prompt="姓名" width="120" />
				<d:column name="password" required="true" editor="password" prompt="密码" width="120" />
				<d:column name="role_id" editor="combo" options="roleStore" displayField="role_name" valueField="role_id" prompt="角色" width="140" />
				<d:column name="status" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="状态" width="100" />
				<d:column name="create_date" prompt="创建日期" width="110" />
				<d:column name="last_update_user" prompt="最后经手人" width="110" />
			</d:columns>
		</d:grid>
	</body>
</html>
