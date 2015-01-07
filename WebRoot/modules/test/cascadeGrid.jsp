<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">

	    var ac_id;
	    
		var statusStore = new Ext.data.SimpleStore( {
			data : [ [ "S", "S-系统级" ],[ "F", "F-功能级" ], [ "N", "N-自由访问" ] ],
			fields : [ "status_code", "status_name" ]
		});
		
		function query() {
			acGrid.query();
		}

		function disableButton(){
			  Ext.getCmp("addBt").disable();
			  Ext.getCmp("saveBt").disable();
			  Ext.getCmp("deleteBt").disable();
			  assignGrid.getStore().removeAll();
		}
		
		function init_data(records,grid){
            for(var i=0;i<records.length;i++){
                records[i].set("ac_id",ac_id);
            }
            return true;
        }

        function commit(json){
        	queryForm.setData(json);
        }

        function rowSelected(sm, row, rec) {
		      if(rec.data.enable_flag=='F'){
		    	  Ext.getCmp("addBt").enable();
				  Ext.getCmp("saveBt").enable();
				  Ext.getCmp("deleteBt").enable();
				  ac_id=rec.json.ac_id;
				  assignGrid.getStore().baseParams["ac_id"]=ac_id;
		    	  assignGrid.query();
		      }else{
		    	  ac_id = null;
		    	  disableButton();
		      }
		  }

		  function reset(){
			  queryForm.reset();
		  }
	</script>
	<body>
	 	<d:initProcedure>
	        <d:dataSet id="functionStore" modelName="sys/function" queryName="combo" />
	        <d:dataSet id="treedata" modelName="test/tree"></d:dataSet>
	    </d:initProcedure>
	    
		<d:form id="queryForm" title="访问控制" labelWidth="120">
			<d:line columnWidth="0.5">
				<d:field name="url" anchor="85%" editor="textfield" prompt="访问路径" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="test" editor="spinnerfield" value="1" maxValue="12" minValue="1"/>
			</d:line>
			<d:line columnWidth="0.5">
				<d:field name="ae" displayField="text" parentField="pid" valueField="id" anchor="85%" editor="treecombo" options="treedata" prompt="下拉tree选择框">
				</d:field>
				<d:field name="area_name"  editor="lov" lovUrl="modules/test/treeLov.jsp" lovHeight="440" lovWidth="340" prompt="区域选择">
				   <d:event name="commit" handle="commit"></d:event>
				</d:field>
			</d:line>
		</d:form>
         <d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button id="reset" title="重置" click="reset" />
		</d:buttonGroup>
		
		<d:panel title="控制列表" height="388" >
		
			<d:grid id="acGrid" height="360"  style="width:70%;position:absolute;left:0px;margin:0px" queryForm="queryForm" singleSelect="true" model="sys/accessControl" autoQuery="true" >
				<d:toolBar>
					<d:gridButton type="add" />
					<d:gridButton type="save" />
					<d:gridButton type="delete" afterAction="disableButton" />
				</d:toolBar>
				<d:columns> 
					<d:column name="url"  required="true" editor="textfield" prompt="访问路径" width="230" />
					<d:column name="enable_flag" required="true" editor="combo" options="statusStore" displayField="status_name" valueField="status_code" prompt="作用域" width="100" />
				    <d:column name="last_update_date" align="center" prompt="最后修改时间" width="140" />
					<d:column name="last_update_user" align="center" prompt="最后经手人" width="150" />
				</d:columns>
			</d:grid>
		
			<d:grid id="assignGrid" height="360" navBar="false" style="margin:0px;margin-left:70%" queryUrl="sys/pagerAssign.query" model="sys/pagerAssign" >
				<d:toolBar>
					<d:gridButton id="addBt" disabled="true" type="add" />
					<d:gridButton id="saveBt" disabled="true" type="save" beforeAction="init_data" />
					<d:gridButton id="deleteBt" disabled="true" type="delete" />
				</d:toolBar>
				<d:columns> 
					<d:column name="function_id"  required="true" options="functionStore" editor="combo" displayField="function_des" valueField="function_id" prompt="分配功能" width="350" />
				</d:columns>
			</d:grid>

		</d:panel>
		<script type="text/javascript">
		      acGrid.getSelectionModel().on("rowselect",rowSelected);
		</script>
	</body>
</html>
