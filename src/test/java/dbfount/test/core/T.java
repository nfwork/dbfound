package dbfount.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.reflector.Column;
import com.nfwork.dbfound.util.JsonUtil;

public class T {
    public static void main(String[] args) {
        Context context = new Context();
        User user = new User();
        context.setParamData("user",user);

        context.setData("param.user.user_id", "10");
        context.setData("param.user.flag", true);
        context.setData("param.user.user_name", "john");
        context.setData("param.user.role", "1");

        long a1 = System.currentTimeMillis();
        for (int i=0; i< 10000;i++) {
            context.getData("param.user.user_id");
            context.getData("param.user.user_name");
            context.getData("param.user.flag");
            context.getData("param.user.role");
        }
        System.out.println(System.currentTimeMillis() - a1);

        System.out.println(context.getData("param.user.user_id"));
        System.out.println(context.getData("param.user.user_name"));
        System.out.println(context.getData("param.user.flag"));
        System.out.println(context.getData("param.user.role"));
        System.out.println(JsonUtil.mapToJson(context.getDatas()));

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
