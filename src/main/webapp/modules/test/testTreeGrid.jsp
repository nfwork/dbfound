<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		var nid = 1000;
		function add(){
			var node = new Ext.tree.TreeNode({id:nid++,pid:null,text:'测试'+nid,name:'节点'+nid,leaf:true,checked:false});
			treegrid.getRootNode().appendChild(node);
			node.select();
		}

		function addc(){
			var node = new Ext.tree.TreeNode({id:nid++,pid:1, text:'测试'+nid,name:'节点'+nid,leaf:true,checked:false});
			var pnode = treegrid.getCurrentNode();
			debugger;
			pnode.leaf = false;
			pnode.appendChild(node);
			pnode.expand();
			node.select();
		}

		function getSelect(){
			$D.showMessage(treegrid.getSelectedText());
		}

		function query(){
			treegrid.query();
		}

		function save(){
			data = viewForm.getData();
			var node = treegrid.getCurrentNode();
			debugger;
			treegrid.setCurrentNodeData(data);
		}
	</script>
	<body>
		<d:form id="queryForm" title="功能管理" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="function_des"  anchor="85%" editor="textfield" prompt="功能名称" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field width="80" editor="button" name="querybtn" prompt="查询">
					<d:event name="click" handle="query"></d:event>
				</d:field>
			</d:line>
		</d:form>
		
		<d:treeGrid id="treegrid" autoQuery="true" viewForm="viewForm" queryForm="queryForm" title="功能树表" idField="id" parentField="pid" showCheckBox="true" queryUrl="menu.query" height="$D.getFullHeight('treegrid')-120">
			<d:toolBar>
			    <d:gridButton icon="DBFoundUI/images/add.gif" title="添加模块" beforeAction="add" />
			    <d:gridButton icon="DBFoundUI/images/add.gif" title="添加功能" beforeAction="addc" />
			    <d:gridButton icon="DBFoundUI/images/check.png" title="选中功能" beforeAction="getSelect" />
			</d:toolBar>
			<d:columns>
				<d:column name="text" prompt="功能名称" width="150"></d:column>
				<d:column name="id" hidden="false" prompt="功能id" width="100"></d:column>
				<d:column name="url" prompt="对应jsp页面" width="200"></d:column>
				<d:column name="priority" prompt="优先级" width="100"></d:column>
			</d:columns>
		</d:treeGrid>
		
		<d:form id="viewForm" title="功能明细" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="text" anchor="95%" editor="textfield" prompt="功能名称" >
				</d:field>
				<d:field name="url" anchor="95%" editor="textfield" prompt="对应jsp页面" >
				</d:field>
				<d:field name="priority" anchor="95%" editor="textfield" prompt="优先级" >
				</d:field>
			</d:line>
			<d:toolBar>
			    <d:formButton action="" title="保存" beforeAction="save"></d:formButton>
			</d:toolBar>
		</d:form>
	</body>
</html>
