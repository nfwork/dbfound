package com.nfwork.dbfound.excel;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;


public class ExcelWriter {

	static Map<String,WriterResolver> writerResolverMap = new ConcurrentHashMap<>();

	static String defaultType = "xls";

	static {
		new XlsWriterResolver().register("xls", false);
		new CsvWriterResolver().register("csv", false);
		new XlsxWriterResolver().register("xlsx",true);
	}

	public static void excelExport(Context context, String modelName, String queryName) throws Exception {

		context.setExport(true);

		// 将parameters中的参数转移到param中
		Map param = (Map) context.getData("param");
		if(param!=null) {
			Object parameters = param.get("parameters");
			if (parameters instanceof Map) {
				param.putAll((Map) parameters);
				param.remove("parameters");
			}
		}

		List<Map> columns = context.getData("param.columns",List.class);
		if(columns == null){
			throw new DBFoundRuntimeException("can not found param columns");
		}

		List result = ModelEngine.query(context, modelName, queryName,false).getDatas();

		String exportType = context.getString("param.export_type");

		doExport(context, result, columns, exportType);
	}

	public static void excelExport(Context context, List result)throws Exception {
		List<Map> columns = context.getData("param.columns",List.class);
		if(columns == null){
			throw new DBFoundRuntimeException("can not found param columns");
		}
		String exportType = context.getString("param.export_type");
		doExport(context, result, columns, exportType);
	}

	private static void doExport(Context context, List result, List columns, String exportType)throws Exception {

		File file = new File(FileUtil.getUploadFolder(null), UUIDUtil.getUUID() + "."+exportType);
		try {
			if(DataUtil.isNull(exportType)){
				exportType = defaultType;
			}
			WriterResolver writerResolver = writerResolverMap.get(exportType);
			if(writerResolver == null){
				throw new DBFoundRuntimeException("excel export type '" + exportType +"' is not support");
			}
			writerResolver.writer(file,result,columns);

			try (InputStream in = new FileInputStream(file);
				 OutputStream out = context.response.getOutputStream()) {

				// 向外输出excel
				context.response.setContentLength((int)file.length());
				context.response.setContentType("application/x-download");
				context.response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				context.response.setHeader("Content-Disposition", "attachment;filename=export."+exportType);

				byte[] b ;
				if(result.size() > 10000){
					b = new byte[10240];
				}else{
					b = new byte[4096];
				}
				int i = -1;
				while ( (i = in.read(b)) != -1) {
					out.write(b, 0, i);
				}
				out.flush();
			}
		}finally {
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
