<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function enable(){
			var param={"GridData":userGrid2.getSelectionsData()}
			$D.request({
				url:"test/user.execute!enabled",
				param:param,
				callback:function(){
					userGrid1.query();
					userGrid2.query();
				}
			});
		}
		
		function disable(){
			var param={"GridData":userGrid1.getSelectionsData()}
			$D.request({
				url:"test/user.execute!disabled",
				param:param,
				callback:function(){
					userGrid1.query();
					userGrid2.query();
				}
			});
		}
	</script>
	<body>
		<d:grid queryUrl="test/user.query!enabled" title="启用用户列表" style="width:220px;left:0px;top:0px;position:absolute;" id="userGrid1" height="300" autoQuery="true" navBar="false">
			<d:columns>
				<d:column name="user_code" prompt="用户编号" width="120" />
				<d:column name="user_name" prompt="用户名" width="120" />
			</d:columns>
		</d:grid>
		
		<d:buttonGroup style="width:70px;left:220px;top:100px;position:absolute;">
			<d:button id="query" click="enable" width="50" title="<=" />
		</d:buttonGroup>
		<d:buttonGroup style="width:70px;left:220px;top:150px;position:absolute;">
			<d:button id="query" click="disable" width="50"  title="=>" />
		</d:buttonGroup>
		
		<d:grid queryUrl="test/user.query!disabled" style="left:280px;width:220px;position:absolute;" title="未启用用户列表" id="userGrid2" height="300" autoQuery="true" navBar="false">
			<d:columns>
				<d:column name="user_code" prompt="用户编号" width="120" />
				<d:column name="user_name" prompt="用户名" width="120" />
			</d:columns>
		</d:grid>
	</body>
</html>
