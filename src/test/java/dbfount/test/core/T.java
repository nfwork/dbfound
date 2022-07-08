package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class T {
    public static void main(String[] args) {
        System.out.println();

        Context context = new Context();
        User user = new User();
        context.setParamData("user",user);
        context.setData("param.user.user_id" , "10");
        context.setData("param.user.flag" , false);
        context.setData("param.user.user_id",'1');
        System.out.println(context.getData("param.user.user_id"));
        System.out.println(JsonUtil.mapToJson(context.getDatas()));

        System.out.println(Map.class.isAssignableFrom(HashMap.class));
    }


    public static class User{
        Integer user_id;
        String user_name;
        Boolean flag = true;

        public Integer getUser_id() {
            return user_id;
        }

        public void setUser_id(Integer user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }

    }
}
