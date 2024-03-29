package com.nfwork.dbfound.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

public class XlsReaderResolver extends XlsxReaderResolver{

    @Override
    protected Workbook getWorkBook(InputStream input) throws IOException {
        return new HSSFWorkbook(input);
    }
}
