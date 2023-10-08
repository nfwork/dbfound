package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class XlsxWriterResolver extends WriterResolver {

    @Override
    protected void writer(File file, List<Object> dataList, List<Map<String, Object>> cls) {
        Map<String,Map<String,Object>> mappers = getMappers(cls);

        try(SXSSFWorkbook workbook = new SXSSFWorkbook(500)) {
            SXSSFSheet sheet = workbook.createSheet("sheet1");

            CellStyle headerStyle = getHeaderStyle(workbook);
            CellStyle lineStyle = getLineStyle(workbook);
            DateStyles dateStyles = new DateStyles(workbook);

            writerSheet(sheet, dataList, cls, mappers, headerStyle, lineStyle, dateStyles);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            } catch (IOException exception) {
                throw new DBFoundPackageException("xlsx writer failed, " + exception.getMessage(), exception);
            } finally {
                workbook.dispose();
            }
        } catch (IOException ignore) {}
    }

    protected void writerSheet(Sheet sheet, List<Object> dataList, List<Map<String, Object>> cls, Map<String,Map<String,Object>> mappers, CellStyle headerStyle, CellStyle lineStyle, DateStyles dateStyles){
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i< cls.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cls.get(i).get("content").toString());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i,  Integer.parseInt(cls.get(i).get("width").toString()) * 39);
        }
        //写入数据
        for (int i = 0; i < dataList.size();i++){
            Object data = dataList.get(i);
            Row row = sheet.createRow(i+1);
            for (int j = 0; j<cls.size(); j++){
                Cell cell = row.createCell(j);
                cell.setCellStyle(lineStyle);
                setCellValue(cell, dateStyles,data, cls.get(j),mappers);
            }
        }
    }

    protected void setCellValue(Cell cell, DateStyles dateStyles, Object data, Map<String, Object> column, Map<String,Map<String,Object>> mappers ){
        String name = column.get("name").toString();
        Object o = DBFoundEL.getDataByProperty(name, data);
        if (o == null) {
            cell.setBlank();
            return;
        }
        Map<String,Object> mapper = mappers.get(name);
        if(mapper != null){
            o = getMapperValue(o,mapper);
        }
        if (o instanceof String) {
            String content = o.toString();
            cell.setCellValue(content);
        } else if (o instanceof Integer) {
            cell.setCellValue((Integer)o);
        } else if (o instanceof Double) {
            cell.setCellValue((Double) o);
        } else if (o instanceof Long) {
            cell.setCellValue((Long)o);
        } else if (o instanceof Float) {
            cell.setCellValue((Float)o);
        } else if (o instanceof Temporal) {
            if(o instanceof LocalDate){
                cell.setCellValue((LocalDate) o);
                cell.setCellStyle(dateStyles.getDateStyle());
            }else if(o instanceof LocalTime){
                cell.setCellValue(Time.valueOf((LocalTime)o));
                cell.setCellStyle(dateStyles.getTimeStyle());
            }else{
                cell.setCellValue((LocalDateTime) o);
                cell.setCellStyle(dateStyles.getDateTimeStyle());
            }
        } else if (o instanceof Date) {
            if(o instanceof java.sql.Date){
                cell.setCellValue((java.sql.Date)o);
                cell.setCellStyle(dateStyles.getDateStyle());
            }else if(o instanceof Time){
                cell.setCellValue((Time)o);
                cell.setCellStyle(dateStyles.getTimeStyle());
            }else{
                cell.setCellValue((Date)o);
                cell.setCellStyle(dateStyles.getDateTimeStyle());
            }
        } else {
            cell.setCellValue(o.toString());
        }
    }

    protected static CellStyle getHeaderStyle(Workbook workbook){
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true); // 加粗
        headerFont.setColor(IndexedColors.GREEN.index);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    protected static CellStyle getLineStyle(Workbook workbook){
        Font cellFont = workbook.createFont();
        cellFont.setFontName("Arial");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        return cellStyle;
    }

    protected static class DateStyles{
        Font cellFont;
        Workbook workbook;
        CellStyle dateStyle;
        CellStyle dateTimeStyle;
        CellStyle timeStyle;

        DateStyles( Workbook workbook){
            this.workbook = workbook;
            cellFont = workbook.createFont();
            cellFont.setFontName("Arial");
        }

        public CellStyle getDateStyle() {
            if(dateStyle == null){
                DataFormat dateFormat  = workbook.createDataFormat();
                short format = dateFormat.getFormat(DBFoundConfig.getDateFormat());
                dateStyle = workbook.createCellStyle();
                dateStyle.setDataFormat(format);
                dateStyle.setFont(cellFont);
            }
            return dateStyle;
        }

        public CellStyle getDateTimeStyle() {
            if(dateTimeStyle == null){
                DataFormat datetimeFormat  = workbook.createDataFormat();
                short format = datetimeFormat.getFormat(DBFoundConfig.getDateTimeFormat());
                dateTimeStyle = workbook.createCellStyle();
                dateTimeStyle.setDataFormat(format);
                dateTimeStyle.setFont(cellFont);
            }
            return dateTimeStyle;
        }

        public CellStyle getTimeStyle() {
            if(timeStyle == null){
                DataFormat timeFormat  = workbook.createDataFormat();
                short format = timeFormat.getFormat(DBFoundConfig.getTimeFormat());
                timeStyle = workbook.createCellStyle();
                timeStyle.setDataFormat(format);
                timeStyle.setFont(cellFont);
            }
            return timeStyle;
        }
    }

}
