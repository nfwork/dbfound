package com.nfwork.dbfound.web.file;
import java.io.IOException;
import java.util.List;

import com.nfwork.dbfound.core.DBFoundConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.nfwork.dbfound.core.Context;

public class FileUploadUtil {

	private static final ThreadLocal<List<FileItem>> fileItemsLocal = new ThreadLocal<>();

	public static boolean isUploadRequest(Context context) {
		return ServletFileUpload.isMultipartContent(context.request);
	}

	public static void initFileUpload(Context context) throws IOException, FileUploadException {
		// FileItem 对象。
		DiskFileItemFactory factory = new DiskFileItemFactory(); // 设置文件的缓存路径

		factory.setSizeThreshold(5 * 1024 * 1024); // 设置最多只允许在内存中存储的数据,单位:字节

		ServletFileUpload upload = new ServletFileUpload(factory);

		String encoding = context.request.getCharacterEncoding();

		upload.setHeaderEncoding(encoding);

		// 设置允许用户上传文件大小,单位:字节
		upload.setSizeMax(1024L * 1024 * DBFoundConfig.getMaxUploadSize());

		List<FileItem> items ;

		items = upload.parseRequest(context.request);
		fileItemsLocal.set(items);

		// 下面对每个字段进行处理，分普通字段和文件字段
		for (FileItem fileItem : items) {
			String filedName = fileItem.getFieldName();
			if (fileItem.isFormField()) {
				context.setParamData(filedName, fileItem.getString(encoding));
			} else {
				FilePart filePart = new CommonFilePart(fileItem);
				context.setParamData(filedName, filePart);
			}
		}
	}

	public static void clearFileItemLocal(){
		List<FileItem> items  = fileItemsLocal.get();
		if(items != null){
			items.forEach(item ->{
				try { item.delete(); } catch (Exception ignore) {}
			});
		}
		fileItemsLocal.remove();
	}
}
