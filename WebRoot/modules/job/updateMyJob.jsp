<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
    <script type="text/javascript">
       function submit(){
    	   if (lineForm.form.isValid()) {
				var objectjson = lineForm.form.getValues();
				objectjson.line_id='${param.line_id}';
				Ext.Ajax.request( {
					url : 'job/myJobControl.execute!submit',
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
       
       function uploadAtt(){
    	   var url ="upload.jsp?pk_value=${param.line_id}&table_name=job_lines";
           DBFound.open("att_window","附件上传",700,400,url,refreshFileNum);
       }

       function refreshFileNum(){
			$D.request({
				url:"upload.query!count?pk_value=${param.line_id}&table_name=job_lines",
				callback:function(res){
					var num = res.datas[0].total_num;
					var button = Ext.getCmp("uploadButton");
					button.setText("附件上传("+num+")");
				}
			});
       }

       function close(){
    	   if(parent){
		         var window=parent.Ext.getCmp("my_job_window");
		         window.close();
	         }
       }
       
    </script>
   
	<body style="overflow:hidden">
	    <d:initProcedure>
	        <d:query modelName="job/jobHeader" queryName="update_query" rootPath="headerStore"></d:query>
	        <d:query modelName="job/jobQuery"  rootPath="lineStore"></d:query>
	    </d:initProcedure>
	    
		<d:form id="headerForm" title="作业信息" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true"  anchor="86%" editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" value="${headerStore[0].teacher_name}" anchor="86%" editor="textfield" prompt="布置老师"/>
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="class_id" readOnly="true" value="${headerStore[0].class_name}" anchor="86%"  editor="textfield" prompt="所对班级" />
			    <d:field name="status" value="${headerStore[0].status_name}" readOnly="true" anchor="86%" editor="textfield"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="course_id"  readOnly="true"  value="${headerStore[0].course_name}"  anchor="86%" editor="textfield" prompt="学科选择" />
			    <d:field name="end_time" readOnly="true"  value="${headerStore[0].end_time}"  anchor="86%" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" readOnly="true"  value="${headerStore[0].description}" anchor="93%" editor="textarea" prompt="描述" />
			</d:line>
		</d:form>
		
		<d:form id="lineForm" labelWidth="80">
			<d:line columnWidth="1">
				<d:field name="description" readOnly="false" required="true" anchor="93%" editor="textarea" prompt="提交信息" />
			</d:line>
		</d:form>
		<d:buttonGroup>
		   <d:button title="返回" click="close" ></d:button>
		   <d:button title="提交" click="submit" ></d:button>
		   <d:button id="uploadButton" title="附件上传(${lineStore[0].att_num})" click="uploadAtt" ></d:button>
		</d:buttonGroup>
	</body>
</html>
