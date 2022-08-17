package dbfount.test.core;

import com.nfwork.dbfound.core.DBFoundConfig;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.WritableCellFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ExcelTest {

    public static void main(String[] args) {
       System.setProperty("user.timezone","GMT+8");
       TimeZone.getDefault();
       SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
       System.out.println(format.format(new Date()));
    }
}
