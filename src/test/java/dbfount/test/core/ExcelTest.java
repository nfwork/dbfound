package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.WritableCellFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ExcelTest {

    public static void main(String[] args) {
        List list = new ArrayList();
        DSqlEngine.checkWhenSql("1=1",list,"mysql", new Context());
    }
}
