package dbfount.test.core;

import com.nfwork.dbfound.dto.QueryResponseObject;

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
}
