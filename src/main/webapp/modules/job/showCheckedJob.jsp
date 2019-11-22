<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
    <script type="text/javascript">
     function upAttuchement(value,meta,record){
           if(record.json.line_id){
		  	 return "<a href = javaScript:openAttWindow('"+record.json.line_id+"')>附件("+value+")</a>";
           }else{
             return null;
           }
	 }

     function openAttWindow(id,status){
        var url="uploadShow.jsp?pk_value="+id+"&table_name=job_lines";
        DBFound.open("att_window","附件查看",690,370,url);
     }

     function openBirt(){
    	 DBFound.open("anlysis_window","报表分析",570,400,'modules/job/jobAnalysis.jsp?header_id=${param.header_id}');
     }

     function close(){
  	     if(parent){
	         var window=parent.Ext.getCmp("check_window");
	         window.close();
         }
     }
    </script>
   
	<body>
	    <d:initProcedure>
	        <d:dataSet id="statusStore" modelName="job/jobStatus" queryName="priority_combo" />
	        <d:query modelName="job/jobHeader" queryName="update_query" rootPath="headerStore"></d:query>
	    </d:initProcedure>
	    
		<d:form labelWidth="70">
			<d:line columnWidth="0.33">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true"  editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" value="${headerStore[0].teacher_name}" editor="textfield" prompt="布置老师"/>
			    <d:field name="class_name" readOnly="true" value="${headerStore[0].class_name}" editor="textfield" prompt="所对班级" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="status" value="${headerStore[0].status_name}" readOnly="true" editor="textfield"  prompt="状态" />
			    <d:field name="course_name"  readOnly="true"  value="${headerStore[0].course_name}" editor="textfield" prompt="学科选择" />
			    <d:field name="end_time" readOnly="true"  value="${headerStore[0].end_time}" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" readOnly="true" anchor="95%" value="${headerStore[0].description}" editor="textarea" prompt="描述" />
			</d:line>
		</d:form>
		<d:buttonGroup>
		   <d:button title="返回" click="close" ></d:button>
		   <d:button click="openBirt" title="分析报表" ></d:button>
		</d:buttonGroup>
		<d:grid selectable="false" height="300" queryUrl="job/jobCheck.query?header_id=${param.header_id}" autoQuery="true" pagerSize="50">
			<d:toolBar>
			   <d:gridButton type="excel" />
			</d:toolBar>
			<d:columns>
			    <d:column name="student_name" prompt="学生" width="120" />
				<d:column name="description" width="220" prompt="作业提交信息" />
				<d:column name="att_num" align="center" renderer="upAttuchement" prompt="附件" width="70" />
			    <d:column name="score" align="right"  width="80" prompt="分数" />
				<d:column name="grade" width="80"  prompt="等级" />
			    <d:column name="teacher_comment"  width="220" prompt="评语" />
			</d:columns>
		</d:grid>
	</body>
</html>
