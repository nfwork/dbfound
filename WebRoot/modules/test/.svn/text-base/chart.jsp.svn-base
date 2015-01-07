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
	</script>
	<body>
		<d:dataSet id="barStore" modelName="test/chart" queryName="bar" />
		<d:dataSet id="pieStore" modelName="test/chart" queryName="pie" />
		<div style="width: 40%; position: absolute;">
			<d:pieChart title="月考成绩饼图" dataLabel="true" bindTarget="pieStore" height="450" valueField="total_num" displayField="grade" />
		</div>
		<div style="width: 60%; left: 40%; position: absolute;">
			<d:barChart style="margin-left:0px" title="月考分析柱状图" yAxis="yAxis" xAxis="xAxis" height="450" bindTarget="barStore" />
		</div>
		<div style="width: 100%; top:460px; position: absolute;">
			<d:lineChart type="spline" style="margin-top:0px" title="月考分析折线图" yAxis="yAxis" xAxis="xAxis" height="350" bindTarget="barStore" />
		</div>
	</body>
</html>
