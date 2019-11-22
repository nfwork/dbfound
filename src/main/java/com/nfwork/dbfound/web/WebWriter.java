package com.nfwork.dbfound.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.excel.ExcelWriter;
import com.nfwork.dbfound.util.LogUtil;

public class WebWriter {
	private static String encoding = "utf-8";// 字符编码，可以从web.xml中设置

	@SuppressWarnings("unchecked")
	public static void excelWriter(Context context, List<Map> result) {
		try {
			ExcelWriter.excelExport(context, result);
		} catch (Exception e) {
			LogUtil.warn("response Excel writer exception：" + e.getMessage());
		}
	}

	public static void jsonWriter(HttpServletResponse response, String message) {
		StringReader reader = new StringReader(message);
		Writer writer = null;
		try {
			response.setContentType("text/html;charset=" + encoding);
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			writer = response.getWriter();
			char b[] = new char[1000];
			int i = reader.read(b);
			while (i != -1) {
				writer.write(b, 0, i);
				i = reader.read(b);
			}
		} catch (Exception e) {
			LogUtil.warn("response writer exception：" + e.getMessage());
		} finally {
			if (writer != null) {
				if (reader != null) {
					reader.close();
				}
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					LogUtil.error(e.getMessage(), e);
				}
			}
		}
	}

	public static String getEncoding() {
		return encoding;
	}

	public static void setEncoding(String encoding) {
		WebWriter.encoding = encoding;
	}

}