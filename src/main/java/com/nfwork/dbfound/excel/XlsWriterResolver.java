package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XlsWriterResolver extends XlsxWriterResolver{

    @Override
    protected void writer(File file, List<Object> dataList, List<Map<String,Object>> cls) {
        Map<String,Map<String,Object>> mappers = getMappers(cls);

        try(Workbook workbook = new HSSFWorkbook()) {
            CellStyle headerStyle = getHeaderStyle(workbook);
            CellStyle lineStyle = getLineStyle(workbook);
            DateStyles dateStyles = new DateStyles(workbook);

            int length = dataList.size();
            int sheetIndex = 0;
            int sheetSize = 50000;

            do {
                int end = sheetSize;
                if (end >= dataList.size()) {
                    end = dataList.size();
                }

                List<Object> datas = dataList.subList(0, end);

                sheetIndex++;
                Sheet sheet = workbook.createSheet("sheet" + (sheetIndex));

                writerSheet(sheet, datas, cls, mappers, headerStyle, lineStyle, dateStyles);

                datas.clear();
            } while (length > sheetIndex * sheetSize);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            } catch (IOException exception) {
                throw new DBFoundPackageException("xls writer failed, " + exception.getMessage(), exception);
            }
        }catch (IOException ignore){}
    }
}
