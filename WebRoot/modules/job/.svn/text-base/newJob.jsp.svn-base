<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function afterSave(obj) {
			if (obj.success == true) {
				location.href="${basePath}modules/job/jobManage.jsp";
			} else{
				$D.showError(obj.message);
			}
		}
		
		function reset() {
			queryForm.reset();
		}

		function resetCourse(dom,field){
			course_ds.baseParams={class_id:field.data.class_id};
			var course_cb=Ext.getCmp("course_cb").setValue(null);
			course_ds.load();
		}
	</script>
	<body>
	    <d:initProcedure>
		    <d:dataSet id="class_ds" modelName="fnd/class" queryName="combo" />
		    <d:dataSet id="course_ds" modelName="fnd/course" queryName="add_combo" loadData="false" fields="course_name,course_id" />
		    <d:dataSet id="job_ds" modelName="job/jobQuery" queryName="teacher_query" />
	    </d:initProcedure>
		<d:form id="queryForm" title="作业新增" bindTarget="job_ds" height="260" width="700" labelWidth="80">
			<d:line columnWidth="0.5">
				<d:field name="title" required="true" width="210" editor="textfield" prompt="作业题目"/>
				<d:field name="teacher_name" readOnly="true" width="210" editor="textfield" prompt="布置老师"/>
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field name="class_id" required="true"  options="class_ds" valueField ="class_id" displayField="class_name" width="210" editor="combo" prompt="所对班级" >
			       <d:event name="select" handle="resetCourse"></d:event>
			    </d:field>
			    <d:field name="status" readOnly="true" value="新建" editor="textfield" width="210" prompt="状态" />
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field id="course_cb" name="course_id" required="true" options="course_ds" valueField ="course_id" displayField="course_name" width="210" editor="combo" prompt="课程选择" />
			    <d:field name="end_time" required="true" width="210" editor="datefield" prompt="完成时间" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="description" width="555" height="80" editor="textarea" prompt="描述" />
			</d:line>
			<d:toolBar>
			    <d:formButton title="保存" icon="DBFoundUI/images/save.gif" action="job/jobHeader.execute!add" afterAction="afterSave"/>
			    <d:formButton title="重置" beforeAction="reset" action="" />
			</d:toolBar>
		</d:form>
	</body>
</html>
