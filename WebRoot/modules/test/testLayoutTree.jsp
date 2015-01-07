<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function sayHello(node) {
			$D.showMessage(node.text);
		}
	
		function query() {
			roleGrid.query();
		}
		
		function reset() {
			queryForm.reset();
		}
		
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "role_code")) {
				return false;
			} else {
				return true;
			}
		}
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="treedata" modelName="test/tree"></d:dataSet>
	    </d:initProcedure>
		
		<div style="width:220px;left:0px;top:0px;position:absolute;">
			<d:tree title="树测试" bindTarget="treedata" idField="id" parentField="pid" displayField="text" height="500" >
			    <d:event name="click" handle="sayHello"></d:event>
			</d:tree>	
		</div>
		<div style="margin-left:220px">
		   <d:form id="queryForm" title="角色查询" labelWidth="80">
				<d:line columnWidth="0.5">
					<d:field name="role_code" upper="true" anchor="85%" editor="textfield" prompt="角色编号">
					   <d:event name="enter" handle="query"/>
					</d:field>
					<d:field name="role_description"  anchor="85%" editor="textfield" prompt="描述">
					   <d:event name="enter" handle="query"/>
					</d:field>
				</d:line>
				<d:line columnWidth="0.5">
					<d:field name="timefrom" anchor="85%" editor="datefield" prompt="创建日期从" />
					<d:field name="timeto" anchor="85%" editor="datefield" prompt="创建日期到" />
				</d:line>
			</d:form>
			
			<d:buttonGroup>
				<d:button id="query" title="查询" click="query" />
				<d:button title="重置" click="reset" />
			</d:buttonGroup>
			
			<d:grid id="roleGrid" title="角色列表" height="375" isCellEditable="isCellEditable" queryForm="queryForm" model="sys/role" autoQuery="true" >
				<d:toolBar>
					<d:gridButton type="add"/>
					<d:gridButton type="save"/>
					<d:gridButton type="delete" />
				</d:toolBar>
				<d:columns>
					<d:column name="role_code" upper="true" required="true" editor="textfield" prompt="角色编号" width="130" />
					<d:column name="role_description" required="true" editor="textfield" prompt="描述" width="180" />
					<d:column name="create_date" prompt="创建时间" width="120" />
					<d:column name="last_update_date" prompt="最后修改时间" width="120" />
					<d:column name="last_update_user" prompt="最后经手人" width="150" />
				</d:columns>
			</d:grid>
	    </div>
	</body>
</html>
