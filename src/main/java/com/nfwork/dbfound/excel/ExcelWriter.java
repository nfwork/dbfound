package com.nfwork.dbfound.excel;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
		Map<String,Object> param = context.getParamDatas();
		Object parameters = param.get("parameters");
		if (parameters instanceof Map) {
			param.putAll((Map) parameters);
			param.remove("parameters");
		}

		List<ExcelColumn> columns = getColumns(context);
		List result = ModelEngine.query(context, modelName, queryName,false).getDatas();
		String exportType = context.getString("param.export_type");

		doExport(context, result, columns, exportType);
	}

	public static void excelExport(Context context, List result)throws Exception {
		List<ExcelColumn> columns = getColumns(context);
		String exportType = context.getString("param.export_type");
		doExport(context, result, columns, exportType);
	}

	private static List<ExcelColumn> getColumns(Context context){
		List<Map> columns = context.getData("param.columns",List.class);
		if(columns == null){
			throw new DBFoundRuntimeException("can not found param columns");
		}
		List<ExcelColumn> excelColumns = new ArrayList<>(columns.size());
		columns.forEach(map -> {
			excelColumns.add(new ExcelColumn(map));
		});
		return excelColumns;
	}

	private static void doExport(Context context, List result, List<ExcelColumn> columns, String exportType)throws Exception {

		File file = new File(FileUtil.getUploadFolder(null), UUIDUtil.getUUID() + "."+exportType);
		try {
			if(DataUtil.isNull(exportType)){
				exportType = defaultType;
			}else{
				exportType = exportType.toLowerCase();
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
