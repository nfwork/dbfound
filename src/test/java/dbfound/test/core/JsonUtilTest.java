package dbfound.test.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.util.JsonUtil;
import org.junit.Test;

import java.util.Map;

public class JsonUtilTest {

    @Test
    public void test() throws JsonProcessingException {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("hello");
        responseObject.setSuccess(Boolean.TRUE);
        String json = JsonUtil.toJson(responseObject);

        Map<?,?> map = JsonUtil.jsonToMap(json);
        assert map.size() == 4;
        assert map.get("success").equals(Boolean.TRUE);
        assert map.containsKey("outParam");
        assert map.containsKey("code");

        ResponseObject object = JsonUtil.getObjectMapper().readValue(json,ResponseObject.class);
        assert object.getMessage().equals("hello");
        assert object.isSuccess();
    }
}
