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
           DBFound.open("role_window","账单明细",800,450,"modules/gc/settlementDetail.jsp?employee_id="+employee_id+"&project_id="+project_id);
        }

        function getTotalMoney(){
            var  total_date = 0;
            var  total_price = 0;
            var jsons = roleGrid.getSelectionsData(true);
            for(var i=0;i<jsons.length;i++){
            	total_date = total_date + jsons[i].total_date;
            	total_price = total_price + jsons[i].total_price;
            }
            $D.showMessage("选择记录总天数："+total_date+"天, 共："+total_price+"元！");
        }
	</script>
	<body>
	
	    <d:initProcedure>
	        <d:dataSet id="projectStore" queryName="combo" modelName="gc/project"/>
	        <d:dataSet id="employeeStore" queryName="combo" modelName="gc/employee"/>
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="工程结算" labelWidth="80">
			<d:line columnWidth="0.33">
			    <d:field name="project_id" required="true" options="projectStore" valueField="project_id" displayField="project_name" anchor="85%" editor="combo" prompt="工程选择" >
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
		
		<d:grid id="roleGrid" title="工时列表" height="380" queryForm="queryForm" model="gc/settlement" autoQuery="false" pagerSize="50">
			<d:toolBar>
			    <d:gridButton action="gc/settlement.execute" afterAction="query" icon="DBFoundUI/images/check.png" title="结算" />
			    <d:gridButton icon="DBFoundUI/images/submit.gif" beforeAction="getTotalMoney" title="合计" />
			    <d:gridButton type="excel" />
			</d:toolBar>
			<d:columns>
			    <d:column name="project_name" prompt="工程名称" width="130" />
				<d:column name="employee_code" prompt="工人编号" width="130" />
				<d:column name="employee_name" renderer="detail" prompt="工人名称" width="130" />
				<d:column name="price" prompt="工价（人/天）" width="100" />
				<d:column name="total_date" prompt="工作天数" width="100" />
				<d:column name="total_price" prompt="工钱（元）" width="100" />
			</d:columns>
		</d:grid>
	</body>
</html>
