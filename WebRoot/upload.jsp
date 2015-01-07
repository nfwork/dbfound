<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@page import="com.nfwork.dbfound.web.file.FileUploadUtil"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<style>
		input {
			BORDER-RIGHT: #999999 1px solid;
			BORDER-TOP: #999999 1px solid;
			FONT-SIZE: 12px;
			BORDER-LEFT: #999999 1px solid;
			BORDER-BOTTOM: #999999 1px solid;
			FONT-FAMILY: "Verdana", "Arial", "Helvetica", "sans-serif"
		}
	</style>
	<script type="text/javascript">
	function openFile(value, meta, record) {
		return "<a href = 'download.execute?file_id="+record.json.file_id+"'>" + value + '</a>'
	}
	function upload(){
		 target = Ext.get("file_cmp").dom;
		 if (uploadform.form.isValid()) {
			 if(checkFileSize(target)==false)return;
			 Ext.getBody().mask('正在上传附件，请耐心等待.......', 'x-mask-loading');
		     uploadform.form.submit({ 
				    url:'upload.execute!add?pk_value=${param.pk_value}&table_name=${param.table_name}', 
				    method:'post',
				    success:function(response, action){ 
		    	        Ext.getBody().unmask();
				    	fileGrid.query();
				    }, 
				    failure:function(response,action){ 
				    	Ext.getBody().unmask();
				    	alert(action.result.message +"!");
				    } 
			});	  
		 }else{
			 $D.showMessage('请选择上传文件！');
		 }
	}
	
	function checkFileSize(target) {
        var fileSize = 0;
        if (Ext.isIE && !target.files) {
        	
        } else {
            fileSize = target.files[0].size;
        }
        var maxSize=<%=FileUploadUtil.maxUploadSize%>;
        if(fileSize>maxSize*1024*1024){
            $D.showMessage("上传文件大小不能大于"+maxSize+"M!");
            return false;
        }
        return true;
    }
    
</script>

	<body>
	    <d:form id="uploadform" title="请选择文件" labelWidth="70" fileUpload="true" >
			<d:line columnWidth="0.5">
				<d:field columnWidth="0.7" name="file" id="file_cmp" anchor="90%" required="true" prompt="附 件" editor="file"></d:field>
				<d:field columnWidth="0.3" width="80" editor="button" name="querybtn" prompt="上传">
					<d:event name="click" handle="upload"></d:event>
				</d:field>
			</d:line>
	    </d:form>	

	    <d:grid id="fileGrid" title="附件列表" autoQuery="true" height="285" queryUrl="upload.query?pk_value=${param.pk_value}&table_name=${param.table_name}" model="upload">
			<d:toolBar>
			  <d:gridButton type="delete" />
			</d:toolBar>
			<d:columns>
				<d:column name="file_name" renderer="openFile" prompt="文件名"
					width="280" />
				<d:column name="file_type" prompt="文件类型" width="200" />
				<d:column name="file_size" prompt="文件大小" width="100" />
			</d:columns>
	    </d:grid>
	</body>
</html>
