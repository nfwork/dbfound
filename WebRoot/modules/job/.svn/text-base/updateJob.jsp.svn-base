<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.nfwork.dbfound.core.Context"%>
<%@page import="com.nfwork.dbfound.util.JsonUtil"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	
	<script type="text/javascript">
		function update() {
			if (queryForm.form.isValid()) {
				var objectjson = queryForm.form.getValues();
				$D.request( {
					url : 'job/jobHeader.execute!update?header_id=${param.header_id}',
					param : objectjson,
					callback : function(obj) {
						if (obj.success == true) {
							$D.showMessage( '修改成功！', close);
						} else{
							$D.showError(obj.message)
						}
					}
				});
			}else{
				$D.showMessage("信息填写不完整！");
			}
		}
		
		function close() {
			if (parent) {
				var window = parent.Ext.getCmp("update_window");
				window.close();
			}
		}
	
		function resetCourse(dom, field) {
			course_ds.baseParams = {
				class_id : field.data.class_id
			};
			var course_cb = Ext.getCmp("course_cb").setValue(null);
			course_ds.load();
		}
	</script>
	<body style="overflow:hidden">
	    <d:initProcedure>
		    <d:query modelName="job/jobHeader" queryName="update_query" rootPath="headerStore"></d:query>
		    <d:dataSet id="class_ds" modelName="fnd/class" queryName="combo" />
		    <d:dataSet id="course_ds" modelName="fnd/course" queryName="add_combo" sourcePath="headerStore[0]" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" height="200" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="tile" value="${headerStore[0].title}" readOnly="true"  width="210" editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" value="${headerStore[0].teacher_name}" width="210" editor="textfield" prompt="布置老师"/>
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="class_id" value="${headerStore[0].class_id}" required="true"  options="class_ds" valueField ="class_id" displayField="class_name" width="210" editor="combo" prompt="所对班级" >
			     <d:event name="select" handle="resetCourse"></d:event>
			    </d:field>
			    <d:field name="status" value="${headerStore[0].status_name}" readOnly="true" width="210" editor="textfield"  prompt="状态" />
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field id="course_cb" name="course_id" value="${headerStore[0].course_id}" required="true" options="course_ds" valueField ="course_id" displayField="course_name" width="210" editor="combo" prompt="课程选择" />
			    <d:field name="end_time" value="${headerStore[0].end_time}" required="true" width="210" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" height="80" value="${headerStore[0].description}" width="555" editor="textarea" prompt="描述" />
			</d:line>
		</d:form>
		<d:buttonGroup align="center">
		    <d:button title="返回" click="close" ></d:button>
			<d:button title="保存" icon="DBFoundUI/images/save.gif"  click="update" />
		</d:buttonGroup>
	</body>
</html>
