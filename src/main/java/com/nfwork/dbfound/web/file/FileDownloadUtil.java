package com.nfwork.dbfound.web.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.util.JsonUtil;
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
			throw new ParamNotFoundException("nameParam: " + nameParamName + " not defined");
		}
		String filename = nameParam.getStringValue();
		if (filename == null || "".equals(filename)) {
			filename = "download.data";
		}
		if (p.getValue() == null) {
			String message = "param value is null, param:" + p.getName() ;
			ResponseObject responseObject = new ResponseObject();
			responseObject.setMessage(message);
			responseObject.setSuccess(false);
			WebWriter.jsonWriter(response, JsonUtil.beanToJson(responseObject));
			LogUtil.warn(message);
		    return;
		}
		
		File file ;
		Object object = p.getValue();
		if (object instanceof File) {
			file = (File)object;
		}else {
			file = new File(Objects.requireNonNull(FileUtil.getDownLoadFolder(p.getStringValue())));
		}
		
		if (file.exists()) {
			try {
				filename = URLEncoder.encode(filename, "utf-8");
				response.setContentType("application/x-download");
				response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setHeader("Content-Disposition","attachment;filename=" + filename);
				ServletOutputStream sout = response.getOutputStream(); // 图片输出的输出流
				try (InputStream in = new FileInputStream(file)) {
					byte[] b = new byte[4096];
					int i = in.read(b);
					while (i != -1) {
						sout.write(b, 0, i);
						i = in.read(b);
					}
				} finally {
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
			String message = "file：" + file.getAbsolutePath() + ", not found";
			ResponseObject responseObject = new ResponseObject();
			responseObject.setMessage(message);
			responseObject.setSuccess(false);
			WebWriter.jsonWriter(response, JsonUtil.beanToJson(responseObject));
			LogUtil.warn(message);
		}
	}
}
