package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.CollectionUtil;
import dbfound.test.entity.User;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelEngineTest {

    @Test
    public void testModelEngine() {
        QueryResponseObject<?> object= ModelEngine.query(new Context(),"test/query","");
        assert !object.getDatas().isEmpty();

        QueryResponseObject<?> object1 = ModelEngine.query(new Context(),"test/query","", User.class);
        assert object1.getDatas().get(0) instanceof User;

        User user = ModelEngine.queryOne(new Context(),"test/query","", User.class);
        assert Objects.requireNonNull(user).getUserName() != null;

        Map<String,Object> map = ModelEngine.queryOne(new Context(),"test/query","");
        assert !Objects.requireNonNull(map).isEmpty();

        List<Map<String,Object>> list = ModelEngine.queryList(new Context(),"test/query","");
        assert !list.isEmpty();
        assert list.get(0).get("user_name") != null;

        List<User> userList = ModelEngine.queryList(new Context(),"test/query","", User.class);
        assert !userList.isEmpty();
        assert userList.get(0).getUserName() != null;

        ResponseObject responseObject = ModelEngine.execute(new Context().withParam("role",1),"test/execute","mt");
        assert responseObject.isSuccess();

        responseObject = ModelEngine.execute(new Context().withParam("role",1),"test/execute","mt","param");
        assert responseObject.isSuccess();

        Context context = new Context().withParam("data",CollectionUtil.asMap("role",1));
        responseObject = ModelEngine.execute(context,"test/execute","mt","param.data" );
        assert responseObject.isSuccess();
        assert Objects.equals(responseObject.getOutParam("role_id") , 1L);

        context = new Context().withParam("GridData",CollectionUtil.asList(CollectionUtil.asMap("role",1)));
        responseObject = ModelEngine.batchExecute(context,"test/execute","mt");
        assert responseObject.isSuccess();
        assert Objects.equals(responseObject.getOutParam("role_id") , 1L);

        context = new Context().withParam("data",CollectionUtil.asList(CollectionUtil.asMap("role",1)));
        responseObject = ModelEngine.batchExecute(context,"test/execute","mt","param.data");
        assert responseObject.isSuccess();
        assert Objects.equals(responseObject.getOutParam("role_id") , 1L);

    }
}
