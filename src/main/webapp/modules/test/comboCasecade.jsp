<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">

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
		    <d:dataSet id="class_ds" modelName="test/combo" queryName="class" />
		    <d:dataSet id="course_ds" modelName="test/combo" queryName="course" loadData="false" fields="course_name,course_id" />
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="级联下拉框" width="700" labelWidth="80">
			
			<d:line columnWidth="0.5">
			    <d:field name="class_id" required="true"  options="class_ds" valueField ="class_id" displayField="class_name" width="210" editor="combo" prompt="所对班级" >
			       <d:event name="select" handle="resetCourse"></d:event>
			    </d:field>
			</d:line>
			<d:line columnWidth="0.5">
			    <d:field id="course_cb" name="course_id" required="true" options="course_ds" valueField ="course_id" displayField="course_name" width="210" editor="combo" prompt="课程选择" />
			</d:line>
			
			<d:toolBar>
			    <d:formButton title="保存" icon="DBFoundUI/images/save.gif" action="job/jobHeader.execute!add" />
			    <d:formButton title="重置" beforeAction="reset" action="" />
			</d:toolBar>
		</d:form>
	</body>
</html>
