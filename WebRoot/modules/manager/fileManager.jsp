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
	function upload(){
		 target = Ext.get("file_cmp").dom;
		 var path = uploadform.getData().path;
		 if(path.substring(path.length-1)!="/"){
			 uploadform.setData({path:path+"/"});
		 }
		 if (uploadform.form.isValid()) {
			 if(checkFileSize(target)==false)return;
			 Ext.getBody().mask('正在上传附件，请耐心等待.......', 'x-mask-loading');
		     uploadform.form.submit({ 
				    url:'manager/fileManager.execute!upload', 
				    method:'post',
				    success:function(response, action){ 
		    	        Ext.getBody().unmask();
		    	        $D.showMessage('上传文件成功！',query);
				    }, 
				    failure:function(response,action){ 
				    	Ext.getBody().unmask();
				    	$D.showMessage(action.result.message +"!");
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
        if(fileSize>10*1024*1024){
            $D.showMessage("上传文件大小不能大于"+maxSize+"M!");
            return false;
        }
        return true;
    }

	function openFile() {
		var data = fileGrid.getCurrentRecordData();
		var path = uploadform.getData().path;
		var index = path.lastIndexOf("/");
		if(index != path.length-1){
			path = path+"/";
		}
		if(data.file_type==1){
			uploadform.setData({path:path+data.file_name+"/"});
			query();
		}else{
			var parameters={};
			parameters.path = path+data.file_name;
			$D.openPostWindow("${basePath}manager/fileManager.execute!down","",Ext.encode(parameters));
			//location.href = "${basePath}manager/fileManager.execute!down?path="+encodeURIComponent(uploadform.getData().path+data.file_name);
		}
	}

	function readerFile(value, meta, record){
		var type = record.get("file_type");
		return "<a href='javascript:openFile()'>"+value+"</a>"
	}

	function query() {
		fileGrid.query();
	}

	function showImage(value){
		var image;
		if(value==1){
			image="DBFoundUI/resources/images/default/tree/folder.gif";
		}else{
			image="DBFoundUI/resources/images/default/tree/leaf.gif"
		}
		return "<img src='"+image+"'/>"
	}

	function back(){
		var path = uploadform.getData().path;
		path = path.substring(0,path.length-1);
		var index = path.lastIndexOf("/");
		if(index==-1){
			$D.showMessage("已经是顶级目录！");
		}else{
			path=path.substring(0,index+1);
			uploadform.setData({path:path});
			query();
		}
	}
</script>

	<body>
	    <d:form id="uploadform" title="请选择文件" width="700" labelWidth="70" fileUpload="true" >
	    	<d:line columnWidth="0.5">
				<d:field columnWidth="0.7" readOnly="true" name="path" value="\${@prejectRoot}/" anchor="90%" required="true" prompt="路径" editor="textfield">
					<d:event name="enter" handle="query"></d:event>
				</d:field>
			</d:line>
			<d:line columnWidth="0.5">
				<d:field columnWidth="0.7" name="file" id="file_cmp" anchor="90%" prompt="文件" editor="file"></d:field>
				<d:field columnWidth="0.3" width="80" editor="button" name="uploadbtn" prompt="上传">
					<d:event name="click" handle="upload"></d:event>
				</d:field>
			</d:line>
	    </d:form>	
	    
	   <d:grid id="fileGrid" queryForm="uploadform" selectable="true" selectFirstRow="false" navBar="false" rowNumber="true" autoQuery="true" height="$D.getFullHeight('fileGrid')-30" queryUrl="manager/fileManager.execute!listFile">
			<d:toolBar>
				<d:gridButton type="delete" afterAction="query" action="manager/fileManager.execute!delete" />
				<d:gridButton title="返回" icon="DBFoundUI/images/email.png" beforeAction="back" />
			</d:toolBar>
			<d:columns>
				<d:column align="center" renderer="showImage" sortable="true" name="file_type" prompt="类型" width="50" />
				<d:column name="file_name" sortable="true" renderer="readerFile" prompt="文件名" width="300" />
				<d:column name="last_update_time" sortable="true" prompt="修改日期" width="120" />
				<d:column name="file_size" sortable="true" prompt="文件大小" align="right" width="70" />
			</d:columns>
		</d:grid>
		
		<script type="text/javascript">
			fileGrid.getStore().sort("file_type","DESC");
		</script>
	</body>
</html>
