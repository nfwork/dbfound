package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
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

    @Test
    public void testPage() {
        Context context = new Context();
        context.setParamData("limit", 5);
        QueryResponseObject<Map<String, Object>> responseObject = ModelEngine.query(context, "test/query", "page");
        assert responseObject.getDatas().size() == 5;
        assert responseObject.getTotalCounts() == 6;

        context = new Context();
        context.setParamData("limit", 10);
        responseObject = ModelEngine.query(context, "test/query", "page");
        assert responseObject.getDatas().size() == 6;
        assert responseObject.getTotalCounts() == 6;

        context = new Context();
        context.setParamData("limit", 5);
        context.setParamData("start",5);
        responseObject = ModelEngine.query(context, "test/query", "page");
        assert responseObject.getDatas().size() == 1;
        assert responseObject.getTotalCounts() == 6;
        assert Objects.equals(responseObject.getDatas().get(0).get("user_name"),"lucy4");

        context = new Context();
        context.setParamData("limit", 5);
        context.setParamData("count","not_required");
        responseObject = ModelEngine.query(context, "test/query", "page");
        assert responseObject.getDatas().size() == 5;
        assert responseObject.getTotalCounts() == -1;
    }

    @Test
    public void testAdapter() {
        Context context = new Context();
        context.setParamData("limit", 5);
        QueryResponseObject<Map<String, Object>> responseObject = ModelEngine.query(context, "test/query", "adapter");
        assert responseObject.getDatas().size() == 5;
        assert responseObject.getTotalCounts() == 6;
        assert context.getInt("param.before") == 1;
        assert context.getInt("param.count") == 1;
        assert context.getInt("param.after") == 1;

        context = new Context();
        context.setParamData("limit", 10);
        context.setParamData("user_id",1);
        responseObject = ModelEngine.query(context, "test/query", "adapter");
        assert responseObject.getDatas().size() == 1;
        assert responseObject.getTotalCounts() == 1;
        assert context.getInt("param.before") == 1;
        assert context.getInt("param.count") == null;
        assert context.getInt("param.after") == 1;
    }

    @Test
    public void testEntity() {
        Context context = new Context();
        context.setParamData("limit", 5);
        QueryResponseObject<?> responseObject = ModelEngine.query(context, "test/query", "entity");
        assert Objects.equals(responseObject.get(), 1);
    }

    @Test
    public void testBean() {
        Context context = new Context();
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "bean", User.class);
        assert responseObject.get().getRole() == Role.ADMIN;
        assert responseObject.get().getUserId() == 1;
        assert responseObject.get().getUserName().equals("lily");

        context = new Context();
        context.setParamData("role", Role.STUDENT);
        responseObject = ModelEngine.query(context, "test/query", "bean", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;
        assert responseObject.get().getUserName().equals("lucy");
    }
}