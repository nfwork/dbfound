<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
    <script type="text/javascript">
     function upAttuchement(value,meta,record){
		   return "<a href = javaScript:openAttWindow('"+record.json.line_id+"','"+record.json.status_code+"')>附件("+value+")</a>";
     }

     function openAttWindow(id,status){
        var url;
		   if (status=='NEW'||status=='DOWN'){
			   url="upload.jsp?pk_value="+id+"&table_name=job_headers";
		   }else{
			   url="uploadShow.jsp?pk_value="+id+"&table_name=job_lines";
		   }
        DBFound.open("att_window","附件上传",690,370,url,function(){jobGrid_ds.reload();});
     }

     function submit(){
  	   if (headerForm.form.isValid()) {
				var objectjson = headerForm.form.getValues();
				objectjson.header_id='${param.header_id}';
				Ext.Ajax.request( {
					url : 'job/jobControl.execute!finish',
					params : objectjson,
					success : function(response, action) {
						var obj = Ext.util.JSON.decode(response.responseText);
						if (obj.success == true) {
							close();
						} else{
							alert(obj.message)
						}
					},
					failure : function() {
						alert('提交失败！');
					}
				});
			}
     }


     function alert(message){
			Ext.MessageBox.show( {
				title : '提示',
				msg :  message,
				buttons : Ext.MessageBox.OK,
				icon : Ext.MessageBox.ERROR
			});
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
	    
		<d:form id="headerForm" title="作业信息查询" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true" editor="textfield" prompt="作业题目"/>
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
		   <d:button click="submit" title="完成" ></d:button>
		</d:buttonGroup>
		<d:grid id="jobGrid" height="270" model="job/jobCheck" queryUrl="job/jobCheck.query!checkData?header_id=${param.header_id}" autoQuery="true" pagerSize="50">
			<d:toolBar>
			   <d:gridButton type="save" />
			</d:toolBar>
			<d:columns>
			    <d:column name="student_name" prompt="学生" width="130" />
				<d:column name="description" width="180" prompt="作业提交信息" />
				<d:column name="att_num" align="center" renderer="upAttuchement" prompt="附件" width="70" />
			    <d:column name="score" align="right" editor="numberfield" width="80" prompt="分数" />
				<d:column name="grade" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code"  width="80"  prompt="等级" />
			    <d:column name="teacher_comment" editor="textfield"  width="220" prompt="评语" />
			</d:columns>
		</d:grid>
	</body>
</html>
