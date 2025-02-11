package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.CollectionUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class SqlPartELseIfTest {


    @Test
    public void testElse(){
        Context context = new Context();
        context.setParamData("fields",CollectionUtil.asList("user_id","user_code"));
        List<Map<String,Object>> datas = ModelEngine.query(context,"test/sqlPart",null).getDatas();
        assert datas.get(0).containsKey("user_id");
        assert !datas.get(0).containsKey("user_name");
        assert datas.get(0).containsKey("user_code");

        context = new Context();
        context.setParamData("name_field",CollectionUtil.asList("user_name"));
        datas = ModelEngine.query(context,"test/sqlPart",null).getDatas();
        assert datas.get(0).containsKey("user_name");
        assert !datas.get(0).containsKey("user_id");

        context = new Context();
        datas = ModelEngine.query(context,"test/sqlPart",null).getDatas();
        assert datas.get(0).containsKey("user_name");
        assert datas.get(0).containsKey("user_id");
    }

    @Test
    public void testTwoElse(){
        Context context = new Context();
        context.setParamData("name_field",CollectionUtil.asList("user_name"));
        List<Map<String,Object>> datas = ModelEngine.query(context,"test/sqlPart","two").getDatas();
        assert datas.get(0).containsKey("number");
        assert !datas.get(0).containsKey("index");
        assert datas.get(0).containsKey("user_name");
        assert !datas.get(0).containsKey("user_code");

        context = new Context();
        context.setParamData("code_field",CollectionUtil.asList("user_code"));
        datas = ModelEngine.query(context,"test/sqlPart","two").getDatas();
        assert !datas.get(0).containsKey("number");
        assert datas.get(0).containsKey("index");
        assert !datas.get(0).containsKey("user_name");
        assert datas.get(0).containsKey("user_code");
    }
}
