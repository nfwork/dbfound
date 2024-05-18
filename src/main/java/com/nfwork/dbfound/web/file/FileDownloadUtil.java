package com.nfwork.dbfound.web.file;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.WebWriter;

public class FileDownloadUtil {

	public static void download(Param p, Map<String, Param> params, HttpServletResponse response) {
		String nameParamName = p.getFileNameParam();
		if (nameParamName == null) {
			nameParamName = p.getName() + "_name";
		}
		Param nameParam = params.get(nameParamName);
		if (nameParam == null) {
			throw new ParamNotFoundException("nameParam: " + nameParamName + " not defined");
		}
		String filename = nameParam.getStringValue();
		if (filename == null || filename.isEmpty()) {
			filename = "download.data";
		}
		if (p.getValue() == null) {
			String message = "param value is null, param:" + p.getName() ;
			ResponseObject responseObject = new ResponseObject();
			responseObject.setMessage(message);
			responseObject.setSuccess(false);
			WebWriter.jsonWriter(response, JsonUtil.toJson(responseObject));
			LogUtil.warn(message);
		    return;
		}
		
		File file ;
		Object object = p.getValue();
		if (object instanceof File) {
			file = (File)object;
		}else {
			String fileName = FileUtil.getDownLoadFolder(p.getStringValue());
			file = new File(fileName);
		}
		
		if (file.exists()) {
			try (InputStream in = Files.newInputStream(file.toPath());
                 OutputStream out = response.getOutputStream()) {

				filename = URLEncoder.encode(filename, DBFoundConfig.getEncoding()).replaceAll("\\+", "%20");
				response.setContentLength((int)file.length());
				response.setContentType("application/x-download");
				response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
				response.setHeader("Content-Disposition","attachment;filename=" + filename);

				byte[] b = new byte[4096];
				int i = in.read(b);
				while (i != -1) {
					out.write(b, 0, i);
					i = in.read(b);
				}
				out.flush(); // 输入完毕，清除缓冲

			} catch (Exception e) {
				LogUtil.error(e.getMessage(), e);
			} finally {
				if (file.getName().endsWith(".lob.dbf")) {
					boolean ignore = file.delete();
				}
			}
		} else {
			String message = "file：" + file.getAbsolutePath() + ", not found";
			ResponseObject responseObject = new ResponseObject();
			responseObject.setMessage(message);
			responseObject.setSuccess(false);
			WebWriter.jsonWriter(response, JsonUtil.toJson(responseObject));
			LogUtil.warn(message);
		}
	}
}
