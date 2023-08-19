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

	public static List<List<Map>> readExcel(FilePart filePart) {
		try (InputStream stream = filePart.inputStream()) {
			return readExcel(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("excel reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map>> readExcel(InputStream input) throws BiffException, IOException {
		List<List<Map>> excelData = new ArrayList<List<Map>>();

		Workbook wb = Workbook.getWorkbook(input);
		Sheet sheet[] = wb.getSheets();
		for (Sheet s : sheet) {
			int rowSize = s.getRows();
			int colSize;
			List<Map> datas = new ArrayList<Map>();
			if (rowSize > 0) {
				// 取第一行数据 为元数据
				Cell[] metaCells = s.getRow(0);
				colSize = metaCells.length;
				String[] metaData = new String[colSize];
				for (int j = 0; j < colSize; j++) {
					metaData[j] = metaCells[j].getContents().trim();
				}

				for (int i = 1; i < rowSize; i++) {
					Map<String, Object> data = new HashMap<String, Object>();
					Cell[] dataCell = s.getRow(i);
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
			excelData.add(datas);
		}
		return excelData;
	}
}
