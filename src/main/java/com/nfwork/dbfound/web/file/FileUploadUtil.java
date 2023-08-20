package com.nfwork.dbfound.web.file;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.jakarta.JakartaServletDiskFileUpload;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.List;

public class FileUploadUtil {

	private static final ThreadLocal<List<DiskFileItem>> fileItemsLocal = new ThreadLocal<>();

	public static boolean isUploadRequest(Context context) {
		return JakartaServletFileUpload.isMultipartContent(context.request);
	}

	public static void initFileUpload(Context context) throws IOException {

		JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload();

		// 设置允许用户上传文件大小,单位:字节
		upload.setSizeMax(1024L * 1024 * DBFoundConfig.getMaxUploadSize());

		List<DiskFileItem> items = upload.parseRequest(context.request);
		fileItemsLocal.set(items);

		// 下面对每个字段进行处理，分普通字段和文件字段
		for (DiskFileItem fileItem : items) {
			String filedName = fileItem.getFieldName();
			if (fileItem.isFormField()) {
				context.setParamData(filedName, fileItem.getString(Charset.forName(context.request.getCharacterEncoding())));
			} else {
				FilePart filePart = new CommonFilePart(fileItem);
				Object object = context.getData("param."+filedName);
				if(object == null) {
					context.setParamData(filedName, filePart);
				}else if(object instanceof FilePart){
					List<FilePart> list = new ArrayList<>();
					list.add((FilePart) object);
					list.add(filePart);
					context.setParamData(filedName, list);
				}else if(object instanceof List){
					((List) object).add(filePart);
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
		}
		fileItemsLocal.remove();
	}
}
