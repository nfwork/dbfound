<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			moduleGrid.query();
		}
		function reset() {
			queryForm.reset();
		}
		function isCellEditable(col, row,name,record) {
			if (record.json && ( name == "module_code")) {
				return false;
			} else {
				return true;
			}
		}
		function commit(json){
			queryForm.setData(json);
		}
	</script>
	<body>
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="module_code" upper="true" anchor="85%" editor="textfield" prompt="模块编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="module_name"  anchor="85%" editor="textfield" prompt="模块名称" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="user_name" anchor="85%" editor="lov" lovUrl="modules/sys/userLov.jsp" lovHeight="440" lovWidth="600" prompt="最后经办人">
				   <d:event name="commit" handle="commit"></d:event>
				</d:field>
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
		<d:grid id="moduleGrid" height="$D.getFullHeight('moduleGrid')" selectFirstRow="false" title="模块列表" isCellEditable="isCellEditable" queryForm="queryForm" model="sys/module" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add" />
				<d:gridButton type="save"/>
				<d:gridButton type="delete" />
			</d:toolBar>
			<d:columns>
				<d:column name="module_code" upper="true" required="true" editor="textfield" prompt="模块编号" width="120" />
				<d:column name="module_name" required="true" editor="textfield" prompt="模块名称" width="150" />
				<d:column name="image" hidden="true" editor="textfield" prompt="图标" width="100" />
				<d:column name="priority" align="right" required="false" editor="numberfield" prompt="优先级" width="80" />
				<d:column name="create_date" align="center" prompt="创建时间" width="120" />
				<d:column name="last_update_date" align="center" prompt="最后修改时间" width="120" />
				<d:column name="last_update_user" align="center" prompt="最后经手人" width="130" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
