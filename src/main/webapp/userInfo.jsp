<%@ page language="java" import="java.util.*" pageEncoding="GB18030"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>

	<body>
		<d:form id="infoForm" labelWidth="70">
			<d:line columnWidth="1">
				<d:field prompt="�û�ID" readOnly="true" name="user_id" editor="textfield" value="${sessionScope.user_id}">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field prompt="�û�CODE" readOnly="true" name="user_name" editor="textfield" value="${sessionScope.user_code}">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field prompt="�û���" readOnly="true" name="user_name" editor="textfield" value="${sessionScope.user_name}">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field prompt="��ɫID" readOnly="true" name="role_id" editor="textfield" value="${sessionScope.role_id}">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field prompt="��ɫCODE" readOnly="true" name="role_id" editor="textfield" value="${sessionScope.role_code}">
				</d:field>
			</d:line>
			<d:line columnWidth="1">
				<d:field prompt="��ɫ��" readOnly="true" name="role_description" editor="textfield" value="${sessionScope.role_description}">
				</d:field>
			</d:line>
		</d:form>
	</body>
</html>
