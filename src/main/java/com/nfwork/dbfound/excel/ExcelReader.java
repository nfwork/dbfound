package com.nfwork.dbfound.excel;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.web.file.FilePart;

public class ExcelReader {

	static Map<String,ReaderResolver> readerResolverMap = new ConcurrentHashMap<>();

	public static final String DEFAULT_TYPE = "_default";

	static {
		new XlsReaderResolver().register("xls");
		new XlsxReaderResolver().register("xlsx");
		new CsvReaderResolver().register("csv");
		new DefaultReaderResolver().register(DEFAULT_TYPE);
	}

	private static ReaderResolver getReaderResolver(String type){
		type = type.toLowerCase();
		ReaderResolver readerResolver = readerResolverMap.get(type);
		if (readerResolver == null){
			throw new DBFoundRuntimeException("type '"+ type+"' is not support");
		}
		return readerResolver;
	}

	public static String getType(FilePart filePart){
		String name = filePart.getName();
		int index = name.lastIndexOf(".");
		if(index > -1) {
			return name.substring(index + 1);
		}else{
			return DEFAULT_TYPE;
		}
	}

	public static List<ExcelColumn> getColumns(Context context){
		Object object = context.getData("param.columns");
		if(object instanceof List){
			List<ExcelColumn> columns = new ArrayList<>();
			List list = (List) object;
			for(Object o : list){
				if(o instanceof Map){
					Map map = (Map) o;
					if(map.containsKey("name")){
						String name = DataUtil.stringValue(map.get("name"));
						String tittle = DataUtil.stringValue(map.get("title"));
						ExcelColumn excelColumn = new ExcelColumn(name,tittle);
						Object mapper = map.get("mapper");
						if (mapper instanceof Map){
							Map mapperMap = (Map) mapper;
							excelColumn.setMapper(mapperMap);
						}
						columns.add(excelColumn);
					}
				}
			}
			return columns;
		}
		return null;
	}

	public static List<List<Map<String,Object>>> readExcel(FilePart filePart) {
		return readExcel(filePart, null);
	}

	public static List<List<Map<String,Object>>> readExcel(FilePart filePart, List<ExcelColumn> columns) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcel(stream, getType(filePart),columns);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(byte[] bytes, String type) {
		return readExcel(bytes, type, null);
	}

	public static List<List<Map<String,Object>>> readExcel(byte[] bytes, String type, List<ExcelColumn> columns) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcel(stream,type,columns);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(InputStream input, String type) {
		return readExcel(input,type,null);
	}

	public static List<List<Map<String,Object>>> readExcel(InputStream input, String type, List<ExcelColumn> columns) {
		ReaderResolver readerResolver = getReaderResolver(type);
		return readerResolver.read(input,columns);
	}

	public static Map<String, List<Map<String,Object>>> readExcelForMap(FilePart filePart) {
		return readExcelForMap(filePart, null);
	}

	public static Map<String, List<Map<String,Object>>> readExcelForMap(FilePart filePart, List<ExcelColumn> columns) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcelForMap(stream, getType(filePart),columns);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(byte[] bytes, String type) {
		return readExcelForMap(bytes,type,null);
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(byte[] bytes, String type, List<ExcelColumn> columns) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcelForMap(stream, type, columns);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(InputStream input, String type){
		return readExcelForMap(input,type,null);
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(InputStream input, String type, List<ExcelColumn> columns){
		ReaderResolver readerResolver = getReaderResolver(type);
		return readerResolver.readForMap(input,columns);
	}
}
