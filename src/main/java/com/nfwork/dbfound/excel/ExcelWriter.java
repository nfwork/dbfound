package com.nfwork.dbfound.excel;

import java.io.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LocalDateUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

import jxl.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.Number;

public class ExcelWriter {

	private static void writeExcel(File file, List dataList, Context context) throws Exception {

		WorkbookSettings wbSetting = new WorkbookSettings();
		if(dataList.size()> 50000) {
			wbSetting.setUseTemporaryFileDuringWrite(true);
		}
		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(file,wbSetting);

		try {
			WritableCellFormat dateFormat = null;
			WritableCellFormat dateTimeFormat = null;

			int length = dataList.size();
			int sheet = 0;
			int sheetSize = 50000;

			// columns处理
			List<Map> cls = (List) context.getData("param.columns");
			if(cls == null){
				throw new DBFoundRuntimeException("can not found param columns");
			}
			ExcelCellMeta[] columns = new ExcelCellMeta[cls.size()];
			int colIndex = 0;
			for (Map map : cls) {
				ExcelCellMeta cellMeta = new ExcelCellMeta(map.get("name").toString(), map.get("content").toString(),
						Integer.parseInt(map.get("width").toString()));
				columns[colIndex++] = cellMeta;
			}

			//mappers处理
			Map<String,Map<String,Object>> mappers = new HashMap<String, Map<String,Object>>();
			for (Map col : cls){
				Object object =col.get("mapper");
				if (DataUtil.isNotNull(object)){
					Map mapper = (Map)object;
					Map<String,Object> newMapper  = new HashMap();

					for (Object key: mapper.keySet()){
						newMapper.put(key.toString(),mapper.get(key));
					}
					mappers.put(col.get("name").toString(),newMapper);
				}
			}

			do {
				int end = sheetSize;
				if(end >= dataList.size()){
					end = dataList.size();
				}

				List datas = dataList.subList(0, end);

				jxl.write.WritableSheet ws = wwb.createSheet("sheet" + (sheet+1), sheet);
				sheet ++;

				jxl.write.WritableFont wfc = new jxl.write.WritableFont(
						WritableFont.ARIAL, 11, WritableFont.BOLD, false,
						UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.GREEN);

				jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(wfc);
				wcfFC.setBackground(Colour.GRAY_25);
				wcfFC.setAlignment(Alignment.CENTRE);

				for (int i = 0; i < columns.length; i++) {
					jxl.write.Label label = new jxl.write.Label(i, 0, columns[i].getContent(), wcfFC);
					ws.addCell(label);
					ws.setColumnView(i, columns[i].getWidth());
				}
				int index = 1;
				for (Object data : datas) {
					for (int i = 0; i < columns.length; i++) {
						String property = columns[i].getName();
						Object o = DBFoundEL.getDataByProperty(property,data);
						if (o == null) {
							continue;
						}

						//mapper映射处理
						Map<String,Object> mapper = mappers.get(property);
						if(mapper != null){
							o = getMapperValue(o.toString(),mapper);
						}

						if (o instanceof String) {
							String content = o.toString();
							Label label = new Label(i, index, content);
							ws.addCell(label);
						} else if (o instanceof Integer) {
							Number number = new Number(i, index, (Integer) o);
							ws.addCell(number);
						} else if (o instanceof Double) {
							Number number = new Number(i, index, (Double) o);
							ws.addCell(number);
						} else if (o instanceof Long) {
							Number number = new Number(i, index, (Long) o);
							ws.addCell(number);
						} else if (o instanceof Float) {
							Number number = new Number(i, index, (Float) o);
							ws.addCell(number);
						} else if (o instanceof java.sql.Date) {
							if(dateFormat == null) {
								DateFormat df = new jxl.write.DateFormat(DBFoundConfig.getDateFormat());
								dateFormat = new WritableCellFormat(df);
							}
							DateTime dateTime = new DateTime(i, index, (Date) o,dateFormat);
							ws.addCell(dateTime);
						} else if (o instanceof Time) {
							Label label = new Label(i, index, LocalDateUtil.formatTime((Time) o));
							ws.addCell(label);
						} else if (o instanceof Date) {
							if(dateTimeFormat == null) {
								DateFormat df = new jxl.write.DateFormat(DBFoundConfig.getDateTimeFormat());
								dateTimeFormat = new WritableCellFormat(df);
							}
							DateTime dateTime = new DateTime(i, index, (Date) o,dateTimeFormat);
							ws.addCell(dateTime);
						} else if (o instanceof LocalDate) {
							if(dateFormat == null) {
								DateFormat df = new jxl.write.DateFormat(DBFoundConfig.getDateFormat());
								dateFormat = new WritableCellFormat(df);
							}
							LocalDate localDate =  (LocalDate) o;
							DateTime dateTime = new DateTime(i, index, java.sql.Date.valueOf(localDate),dateFormat);
							ws.addCell(dateTime);
						} else if (o instanceof LocalDateTime) {
							if(dateTimeFormat == null) {
								DateFormat df = new jxl.write.DateFormat(DBFoundConfig.getDateTimeFormat());
								dateTimeFormat = new WritableCellFormat(df);
							}
							LocalDateTime localDateTime =  (LocalDateTime) o;
							DateTime dateTime = new DateTime(i, index, Timestamp.valueOf(localDateTime),dateTimeFormat);
							ws.addCell(dateTime);
						} else if (o instanceof LocalTime) {
							Label label = new Label(i, index, LocalDateUtil.formatTime((LocalTime) o));
							ws.addCell(label);
						} else {
							String content = o.toString();
							Label label = new Label(i, index, content);
							ws.addCell(label);
						}
					}
					index++;
				}

				datas.clear();

			} while(length > sheet * sheetSize);
			wwb.write();
		} finally {
			wwb.close();
		}
	}

	private static Object getMapperValue(String value, Map<String,Object> mapper){

		Object valueResult = mapper.get(value);

		//判断是否逗号隔开的多组值
		if(valueResult == null && value.contains(",")){
			String[] vals = value.split(",");
			String values = "";
			for (int i =0; i < vals.length; i++){
				Object valItem = mapper.get(vals[i].trim());
				if(i == 0){
					values = valItem != null ? valItem.toString(): vals[i];
				}else{
					values = values + ", " + (valItem != null ? valItem.toString(): vals[i]);
				}
			}
			valueResult = values;
		}
		return valueResult == null ? value: valueResult;
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

		Object columns = context.getData("param.columns");
		if(columns == null){
			throw new DBFoundRuntimeException("can not found param columns");
		}

		List result = ModelEngine.query(context, modelName, queryName, null,false).getDatas();
		excelExport(context, result);
	}

	public static void excelExport(Context context, List result)throws Exception {

		File file = new File(FileUtil.getUploadFolder(null), UUIDUtil.getUUID() + ".xls");
		try {
			writeExcel(file, result, context);

			try (InputStream in = new FileInputStream(file);
				 OutputStream out = context.response.getOutputStream()) {

				// 向外输出excel
				context.response.setContentLength((int)file.length());
				context.response.setContentType("application/x-download");
				context.response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
				context.response.setHeader("Content-Disposition", "attachment;filename=export.xls");

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
