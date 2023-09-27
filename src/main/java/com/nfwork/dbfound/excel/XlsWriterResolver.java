package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.LocalDateUtil;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class XlsWriterResolver extends WriterResolver{

    @Override
    protected void writer(File file, List<Object> dataList, List<Map<String,Object>> cls) {
        WorkbookSettings wbSetting = new WorkbookSettings();
        if(dataList.size()> 50000) {
            wbSetting.setUseTemporaryFileDuringWrite(true);
        }
        jxl.write.WritableWorkbook wwb = null;

        try {
            wwb = Workbook.createWorkbook(file,wbSetting);

            WritableCellFormat dateFormat = null;
            WritableCellFormat dateTimeFormat = null;

            int length = dataList.size();
            int sheet = 0;
            int sheetSize = 50000;

            //mappers处理
            Map<String,Map<String,Object>> mappers = getMappers(cls);

            do {
                int end = sheetSize;
                if(end >= dataList.size()){
                    end = dataList.size();
                }

                List<Object> datas = dataList.subList(0, end);

                jxl.write.WritableSheet ws = wwb.createSheet("sheet" + (sheet+1), sheet);
                sheet ++;

                jxl.write.WritableFont wfc = new jxl.write.WritableFont(
                        WritableFont.ARIAL, 11, WritableFont.BOLD, false,
                        UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.GREEN);

                jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(wfc);
                wcfFC.setBackground(Colour.GRAY_25);
                wcfFC.setAlignment(Alignment.CENTRE);

                for (int i = 0; i < cls.size(); i++) {
                    jxl.write.Label label = new jxl.write.Label(i, 0, cls.get(i).get("content").toString(), wcfFC);
                    ws.addCell(label);
                    ws.setColumnView(i, (int) (Integer.parseInt(cls.get(i).get("width").toString())*0.15));
                }
                int index = 1;
                for (Object data : datas) {
                    for (int i = 0; i < cls.size(); i++) {
                        String property = cls.get(i).get("name").toString();
                        Object o = DBFoundEL.getDataByProperty(property,data);
                        if (o == null) {
                            continue;
                        }
                        //mapper映射处理
                        Map<String,Object> mapper = mappers.get(property);
                        if(mapper != null){
                            o = getMapperValue(o,mapper);
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
                            LocalDateTime localDateTime = (LocalDateTime) o;
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
        }catch (IOException | WriteException e){
            throw new DBFoundPackageException("xls writer failed, "+ e.getMessage(),e);
        }finally {
            if(wwb != null)try { wwb.close();} catch (Exception ignore) {}
        }
    }
}
