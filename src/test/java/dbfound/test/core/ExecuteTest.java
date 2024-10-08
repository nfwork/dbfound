package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.CollectionUtil;
import com.nfwork.dbfound.util.TransactionUtil;
import dbfound.test.entity.Role;
import org.junit.Test;

import java.util.*;

public class ExecuteTest {

    @Test
    public void test() {
        Context context = new Context();

        int a = 0;
        try {
            ModelEngine.execute(context, "test/execute", null);
        }catch (CollisionException exception){
            assert exception.getMessage().equals("role不能为空");
            assert exception.getCode().equals("10010");
            a = 1;
        }
        assert a==1;

        Map<String,String> map1 = new HashMap<>();
        map1.put("code","user_id");
        map1.put("value","1");
        Map<String,String> map2 = new HashMap<>();
        map2.put("code","user_name");
        map2.put("value","lucy");
        context.setParamData("fields", CollectionUtil.asList(map1,map2));
        context.setParamData("role", Role.STUDENT);

        ResponseObject responseObject = ModelEngine.execute(context, "test/execute", null);
        assert Objects.equals(responseObject.getOutParam().get("role_id"), 2L);
        assert Objects.equals(responseObject.getOutParam().get("user_id"), "1");
        assert Objects.equals(responseObject.getOutParam().get("user_name"), "lucy");
    }

    @Test
    public void testForPart(){
        Context context = new Context();
        Map<String,String> map1 = new HashMap<>();
        map1.put("code","user_id");
        map1.put("value","1");
        Map<String,String> map2 = new HashMap<>();
        map2.put("code","user_name");
        map2.put("value","lucy");
        context.setParamData("fields1", CollectionUtil.asList(map1,map2));
        context.setParamData("fields2", CollectionUtil.asList(map1,map2));
        ResponseObject responseObject = ModelEngine.execute(context, "test/execute", "forPart");
        assert Objects.equals(responseObject.getOutParam().get("user_id"), "1");
        assert Objects.equals(responseObject.getOutParam().get("user_name"), "lucy");
    }

    @Test
    public void testIfPart() {
        Context context = new Context();
        context.setParamData("user_id",1);
        context.setParamData("user_code", "hello");
        ResponseObject responseObject = ModelEngine.execute(context, "test/execute", "ifPart");
        assert Objects.equals(responseObject.getOutParam().get("user_id"), 1L);
        assert Objects.equals(responseObject.getOutParam().get("user_name"), null);

        context.setParamData("user_name", "lily");
        responseObject = ModelEngine.execute(context, "test/execute", "ifPart");
        assert Objects.equals(responseObject.getOutParam().get("user_name"), "lily");
    }

    @Test
    public void testBatchSql() {
        Context context = new Context();
        Integer[] users = {1,2};
        context.setParamData("users",users);
        ResponseObject responseObject = ModelEngine.execute(context, "test/execute", "batchSql");
        assert Objects.equals(responseObject.getOutParam().get("user_id_0"), 1L);
        assert Objects.equals(responseObject.getOutParam().get("user_id_1"), 2L);
    }

    @Test
    public void testBatchExecuteSql() {
        Context context = new Context();
        String[] users1 = {"1","2",null,"","3","4","5"};
        context.setParamData("users",users1);
        context.setParamData("users_copy",users1);
        ModelEngine.execute(context, "test/execute", "batchExecuteSql");
    }

    @Test
    public void testBatchInsert() throws Exception {
        Context context = new Context();
        String[] roles = {"batch_role_0", "batch_role_1", "batch_role_2", "batch_role_3", "batch_role_4"};
        context.setParamData("roles", roles);
        TransactionUtil.executeWithoutResult(context,()-> {
            ModelEngine.execute(context, "test/execute", "batchInsert");
            assert context.getInt("outParam.num") == 5;
            List<Map<String, Object>> roleList = ModelEngine.query(context, "test/execute", "getRoles").getDatas();
            assert roleList.get(1).get("role_code").equals("batch_role_1");
            assert roleList.get(4).get("role_code").equals("batch_role_4");
            assert roleList.get(3).get("role_description").equals("batch_role_3");
            assert roleList.size() == 5;
            ModelEngine.execute(context, "test/execute", "deleteRoles");
            assert context.getInt("outParam.delete_num") == 5;
        });
    }

    @Test
    public void testAdapter() {
        Context context = new Context();
        ModelEngine.execute(context, "test/execute", "adapter");
        assert context.getInt("param.before") == 1;
        assert context.getInt("param.after") == 1;
    }

    @Test
    public void testGeneratedKey() throws Exception {
        Context context = new Context();
        TransactionUtil.executeWithoutResult(context,()->{
            ResponseObject responseObject = ModelEngine.execute(context, "test/execute", "generatedKey");
            assert responseObject.getOutParam().get("role_id") != null;

            Integer role_id = context.getInt("outParam.role_id");
            context.setParamData("role_id" ,role_id);
            responseObject = ModelEngine.execute(context, "test/execute", "affectedCount");
            assert responseObject.getOutParam().get("delete_num") != null;
            assert context.getInt("outParam.delete_num") == 1;
        });
    }

    @Test
    public void testEmpty() {
        Context context = new Context();
        context.setParamData("user_name","");
        context.setParamData("names", CollectionUtil.asList("","1",""));
        ModelEngine.execute(context, "test/execute", "empty");
        assert context.getString("outParam.user_name").equals("lucy");
    }

    @Test
    public void testDefaultValue() {
        Context context = new Context();
        context.setParamData("user_name","");
        context.setParamData("names", CollectionUtil.asList("","1",""));
        ModelEngine.execute(context, "test/execute", "defaultValue");
        assert context.getString("outParam.user_name").equals("lucy");
    }

}
