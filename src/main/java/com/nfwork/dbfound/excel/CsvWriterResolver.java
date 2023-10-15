package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.csv.CSVFormat;
import com.nfwork.dbfound.csv.CSVPrinter;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.LocalDateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CsvWriterResolver extends WriterResolver{

    @Override
    protected void writer(File file, List<?> dataList, List<ExcelColumn> cls){
        String[] headers = new String[cls.size()];
        int colIndex = 0;
        for (ExcelColumn column : cls) {
            headers[colIndex]= column.getTitle();
            colIndex++;
        }

        CSVFormat format = CSVFormat.Builder.create().setHeader(headers).build();

        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), Charset.forName(DBFoundConfig.getEncoding()));
             CSVPrinter printer = new CSVPrinter(fileWriter,format)){
            for(Object data : dataList) {
                List<Object> line = new ArrayList<>(headers.length);
                for (ExcelColumn column : cls) {
                    Object value = DBFoundEL.getDataByProperty(data, column.getName());
                    if(value == null){
                        line.add(null);
                        continue;
                    }
                    Map<String,Object> mapper = column.getMapper();
                    if(mapper != null){
                        value = getMapperValue(value,mapper);
                    }
                    if(value instanceof Temporal){
                        value = LocalDateUtil.formatTemporal((Temporal) value);
                    }else if(value instanceof Date){
                        value = LocalDateUtil.formatDate((Date)value);
                    }
                    line.add(value);
                }
                printer.printRecord(line);
            }
            printer.flush();
        }catch (IOException exception){
            throw new DBFoundPackageException("csv writer failed, "+ exception.getMessage(), exception);
        }
    }
}
