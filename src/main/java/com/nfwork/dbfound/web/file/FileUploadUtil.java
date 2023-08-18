package com.nfwork.dbfound.web.file;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import java.util.List;

public class FileUploadUtil {

	private static final ThreadLocal<List<DiskFileItem>> fileItemsLocal = new ThreadLocal<>();

	public static void initFileUpload(Context context) throws FileUploadException {

		if (JakartaServletFileUpload.isMultipartContent(context.request)) { // 创建文件处理工厂，它用于生成

			JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload();

			// 设置允许用户上传文件大小,单位:字节
			upload.setSizeMax(1024L * 1024 * DBFoundConfig.getMaxUploadSize());

			List<DiskFileItem> items = upload.parseRequest(context.request);
			fileItemsLocal.set(items);

			// 下面对每个字段进行处理，分普通字段和文件字段
			for (DiskFileItem fileItem : items) {
				String filedName = fileItem.getFieldName();
				if (fileItem.isFormField()) {
					context.setParamData(filedName, fileItem.getString());
				} else {
					context.setParamData(filedName, fileItem);
					context.setParamData(filedName + "_name", fileItem.getName().substring(
							fileItem.getName().lastIndexOf("\\") + 1));
					context.setParamData(filedName + "_type", fileItem.getContentType());
					context.setParamData(filedName + "_size", FileSizeCalculator.getFileSize(fileItem.getSize()));
				}
			}
		}
	}

	public static void clearFileItemLocal(){
		List<DiskFileItem> items  = fileItemsLocal.get();
		if(items != null){
			items.forEach(item ->{
				try { item.delete(); } catch (Exception ignore) {}
			});
			fileItemsLocal.remove();
		}
	}
}
