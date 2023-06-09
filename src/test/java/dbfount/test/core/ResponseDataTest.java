package dbfount.test.core;

import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseDataTest {

    public static void main(String[] args) {
        QueryResponseObject object = new QueryResponseObject();
        List<Map> datas = new ArrayList<>();
        Map data1 = new HashMap();
        data1.put("user_id",1);
        datas.add(data1);

        Map data2 = new HashMap();
        data2.put("user_id",2);
        datas.add(data2);

        object.setDatas(datas);

        System.out.println(object.get());
        System.out.println(object.getMap("user_id"));

        System.out.println(object.getPropertyList("user_id"));
        System.out.println(object.getIntList("user_id"));
        System.out.println(object.getLongList("user_id"));
        System.out.println(object.getStringList("user_id"));
        System.out.println(object.getDoubleList("user_id"));
        System.out.println(object.getFloatList("user_id"));

        System.out.println(object.getProperty("user_id"));
        System.out.println(object.getInt("user_id"));
        System.out.println(object.getLong("user_id"));
        System.out.println(object.getString("user_id"));
        System.out.println(object.getDouble("user_id"));
        System.out.println(object.getFloat("user_id"));

    }

    @Test
    public void testJoin(){
        QueryResponseObject<Map<String,Object>> data1 = new QueryResponseObject<>();
        data1.setDatas(new ArrayList<>());
        Map<String,Object> map1 = new HashMap<>();
        map1.put("user_id",1);
        map1.put("user_name","lily");
        map1.put("role_id",1);
        data1.getDatas().add(map1);

        Map<String,Object> map11 = new HashMap<>();
        map11.put("user_id",2);
        map11.put("user_name","lucy");
        map11.put("role_id","1");
        data1.getDatas().add(map11);

        QueryResponseObject<Map<String,Object>> data2 = new QueryResponseObject<>();
        data2.setDatas(new ArrayList<>());
        Map<String,Object> map2 = new HashMap<>();
        map2.put("role_id",1);
        map2.put("role_name","student");
        data2.getDatas().add(map2);

        data1.join(data2,"role_id");
        System.out.println(JsonUtil.beanToJson(data1));
    }
}
