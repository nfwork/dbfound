<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			$D.showMessage("保存成功！");
		}
		function reset() {
			queryForm.reset();
		}
		function commit(json){
        	queryForm.setData(json);
        }
		var enableItmes = [ {
			boxLabel : '系统级',
			name : 'c1',
			inputValue : 'S'
		}, {
			boxLabel : '功能级',
			name : 'c2',
			inputValue : 'F'
		}, {
			boxLabel : '自由访问',
			name : 'c3',
			inputValue : 'N'
		} ];
		var sexItmes = [ {
			boxLabel : '男',
			name : 'sex',
			inputValue : 'man'
		}, {
			boxLabel : '女',
			name : 'sex',
			inputValue : 'women'
		}, {
			boxLabel : '其它',
			name : 'sex',
			inputValue : 'others'
		} ];
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="moduleStore" modelName="sys/module" queryName="combo" />
	    	<d:dataSet id="treedata" modelName="test/tree" queryName="area" />
	    </d:initProcedure>
	    
	    <d:form id="queryForm" title="表单元素" labelWidth="120">
			<d:line columnWidth="0.33">
				<d:field name="function_code" editor="textfield" prompt="文本输入框" />
				<d:field name="function_des" upper="true" editor="textfield" prompt="强制大写文本框" />
				<d:field name="function_aa" required="true" editor="textfield" prompt="必输文本框" />
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="functisdf" editor="numberfield" prompt="数字输入框" />
				<d:field name="dfdrt" required="true" editor="numberfield" prompt="必输数字框" />
				<d:field name="dfdrtio" editor="spinnerfield" prompt="spinner数字框" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="function_module" options="moduleStore" valueField="module_id" displayField="module_name" editor="combo" prompt="下拉框" />
				<d:field name="function_ulesad" options="moduleStore" valueField="module_id" displayField="module_name" editor="lovcombo" prompt="多选下拉框" />
			    <d:field name="ae" editor="treecombo" options="treedata" displayField="area_name" parentField="parent_id" valueField="area_id" prompt="下拉tree">
				</d:field>
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="funcfon_code" editor="datefield" prompt="日期选择框" />
				<d:field name="funopion_des" editor="datetimefield" prompt="日期时间选择框" />
				<d:field name="funopion_des" editor="monthfield" prompt="月份择框" />
			</d:line>
			<d:line columnWidth="0.33">
			    <d:field name="user_name" editor="lov" lovUrl="modules/sys/userLov.jsp" lovHeight="445" lovWidth="600" prompt="弹出选择框">
				   <d:event name="commit" handle="commit"></d:event>
				</d:field>
				<d:field name="area_name" editor="lov" lovUrl="modules/test/treeLov.jsp" lovHeight="445" lovWidth="340" prompt="弹出tree选择框">
				   <d:event name="commit" handle="commit"></d:event>
				</d:field>
				 <d:field name="funcdfdik" required="true" emptyText="请输入文字" editor="textfield" prompt="为空提示" />
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="funode" required="true" editor="password" prompt="密码输入框" />
				<d:field name="functies" editor="file" prompt="文件选择文本框" />
				<d:field name="funcdfd" readOnly="true" value="hello kity" editor="textfield" prompt="只读框" />
			</d:line>
			<d:line columnWidth="0.4">
				<d:field name="enable_flagas" editor="checkboxgroup" items="enableItmes" prompt="多选组" />
				<d:field name="enable_flag" editor="radiogroup" items="sexItmes" prompt="单选组" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="endf" height="70" anchor="95.5%" editor="textarea" prompt="大文本输入框" />
			</d:line>
			<d:line columnWidth="1">
				<d:field name="htmle" anchor="95.5%" editor="htmleditor" prompt="html文本输入框" />
			</d:line>
			<d:toolBar>
			    <d:formButton title="保存" icon="DBFoundUI/images/save.gif" action="job/jobHeader.execute!add" afterAction="afterSave"/>
			    <d:formButton title="重置" beforeAction="reset" action="" />
			</d:toolBar>
		</d:form>
	</body>
</html>
