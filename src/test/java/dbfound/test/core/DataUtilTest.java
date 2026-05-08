package dbfound.test.core;

import com.nfwork.dbfound.util.DataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class DataUtilTest {

    @Test
    public void testBigDecimalValue_null() {
        Assert.assertNull(DataUtil.bigDecimalValue(null));
        Assert.assertNull(DataUtil.bigDecimalValue(""));
    }

    @Test
    public void testBigDecimalValue_fromBigDecimal() {
        BigDecimal bd = new BigDecimal("123.456");
        Assert.assertEquals(bd, DataUtil.bigDecimalValue(bd));
    }

    @Test
    public void testBigDecimalValue_fromString() {
        Assert.assertEquals(new BigDecimal("99.99"), DataUtil.bigDecimalValue("99.99"));
        Assert.assertEquals(new BigDecimal("0"), DataUtil.bigDecimalValue("0"));
        Assert.assertEquals(new BigDecimal("-10.5"), DataUtil.bigDecimalValue("-10.5"));
    }

    @Test
    public void testBigDecimalValue_fromInteger() {
        Assert.assertEquals(new BigDecimal("100"), DataUtil.bigDecimalValue(100));
    }

    @Test
    public void testBigDecimalValue_fromLong() {
        Assert.assertEquals(new BigDecimal("9999999999"), DataUtil.bigDecimalValue(9999999999L));
    }

    @Test
    public void testBigDecimalValue_fromDouble() {
        Assert.assertEquals(new BigDecimal("3.14"), DataUtil.bigDecimalValue("3.14"));
    }

    @Test(expected = NumberFormatException.class)
    public void testBigDecimalValue_invalidString() {
        DataUtil.bigDecimalValue("abc");
    }
}
