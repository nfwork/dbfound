package com.nfwork.dbfound.web.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;

import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.ParamsAware;
import com.nfwork.dbfound.model.bean.Param;

public class FileUtil implements ParamsAware {

	private static String baseFolder = null;

	Map<String, Param> params;

	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMM");

	public static String getDownLoadFolder(String value) {
		if (value == null) {
			return null;
		} else if (!value.startsWith("/") && value.indexOf(":") == -1) {
			return baseFolder + "/" + value;
		} else {
			return value;
		}
	}

	public static void init(FilterConfig cf) {
		baseFolder = cf.getInitParameter("uploadFolder");
		if (baseFolder == null) {
			return;
		}
		if (!baseFolder.startsWith("/") && baseFolder.indexOf(":") == -1) {
			baseFolder = cf.getServletContext().getRealPath("/" + baseFolder);
		}
	}

	public static void init(ServletConfig cf) {
		baseFolder = cf.getInitParameter("uploadFolder");
		if (baseFolder == null) {
			return;
		}
		if (!baseFolder.startsWith("/") && baseFolder.indexOf(":") == -1) {
			baseFolder = cf.getServletContext().getRealPath("/" + baseFolder);
		}
	}
	
	public static void init(String fold) {
		baseFolder = fold;
	}

	public static synchronized File getUploadFolder(String foldName) {
		if (baseFolder == null) {
			baseFolder = System.getProperty("java.io.tmpdir");
		}
		if (foldName == null) {
			foldName = baseFolder;
		}else {
			foldName = baseFolder + "/" + foldName;
		}
		File fold = new File(foldName);
		if (!fold.exists()) {
			fold.mkdirs();
		}
		return fold;
	}

	public static String getUploadFolderName() {
		return format.format(new Date());
	}

	public void delete() {
		Param param = params.get("file_disk_name");
		if (param == null) {
			throw new ParamNotFoundException(
					"param:file_disk_name没有定义,使用该方法必须定义file_disk_name来获取文件的硬盘存储名称！");
		}
		String fileDiskName = param.getStringValue();
		File file = new File(baseFolder + "/" + fileDiskName);
		if (file.exists()) {
			file.delete();
		}
	}

	public void setParams(Map<String, Param> params) {
		this.params = params;
	}
}
