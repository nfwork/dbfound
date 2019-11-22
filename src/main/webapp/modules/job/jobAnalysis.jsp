<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML>
<html>
	<head>
		<d:includeLibrary />
		<script type="text/javascript" src="DBFoundUI/chart/jquery.min.js"></script>
		<script src="DBFoundUI/chart/highcharts.js"></script>
	</head>
	<body>
	    <d:dataSet id="job_message_ds" modelName="job/jobAnalysis"/>
	    <d:pieChart bindTarget="job_message_ds" height="350" dataLabel="true"
			valueField="total_num" displayField="grade"></d:pieChart>
	</body>
</html>