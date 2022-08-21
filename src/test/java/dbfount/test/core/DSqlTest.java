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

}
