package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsxWriterResolver extends WriterResolver {

    @Override
    protected void writer(File file, List<Object> dataList, List<ExcelColumn> cls) {
        try(InputStream inputStream = getTemplateInputStream();
            SXSSFWorkbook workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream),500)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                SXSSFSheet sheet = workbook.getSheet("sheet1");

                CellStyle headerStyle = getHeaderStyle(workbook);
                DataStyles dataStyles = new DataStyles(workbook);

                writerSheet(sheet, dataList, cls, headerStyle, dataStyles);

                workbook.write(fos);
                fos.flush();
            } catch (IOException exception) {
                throw new DBFoundPackageException("xlsx writer failed, " + exception.getMessage(), exception);
            } finally {
                workbook.dispose();
            }
        } catch (IOException ignore) {}
    }

    protected void writerSheet(Sheet sheet, List<Object> dataList, List<ExcelColumn> cls, CellStyle headerStyle, DataStyles dataStyles){
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i< cls.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(cls.get(i).getTitle());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i,  cls.get(i).getWidth() * 39);
        }
        //写入数据
        for (int i = 0; i < dataList.size();i++){
            Object data = dataList.get(i);
            Row row = sheet.createRow(i+1);
            row.setHeightInPoints(16);
            for (int j = 0; j<cls.size(); j++){
                Cell cell = row.createCell(j);
                setCellValue(cell, dataStyles,data, cls.get(j));
            }
        }
    }

    protected void setCellValue(Cell cell, DataStyles dataStyles, Object data, ExcelColumn column){
        CellStyle userStyle = null;
        if(DataUtil.isNotNull(column.getFormat())){
            userStyle = dataStyles.getUserStyle(column.getName(),column.getFormat());
        }
        String name = column.getName();
        Object o = DBFoundEL.getDataByProperty(name, data);
        if (o == null) {
            cell.setBlank();
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
            return;
        }
        Map<String,Object> mapper = column.getMapper();
        if(mapper != null){
            o = getMapperValue(o,mapper);
        }
        if (o instanceof String) {
            String content = o.toString();
            cell.setCellValue(content);
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        } else if (o instanceof Integer) {
            cell.setCellValue((Integer)o);
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        } else if (o instanceof Double) {
            cell.setCellValue((Double) o);
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        } else if (o instanceof Long) {
            cell.setCellValue((Long)o);
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        } else if (o instanceof Float) {
            cell.setCellValue((Float)o);
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        } else if (o instanceof Temporal) {
            if(o instanceof LocalDate){
                cell.setCellValue((LocalDate) o);
                cell.setCellStyle(userStyle==null?dataStyles.getDateStyle():userStyle);
            }else if(o instanceof LocalTime){
                cell.setCellValue(Time.valueOf((LocalTime)o));
                cell.setCellStyle(userStyle==null?dataStyles.getTimeStyle():userStyle);
            }else{
                cell.setCellValue((LocalDateTime) o);
                cell.setCellStyle(userStyle==null?dataStyles.getDateTimeStyle():userStyle);
            }
        } else if (o instanceof Date) {
            if(o instanceof java.sql.Date){
                cell.setCellValue((java.sql.Date)o);
                cell.setCellStyle(userStyle==null?dataStyles.getDateStyle():userStyle);
            }else if(o instanceof Time){
                cell.setCellValue((Time)o);
                cell.setCellStyle(userStyle==null?dataStyles.getTimeStyle():userStyle);
            }else{
                cell.setCellValue((Date)o);
                cell.setCellStyle(userStyle==null?dataStyles.getDateTimeStyle():userStyle);
            }
        } else {
            cell.setCellValue(o.toString());
            cell.setCellStyle(userStyle==null?dataStyles.getDefaultStyle():userStyle);
        }
    }

    protected static CellStyle getHeaderStyle(Workbook workbook){
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setFontName("Calibri");
        headerFont.setBold(true); // 加粗
        headerFont.setColor(IndexedColors.BLACK.index);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    private InputStream getTemplateInputStream() throws IOException {
        ClassLoader loader = XlsxWriterResolver.class.getClassLoader();
        URL url = loader.getResource("templates/dbfound_export.xlsx");
        if(url == null){
            throw new DBFoundRuntimeException("cant not found xlsx templates 'templates/dbfound_export.xlsx'");
        }
        return url.openStream();
    }

    protected static class DataStyles{
        Workbook workbook;
        CellStyle dateStyle;
        CellStyle dateTimeStyle;
        CellStyle timeStyle;
        Map<String, CellStyle> styleMap = new HashMap<>();
        CellStyle defaultStyle;
        Font cellFont;

        DataStyles( Workbook workbook){
            this.workbook = workbook;
            cellFont = workbook.createFont();
            cellFont.setFontName("Calibri");
            cellFont.setFontHeightInPoints((short) 11);
        }

        public CellStyle getUserStyle(String name, String format){
            CellStyle cellStyle = styleMap.get(name);
            if(cellStyle == null){
                DataFormat dateFormat  = workbook.createDataFormat();
                short formatValue = dateFormat.getFormat(format);
                cellStyle = workbook.createCellStyle();
                cellStyle.setDataFormat(formatValue);
                cellStyle.setFont(cellFont);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                styleMap.put(name,cellStyle);
            }
            return cellStyle;
        }

        public CellStyle getDefaultStyle(){
            if(defaultStyle == null) {
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFont(cellFont);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                defaultStyle = cellStyle;
            }
            return defaultStyle;
        }

        public CellStyle getDateStyle() {
            if(dateStyle == null){
                DataFormat dateFormat  = workbook.createDataFormat();
                short format = dateFormat.getFormat(DBFoundConfig.getDateFormat());
                dateStyle = workbook.createCellStyle();
                dateStyle.setDataFormat(format);
                dateStyle.setFont(cellFont);
                dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                dateStyle.setAlignment(HorizontalAlignment.CENTER);
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
                dateTimeStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                dateTimeStyle.setAlignment(HorizontalAlignment.CENTER);
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
                timeStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                timeStyle.setAlignment(HorizontalAlignment.CENTER);
            }
            return timeStyle;
        }
    }

}
