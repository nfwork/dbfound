<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			roleGrid.query();
		}
		
		function reset() {
			queryForm.reset();
		}

		function initData(data,form,button){
			var json ={};
			json.GridData = roleGrid.getSelectionsData();
			form.setData(json);
			return true;
		}

		function detail(value,meta,record){
		   return "<a href = 'javaScript:openAssign("+record.json.employee_id+","+record.json.project_id+")'>"+value+"</a>";
        }

        function openAssign(employee_id,project_id){
           DBFound.open("role_window","账单明细",800,450,"modules/gc/settlementQueryDetail.jsp?employee_id="+employee_id+"&project_id="+project_id);
        }
	</script>
	<body>
	
	    <d:initProcedure>
	        <d:dataSet id="projectStore" queryName="combo" modelName="gc/project"/>
	        <d:dataSet id="employeeStore" queryName="combo" modelName="gc/employee"/>
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="结算查询" labelWidth="80">
			<d:line columnWidth="0.33">
			    <d:field name="project_id" options="projectStore" valueField="project_id" displayField="project_name" anchor="85%" editor="combo" prompt="工程选择" >
			        <d:event name="select" handle="query"></d:event>
			    </d:field>
			    <d:field name="employee_id" options="employeeStore" valueField="employee_id" displayField="employee_name" anchor="85%" editor="combo" prompt="工人选择" >
			        <d:event name="select" handle="query"></d:event>
			    </d:field>
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:grid id="roleGrid"  title="工时列表" height="380" queryForm="queryForm" model="gc/settlementQuery" autoQuery="true">
			<d:toolBar>
			   <d:gridButton type="excel" />
			</d:toolBar>
			<d:columns>
			    <d:column name="project_name" prompt="工程名称" width="130" />
				<d:column name="employee_code" prompt="工人编号" width="100" />
				<d:column name="employee_name" renderer="detail" prompt="工人名称" width="100" />
				<d:column name="price" prompt="工价（人/天）" width="100" />
				<d:column name="unsettle_total_date" prompt="未结算工作天数" width="130" />
				<d:column name="unsettle_total_price" prompt="未结算工钱（元）" width="130" />
				<d:column name="settle_total_date" prompt="已结算工作天数" width="130" />
				<d:column name="settle_total_price" prompt="已结算工钱（元）" width="130" />
				<d:column name="total_date" prompt="总工作天数" width="100" />
				<d:column name="total_price" prompt="总工钱（元）" width="100" />
			</d:columns>
		</d:grid>
	</body>
</html>
