package com.nfwork.dbfound.web;

import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.excel.ExcelWriter;
import com.nfwork.dbfound.util.LogUtil;

public final class WebWriter {

	public static void excelWriter(Context context, List<?> result) {
		try {
			ExcelWriter.excelExport(context, result);
		} catch (Exception e) {
			LogUtil.warn("response Excel writer exception: " + e.getMessage());
		}
	}

	public static void jsonWriter(HttpServletResponse response, String message) {
		response.setCharacterEncoding(DBFoundConfig.getEncoding());

		try (Writer writer = response.getWriter();
			 StringReader reader = new StringReader(message)){

			response.setContentType("application/json;charset=" + DBFoundConfig.getEncoding());
			response.setHeader("Cache-Control", "no-cache, must-revalidate");

			char[] data = new char[1024];
			int i;
			while ((i = reader.read(data))!= -1) {
				writer.write(data, 0, i);
			}
			writer.flush();
		} catch (Exception e) {
			LogUtil.warn("response writer exception: " + e.getMessage());
		}
	}

}