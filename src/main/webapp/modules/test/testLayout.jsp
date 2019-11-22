<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<body>
		<div class="dbfound-table">
			<div class="dbfound-tr">
				<div class="dbfound-td" style="width: 50%">
					<d:grid id="userGrid0" height="230" model="sys/user" autoQuery="true">
						<d:toolBar>
							<d:gridButton type="add" afterAction="initDefaultValue" />
							<d:gridButton type="save" />
						</d:toolBar>
						<d:columns>
							<d:column name="user_code" prompt="用户编号" width="120" />
							<d:column name="user_name" prompt="用户名" width="120" />
							<d:column name="password" prompt="密码" width="120" />
							<d:column name="create_date" prompt="创建日期" width="110" />
							<d:column name="last_update_user" prompt="最后经手人" width="110" />
						</d:columns>
					</d:grid>
				</div>
				<div class="dbfound-td" style="width: 50%">
					<d:grid id="userGrid1" height="230" model="sys/user" autoQuery="true">
						<d:toolBar>
							<d:gridButton type="add" afterAction="initDefaultValue" />
							<d:gridButton type="save" />
						</d:toolBar>
						<d:columns>
							<d:column name="user_code" prompt="用户编号" width="120" />
							<d:column name="user_name" prompt="用户名" width="120" />
							<d:column name="password" prompt="密码" width="120" />
							<d:column name="create_date" prompt="创建日期" width="110" />
							<d:column name="last_update_user" prompt="最后经手人" width="110" />
						</d:columns>
					</d:grid>
				</div>
			</div>
			
			<div class="dbfound-tr">
				<div class="dbfound-td" style="width: 50%">
					<d:grid id="userGrid2" height="230" model="sys/user" autoQuery="true">
						<d:toolBar>
							<d:gridButton type="add" afterAction="initDefaultValue" />
							<d:gridButton type="save" />
						</d:toolBar>
						<d:columns>
							<d:column name="user_code" prompt="用户编号" width="120" />
							<d:column name="user_name" prompt="用户名" width="120" />
							<d:column name="password" prompt="密码" width="120" />
							<d:column name="create_date" prompt="创建日期" width="110" />
							<d:column name="last_update_user" prompt="最后经手人" width="110" />
						</d:columns>
					</d:grid>
				</div>
				<div class="dbfound-td" style="width: 50%">
					<d:grid id="userGrid3" height="230" model="sys/user" autoQuery="true">
						<d:toolBar>
							<d:gridButton type="add" afterAction="initDefaultValue" />
							<d:gridButton type="save" />
						</d:toolBar>
						<d:columns>
							<d:column name="user_code" prompt="用户编号" width="120" />
							<d:column name="user_name" prompt="用户名" width="120" />
							<d:column name="password" prompt="密码" width="120" />
							<d:column name="create_date" prompt="创建日期" width="110" />
							<d:column name="last_update_user" prompt="最后经手人" width="110" />
						</d:columns>
					</d:grid>
				</div>
			</div>
		</div>
	</body>
</html>
