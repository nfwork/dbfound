<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">

		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "S", "S-系统级" ],[ "F", "F-功能级" ], [ "N", "N-自由访问" ] ],
			fields : [ "status_code", "status_name" ]
		});
		
		function query() {
			acGrid.query();
		}

	    function assign(value,meta,record){
		  if(value!=""&&record.get("enable_flag")=='F')
		   return "<a href = 'javaScript:openAssign("+value+")'>分配到功能</a>";
        }

        function openAssign(id){
           DBFound.open("role_window","分配到功能",600,370,"modules/sys/pagerAssign.jsp?ac_id="+id,function(){});
        }
	</script>
	<body>
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="1">
				<d:field name="url" width="290" editor="textfield" prompt="访问路径" >
				   <d:event name="enter" handle="query"/>
				</d:field>
			</d:line>
		</d:form>
         <d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
		</d:buttonGroup>
		<d:grid id="acGrid" title="url控制列表" height="365" queryForm="queryForm"  model="sys/accessControl" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add" />
				<d:gridButton type="save" />
				<d:gridButton type="delete" />
			</d:toolBar>
			<d:columns> 
				<d:column name="url"  required="true" editor="textfield" prompt="访问路径" width="230" />
				<d:column name="enable_flag" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="作用域" width="100" />
			    <d:column name="ac_id" align="center" renderer="assign" prompt="分配到功能" width="120" />
			    <d:column name="last_update_date" prompt="最后修改时间" width="140" />
				<d:column name="last_update_user" prompt="最后经手人" width="150" />
			</d:columns>
		</d:grid>
		
	</body>
</html>
