<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
    <script type="text/javascript">
       function save(){
           alert('OK');
           if($D.getTab("userDefine").reset){$D.getTab("userDefine").reset();}
       }
    </script>
	<body>
	<d:dataSet id="treedata" modelName="test/tree"></d:dataSet>
	
		<d:tabs> 
			<d:tab height="450" title="功能定义">
		       <d:treeGrid id="treegrid" autoQuery="true" title="功能树表" idField="id" parentField="pid" showCheckBox="true" queryUrl="menu.query" height="350">
					<d:toolBar>
					    <d:gridButton icon="DBFoundUI/images/add.gif" title="添加模块" beforeAction="" />
					    <d:gridButton icon="DBFoundUI/images/add.gif" title="添加功能" beforeAction="" />
					    <d:gridButton icon="DBFoundUI/images/check.png" title="选中功能" beforeAction="" />
					</d:toolBar>
					<d:columns>
						<d:column name="text" prompt="功能名称" width="150"></d:column>
						<d:column name="id" hidden="false" prompt="功能id" width="100"></d:column>
						<d:column name="url" prompt="对应jsp页面" width="200"></d:column>
						<d:column name="priority" prompt="优先级" width="100"></d:column>
					</d:columns>
				</d:treeGrid>
		   </d:tab>
		   <d:tab height="450" title="角色定义">
		       <d:tree style="width:220px;position:absolute;left:0px;top:0px" id="menuTree" title="树测试" bindTarget="treedata" idField="id" parentField="pid" displayField="text" height="440" >
			   </d:tree>
		       <d:grid style="margin-left:230px;"  id="roleGrid" height="440" model="sys/role" autoQuery="true" pagerSize="10">
					<d:toolBar>
						<d:gridButton type="add" id="add_cmp"/>
						<d:gridButton type="save" action="test.jsp"/>
						<d:gridButton type="delete" />
					</d:toolBar>
					<d:columns>
						<d:column name="role_code" required="true" editor="textfield" prompt="角色编号" width="120" />
						<d:column name="role_description" required="true" editor="textfield" prompt="描述" width="150" />
						<d:column name="create_date" prompt="创建时间" width="120" />
						<d:column name="last_update_date" prompt="最后修改时间" width="120" />
						<d:column name="last_update_user" prompt="最后经手人" width="150" />
					</d:columns>
				</d:grid>
		   </d:tab>
		    <d:tab height="450" title="角色定义">
		       <d:grid height="440" model="sys/role" autoQuery="true" pagerSize="10">
					<d:toolBar>
						<d:gridButton type="add" id="add_cmp"/>
						<d:gridButton type="save" action="test.jsp"/>
						<d:gridButton type="delete" />
					</d:toolBar>
					<d:columns>
						<d:column name="role_code" required="true" editor="textfield" prompt="角色编号" width="120" />
						<d:column name="role_description" required="true" editor="textfield" prompt="描述" width="150" />
						<d:column name="create_date" prompt="创建时间" width="120" />
						<d:column name="last_update_date" prompt="最后修改时间" width="120" />
						<d:column name="last_update_user" prompt="最后经手人" width="150" />
					</d:columns>
				</d:grid>
		   </d:tab>
		   <d:tab height="200" title="模块定义">
		     <d:form id="queryForm" title="角色查询" labelWidth="80">
				<d:line columnWidth="0.33">
					<d:field name="role_code" width="150" editor="textfield" prompt="角色编号" />
					<d:field name="role_description" readOnly="true"  width="150" editor="textfield" prompt="描述" />
				</d:line>
				<d:line columnWidth="0.33">
					<d:field name="time" width="150" editor="datefield" prompt="日期" />
					<d:field name="timefrom" width="150" editor="datefield" prompt="创建日期从" />
					<d:field name="timeto" width="150" editor="datefield" prompt="创建日期到" />
				</d:line>
			  </d:form>
			  <d:buttonGroup>
			     <d:button click="save" title="保存"></d:button>
			  </d:buttonGroup>
		   </d:tab>
		   <d:tab height="480" id="userDefine" title="用户定义" url="modules/sys/user.jsp"></d:tab>
		</d:tabs>
	</body>
</html>
