package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.exception.VerifyException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.CollectionUtil;
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
        context.setParamData("ids", CollectionUtil.asSet(1, 2));
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.size() == 2;
        assert datas.get(1).get("user_name").equals("lucy");

        context = new Context();
        context.setParamData("ids", CollectionUtil.asList(1, 2));
        context.setParamData("fields",CollectionUtil.asList("user_id","user_code"));
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.get(0).containsKey("user_id");
        assert !datas.get(0).containsKey("user_name");
        assert datas.get(0).containsKey("user_code");

        context = new Context();
        context.setParamData("order_fields", CollectionUtil.asList("user_id desc","user_code"));
        datas = ModelEngine.query(context,"test/query",null).getDatas();
        assert datas.get(0).get("user_name").equals("lucy1");
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

        responseObject = ModelEngine.query(context, "test/query", "entity", Role.class);
        assert Objects.equals(responseObject.get(), Role.ADMIN);
    }

    @Test
    public void testBean() {
        Context context = new Context();
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "bean", User.class);
        assert responseObject.get().getRole() == Role.ADMIN;
        assert responseObject.get().getUserId() == 1;
        assert responseObject.get().getUserName().equals("lily");
        assert responseObject.get().getCreateDate() != null;

        context = new Context();
        context.setParamData("role", Role.STUDENT);
        responseObject = ModelEngine.query(context, "test/query", "bean", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;
        assert responseObject.get().getUserName().equals("lucy");
    }

    @Test
    public void testVerify() {
        Context context = new Context();

        int a = 0;
        try {
            ModelEngine.query(context, "test/query", "verifier", User.class);
        }catch (VerifyException exception){
            assert exception.getMessage().equals("role不能为空");
            assert exception.getCode().equals("10010");
            a = 1;
        }
        assert a==1;
        context.setParamData("role", Role.STUDENT);
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "verifier", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;
    }

    @Test
    public void testAutoCompletion() {
        Context context = new Context();
        context.setParamData("role", Role.STUDENT);
        context.setParamData("user_id", 2);
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "autoCompletion", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;
    }

    @Test
    public void testClauseInSqlPart(){
        Context context = new Context();
        context.setParamData("role", Role.STUDENT);
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "clauseInSqlPart", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;

        context = new Context();
        context.setParamData("user_id", 1);
        responseObject = ModelEngine.query(context, "test/query", "clauseInSqlPart", User.class);
        assert responseObject.get().getRole() == Role.ADMIN;
        assert responseObject.getDatas().size() == 1;
    }

    @Test
    public void testFilter(){
        Context context = new Context();
        QueryResponseObject<User> responseObject = ModelEngine.query(context, "test/query", "filter", User.class);
        assert responseObject.get().getRole() == Role.STUDENT;
        assert responseObject.get().getUserId() == 2;

        context = new Context();
        context.setParamData("role", Role.ADMIN);
        responseObject = ModelEngine.query(context, "test/query", "filter", User.class);
        assert responseObject.getDatas().isEmpty();

        context = new Context();
        context.setParamData("ids", CollectionUtil.asList(1,3));
        responseObject = ModelEngine.query(context, "test/query", "filter", User.class);
        assert responseObject.getDatas().isEmpty();

        context = new Context();
        context.setParamData("ids", new ArrayList<>());
        responseObject = ModelEngine.query(context, "test/query", "filter", User.class);
        assert responseObject.getDatas().size() == 1;
    }
}
