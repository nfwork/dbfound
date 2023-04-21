package com.nfwork.dbfound.web.file;
import java.util.List;

import com.nfwork.dbfound.core.DBFoundConfig;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.UploadSizeException;

public class FileUploadUtil {

	public static void initFileUpload(Context context) {
		try {
			if (ServletFileUpload.isMultipartContent(context.request)) { // 创建文件处理工厂，它用于生成
				// FileItem 对象。
				DiskFileItemFactory factory = new DiskFileItemFactory(); // 设置文件的缓存路径

				factory.setSizeThreshold(1024 * 1024); // 设置最多只允许在内存中存储的数据,单位:字节

				ServletFileUpload upload = new ServletFileUpload(factory);

				String encoding = context.request.getCharacterEncoding();

				upload.setHeaderEncoding(encoding);

				// 设置允许用户上传文件大小,单位:字节
				upload.setSizeMax(1024L * 1024 * DBFoundConfig.getMaxUploadSize());

				List<FileItem> items ;

				items = upload.parseRequest(context.request);

				// 下面对每个字段进行处理，分普通字段和文件字段
				for (FileItem fileItem : items) {
					String filedName = fileItem.getFieldName();
					if (fileItem.isFormField()) {
						context.setParamData(filedName, fileItem.getString(encoding));
					} else {
						context.setParamData(filedName, fileItem);
						context.setParamData(filedName + "_name", fileItem.getName().substring(
								fileItem.getName().lastIndexOf("\\") + 1));
						context.setParamData(filedName + "_type", fileItem.getContentType());
						context.setParamData(filedName + "_size", FileSizeCalculator.getFileSize(fileItem.getSize()));
					}
				}
			}
		} catch (SizeLimitExceededException e) {
			throw new UploadSizeException("上传附件大小超过最大限制" + DBFoundConfig.getMaxUploadSize() + "M");
		} catch (Exception e) {
			throw new DBFoundPackageException("文件上传处理异常:" + e.getMessage(), e);
		}
	}
}
