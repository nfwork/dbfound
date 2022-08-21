package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import org.junit.Test;
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

        result = DSqlEngine.checkWhenSql("isnull(null)",list,"_default", context);
        assert Boolean.TRUE.equals(result);

        result = DSqlEngine.checkWhenSql("!isnull(?)",list,"_default", context);
        assert Boolean.TRUE.equals(result);
    }

}
