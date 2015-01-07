<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function submit() {
			parent.Ext.getCmp("${param.windowId}").commit({area_name:areaTree.getSelectedText(),area_id:areaTree.getSelectedId()});
		}
		function clear(){
			parent.Ext.getCmp("${param.windowId}").commit({area_name:'',area_id:''});
		}
	</script>
	<body>
		<d:initProcedure>
			<d:dataSet id="treedata" modelName="test/tree" queryName="area" />
		</d:initProcedure>

		<d:buttonGroup>
			<d:button title="确定" click="submit"></d:button>
			<d:button title="清除" click="clear"></d:button>
		</d:buttonGroup>
		<d:tree id="areaTree" showCheckBox="true" bindTarget="treedata" idField="area_id" parentField="parent_id" displayField="area_name" width="300" height="350" >
		     <d:event name="dblclick" handle="submit"></d:event>
		</d:tree>
		<script type="text/javascript">
			areaTree.getRootNode().expandChildNodes();
		</script>
	</body>
</html>
