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
    protected void writer(File file, List<Object> dataList, List<Map<String,Object>> cls){
        // columns处理
        String[] names = new String[cls.size()];
        String[] headers = new String[cls.size()];
        int colIndex = 0;
        for (Map<String,Object> map : cls) {
            headers[colIndex]= map.get("content").toString();
            names[colIndex] = map.get("name").toString();
            colIndex++;
        }
        //mappers处理
        Map<String,Map<String,Object>> mappers = getMappers(cls);

        CSVFormat format = CSVFormat.Builder.create().setHeader(headers).build();

        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), Charset.forName(DBFoundConfig.getEncoding()));
             CSVPrinter printer = new CSVPrinter(fileWriter,format)){
            for(Object data : dataList) {
                List<Object> line = new ArrayList<>(headers.length);
                for (String name : names) {
                    Object value = DBFoundEL.getDataByProperty(name,data);
                    if(value == null){
                        line.add(null);
                        continue;
                    }
                    Map<String,Object> mapper = mappers.get(name);
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
