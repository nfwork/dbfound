package dbfound.test.core;

import com.nfwork.dbfound.util.DataUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DataUtilTest {

    @Test
    public void testIsNull() {
        Assert.assertTrue(DataUtil.isNull(""));
        Assert.assertFalse(DataUtil.isNull(" "));
        Assert.assertFalse(DataUtil.isNull(0));
    }

    @Test
    public void testIsNotNull() {
        Assert.assertFalse(DataUtil.isNotNull(""));
        Assert.assertTrue(DataUtil.isNotNull("value"));
    }

    @Test
    public void testShortValue_null() {
        Assert.assertNull(DataUtil.shortValue(null));
        Assert.assertNull(DataUtil.shortValue(""));
    }

    @Test
    public void testShortValue_fromShort() {
        Short value = 12;
        Assert.assertSame(value, DataUtil.shortValue(value));
    }

    @Test
    public void testShortValue_fromNumber() {
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue(12));
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue(12L));
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue(12.0D));
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue(new BigDecimal("12.0")));
    }

    @Test(expected = ArithmeticException.class)
    public void testShortValue_fractionalNumber() {
        DataUtil.shortValue(12.9D);
    }

    @Test
    public void testShortValue_fromString() {
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue("12"));
        Assert.assertEquals(Short.valueOf((short) -12), DataUtil.shortValue("-12"));
    }

    @Test
    public void testShortValue_fromDecimalString() {
        Assert.assertEquals(Short.valueOf((short) 12), DataUtil.shortValue("12.0"));
        Assert.assertEquals(Short.valueOf((short) 120), DataUtil.shortValue("1.2E2"));
    }

    @Test(expected = ArithmeticException.class)
    public void testShortValue_fractionalString() {
        DataUtil.shortValue("12.1");
    }

    @Test
    public void testByteValue_null() {
        Assert.assertNull(DataUtil.byteValue(null));
        Assert.assertNull(DataUtil.byteValue(""));
    }

    @Test
    public void testByteValue_fromByte() {
        Byte value = 1;
        Assert.assertSame(value, DataUtil.byteValue(value));
    }

    @Test
    public void testByteValue_fromString() {
        Assert.assertEquals(Byte.valueOf((byte) 1), DataUtil.byteValue("1"));
        Assert.assertEquals(Byte.valueOf((byte) -1), DataUtil.byteValue("-1"));
    }

    @Test(expected = NumberFormatException.class)
    public void testByteValue_invalidString() {
        DataUtil.byteValue("1.0");
    }

    @Test
    public void testDateValue_null() {
        Assert.assertNull(DataUtil.dateValue(null));
        Assert.assertNull(DataUtil.dateValue(""));
    }

    @Test
    public void testDateValue_fromDate() {
        Date value = new Date();
        Assert.assertSame(value, DataUtil.dateValue(value));
    }

    @Test
    public void testDateValue_fromDateString() throws Exception {
        Date expected = new SimpleDateFormat("yyyy-MM-dd").parse("2026-05-09");
        Assert.assertEquals(expected, DataUtil.dateValue("2026-05-09"));
    }

    @Test
    public void testDateValue_fromDateTimeString() throws Exception {
        Date expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2026-05-09 17:25:30");
        Assert.assertEquals(expected, DataUtil.dateValue("2026-05-09 17:25:30"));
    }

    @Test
    public void testLongValue_null() {
        Assert.assertNull(DataUtil.longValue(null));
        Assert.assertNull(DataUtil.longValue(""));
    }

    @Test
    public void testLongValue_fromLong() {
        Long value = 123L;
        Assert.assertSame(value, DataUtil.longValue(value));
    }

    @Test
    public void testLongValue_fromNumber() {
        Assert.assertEquals(Long.valueOf(123L), DataUtil.longValue(123));
        Assert.assertEquals(Long.valueOf(123L), DataUtil.longValue(123.0D));
        Assert.assertEquals(Long.valueOf(123L), DataUtil.longValue(new BigDecimal("123.0")));
    }

    @Test(expected = ArithmeticException.class)
    public void testLongValue_fractionalNumber() {
        DataUtil.longValue(123.9D);
    }

    @Test
    public void testLongValue_fromString() {
        Assert.assertEquals(Long.valueOf(123L), DataUtil.longValue("123"));
    }

    @Test
    public void testLongValue_fromDecimalString() {
        Assert.assertEquals(Long.valueOf(123L), DataUtil.longValue("123.0"));
        Assert.assertEquals(Long.valueOf(-123L), DataUtil.longValue("-123.0"));
        Assert.assertEquals(Long.valueOf(1200L), DataUtil.longValue("1.2E3"));
    }

    @Test(expected = ArithmeticException.class)
    public void testLongValue_fractionalString() {
        DataUtil.longValue("123.9");
    }

    @Test(expected = NumberFormatException.class)
    public void testLongValue_invalidString() {
        DataUtil.longValue("abc");
    }

    @Test
    public void testIntValue_null() {
        Assert.assertNull(DataUtil.intValue(null));
        Assert.assertNull(DataUtil.intValue(""));
    }

    @Test
    public void testIntValue_fromInteger() {
        Integer value = 123;
        Assert.assertSame(value, DataUtil.intValue(value));
    }

    @Test
    public void testIntValue_fromNumber() {
        Assert.assertEquals(Integer.valueOf(123), DataUtil.intValue(123L));
        Assert.assertEquals(Integer.valueOf(123), DataUtil.intValue(123.0D));
        Assert.assertEquals(Integer.valueOf(123), DataUtil.intValue(new BigDecimal("123.0")));
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValue_fractionalNumber() {
        DataUtil.intValue(123.9D);
    }

    @Test
    public void testIntValue_fromString() {
        Assert.assertEquals(Integer.valueOf(123), DataUtil.intValue("123"));
    }

    @Test
    public void testIntValue_fromDecimalString() {
        Assert.assertEquals(Integer.valueOf(123), DataUtil.intValue("123.0"));
        Assert.assertEquals(Integer.valueOf(-123), DataUtil.intValue("-123.0"));
        Assert.assertEquals(Integer.valueOf(1200), DataUtil.intValue("1.2E3"));
    }

    @Test(expected = ArithmeticException.class)
    public void testIntValue_fractionalString() {
        DataUtil.intValue("123.9");
    }

    @Test(expected = NumberFormatException.class)
    public void testIntValue_invalidString() {
        DataUtil.intValue("abc");
    }

    @Test
    public void testDoubleValue_null() {
        Assert.assertNull(DataUtil.doubleValue(null));
        Assert.assertNull(DataUtil.doubleValue(""));
    }

    @Test
    public void testDoubleValue_fromDouble() {
        Double value = 12.34D;
        Assert.assertSame(value, DataUtil.doubleValue(value));
    }

    @Test
    public void testDoubleValue_fromNumber() {
        Assert.assertEquals(Double.valueOf(12D), DataUtil.doubleValue(12));
        Assert.assertEquals(Double.valueOf(12.34D), DataUtil.doubleValue(new BigDecimal("12.34")));
    }

    @Test
    public void testDoubleValue_fromString() {
        Assert.assertEquals(Double.valueOf(12.34D), DataUtil.doubleValue("12.34"));
        Assert.assertEquals(Double.valueOf(-12.34D), DataUtil.doubleValue("-12.34"));
    }

    @Test(expected = NumberFormatException.class)
    public void testDoubleValue_invalidString() {
        DataUtil.doubleValue("abc");
    }

    @Test
    public void testBooleanValue_null() {
        Assert.assertNull(DataUtil.booleanValue(null));
        Assert.assertNull(DataUtil.booleanValue(""));
    }

    @Test
    public void testBooleanValue_fromBoolean() {
        Assert.assertSame(Boolean.TRUE, DataUtil.booleanValue(Boolean.TRUE));
        Assert.assertSame(Boolean.FALSE, DataUtil.booleanValue(Boolean.FALSE));
    }

    @Test
    public void testBooleanValue_fromString() {
        Assert.assertEquals(Boolean.TRUE, DataUtil.booleanValue("true"));
        Assert.assertEquals(Boolean.TRUE, DataUtil.booleanValue("TRUE"));
        Assert.assertEquals(Boolean.FALSE, DataUtil.booleanValue("false"));
    }

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
        Assert.assertEquals(new BigDecimal("3.14"), DataUtil.bigDecimalValue(3.14D));
    }

    @Test(expected = NumberFormatException.class)
    public void testBigDecimalValue_invalidString() {
        DataUtil.bigDecimalValue("abc");
    }

    @Test
    public void testFloatValue_null() {
        Assert.assertNull(DataUtil.floatValue(null));
        Assert.assertNull(DataUtil.floatValue(""));
    }

    @Test
    public void testFloatValue_fromFloat() {
        Float value = 12.34F;
        Assert.assertSame(value, DataUtil.floatValue(value));
    }

    @Test
    public void testFloatValue_fromNumber() {
        Assert.assertEquals(Float.valueOf(12F), DataUtil.floatValue(12));
        Assert.assertEquals(Float.valueOf(12.34F), DataUtil.floatValue(new BigDecimal("12.34")));
    }

    @Test
    public void testFloatValue_fromString() {
        Assert.assertEquals(Float.valueOf(12.34F), DataUtil.floatValue("12.34"));
        Assert.assertEquals(Float.valueOf(-12.34F), DataUtil.floatValue("-12.34"));
    }

    @Test(expected = NumberFormatException.class)
    public void testFloatValue_invalidString() {
        DataUtil.floatValue("abc");
    }

    @Test
    public void testStringValue() {
        Assert.assertNull(DataUtil.stringValue(null));
        Assert.assertSame("abc", DataUtil.stringValue("abc"));
        Assert.assertEquals("123", DataUtil.stringValue(123));
    }

    @Test
    public void testGetDataLength() {
        Assert.assertEquals(3, DataUtil.getDataLength(Arrays.asList(1, 2, 3)));
        Assert.assertEquals(2, DataUtil.getDataLength(new String[] {"a", "b"}));
        Assert.assertEquals(3, DataUtil.getDataLength(new int[] {1, 2, 3}));
        Assert.assertEquals(-1, DataUtil.getDataLength("abc"));
        Assert.assertEquals(-1, DataUtil.getDataLength(null));
    }

    @Test
    public void testGetArrayDataByIndex() {
        Assert.assertEquals("b", DataUtil.getArrayDataByIndex(new String[] {"a", "b"}, 1));
        Assert.assertEquals(2, DataUtil.getArrayDataByIndex(new int[] {1, 2}, 1));
    }

    @Test
    public void testIsArray() {
        Assert.assertFalse(DataUtil.isArray(Arrays.asList(1, 2)));
        Assert.assertTrue(DataUtil.isArray(new Object[] {"a"}));
        Assert.assertTrue(DataUtil.isArray(new int[] {1}));
        Assert.assertTrue(DataUtil.isArray(new long[] {1L}));
        Assert.assertTrue(DataUtil.isArray(new float[] {1F}));
        Assert.assertTrue(DataUtil.isArray(new double[] {1D}));
        Assert.assertTrue(DataUtil.isArray(new short[] {1}));
        Assert.assertTrue(DataUtil.isArray(new boolean[] {true}));
        Assert.assertTrue(DataUtil.isArray(new char[] {'a'}));
        Assert.assertTrue(DataUtil.isArray(new byte[] {1}));
    }
}
