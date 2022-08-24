package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.reflector.Column;
import com.nfwork.dbfound.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ELTest {

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
    }
}