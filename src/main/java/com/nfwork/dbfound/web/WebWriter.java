package com.nfwork.dbfound.web;

import java.io.StringReader;
import java.io.Writer;
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
		response.setCharacterEncoding(encoding);

		try (Writer writer = response.getWriter();
			 StringReader reader = new StringReader(message)){

			response.setContentType("text/html;charset=" + encoding);
			response.setHeader("Cache-Control", "no-cache, must-revalidate");

			char[] data = new char[1024];
			int i;
			while ((i = reader.read(data))!= -1) {
				writer.write(data, 0, i);
			}
			writer.flush();
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