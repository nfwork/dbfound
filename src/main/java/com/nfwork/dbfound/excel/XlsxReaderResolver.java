package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class XlsxReaderResolver extends ReaderResolver{

    @Override
    protected List<List<Map<String, Object>>> read(InputStream input){
        List<List<Map<String, Object>>> result = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(input);
            int num = workbook.getNumberOfSheets();
            for(int i=0; i< num; i++){
                Sheet sheet = workbook.getSheetAt(i);
                List<Map<String,Object>> data = readSheet(sheet);
                result.add(data);
            }
        } catch (IOException e) {
            throw new DBFoundPackageException("xlsx reader failed, "+ e.getMessage(),e);
        }
        return result;
    }

    @Override
    protected Map<String, List<Map<String, Object>>> readForMap(InputStream input){
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        try {
            Workbook workbook = new XSSFWorkbook(input);
            int num = workbook.getNumberOfSheets();
            for(int i=0; i< num; i++){
                Sheet sheet = workbook.getSheetAt(i);
                List<Map<String,Object>> data = readSheet(sheet);
                result.put(sheet.getSheetName(),data);
            }
        } catch (IOException e) {
            throw new DBFoundPackageException("xlsx reader failed, "+ e.getMessage(),e);
        }
        return result;
    }

    private List<Map<String,Object>> readSheet(Sheet sheet){
        int rowSize = sheet.getLastRowNum();
        if(rowSize==-1){
            return null;
        }
        Row header = sheet.getRow(0);
        int colSize = header.getLastCellNum();

        String[] metaData = new String[colSize];
        for (int j = 0; j < colSize; j++) {
            metaData[j] = header.getCell(j).getStringCellValue().trim();
        }
        List<Map<String,Object>> result = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DBFoundConfig.getDateFormat());
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
        SimpleDateFormat timeFormat = new SimpleDateFormat(DBFoundConfig.getTimeFormat());
        Calendar calendar = Calendar.getInstance();

        for (int i = 1; i < rowSize; i++) {
            Map<String, Object> data = new HashMap<>();
            result.add(data);

            Row line = sheet.getRow(i);

            for (int j = 0; j < colSize; j++) {
                Cell cell = line.getCell(j);
                if(cell == null){
                    continue;
                }
                CellType cellType = cell.getCellType();
                Object cellValue = null;

                switch (cellType){
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            if(date == null){
                                break;
                            }
                            calendar.setTime(date);
                            if(calendar.get(Calendar.HOUR_OF_DAY) ==0 && calendar.get(Calendar.MINUTE) ==0 && calendar.get(Calendar.SECOND) ==0) {
                                cellValue = dateFormat.format(date);
                            }else if(calendar.get(Calendar.YEAR) == 1899 && calendar.get(Calendar.MONTH)==Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH)==31){
                                cellValue = timeFormat.format(date);
                            }else{
                                cellValue = datetimeFormat.format(date);
                            }
                        } else {
                            double numericCellValue = cell.getNumericCellValue();
                            if(isLong(numericCellValue)){
                                cellValue = (long)numericCellValue;
                            }else{
                                cellValue = numericCellValue;
                            }
                        }
                        break;
                    case BLANK:
                        break;
                    default:
                        cellValue = cell.getStringCellValue();
                }
                data.put(metaData[j], cellValue);
            }
        }
        return result;
    }
}
