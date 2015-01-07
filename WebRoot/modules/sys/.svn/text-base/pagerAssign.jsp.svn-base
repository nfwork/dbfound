<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		
        function init_data(records,grid){
            var ac_id = ${param.ac_id};
            for(var i=0;i<records.length;i++){
                records[i].set("ac_id",ac_id);
            }
            return true;
        }
	</script>
	<body>
	    <d:initProcedure>
	        <d:dataSet id="functionStore" modelName="sys/function" queryName="combo" />
	    </d:initProcedure>
	    
		<d:grid id="assignGrid" height="320" queryUrl="sys/pagerAssign.query?ac_id=${param.ac_id}" model="sys/pagerAssign" autoQuery="true" >
			<d:toolBar>
				<d:gridButton type="add" />
				<d:gridButton type="save" beforeAction="init_data" />
				<d:gridButton type="delete" />
			</d:toolBar>
			<d:columns> 
				<d:column name="function_id"  required="true" options="functionStore" editor="combo" displayField="function_des" valueField="function_id" prompt="分配功能" width="350" />
			</d:columns>
		</d:grid>
	</body>
</html>
