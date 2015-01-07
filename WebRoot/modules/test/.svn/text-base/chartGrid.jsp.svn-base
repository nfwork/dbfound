<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML>
<html>
	<head>
		<d:includeLibrary />
		<script type="text/javascript" src="${basePath}DBFoundUI/chart/jquery.min.js"></script>
		<script src="${basePath}DBFoundUI/chart/highcharts.js"></script>
	</head>
	<script type="text/javascript">
		var yAxis = [ {
			name : '10月份',
			field : 'total_num_10'
		} ,{
			name : '11月份',
			field : 'total_num_11'
		} ,{
			name : '12月份',
			field : 'total_num_12'
		}];
		var xAxis = {
			name : '人数',
			field : 'grade'
		};
		function query(){
			grid.query();
		}
	</script>
	<body>
		<d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="role_code" upper="true" anchor="85%" editor="textfield" prompt="角色编号">
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="role_description"  anchor="85%" editor="textfield" prompt="描述">
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="user_name" anchor="85%" editor="lov" lovUrl="modules/sys/userLov.jsp" lovHeight="440" lovWidth="600" prompt="最后经办人">
				</d:field>
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="timefrom" anchor="85%" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" anchor="85%" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query"/>
			<d:button title="重置" />
		</d:buttonGroup>
		
		<div style="margin:5px" id="lineChart_div"></div>
		
		<d:grid id="grid" title="月考分析折" autoQuery="true" height="350" queryUrl="test/chart.query!bar">
			<d:columns>
				<d:column name="total_num_10" prompt="10月份"/>
				<d:column name="total_num_11" prompt="11月份"/>
				<d:column name="total_num_12" prompt="12月份"/>
				<d:column name="grade" prompt="等级"/>
			</d:columns>
		</d:grid>
		
		<d:lineChart id="lineChart" title="月考分析折线图" yAxis="yAxis" xAxis="xAxis" height="350" bindTarget="grid.getStore()" />
	</body>
</html>
