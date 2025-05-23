package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.CollectionUtil;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
import org.junit.Test;

import java.util.Objects;

public class ContextTest {

    @Test
    public void testWithBeanParam() {
        User user = new User();
        user.setUserName("john");
        user.setUserDescription("john desc");
        user.setUserId(1);
        user.setRole(Role.ADMIN);

        Context context = new Context()
                .withParam("create_by",1)
                .withParam("user",user)
                .withMapParam(CollectionUtil.asMap("user_email","nfwork@163.com"))
                .withBeanParam(user);

        assert context.getString("param.userName").equals("john");
        assert context.getInt("param.userId") == 1;
        assert context.getData("param.role") == Role.ADMIN;
        assert context.getData("param.flag") == null;
        assert context.getData("aaa") == null;

        assert context.getString("param.user_name").equals("john");
        assert context.getInt("param.user_id") == 1;
        assert context.getString("param.user_email").equals("nfwork@163.com");

        assert Objects.equals(context.getString("param.userDescription"), "john desc");
        assert Objects.equals(context.getString("param.user_description"), "john desc");
        assert Objects.equals(context.getString("param.user_desc"), null);

        assert Objects.equals(context.getString("param.user.userDescription"), "john desc");
        assert Objects.equals(context.getString("param.user.user_description"), "john desc");
        assert Objects.equals(context.getString("param.user.user_desc"), "john desc");
    }
}
