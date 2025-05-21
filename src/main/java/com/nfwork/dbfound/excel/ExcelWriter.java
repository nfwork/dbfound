package com.nfwork.dbfound.excel;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
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

	public static void prepareContext(Context context){
		context.setExport(true);
		Map<String,Object> param = context.getParamDatas();
		Object parameters = param.get("parameters");
		if (parameters instanceof Map) {
			param.putAll((Map) parameters);
			param.remove("parameters");
		}
	}

	public static List<ExcelColumn> getColumns(Context context){
		List<Map<String,Object>> columns = context.getList("param.columns");
		if(columns == null){
			throw new DBFoundRuntimeException("can not found param columns");
		}
		List<ExcelColumn> excelColumns = new ArrayList<>(columns.size());
		columns.forEach(map -> excelColumns.add(new ExcelColumn(map)));
		return excelColumns;
	}

	public static void setDefaultType(String type){
		defaultType = type;
	}

	public static String getExportType(Context context){
		return context.getString("param.export_type");
	}

	public static void setExportType(Context context,String type){
		context.setParamData("export_type",type);
	}

	public static String getExportFilename(Context context){
		return context.getString("param.export_filename");
	}

	public static void setExportFilename(Context context, String filename){
		context.setParamData("export_filename",filename);
	}

	public static void excelExport(Context context, String modelName, String queryName) throws Exception {
		prepareContext(context);
		List<ExcelColumn> columns = getColumns(context);
		context.setAutoPaging(false);
		List<?> result = ModelEngine.query(context, modelName, queryName).getDatas();
		doExport(context, result, columns);
	}

	public static void excelExport(Context context, List<?> dataList)throws Exception {
		List<ExcelColumn> columns = getColumns(context);
		doExport(context, dataList, columns);
	}

	public static void excelExport(Context context, List<?> dataList, List<ExcelColumn> columns)throws Exception {
		doExport(context, dataList, columns);
	}

	private static void doExport(Context context, List<?> result, List<ExcelColumn> columns)throws Exception {

		File file = new File(FileUtil.getUploadFolder(null), UUIDUtil.getUUID() + ".export.dbf");
		try {
			String exportType = getExportType(context);
			if(DataUtil.isNull(exportType)){
				exportType = defaultType;
			}else{
				exportType = exportType.toLowerCase();
			}

			String exportFilename = getExportFilename(context);
			if(DataUtil.isNull(exportFilename)){
				exportFilename = "export."+exportType;
			}else{
				if(!exportFilename.endsWith(exportType)){
					throw new DBFoundRuntimeException("excel export, the export_filename '"+exportFilename+"' must end with the export_type '"+exportType+"'");
				}
				exportFilename = URLEncoder.encode(exportFilename, DBFoundConfig.getEncoding()).replaceAll("\\+", "%20");
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
				context.response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
				context.response.setHeader("Content-Disposition", "attachment;filename="+exportFilename);

				byte[] b ;
				if(result.size() > 10000){
					b = new byte[10240];
				}else{
					b = new byte[4096];
				}
				int i;
				while ( (i = in.read(b)) != -1) {
					out.write(b, 0, i);
				}
				out.flush();
			}
		}finally {
			if (file.exists()) {
				boolean success = file.delete();
				if(!success){
					LogUtil.warn("file delete failed, file:"+file);
				}
			}
		}
	}
}
