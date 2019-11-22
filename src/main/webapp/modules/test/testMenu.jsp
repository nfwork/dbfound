<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
		<script type="text/javascript" charset="utf-8" src="DBFoundUI/chart/jquery.min.js"></script>
	</head>
	<script type="text/javascript">

		function say() {
			$D.showMessage("123");
		}

		function contextmenu(node,e){
			menu.showAt([e.getPageX(),e.getPageY()]);            
			node.select();//让右击是选中当前节点        
		}

		function rootmenu(tree,e){
			rootMenu.showAt([e.getPageX(),e.getPageY()]);            
		}

	</script>
	<body>
		<d:menu id="rootMenu">
	       <d:menuItem title="添加跟节点" icon="DBFoundUI/images/add.gif" click="say"></d:menuItem>
	    </d:menu>
	    <d:menu id="menu">
	       <d:menuItem title="添加子节点" icon="DBFoundUI/images/add.gif" click="say"></d:menuItem>
	       <d:menuItem title="修改"></d:menuItem>
	    </d:menu>
	    <d:dataSet id="treedata" modelName="test/tree"></d:dataSet>
		<d:tree style="width:240px;left:0px;top:0px;position:absolute;" title="树右击目录测试" bindTarget="treedata" idField="id" parentField="pid" displayField="text" height="500" >
			<d:event name="click" handle="say"></d:event>
			<d:event name="contextmenu" handle="contextmenu"></d:event>
			<d:event name="containercontextmenu" handle="rootmenu"></d:event>
		</d:tree>
		<div style="margin-left:250px;">
			<div id="menuDiv" style="margin-top:10px;background:#ee3434;width:200px;text-align:center">右击div 弹出菜单</div>
			
			<div style="margin:2 2 2 2px">
				<label>单击输入框弹出菜单：</label> <input style="width:160px;" name="value" type="text">
			</div>
			<div style="margin:2 2 2 2px">
				<label>单击输入框弹出菜单：</label> <input name="value" style="width:160px;" type="password">
			</div>
			<div style="margin:2 2 2 2px">
				<label>单击输入框弹出菜单：</label>
				<select name="h" style="width:160px;">
				  <option value="1">hello 1</option>
				  <option value="2" selected="selected">hello 2</option>
				</select>
			</div>
			
		</div>
		
		<script type="text/javascript">
			Ext.get("menuDiv").on("contextmenu",function(e){
				menu.showAt([e.getPageX(),e.getPageY()]);
				e.stopEvent();
			})
		
		</script>
	</body>
</html>
