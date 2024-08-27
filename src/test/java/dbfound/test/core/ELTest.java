package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.util.DataUtil;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
import org.junit.Test;

import java.util.*;

public class ELTest {

    @Test
    public void testCollection(){
        Set<String> set = new LinkedHashSet<>();
        set.add("xiao");
        set.add("ming");
        Map<String,Object> data = new HashMap<>();
        data.put("set",set);

        List<String> list = new ArrayList<>();
        Map<String,Object> param = new HashMap<>();
        param.put("list",list);
        list.add("hello");
        list.add("world");
        data.put("param",param);

        List<User> users = new ArrayList<>();
        User lucy = new User();
        lucy.setUserName("lucy");
        users.add(lucy);
        User lily = new User();
        lily.setUserName("lily");
        users.add(lily);
        param.put("users", users);

        assert "xiao".equals(DBFoundEL.getData("set[0]",data));
        assert "ming".equals(DBFoundEL.getData("set[1]",data));
        assert "hello".equals(DBFoundEL.getData("param.list[0]",data));
        assert "world".equals(DBFoundEL.getData("param.list[1]",data));
        assert lucy == DBFoundEL.getData("param.users[0]",data);
        assert lily == DBFoundEL.getData("param.users[1]",data);
        assert "lucy".equals(DBFoundEL.getData("param.users[0].user_name",data));
        assert "lily".equals(DBFoundEL.getData("param.users[1].user_name",data));

        Map<String,Object> elCache = new HashMap<>();
        assert "xiao".equals(DBFoundEL.getData("set[0]",data,elCache));
        assert "ming".equals(DBFoundEL.getData("set[1]",data,elCache));
        assert "hello".equals(DBFoundEL.getData("param.list[0]",data,elCache));
        assert "world".equals(DBFoundEL.getData("param.list[1]",data,elCache));
        assert lucy == DBFoundEL.getData("param.users[0]",data,elCache);
        assert lily == DBFoundEL.getData("param.users[1]",data,elCache);
        assert "lucy".equals(DBFoundEL.getData("param.users[0].user_name",data,elCache));
        assert "lily".equals(DBFoundEL.getData("param.users[1].user_name",data,elCache));

        List<String> c1 = new ArrayList<>();
        List<String > c3 = new ArrayList<>();
        c3.add("lily");
        c3.add("lucy");
        DBFoundEL.setData("class",data,new ArrayList<>());
        DBFoundEL.setData("class[1]", data, c1);
        DBFoundEL.setData("class[3]", data, c3);
        DBFoundEL.setData("class[36]", data, c3);

        assert "lily".equals(DBFoundEL.getData("class[3][0]",data,elCache));
        assert "lily".equals(DBFoundEL.getData("class[ 3][ 0 ]",data,elCache));
        assert "lucy".equals(DBFoundEL.getData("class[3][1]",data));
        assert "lily".equals(DBFoundEL.getData("class[3].value[0]",data,elCache));
        assert "lucy".equals(DBFoundEL.getData("class[3].value[1]",data));
        assert DBFoundEL.getData("class[2][0]",data,elCache) == null;
        assert DBFoundEL.getData("class[2][1]",data) == null;
        assert DBFoundEL.getData("class[1][1]",data) == null;
        assert DBFoundEL.getData("class[36]",data) == c3;
        assert DBFoundEL.getData("class[36 ]",data) == c3;
        assert DBFoundEL.getData("class[35]",data) == null;
    }

    @Test
    public void testElCache(){
        Context context = new Context();

        User user = new User();
        context.setParamData("user",user);

        List<User> users = new ArrayList<>();
        users.add(user);
        context.setParamData("users",users);

        context.setData("param.user.user_id", "10");
        context.setData("param.user.flag", true);
        context.setData("param.user.user_name", "john");
        context.setData("param.user.role", "1");

        Map<String, Object> elCache = new HashMap<>();

        assert (int)context.getData("param.users[0].user_id.value", elCache) == 10;
        assert context.getData("param.users[0].userName", elCache).toString().equals("john");
        assert (Boolean) context.getData("param.users[0].flag", elCache);
        assert context.getData("param.users[0].role", elCache) == Role.ADMIN;
    }

    @Test
    public void testEl(){
        Context context = new Context();

        User user = new User();
        context.setParamData("user",user);

        List<User> users = new ArrayList<>();
        users.add(user);
        context.setParamData("users",users);

        context.setData("param.user.user_id", "10");
        context.setData("param.user.flag", true);
        context.setData("param.user.user_name", "john");
        context.setData("param.user.role", "1");

        assert (int)context.getData("param.users[0].user_id.value") == 10;
        assert context.getData("param.users[0].userName").toString().equals("john");
        assert (Boolean) context.getData("param.users[0].flag");
        assert context.getData("param.users[0].role") == Role.ADMIN;
    }

    @Test
    public void testSetData(){
        Context context = new Context();
        User user = new User();
        user.setUserName("lily");
        context.setData("param.user", user);
        context.setParamData("user.role", Role.STUDENT);
        assert context.getString("param.user.user_name").equals(user.getUserName());
        assert context.getData("param.user.role") == Role.STUDENT;

        context.setData("param.hello.world",1);
        assert context.getInt("param.hello.world") == 1;

        context.setParamData("hello.john[1]",1);
        assert context.getInt("param.hello.john[1]") == 1;

        context.setParamData("hello.john[10]",10);
        assert context.getInt("param.hello.john[10]") == 10;

        context.setParamData("hello.john[5].name[10]",100);
        assert context.getInt("param.hello.john[5].name[10]") == 100;

        assert context.getInt("param.hello.john[9].name[10]") == null;
        assert context.getInt("param.hello1.john[9].name[10]") == null;
    }

    @Test
    public void testSizeAndLength(){
        Context context = new Context();

        User user = new User();
        List<User> users = new ArrayList<>();
        users.add(user);
        context.setParamData("users",users);
        context.setParamData("user_name","john");

        assert (int)context.getData("param.users.size") == 1;
        assert (int)context.getData("param.user_name.length") == 4;
    }

    @Test
    public void testContext(){
        User user = new User();
        user.setUserName("join");
        List<User> users = new ArrayList<>();
        users.add(user);
        Context context = new Context();
        context.setParamData("users",users);
        context.setData("param.roles[0]", new HashMap<>());

        List<User> l1 = context.getList("param.users");
        assert l1.size() == 1;
        List<Map<String,Object>> l2 = context.getList("param.roles");
        assert l2.size() == 1;
        Map<String,Object> m1 = context.getMap("param");
        assert m1.get("users") == users;
        User u1 = context.getData("param.users[0]",User.class);
        assert u1 ==user;
        String s1 = context.getData("param.users[0].user_name",String.class);
        assert "join".equals(s1);
    }

    @Test
    public void testBeanMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",123);
        map.put("user_name","join");
        map.put("flag",true);
        map.put("tags",null);
        User user = DataUtil.convertMapToBean(map, User.class);
        assert "join".equals(user.getUserName());
        assert 123 == user.getUserId();
        assert user.getFlag();
        assert user.getTags() == null;
    }
}
