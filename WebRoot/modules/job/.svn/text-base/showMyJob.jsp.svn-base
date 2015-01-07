<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
    <script type="text/javascript">

       function uploadAtt(){
    	   var url ="uploadShow.jsp?pk_value=${param.line_id}&table_name=job_lines";
           DBFound.open("att_window","附件查看",690,370,url);
       }

       function back(){
           if(parent){
	           var window=parent.Ext.getCmp("history_window");
	           window.close();
           }
       }
    </script>
   
	<body>
	    <d:initProcedure>
	        <d:query modelName="job/jobHeader" queryName="update_query" rootPath="headerStore"></d:query>
	        <d:query modelName="job/jobQuery"  rootPath="lineStore"></d:query>
	    </d:initProcedure>
	    
		<d:form title="作业信息" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true" editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" value="${headerStore[0].teacher_name}" editor="textfield" prompt="布置老师"/>
			    <d:field name="class_id" readOnly="true" value="${headerStore[0].class_name}" editor="textfield" prompt="所对班级" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="status" value="${headerStore[0].status_name}" readOnly="true" editor="textfield"  prompt="状态" />
			    <d:field name="course_id"  readOnly="true"  value="${headerStore[0].course_name}" editor="textfield" prompt="学科选择" />
			    <d:field name="end_time" readOnly="true"  value="${headerStore[0].end_time}" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" anchor="95.7%" readOnly="true"  value="${headerStore[0].description}" editor="textarea" prompt="描述" />
			</d:line>
		</d:form>
		
		<d:form title="提交信息" labelWidth="80">
			<d:line columnWidth="0.8">
				<d:field name="description" anchor="98%" value="${lineStore[0].description}" readOnly="true" editor="textarea" prompt="提交信息" />
				<d:field name="upButton" editor="button" columnWidth="0.2" width="80" prompt="附件查看(${lineStore[0].att_num})">
					<d:event name="click" handle="uploadAtt"></d:event>
				</d:field>
			</d:line>
		</d:form>
		
		<d:form title="审批信息" labelWidth="80">
		    <d:line columnWidth="0.33">
				<d:field name="status" value="${lineStore[0].status}" readOnly="true" editor="textfield" prompt="完成状态" />
			    <d:field name="score" value="${lineStore[0].score}" readOnly="true" editor="textfield" prompt="分数" />
			    <d:field name="grade" value="${lineStore[0].grade}" readOnly="true" editor="textfield" prompt="等级" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="teacher_comment" anchor="95.7%" value="${lineStore[0].teacher_comment}" readOnly="true" editor="textarea" prompt="教师评语" />
			</d:line>
		</d:form>
		<d:buttonGroup>
		   <d:button title="返回" click="back" ></d:button>
		</d:buttonGroup>
	</body>
</html>
