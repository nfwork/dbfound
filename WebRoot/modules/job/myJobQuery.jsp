<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<d:controlBody>
	<d:execute modelName="job/jobLine" executeName="init" />
	<d:forward modelName="job/jobLine" />
</d:controlBody>