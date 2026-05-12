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
    public void testListIndex(){
        Map<String,Object> data = new HashMap<>();
        List<String> linkedList = new LinkedList<>();
        linkedList.add("hello");
        linkedList.add("world");
        data.put("list", linkedList);

        assert "hello".equals(DBFoundEL.getData("list[0]",data));
        assert "world".equals(DBFoundEL.getData("list[1]",data));
        assert DBFoundEL.getData("list[2]",data) == null;
    }

    @Test
    public void testExpressSplitCompatibility(){
        Map<String,Object> data = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("hello");
        param.put("list", list);
        data.put("param", param);

        assert list == DBFoundEL.getData("param.list.",data);
        assert list == DBFoundEL.getData("param.list..",data);
        assert "hello".equals(DBFoundEL.getData("param.list[0]..",data));
        assert DBFoundEL.getData("param..list",data) == null;
        assert DBFoundEL.getData("...",data) == null;
    }

    @Test(expected = NumberFormatException.class)
    public void testIndexOverflow(){
        Map<String,Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        DBFoundEL.getData("list[2147483648]",data);
    }

    @Test
    public void testIndexMaxValue(){
        Map<String,Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        assert DBFoundEL.getData("list[2147483647]",data) == null;
    }

    @Test
    public void testArrayIndex(){
        Map<String,Object> data = new HashMap<>();
        data.put("arr", new String[]{"a","b","c"});
        data.put("nums", new int[]{10,20,30});

        assert "a".equals(DBFoundEL.getData("arr[0]",data));
        assert "c".equals(DBFoundEL.getData("arr[2]",data));
        assert DBFoundEL.getData("arr[3]",data) == null;
        assert (Integer) DBFoundEL.getData("nums[1]",data) == 20;
    }

    @Test
    public void testSetDataTrailingDot(){
        Map<String,Object> root = new HashMap<>();
        DBFoundEL.setData("a.b.", root, 1);
        DBFoundEL.setData("a.c..", root, 2);

        assert ((Integer) DBFoundEL.getData("a.b", root)) == 1;
        assert ((Integer) DBFoundEL.getData("a.c", root)) == 2;
    }

    @Test
    public void testMapNullValueDistinguished(){
        Map<String,Object> root = new HashMap<>();
        Map<String,Object> param = new HashMap<>();

        param.put("userName", "wrong");
        root.put("param", param);
        assert DBFoundEL.getData("param.user_name", root).equals("wrong");
        assert DBFoundEL.getData("param.userName", root).equals("wrong");

        param.put("user_name", null);
        assert DBFoundEL.getData("param.user_name", root) == null;
        assert DBFoundEL.getData("param.userName", root).equals("wrong");
    }

    @Test
    public void testInvalidIndexFallback(){
        Map<String,Object> data = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("hello");
        data.put("list", list);

        assert DBFoundEL.getData("list[]",data) == null;
        assert DBFoundEL.getData("list[abc]",data) == null;
        assert "hello".equals(DBFoundEL.getData("list[0][abc]",data));
    }

    @Test(expected = NumberFormatException.class)
    public void testBlankIndexCompatibility(){
        Map<String,Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        DBFoundEL.getData("list[ ]",data);
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
        assert (int)context.getData("param.users[0].userId.value", elCache) == 10;
        assert context.getData("param.users[0].userName", elCache).toString().equals("john");
        assert context.getData("param.users[0].user_name", elCache).toString().equals("john");
        assert (Boolean) context.getData("param.users[0].flag", elCache);
        assert context.getData("param.users[0].role", elCache) == Role.ADMIN;

        context.setData("param.user.userName", "lily");
        assert context.getData("param.users[0].user_name", elCache).toString().equals("lily");
        assert context.getData("param.users[0]") instanceof User;

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
