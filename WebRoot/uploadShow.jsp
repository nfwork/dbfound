<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>

	<script type="text/javascript">
	function openFile(value, meta, record) {
		return "<a href = 'download.execute?file_id="+record.json.file_id+"'>" + value + '</a>'
	}
    </script>
	<body style="overflow:hidden">
		<d:grid title="附件查看" selectable="false" autoQuery="true" height="310" queryUrl="upload.query?pk_value=${param.pk_value}&table_name=${param.table_name}" model="upload">
			<d:columns>
				<d:column name="file_name" renderer="openFile" prompt="文件名"
					width="280" />
				<d:column name="file_type" prompt="文件类型" width="200" />
				<d:column name="file_size" prompt="文件大小" width="100" />
			</d:columns>
		</d:grid>
	</body>
</html>
