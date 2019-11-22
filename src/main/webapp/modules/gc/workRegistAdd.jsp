<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			if (!queryForm.form.isValid()) {$D.showMessage('验证通不过！');return;}
			var json = queryForm.getData();
			var url = "modules/gc/test.jsp?project_id="+json.project_id+"&employee_id="+json.employee_id+"&date="+json.date;
			DBFound.open("role_window","工时补报",445,365,url);
		}
		
		function reset() {
			queryForm.reset();
		}

		var dateStore = new Ext.data.SimpleStore( {
			data : [ [ "2013-03" ],[ "2013-04" ],[ "2013-05" ],[ "2013-06" ],[ "2013-07" ],[ "2013-08" ],[ "2013-09" ],
			         [ "2013-10" ],[ "2013-11" ],[ "2013-12" ]],
			fields : [ "date" ]
		});

		function addEmployee(){
			var url = "modules/gc/employeeAdd.jsp";
			DBFound.open("role_window","添加工人",462,200,url,function(){
				employeeStore.load();
				queryForm.setData({employee_id:null});
			});
		}
		
	</script>
	<body>
	
	    <d:initProcedure>
	        <d:dataSet id="projectStore" queryName="combo" modelName="gc/project"/>
	        <d:dataSet id="employeeStore" queryName="combo" fields="employee_name,employee_id" modelName="gc/employee"/>
	    </d:initProcedure>
	    
		<d:form width="500" id="queryForm" title="工时补报" labelWidth="80">
			<d:line columnWidth="1">
			    <d:field name="project_id" editable="false" required="true" options="projectStore" valueField="project_id" displayField="project_name" anchor="85%" editor="combo" prompt="工程选择" >
			    </d:field>
			</d:line>   
			<d:line columnWidth="1">
			    <d:field name="employee_id" required="true" options="employeeStore" valueField="employee_id" displayField="employee_name" anchor="85%" editor="combo" prompt="工人选择" >
			    </d:field>
		    </d:line>   
			<d:line columnWidth="1">	    
			    <d:field name="date" editable="false" required="true" anchor="85%" editor="monthfield" prompt="月份选择" >
			    </d:field>
			</d:line>
		</d:form>
		
		<d:buttonGroup width="500" align="center">
			<d:button id="query" title="补报" click="query" />
			<d:button title="重置" click="reset" />
			<d:button id="addEmployee" title="添加工人" click="addEmployee" />
		</d:buttonGroup>
	</body>
</html>
