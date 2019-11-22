package com.nfwork.dbfound.web.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.WebWriter;

public class FileDownloadUtil {

	public static void download(Param p, Map<String, Param> params,
			HttpServletResponse response) {
		String nameParamName = p.getFileNameParam();
		if (nameParamName == null) {
			nameParamName = p.getName() + "_name";
		}
		Param nameParam = params.get(nameParamName);
		if (nameParam == null) {
			throw new ParamNotFoundException("nameParam: " + nameParamName
					+ " 没有定义");
		}
		String filename = nameParam.getStringValue();
		if (filename == null || "".equals(filename)) {
			filename = "download.data";
		}
		if (p.getValue() == null) {
			WebWriter.jsonWriter(response, "<font color='#e51212'>系统未找到下载文件路径！<a href='JavaScript:history.back();'>返回</a>");
		    return;
		}
		
		File file = null;
		Object object = p.getValue();
		if (object instanceof File) {
			file = (File)object;
		}else {
			file = new File(FileUtil.getDownLoadFolder(p.getStringValue()));
		}
		
		if (file.exists()) {
			try {
				filename = URLEncoder.encode(filename, "utf-8");
				response.setContentType("application/x-download;");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + filename);
				ServletOutputStream sout = response.getOutputStream(); // 图片输出的输出流
				InputStream in = new FileInputStream(file);
				try {
					if (in != null) {
						byte b[] = new byte[2048];
						int i = in.read(b);
						while (i != -1) {
							sout.write(b, 0, i);
							i = in.read(b);
						}
					}
				} finally {
					if (in != null) {
						in.close();
					}
					if (sout != null) {
						sout.flush(); // 输入完毕，清除缓冲
						sout.close();
					}
				}
			} catch (Exception e) {
				LogUtil.error(e.getMessage(), e);
			} finally {
				if ("db".equals(p.getFileSaveType())
						&& p.getStringValue().endsWith(".dbf")) {
					file.delete();
				}
			}
		} else {
			WebWriter.jsonWriter(response, "文件：<font color='#e51212'>"
					+ filename + "</font>,不存在! "
					+ " <a href='JavaScript:history.back();'>返回</a>");
			LogUtil.warn("文件：" + file.getAbsolutePath() + ",不存在");
		}
	}
}
