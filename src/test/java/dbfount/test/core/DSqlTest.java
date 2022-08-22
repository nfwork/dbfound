package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DSqlTest {

    @Test
    public void testLike(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add("john");

        Boolean result = DSqlEngine.checkWhenSql("? like 'john'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? like 'joh%'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? like '%ohn'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? like '%oh%'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? like null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? like '%o%n'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? like '%o11%n'",list,"", context);
        assert Boolean.FALSE.equals(result);
    }

    @Test
    public void testIsNull(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add("john");

        Boolean result = DSqlEngine.checkWhenSql("? is null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? is not null",list,"", context);
        assert Boolean.TRUE.equals(result);

        list.clear();
        list.add(null);

        result = DSqlEngine.checkWhenSql("? is null",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? is not null",list,"", context);
        assert Boolean.FALSE.equals(result);
    }

    @Test
    public void testEquals(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add("john");

        Boolean result = DSqlEngine.checkWhenSql("? = null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? = 'john'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? != 'john'",list,"", context);
        assert Boolean.FALSE.equals(result);

        list.clear();
        list.add(10000);

        result = DSqlEngine.checkWhenSql("? = 10000",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? = '10000'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? = 9999",list,"", context);
        assert Boolean.FALSE.equals(result);

        list.clear();
        list.add(true);

        result = DSqlEngine.checkWhenSql("? = true",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? = false",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? != 1",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? = 1",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("0 = false",list,"", context);
        assert Boolean.TRUE.equals(result);
    }

    @Test
    public void testCompare(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add(10000);

        Boolean result = DSqlEngine.checkWhenSql("? > 9999",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? >= 10000",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? < 10001",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? <= 10000",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? < '99999'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? > null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? <= null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("? <= true",list,"", context);
        assert result == null;

        result = DSqlEngine.checkWhenSql("'abc' <= 'bcd'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("'222' >= '211'",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("'222' >= '222'",list,"", context);
        assert Boolean.TRUE.equals(result);
    }

    @Test
    public void testFunction(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add("john");

        Boolean result = DSqlEngine.checkWhenSql("length(?) = 4",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("char_length(?) = 4",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? = trim('john ')",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("1 = ifnull(null,1)",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("1 = if(true,1,2)",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("1 = if(null,1,2)",list,"_default", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("isnull(null)",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("!isnull(?)",list,"_default", context);
        assert Boolean.TRUE.equals(result);
    }

    @Test
    public void testComputer(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add(10000);

        Boolean result = DSqlEngine.checkWhenSql("? * 2 = 20000",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? / 100 = 100",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? + 100 = 10100",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? - 100 = 9900",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? % 100 = 0",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("(? - 100) * 2 / 2 + 100  = 10000",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("2.22 /2 = 1.11",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("9.4 % 2 = 1.4 and 0.9 * 0.9 = 0.81",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("10 / 3 * 3 = 10",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("-10 / 3 * 3 = -10",list,"", context);
        assert Boolean.TRUE.equals(result);
    }

    @Test
    public void testAndOR(){
        Context context = new Context();
        List<Object> list = new ArrayList<>();
        list.add(10000);

        Boolean result = DSqlEngine.checkWhenSql("? * 2 = 20000 and 100 = 100",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? / 100 = 99 or 100 = 100",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? / 100 = 100 or 100 = 99",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("? / 100 = 100 or null",list,"", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql(" '100' = 100 and true = false",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql(" '100' = 100 and null",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("not (100 = 100)",list,"", context);
        assert Boolean.FALSE.equals(result);

        result = DSqlEngine.checkWhenSql("! (100 = 100)",list,"", context);
        assert Boolean.FALSE.equals(result);

    }

    @Test
    public void test() {
        System.out.println(331d/447);
        BigDecimal a = new BigDecimal("331");
        BigDecimal b = new BigDecimal("447");
        System.out.println(a.divide(b,18, RoundingMode.HALF_UP).doubleValue());
    }
}
