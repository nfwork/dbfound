<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>

   <script type="text/javascript">

       function back(){
           if(parent){
	           var window=parent.Ext.getCmp("showJob_window");
	           window.close();
           }
       }
    </script>
    
	<body>
	    <d:initProcedure>
	        <d:query modelName="job/jobHeader" queryName="update_query" rootPath="headerStore"></d:query>
	    </d:initProcedure>
	    
		<d:form title="作业信息查询" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true"  width="210" editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" value="${headerStore[0].teacher_name}" width="210" editor="textfield" prompt="布置老师"/>
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="class_id" readOnly="true" value="${headerStore[0].class_name}" width="210"  editor="textfield" prompt="所对班级" />
			    <d:field name="status" value="${headerStore[0].status_name}" readOnly="true" width="210" editor="textfield"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="course_id"  readOnly="true"  value="${headerStore[0].course_name}"  width="210" editor="textfield" prompt="学科选择" />
			    <d:field name="end_time" readOnly="true"  value="${headerStore[0].end_time}"  width="210" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" readOnly="true"  value="${headerStore[0].description}" width="555" editor="textarea" prompt="描述" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
		   <d:button title="返回" click="back" ></d:button>
		</d:buttonGroup>
	</body>
</html>
