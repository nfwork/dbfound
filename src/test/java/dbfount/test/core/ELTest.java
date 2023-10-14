package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.model.reflector.Column;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.JsonUtil;
import org.junit.Test;

import java.util.*;

public class ELTest {

    @Test
    public void testCollection(){
        Set<String> set = new HashSet<>();
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

        Map<String,Object> elCache = new HashMap<>();
        System.out.println(DBFoundEL.getData("set[0]",data,elCache));
        System.out.println(DBFoundEL.getData("set[1]",data,elCache));
        System.out.println(DBFoundEL.getData("param.list[0]",data,elCache));
        System.out.println(DBFoundEL.getData("param.list[1]",data,elCache));
        System.out.println(DBFoundEL.getData("param.users[0].user_name",data,elCache));
        System.out.println(DBFoundEL.getData("param.users[1].user_name",data,elCache));
        System.out.println(JsonUtil.toJson(elCache));
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
        System.out.println(JsonUtil.toJson(l1));
        List<Map<String,Object>> l2 = context.getList("param.roles");
        System.out.println(JsonUtil.toJson(l2));
        Map<String,Object> m1 = context.getMap("param");
        System.out.println(JsonUtil.toJson(m1));
        User u1 = context.getData("param.users[0]",User.class);
        System.out.println(JsonUtil.toJson(u1));
        String s1 = context.getData("param.users[0].user_name",String.class);
        System.out.println(s1);
    }

    @Test
    public void testBeanMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",123);
        map.put("user_name","join");
        map.put("flag",true);
        map.put("tags",null);
        User user = DataUtil.convertMapToBean(map,User.class);
        System.out.println(JsonUtil.toJson(user));
        long begin = System.currentTimeMillis();
        for (int i=0;i <100000;i++){
            DataUtil.convertMapToBean(map,User.class);
        }
        System.out.println(System.currentTimeMillis() - begin);
    }

    public enum Role{
        ADMIN(1),
        STUDENT(2);

        final Integer value;

        Role(Integer value){
            this.value = value;
        }

        public Integer getValue(){
            return value;
        }
    }

    public static class User{
        @Column(name = "user_id")
        Integer userId;
        String userName;
        Boolean flag;
        Role role;
        List<String> tags;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}
