package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.csv.CSVFormat;
import com.nfwork.dbfound.csv.CSVParser;
import com.nfwork.dbfound.csv.CSVRecord;
import com.nfwork.dbfound.exception.DBFoundPackageException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class CsvReaderResolver extends ReaderResolver{

    @Override
    protected List<List<Map<String, Object>>> read(InputStream input, List<ExcelColumn> columns){
        List<List<Map<String,Object>>> result = new ArrayList<>(1);
        result.add(readCsv(input,columns));
        return result;
    }

    @Override
    protected Map<String, List<Map<String, Object>>> readForMap(InputStream input, List<ExcelColumn> columns){
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("sheet1",readCsv(input,columns));
        return result;
    }

    private List<Map<String,Object>> readCsv(InputStream input, List<ExcelColumn> columns) {
        CSVFormat format = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build();
        try(CSVParser parse = format.parse(new InputStreamReader(input, Charset.forName(DBFoundConfig.getEncoding())))) {
            Iterator<CSVRecord> csvRecordIterator = parse.iterator();
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> headers = parse.getHeaderNames();

            Map<String, String> headerNameMap = new HashMap<>();
            if (columns != null) {
                headerNameMap = columns.stream().collect(Collectors.toMap(ExcelColumn::getTitle, ExcelColumn::getName));
            }

            List<String> headerNames = new ArrayList<>();
            for (String header : headers) {
                String name = headerNameMap.get(header);
                if (name == null) {
                    name = header;
                }
                headerNames.add(name);
            }

            while (csvRecordIterator.hasNext()) {
                CSVRecord record = csvRecordIterator.next();
                Map<String, Object> map = new HashMap<>();
                for (int i=0; i < headerNames.size();i++) {
                    map.put(headerNames.get(i), record.get(i));
                }
                list.add(map);
            }
            return list;
        }catch (IOException e){
            throw new DBFoundPackageException("csv reader failed, "+e.getMessage(), e);
        }
    }
}
