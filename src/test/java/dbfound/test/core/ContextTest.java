package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
import org.junit.Test;

public class ContextTest {

    @Test
    public void testWithBeanParam() {
        User user = new User();
        user.setUserName("john");
        user.setUserId(1);
        user.setRole(Role.ADMIN);

        Context context = new Context()
                .withParam("create_by",1)
                .withBeanParam(user);

        assert context.getString("param.userName").equals("john");
        assert context.getInt("param.userId") == 1;
        assert context.getData("param.role") == Role.ADMIN;

        assert context.getString("param.user_name").equals("john");
        assert context.getInt("param.user_id") == 1;
    }
}
