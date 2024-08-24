package dbfound.test.core;

import com.nfwork.dbfound.dto.QueryResponseObject;
import org.junit.Test;

import java.util.*;

public class ResponseDataTest {

    @Test
    public void test(){
        QueryResponseObject<Map<String,Object>> object = new QueryResponseObject<>();
        List<Map<String,Object>> datas = new ArrayList<>();
        Map<String,Object> data1 = new HashMap<>();
        data1.put("user_id",1);
        datas.add(data1);

        Map<String,Object> data2 = new HashMap<>();
        data2.put("user_id",2);
        datas.add(data2);

        object.setDatas(datas);

        assert object.get() == data1;
        assert object.getMap("user_id").get("1") == data1;
        assert object.getMap("user_id").get("2") == data2;

        assert Objects.equals(object.getPropertyList("user_id").get(0) , 1);
        assert Objects.equals(object.getIntList("user_id").get(1) ,2);
        assert Objects.equals(object.getLongList("user_id").get(1) ,2L);
        assert Objects.equals(object.getStringList("user_id").get(1) ,"2");
        assert Objects.equals(object.getDoubleList("user_id").get(1) ,2d);
        assert Objects.equals(object.getFloatList("user_id").get(1) ,2f);

        assert Objects.equals(object.getProperty("user_id"),1);
        assert Objects.equals(object.getInt("user_id"),1);
        assert Objects.equals(object.getLong("user_id"),1L);
        assert Objects.equals(object.getString("user_id"),"1");
        assert Objects.equals(object.getDouble("user_id"),1d);
        assert Objects.equals(object.getFloat("user_id"),1f);
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
        map11.put("role_id","2");
        data1.getDatas().add(map11);

        QueryResponseObject<Map<String,Object>> data2 = new QueryResponseObject<>();
        data2.setDatas(new ArrayList<>());
        Map<String,Object> map2 = new HashMap<>();
        map2.put("role_id",1);
        map2.put("role_name","student");
        data2.getDatas().add(map2);

        data1.join(data2,"role_id");

        assert Objects.equals(data1.get().get("role_name"),"student");
        assert Objects.equals(data1.getDatas().get(1).get("role_name"),null);
    }
}
