<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">

		function reset() {
			viewForm.reset();
			viewForm.isNew = true;
			$D.setFieldReadOnly("cod_f",false);
			return false;
		}

		function beforeAction(){
			if(viewForm.isNew){
				Ext.getCmp("save_bt").action="sys/function.execute!add";
			}else{
				Ext.getCmp("save_bt").action="sys/function.execute!update";
			}
			return true;
		}
		
		function afterAction(res,data){
			if(viewForm.isNew){
				reset();
			}else{
				functionGrid.setCurrentRecordData(viewForm.getForm().getValues());
			}
			return true;
		}

		function query() {
			functionGrid.query();
		}
		function formReset() {
			queryForm.reset();
		}
	</script>
	<body>
	    <d:initProcedure>
	    	<d:dataSet id="moduleStore" modelName="sys/module" queryName="combo" />
	    </d:initProcedure>
	    
	    <d:form id="queryForm" title="功能管理" labelWidth="80">
			<d:line columnWidth="0.33">
				<d:field name="function_code" upper="true" anchor="85%" editor="textfield" prompt="功能编号" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="function_des"  anchor="85%" editor="textfield" prompt="功能描述" >
				   <d:event name="enter" handle="query"/>
				</d:field>
				<d:field name="module_id" options="moduleStore" valueField="module_id" displayField="module_name" anchor="85%" editor="lovcombo" prompt="从属模块" />
			</d:line>
			<d:line columnWidth="0.33">
				<d:field name="timefrom" anchor="85%" editor="datefield" prompt="创建日期从" />
				<d:field name="timeto" anchor="85%" editor="datefield" prompt="创建日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="formReset" />
		</d:buttonGroup>
		
	    <d:grid id="functionGrid" title="功能列表" viewForm="viewForm" queryForm="queryForm" model="sys/function" autoQuery="true" height="$D.getFullHeight('functionGrid')-150">
			<d:toolBar>
			   <d:gridButton type="excel"></d:gridButton>
			</d:toolBar>
			<d:columns>
				<d:column name="function_code"  upper="true" required="true" prompt="功能编号" width="100" />
				<d:column name="function_des" required="true"  prompt="功能描述" width="120" />
				<d:column name="image" prompt="图标" width="100" />
				<d:column name="jsp_pager" prompt="对应jsp页面" width="160" />
				<d:column name="priority" align="right" prompt="优先级" width="60" />
				<d:column name="create_date" align="center" prompt="创建日期" width="100" />
				<d:column name="last_update_user" align="center" prompt="最后经手人" width="100" />
			</d:columns>
		</d:grid>
	    
	    <d:tabs>
	       <d:tab title="信息修改">
	          <d:form id="viewForm" labelWidth="100">
				<d:line columnWidth="0.33">
					<d:field id="cod_f" required="true" name="function_code" upper="true" anchor="85%" editor="textfield" prompt="功能编号" />
					<d:field id="des_f" required="true"  name="function_des"  anchor="85%" editor="textfield" prompt="功能描述" />
					<d:field name="module_des" required="true" hiddenName="function_module" options="moduleStore" valueField="module_id" displayField="module_name" anchor="85%" editor="combo" prompt="从属模块" />
				</d:line>
				<d:line columnWidth="0.33">
					<d:field name="jsp_pager" required="true" anchor="85%" editor="textfield" prompt="对应jsp页面" />
					<d:field name="image" anchor="85%" editor="textfield" prompt="图标" />
				</d:line>
				<d:toolBar>
					<d:formButton action="" beforeAction="reset" title="新建" ></d:formButton>
				    <d:formButton id="save_bt" beforeAction="beforeAction" action="sys/function.execute!update" afterAction="afterAction" title="保存" icon="DBFoundUI/images/save.gif" ></d:formButton>
				</d:toolBar>
			</d:form>
	       </d:tab>
	       <d:tab id="userDefine" title="其它信息" ></d:tab>
	    </d:tabs>
	    
		<script type="text/javascript">
		  functionGrid.getSelectionModel().on("rowselect",function(sm, row, rec) {
			  $D.setFieldReadOnly("cod_f",true);
			  viewForm.isNew = false;
		  });
		  viewForm.isNew = true;
		</script>
	</body>
</html>
