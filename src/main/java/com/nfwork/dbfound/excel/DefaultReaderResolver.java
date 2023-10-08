package com.nfwork.dbfound.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;

public class DefaultReaderResolver extends XlsxReaderResolver{

    @Override
    protected Workbook getWorkBook(InputStream input) throws IOException {
        return WorkbookFactory.create(input);
    }
}
