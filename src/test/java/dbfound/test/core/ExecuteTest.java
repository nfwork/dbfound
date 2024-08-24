package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.model.ModelEngine;
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
        List<Map<String,String>> list = new ArrayList<>();
        list.add(map1);
        list.add(map2);
        context.setParamData("fields", list);
        context.setParamData("role", Role.STUDENT);

        ResponseObject responseObject = ModelEngine.execute(context, "test/execute", null);
        assert Objects.equals(responseObject.getOutParam().get("role_id"), 2L);
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

        ModelEngine.execute(context, "test/execute", "batchExecuteSql");
    }

    @Test
    public void testAdapter() {
        Context context = new Context();
        ModelEngine.execute(context, "test/execute", "adapter");
        assert context.getInt("param.before") == 1;
        assert context.getInt("param.after") == 1;
    }
}
