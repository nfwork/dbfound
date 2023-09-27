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

public class CsvReaderResolver extends ReaderResolver{

    @Override
    protected List<List<Map<String, Object>>> read(InputStream input){
        List<List<Map<String,Object>>> result = new ArrayList<>(1);
        result.add(readCsv(input));
        return result;
    }

    @Override
    protected Map<String, List<Map<String, Object>>> readForMap(InputStream input){
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("sheet1",readCsv(input));
        return result;
    }

    private List<Map<String,Object>> readCsv(InputStream input) {
        CSVFormat format = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).build();
        try(CSVParser parse = format.parse(new InputStreamReader(input, Charset.forName(DBFoundConfig.getEncoding())))) {
            Iterator<CSVRecord> csvRecordIterator = parse.iterator();
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> headers = parse.getHeaderNames();

            while (csvRecordIterator.hasNext()) {
                CSVRecord record = csvRecordIterator.next();
                Map<String, Object> map = new HashMap<>();
                for (int i=0; i < headers.size();i++) {
                    map.put(headers.get(i), record.get(i));
                }
                list.add(map);
            }
            return list;
        }catch (IOException e){
            throw new DBFoundPackageException("csv reader failed, "+e.getMessage(), e);
        }
    }
}
