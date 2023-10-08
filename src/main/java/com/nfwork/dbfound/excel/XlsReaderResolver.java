package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class XlsReaderResolver extends ReaderResolver{

    @Override
    protected List<List<Map<String, Object>>> read(InputStream input){
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(input);
            Sheet[] sheets = wb.getSheets();
            List<List<Map<String,Object>>> list = new ArrayList<>(sheets.length);
            for (Sheet sheet : sheets) {
                list.add(readSheet(sheet));
            }
            return list;
        }catch (IOException | BiffException e){
            throw new DBFoundPackageException("xls reader failed, "+e.getMessage(), e);
        }finally {
            if(wb != null){
                wb.close();
            }
        }
    }

    @Override
    protected Map<String, List<Map<String, Object>>> readForMap(InputStream input){
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(input);
            Sheet[] sheets = wb.getSheets();
            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            for (Sheet sheet : sheets) {
                map.put(sheet.getName(), readSheet(sheet));
            }
            return map;
        }catch (IOException | BiffException e){
            throw new DBFoundPackageException("xls reader failed, "+e.getMessage(), e);
        }finally {
            if(wb != null){
                wb.close();
            }
        }
    }

    private List<Map<String,Object>> readSheet(Sheet sheet){
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
                        double numericCellValue = numberCell.getValue();
                        if(isLong(numericCellValue)) {
                            data.put(metaData[j], (long)numberCell.getValue());
                        }else{
                            data.put(metaData[j], numberCell.getValue());
                        }
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
