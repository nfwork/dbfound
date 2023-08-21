package com.nfwork.dbfound.excel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.core.DBFoundConfig;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.web.file.FilePart;
import jxl.*;
import jxl.read.biff.BiffException;

public class ExcelReader {

	public static List<List<Map<String,Object>>> readExcel(FilePart filePart) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcel(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(byte[] bytes) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcel(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readExcel(InputStream input) throws BiffException, IOException {
		Workbook wb = Workbook.getWorkbook(input);
		Sheet[] sheets = wb.getSheets();
		List<List<Map<String,Object>>> list = new ArrayList<>(sheets.length);
		for (Sheet sheet : sheets) {
			list.add(readSheet(sheet));
		}
		return list;
	}

	public static Map<String, List<Map<String,Object>>> readExcelForMap(FilePart filePart) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcelForMap(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(byte[] bytes) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readExcelForMap(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static Map<String,List<Map<String,Object>>> readExcelForMap(InputStream input) throws BiffException, IOException {
		Workbook wb = Workbook.getWorkbook(input);
		Sheet[] sheets = wb.getSheets();
		Map<String, List<Map<String,Object>>> map = new HashMap<>();
		for (Sheet sheet : sheets) {
			map.put(sheet.getName(), readSheet(sheet));
		}
		return map;
	}

	private static List<Map<String,Object>> readSheet(Sheet sheet){
		int rowSize = sheet.getRows();
		int colSize;
		List<Map<String,Object>> datas = new ArrayList<>();
		if (rowSize > 0) {
			// 取第一行数据 为元数据
			Cell[] metaCells = sheet.getRow(0);
			colSize = metaCells.length;
			String[] metaData = new String[colSize];
			for (int j = 0; j < colSize; j++) {
				metaData[j] = metaCells[j].getContents().trim();
			}

			for (int i = 1; i < rowSize; i++) {
				Map<String, Object> data = new HashMap<>();
				Cell[] dataCell = sheet.getRow(i);
				int rowColSize = dataCell.length;
				for (int j = 0; j < colSize; j++) {
					if (j >= rowColSize) {
						data.put(metaData[j], null);
					} else if (dataCell[j].getType() == CellType.DATE) {
						DateCell dateCell = (DateCell) dataCell[j];
						data.put(metaData[j], new SimpleDateFormat(DBFoundConfig.getDateFormat()).format(dateCell.getDate()));
					} else if (dataCell[j].getType() == CellType.NUMBER) {
						NumberCell numberCell = (NumberCell) dataCell[j];
						data.put(metaData[j], numberCell.getValue());
					} else {
						data.put(metaData[j], dataCell[j].getContents());
					}
				}
				datas.add(data);
			}
		}
		return datas;
	}
}
