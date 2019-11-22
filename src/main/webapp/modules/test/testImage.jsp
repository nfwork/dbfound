<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function save() {
			$D.showMessage("保存成功！");
		}
		function reset() {
			queryForm.reset();
		}
	</script>
	<body>
	    
	    <d:form id="queryForm" title="员工详细信息" width="600" labelWidth="120">
	    	<d:line columnWidth="0.8">
	    		<d:field id="image" name="user_name" width="10" height="70" editor="textarea" prompt="员工图像" />
			</d:line>
			<d:line columnWidth="0.8">
				<d:field name="function_code" editor="textfield" prompt="员工编号" value="10000" />
			</d:line>
			<d:line columnWidth="0.8">
				<d:field name="function_code" editor="textfield" prompt="员工姓名" value="小毛" />
			</d:line>
			<d:line columnWidth="0.8">
				<d:field name="function_code" editor="datefield" prompt="出生日期" value="1985-01-02" />
			</d:line>
			<d:line columnWidth="0.8">
				<d:field name="function_code" editor="textfield" prompt="毕业院校" value="中山大学" />
			</d:line>
			<d:toolBar>
			    <d:formButton title="保存" icon="DBFoundUI/images/save.gif" beforeAction="save" action=""/>
			    <d:formButton title="重置" beforeAction="reset" action="" />
			</d:toolBar>
		</d:form>
		
		<img id="img" alt="hello kity" height="70" width="70" src="images/user.jpg">
		
		<script type="text/javascript">
		   var image = Ext.get("image");
		   var parent = image.parent();
		   image.hide();
		   parent.appendChild(Ext.get("img"));
		</script>
	</body>
</html>
