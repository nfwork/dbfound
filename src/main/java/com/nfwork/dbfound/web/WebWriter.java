package com.nfwork.dbfound.web;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.excel.ExcelWriter;
import com.nfwork.dbfound.util.LogUtil;

public class WebWriter {
	private static String encoding = "UTF-8";

	public static void excelWriter(Context context, List<Map> result) {
		try {
			ExcelWriter.excelExport(context, result);
		} catch (Exception e) {
			LogUtil.warn("response Excel writer exception：" + e.getMessage());
		}
	}

	public static void jsonWriter(HttpServletResponse response, String message) {
		try(OutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/json;charset=" + encoding);
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			byte[] b = message.getBytes(encoding);
			outputStream.write(b, 0, b.length);
			outputStream.flush();
		} catch (Exception e) {
			LogUtil.warn("response writer exception：" + e.getMessage());
		}
	}

	public static String getEncoding() {
		return encoding;
	}

	public static void setEncoding(String encoding) {
		WebWriter.encoding = encoding.toUpperCase();
	}

}