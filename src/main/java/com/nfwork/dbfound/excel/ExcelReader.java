package com.nfwork.dbfound.excel;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.file.FilePart;

public class ExcelReader {

	static Map<String,ReaderResolver> readerResolverMap = new ConcurrentHashMap<>();

	static {
		new XlsReaderResolver().register("xls");
		new CsvReaderResolver().register("csv");
	}

	public static ReaderResolver getReaderResolver(String type){
		ReaderResolver readerResolver = readerResolverMap.get(type);
		if (readerResolver == null){
			throw new DBFoundRuntimeException("type '"+ type+"' is not support");
		}
		return readerResolver;
	}

	public static String getType(FilePart filePart){
		String name = filePart.getName();
		int index = name.lastIndexOf(".");
		return name.substring(index+1);
	}

	public static List<List<Map<String,Object>>> readExcel(FilePart filePart) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcel(stream, getType(filePart));
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(byte[] bytes, String type) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcel(stream,type);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(InputStream input, String type) {
		ReaderResolver readerResolver = getReaderResolver(type);
		return readerResolver.read(input);
	}

	public static Map<String, List<Map<String,Object>>> readExcelForMap(FilePart filePart) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcelForMap(stream, getType(filePart));
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(byte[] bytes, String type) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcelForMap(stream, type);
		} catch (IOException e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(InputStream input,String type){
		ReaderResolver readerResolver = getReaderResolver(type);
		return readerResolver.readForMap(input);
	}
}
