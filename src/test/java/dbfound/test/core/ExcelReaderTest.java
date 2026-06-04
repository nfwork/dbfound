package dbfound.test.core;

import com.nfwork.dbfound.excel.ExcelColumn;
import com.nfwork.dbfound.excel.ExcelReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReaderTest {

    @Test
    public void testReadXlsxSkipEmptyRows() throws Exception {
        List<ExcelColumn> columns = Arrays.asList(
                new ExcelColumn("name", "Name"),
                statusColumn()
        );

        List<List<Map<String, Object>>> sheets = ExcelReader.readExcel(createWorkbookWithEmptyRows(), "xlsx", columns);
        List<Map<String, Object>> data = sheets.get(0);

        Assert.assertEquals(2, data.size());
        Assert.assertEquals("Alice", data.get(0).get("name"));
        Assert.assertEquals("active", data.get(0).get("status"));
        Assert.assertEquals("Bob", data.get(1).get("name"));
        Assert.assertNull(data.get(1).get("status"));
    }

    @Test
    public void testWriterMapperValueAllowNull() {
        Assert.assertNull(new TestWriterResolver().map(null, new HashMap<>()));
    }

    @Test
    public void testReadXlsxWithoutHeaderReturnsEmptyData() throws Exception {
        List<List<Map<String, Object>>> sheets = ExcelReader.readExcel(createWorkbookWithoutHeader(), "xlsx");

        Assert.assertEquals(1, sheets.size());
        Assert.assertTrue(sheets.get(0).isEmpty());
    }

    private ExcelColumn statusColumn() {
        ExcelColumn column = new ExcelColumn("status", "Status");
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("Y", "active");
        column.setMapper(mapper);
        return column;
    }

    private byte[] createWorkbookWithEmptyRows() throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("sheet1");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Status");

            Row firstDataRow = sheet.createRow(1);
            firstDataRow.createCell(0).setCellValue("Alice");
            firstDataRow.createCell(1).setCellValue("Y");

            Row emptyRow = sheet.createRow(2);
            emptyRow.createCell(0).setBlank();
            emptyRow.createCell(1).setBlank();

            Row blankTextRow = sheet.createRow(3);
            blankTextRow.createCell(0).setCellValue("   ");
            blankTextRow.createCell(1).setCellValue("   ");

            Row secondDataRow = sheet.createRow(5);
            secondDataRow.createCell(0).setCellValue("Bob");
            secondDataRow.createCell(1).setBlank();

            workbook.write(output);
            return output.toByteArray();
        }
    }

    private byte[] createWorkbookWithoutHeader() throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("sheet1");
            sheet.createRow(1).createCell(0).setBlank();

            workbook.write(output);
            return output.toByteArray();
        }
    }

    private static class TestWriterResolver extends com.nfwork.dbfound.excel.WriterResolver {

        @Override
        protected void writer(File file, List<?> dataList, List<ExcelColumn> columns) {
        }

        private Object map(Object values, Map<String, Object> mapper) {
            return getMapperValue(values, mapper);
        }
    }
}
