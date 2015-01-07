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
			$D.setFieldReadOnly("cod_f",false);
			return false;
		}

		function beforeAction(){
			if(viewForm.getData().function_id){
				Ext.getCmp("save_bt").action="sys/function.execute!update";
			}else{
				Ext.getCmp("save_bt").action="sys/function.execute!add";
			}
			return true;
		}
		
		function afterAction(res,data){
			if(data.function_id){
			   functionGrid.setCurrentRecordData(viewForm.getForm().getValues());
			}else{
			   data.function_id = res.outParam.function_id;
			   functionGrid.addLine(data,true);
			}
		}
		
	</script>
	<body>
		<d:initProcedure>
	    	<d:dataSet id="moduleStore" modelName="sys/module" queryName="combo" />
	    </d:initProcedure>
	    
		<d:panel height="400" title="功能列表">
			 <d:grid id="functionGrid" height="372" style="width:70%;position:absolute;left:0px;top:0px;margin:0px" viewForm="viewForm" model="sys/function" autoQuery="true">
				<d:toolBar>
				   <d:gridButton type="excel"></d:gridButton>
				</d:toolBar>
				<d:columns>
					<d:column name="function_code"  upper="true" required="true" prompt="功能编号" width="100" />
					<d:column name="function_des" required="true"  prompt="功能描述" width="110" />
					<d:column name="image" prompt="图标" width="100" />
					<d:column name="jsp_pager" prompt="对应jsp页面" width="160" />
					<d:column name="priority" align="right" prompt="优先级" width="60" />
					<d:column name="create_date" align="center" prompt="创建日期" width="100" />
					<d:column name="last_update_user" align="center" prompt="最后经手人" width="100" />
				</d:columns>
			</d:grid>
		
			<d:form id="viewForm" style="margin:0px;margin-left:70%;" labelWidth="90">
				<d:line columnWidth="1">
					<d:field id="cod_f" required="true" name="function_code" upper="true" anchor="95%" editor="textfield" prompt="功能编号" />
				</d:line>
				<d:line columnWidth="1">
					<d:field id="des_f" required="true"  name="function_des"  anchor="95%" editor="textfield" prompt="功能描述" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="module_des" required="true" hiddenName="function_module" options="moduleStore" valueField="module_id" displayField="module_name" anchor="95%" editor="combo" prompt="从属模块" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="jsp_pager" required="true" anchor="95%" editor="textfield" prompt="对应jsp页面" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="image" anchor="95%" editor="textfield" prompt="图标" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="c" anchor="95%" editor="textfield" prompt="预留字段一" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="b" anchor="95%" editor="textfield" prompt="预留字段二" />
				</d:line>
				<d:line columnWidth="1">
					<d:field name="a" anchor="95%" editor="textfield" prompt="预留字段三" />
				</d:line>
				<d:toolBar>
					<d:formButton action="" beforeAction="reset" title="新建" ></d:formButton>
				    <d:formButton id="save_bt" beforeAction="beforeAction" action="sys/function.execute!update" afterAction="afterAction" title="保存" icon="DBFoundUI/images/save.gif" ></d:formButton>
				</d:toolBar>
			</d:form>
		</d:panel>   
		<script type="text/javascript">
		  functionGrid.getSelectionModel().on("rowselect",function(sm, row, rec) {
			  $D.setFieldReadOnly("cod_f",true);
		  });
		</script>
	</body>
</html>
