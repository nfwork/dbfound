<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML>
<html>
	<head>
		<d:includeLibrary />
		<script type="text/javascript" src="${basePath}DBFoundUI/chart/jquery.min.js" charset='utf-8'></script>
		<script type="text/javascript" src="${basePath}DBFoundUI/chart/highcharts.js" charset='utf-8'></script>
		<style type="text/css">.x-panel-header {height:17px;}</style>
	</head>
	<script type="text/javascript">
		var yAxis = [ {
			name : '大米',
			field : 'total_num_10'
		} ,{
			name : '小麦',
			color : '#ee3400',
			field : 'total_num_11'
		} ,{
			name : '玉米',
			field : 'total_num_12'
		}];
		var xAxis = {
			name : '产量（吨）',
			field : 'c_date'
		};
	</script>
	<body>
		<d:dataSet id="lineStore" modelName="test/chart" queryName="line" />
		<d:lineChart title="月考分析折线图" yAxis="yAxis" xAxis="xAxis" height="450" bindTarget="lineStore" />
	</body>
</html>
