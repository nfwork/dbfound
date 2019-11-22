<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<body>
		<script type="text/javascript">
			Ext.onReady(function() {
				DBFound.open("login_window", "密码修改", ${param.width}, ${param.height},
						"${param.url}");
			});
		</script>
	</body>
</html>