package com.nfwork.dbfound.excel;

import java.io.*;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

import jxl.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableFont;

public class ExcelWriter {

	@SuppressWarnings("unchecked")
	public static File writeExcel(File file, List<Map> datas,
			ExcelCellMeta[] columns) throws Exception {

		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(file);
		jxl.write.WritableSheet ws = wwb.createSheet("sheet1", 0);

		jxl.write.WritableFont wfc = new jxl.write.WritableFont(
				WritableFont.ARIAL, 11, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.GREEN);

		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
				wfc);
		wcfFC.setBackground(Colour.GRAY_25);
		wcfFC.setAlignment(Alignment.CENTRE);

		try {
			for (int i = 0; i < columns.length; i++) {
				jxl.write.Label label = new jxl.write.Label(i, 0, columns[i]
						.getContent(), wcfFC);
				ws.addCell(label);
				ws.setColumnView(i, columns[i].getWidth());
			}
			int index = 1;
			for (Map data : datas) {
				for (int i = 0; i < columns.length; i++) {
					Object o = data.get(columns[i].getName());
					if (o == null) {
						Blank blank = new Blank(i, index);
						ws.addCell(blank);
					} else if (o instanceof String) {
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
					}
				}
				index++;
			}
			wwb.write();
		} finally {
			wwb.close();
		}
		return file;
	}

	@SuppressWarnings("unchecked")
	public static void excelExport(Context context, String modelName,
			String queryName) throws Exception {
		context.isExport = true;
		// 将parameters中的参数转移到param中
		Map param = (Map) context.getData("param");
		Map parameters = (Map) param.get("parameters");
		param.putAll(parameters);
		param.remove("parameters");

		List<Map> result = ModelEngine.query(context, modelName, queryName, null,
				false).getDatas();
		excelExport(context, result);
	}

	@SuppressWarnings("unchecked")
	public static void excelExport(Context context, List<Map> result)
			throws Exception {
		// 列处理
		List<Map> cls = (List<Map>) context.getData("param.columns");
		ExcelCellMeta[] columns = new ExcelCellMeta[cls.size()];
		int index = 0;
		for (Map map : cls) {
			ExcelCellMeta cellMeta = new ExcelCellMeta(map.get("name")
					.toString(), map.get("content").toString(), Integer
					.parseInt(map.get("width").toString()));
			columns[index++] = cellMeta;
		}

		File file = new File(FileUtil.getUploadFolder(null), UUIDUtil.getUUID()
				+ ".xls");
		ServletOutputStream sout = null;
		InputStream in = null;

		try {
			file.createNewFile();
			writeExcel(file, result, columns);

			// 向外输出excel
			context.response.setContentType("application/x-download");
			context.response.setHeader("Content-Disposition",
					"attachment;filename=export.xls");
			sout = context.response.getOutputStream(); // 图片输出的输出流
			in = new FileInputStream(file);

			if (in != null) {
				byte b[] = new byte[2048];
				int i = in.read(b);
				while (i != -1) {
					sout.write(b, 0, i);
					i = in.read(b);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (sout != null) {
				sout.flush(); // 输入完毕，清除缓冲
				sout.close();
			}
			if (file != null) {
				file.delete();
			}
		}
	}
}
