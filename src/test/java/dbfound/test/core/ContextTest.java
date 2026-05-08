package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.CollectionUtil;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
import org.junit.Test;

import java.math.BigDecimal;
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

    @Test
    public void testGetBigDecimal() {
        Context context = new Context()
                .withParam("amount", "123.45")
                .withParam("count", 100)
                .withParam("price", new BigDecimal("99.99"))
                .withParam("empty", null);

        assert Objects.equals(context.getBigDecimal("param.amount"), new BigDecimal("123.45"));
        assert Objects.equals(context.getBigDecimal("param.count"), new BigDecimal("100"));
        assert Objects.equals(context.getBigDecimal("param.price"), new BigDecimal("99.99"));
        assert context.getBigDecimal("param.empty") == null;
        assert Objects.equals(context.getData("param.amount", BigDecimal.class), new BigDecimal("123.45"));
    }
}
