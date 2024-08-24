package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import org.junit.Test;

import java.util.*;

public class QueryTest {

    @Test
    public void testQuery(){
        Context context = new Context();
        context.setParamData("user_id",1);
        List<Map<String,Object>> datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.size() ==1;
        assert datas.get(0).get("user_name").equals("lily");

        context = new Context();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        context.setParamData("ids",list);
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.size() == 2;
        assert datas.get(1).get("user_name").equals("lucy");

        context = new Context();
        List<String> fields = new ArrayList<>();
        fields.add("user_id");
        fields.add("user_code");
        context.setParamData("fields",fields);
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.get(0).containsKey("user_id");
        assert !datas.get(0).containsKey("user_name");
        assert datas.get(0).containsKey("user_code");

        context = new Context();
        List<String> orderFields = new ArrayList<>();
        orderFields.add("user_id desc");
        orderFields.add("user_code");
        context.setParamData("order_fields",orderFields);
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.get(0).get("user_name").equals("lucy");
    }

}
