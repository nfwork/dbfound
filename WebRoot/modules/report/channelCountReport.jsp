<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>
<%@ taglib uri="report-functions" prefix="f"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<script type="text/javascript">
		function query() {
			listGrid.query();
		}
		function formReset() {
			queryForm.reset();
			setDefaultValue();
		}
		function setDefaultValue(){
			var json ={};
			json.timefrom = "${f:getData(-9)}";
			json.timeto = "${f:getData(0)}";
			queryForm.setData(json);
		}
	</script>
	<body>
	    <d:form id="queryForm" title="查询条件" labelWidth="80">
			<d:line columnWidth="0.25">
				<d:field name="timefrom" anchor="95%" editor="datefield" prompt="费用日期从" />
				<d:field name="timeto" anchor="95%" editor="datefield" prompt="费用日期到" />
			</d:line>
		</d:form>
		
		<d:buttonGroup>
			<d:button id="query" title="查询" click="query" />
			<d:button title="重置" click="formReset" />
		</d:buttonGroup>
		
	    <d:grid id="listGrid" title="GO桌面用户统计" queryForm="queryForm" forceFit="false" queryUrl="report/channelCountReport.execute" rowNumber="false" navBar="false" selectable="false" autoQuery="true" height="$D.getFullHeight('listGrid')">
			<d:columns>
				<d:column name="c" sortable="true" prompt="国家" width="100" />
				<d:column align="right" name="c1" sortable="true" hidden="true" prompt="c1" width="100" />
				<d:column align="right" name="c2" sortable="true" hidden="true" prompt="c2" width="100" />
				<d:column align="right" name="c3" sortable="true" hidden="true" prompt="c3" width="100" />
				<d:column align="right" name="c4" sortable="true" hidden="true" prompt="c4" width="100" />
				<d:column align="right" name="c5" sortable="true" hidden="true" prompt="c5" width="100" />
				<d:column align="right" name="c6" sortable="true" hidden="true" prompt="c6" width="100" />
				<d:column align="right" name="c7" sortable="true" hidden="true" prompt="c7" width="100" />
				<d:column align="right" name="c8" sortable="true" hidden="true" prompt="c8" width="100" />
				<d:column align="right" name="c9" sortable="true" hidden="true" prompt="c9" width="100" />
				<d:column align="right" name="c10" sortable="true" hidden="true" prompt="c10" width="100" />
				<d:column align="right" name="c11" sortable="true" hidden="true" prompt="c11" width="100" />
				<d:column align="right" name="c12" sortable="true" hidden="true" prompt="c12" width="100" />
				<d:column align="right" name="c13" sortable="true" hidden="true" prompt="c13" width="100" />
				<d:column align="right" name="c14" sortable="true" hidden="true" prompt="c14" width="100" />
				<d:column align="right" name="c15" sortable="true" hidden="true" prompt="c15" width="100" />
				<d:column align="right" name="c16" sortable="true" hidden="true" prompt="c16" width="100" />
				<d:column align="right" name="c17" sortable="true" hidden="true" prompt="c17" width="100" />
				<d:column align="right" name="c18" sortable="true" hidden="true" prompt="c18" width="100" />
				<d:column align="right" name="c19" sortable="true" hidden="true" prompt="c19" width="100" />
				<d:column align="right" name="c20" sortable="true" hidden="true" prompt="c20" width="100" />
				<d:column align="right" name="c21" sortable="true" hidden="true" prompt="c21" width="100" />
				<d:column align="right" name="c22" sortable="true" hidden="true" prompt="c22" width="100" />
				<d:column align="right" name="c23" sortable="true" hidden="true" prompt="c23" width="100" />
				<d:column align="right" name="c24" sortable="true" hidden="true" prompt="c24" width="100" />
				<d:column align="right" name="c25" sortable="true" hidden="true" prompt="c25" width="100" />
				<d:column align="right" name="c26" sortable="true" hidden="true" prompt="c26" width="100" />
				<d:column align="right" name="c27" sortable="true" hidden="true" prompt="c27" width="100" />
				<d:column align="right" name="c28" sortable="true" hidden="true" prompt="c28" width="100" />
				<d:column align="right" name="c29" sortable="true" hidden="true" prompt="c29" width="100" />
				<d:column align="right" name="c30" sortable="true" hidden="true" prompt="c30" width="100" />
			</d:columns>
		</d:grid>
		
		<%--后面代码 无须修改 --%>
	    <script type="text/javascript">

	    	<%--初始化查询表单默认值--%>
	    	setDefaultValue();
	    	
	    	listGrid.getStore().on("load",function(store){
		    	
	    		var columns = store.reader.meta.reader.jsonData.columns;
    		 	var cm = listGrid.getColumnModel();
			   
    		 	<%--先隐藏所以的列--%>
			    var length= cm.columns.length;
				for(var i=0;i<length;i++){
					var index = cm.getDataIndex(i);
					if(index!=""&&index!="c"){
						cm.setHidden(i,true);
					}
				}

				<%--显示查询的新列--%>
			    for(var i=0;i<columns.length;i++){
				    var column = columns[i];
			    	var index = cm.findColumnIndex(column.jsName);
				    cm.setColumnHeader(index,column.javaName);
				    cm.setHidden(index,false);
			    }

			    <%--添加合计行--%>
			    var datas = store.reader.meta.reader.jsonData.datas;
				var json={c:'合计'};
				for(var i=0;i<datas.length;i++){
					for(var j=0;j<columns.length;j++){
						var column = columns[j];
						var c = json[column.jsName];
						if(c==null){
							c=0;
							json[column.jsName]=c;
						}
						var cc= datas[i][column.jsName];
						if(cc>0){
							json[column.jsName] = c+cc;
						}
					}
				}
				listGrid.addLine(json,true);
			});
		</script>
	</body>
</html>
