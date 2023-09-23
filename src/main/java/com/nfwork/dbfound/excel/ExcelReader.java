package com.nfwork.dbfound.excel;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import com.nfwork.dbfound.core.DBFoundConfig;

import com.nfwork.dbfound.csv.CSVFormat;
import com.nfwork.dbfound.csv.CSVParser;
import com.nfwork.dbfound.csv.CSVRecord;
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

			SimpleDateFormat dateFormat = new SimpleDateFormat(DBFoundConfig.getDateFormat());
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat datetimeFormat = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
			datetimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat timeFormat = new SimpleDateFormat(DBFoundConfig.getTimeFormat());
			timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

			for (int i = 1; i < rowSize; i++) {
				Map<String, Object> data = new HashMap<>();
				Cell[] dataCell = sheet.getRow(i);
				int rowColSize = dataCell.length;
				for (int j = 0; j < colSize; j++) {
					if (j >= rowColSize) {
						data.put(metaData[j], null);
					}else if(dataCell[j] == null){
						data.put(metaData[j], null);
					}else if (dataCell[j].getType() == CellType.DATE) {
						DateCell dateCell = ((DateCell) dataCell[j]);
						Date date = dateCell.getDate();
						if(date == null){
							data.put(metaData[j], null);
							continue;
						}
						if(dateCell.isTime()){
							data.put(metaData[j], timeFormat.format(date));
							continue;
						}
						calendar.setTime(date);
						if(calendar.get(Calendar.HOUR_OF_DAY) ==0 && calendar.get(Calendar.MINUTE) ==0 && calendar.get(Calendar.SECOND) ==0){
							data.put(metaData[j], dateFormat.format(date));
						}else{
							data.put(metaData[j], datetimeFormat.format(date));
						}
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

	public static List<List<Map<String,Object>>> readCsv(byte[] bytes) {
		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			return readCsv(stream);
		} catch (Exception e) {
			throw new DBFoundPackageException("csv reader exception:" + e.getMessage(),e);
		}
	}

	public static List<List<Map<String,Object>>> readCsv(InputStream input) throws IOException {
		CSVFormat format = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build();
		try(CSVParser parse = format.parse(new InputStreamReader(input, Charset.forName(DBFoundConfig.getEncoding())))) {
			Iterator<CSVRecord> csvRecordIterator = parse.iterator();
			List<Map<String, Object>> list = new ArrayList<>();
			List<String> headers = parse.getHeaderNames();

			while (csvRecordIterator.hasNext()) {
				CSVRecord record = csvRecordIterator.next();
				Map<String, Object> map = new HashMap<>();
				for (int i=0; i < headers.size();i++) {
					map.put(headers.get(i), record.get(i));
				}
				list.add(map);
			}
			List<List<Map<String,Object>>> result = new ArrayList<>(1);
			result.add(list);
			return result;
		}
	}
}
