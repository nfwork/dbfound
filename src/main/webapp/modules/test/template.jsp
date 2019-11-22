<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<body style="overflow:hidden">		
	    <d:if test="${param.table_name!=null}">
		    <d:initProcedure>
			    <d:query modelName="test/dynamic" queryName="init" rootPath="columns"></d:query>
			</d:initProcedure>
			<d:grid id="userGrid" queryUrl="test/dynamic.query?table_name=${param.table_name}" autoQuery="true" >
				<d:toolBar>
					<d:gridButton type="excel"/>
				</d:toolBar>
				<d:columns>
				    <d:forEach items="${columns}" var="column">
				    	<d:column name="${column.name}" prompt="${column.prompt}"/>
				    </d:forEach>
				</d:columns>
			</d:grid>
	    </d:if>
	</body>
</html>
