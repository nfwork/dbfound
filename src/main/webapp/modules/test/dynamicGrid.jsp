<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<body>
		<d:query modelName="test/dynamic" queryName="getMeta" rootPath="metas"></d:query>
	    <d:grid id="userGrid" title="动态列表" height="365" model="sys/user" autoQuery="true" >
			<d:columns>
				<d:forEach items="${metas}" var="meta">
					<d:column name="${meta.name}" prompt="${meta.prompt}" width="${meta.width}" sortable="true" />
				</d:forEach>
			</d:columns>
		</d:grid>

	</body>
</html>
