package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.CollectionUtil;
import dbfound.test.entity.Role;
import dbfound.test.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

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

        context.setData("param.user.user_desc","john asc");
        assert Objects.equals(context.getString("param.user.user_desc"), "john asc");
        context.setData("param.user.userDescription","john 123");
        assert Objects.equals(context.getString("param.user.userDescription"), "john 123");
        context.setData("param.user.user_description","john 456");
        assert Objects.equals(context.getString("param.user.user_description"), "john 456");
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

    @Test
    public void testRootDataMapReuse() {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "john");
        Map<String, Object> request = new HashMap<>();
        request.put("token", "request-token");
        Map<String, Object> root = new HashMap<>();
        root.put("param", param);
        root.put("request", request);

        Context context = new Context(root);

        Assert.assertSame(root, context.getDatas());
        Assert.assertSame(param, context.getParamDatas());
        Assert.assertSame(request, context.getRequestDatas());
        Assert.assertEquals("john", context.getData("param.name"));

        context.setParamData("name", "lily");
        context.setRequestData("token", "new-token");
        Assert.assertEquals("lily", param.get("name"));
        Assert.assertEquals("new-token", request.get("token"));
    }

    @Test
    public void testScopeMapLazyCreateAndRootPathRead() {
        Context context = new Context();

        Map<String, Object> param = context.getParamDatas();
        Map<String, Object> outParam = context.getOutParamDatas();
        Map<String, Object> request = context.getRequestDatas();
        Map<String, Object> cookie = context.getCookieDatas();
        Map<String, Object> header = context.getHeaderDatas();

        Assert.assertSame(param, context.getData("param"));
        Assert.assertSame(outParam, context.getData("outParam"));
        Assert.assertSame(request, context.getData("request"));
        Assert.assertSame(cookie, context.getData("cookie"));
        Assert.assertSame(header, context.getData("header"));
        Assert.assertSame(param, context.getDatas().get("param"));
        Assert.assertSame(outParam, context.getDatas().get("outParam"));
        Assert.assertSame(request, context.getDatas().get("request"));
        Assert.assertSame(cookie, context.getDatas().get("cookie"));
        Assert.assertSame(header, context.getDatas().get("header"));
    }

    @Test
    public void testScopeGetterReplacesNonMapRootValue() {
        Map<String, Object> root = new HashMap<>();
        root.put("param", "not-map");
        root.put("outParam", 1);
        root.put("request", Collections.singletonList("not-map"));
        root.put("cookie", new Object());
        root.put("header", Boolean.TRUE);

        Context context = new Context(root);

        Assert.assertTrue(context.getParamDatas().isEmpty());
        Assert.assertTrue(context.getOutParamDatas().isEmpty());
        Assert.assertTrue(context.getRequestDatas().isEmpty());
        Assert.assertTrue(context.getCookieDatas().isEmpty());
        Assert.assertTrue(context.getHeaderDatas().isEmpty());
        Assert.assertSame(context.getParamDatas(), root.get("param"));
        Assert.assertSame(context.getOutParamDatas(), root.get("outParam"));
        Assert.assertSame(context.getRequestDatas(), root.get("request"));
        Assert.assertSame(context.getCookieDatas(), root.get("cookie"));
        Assert.assertSame(context.getHeaderDatas(), root.get("header"));
    }

    @Test
    public void testSetDataScopeRouting() {
        boolean openSession = DBFoundConfig.isOpenSession();
        setDBFoundConfigField("openSession", true);
        try {
            Context context = new Context();

            context.setData("param.name", "john");
            context.setData("outParam.count", 2);
            context.setData("request.message", "ok");
            context.setData("session.token", "session-token");

            Assert.assertEquals("john", context.getData("param.name"));
            Assert.assertEquals(2, context.getData("outParam.count"));
            Assert.assertEquals("ok", context.getData("request.message"));
            Assert.assertEquals("session-token", context.getData("session.token"));
        } finally {
            setDBFoundConfigField("openSession", openSession);
        }
    }

    @Test
    public void testNestedParamAndOutParamData() {
        Context context = new Context();

        context.setData("param.user.name", "john");
        context.setOutParamData("rows[0].id", 10);
        context.setData("outParam.rows[0].name", "first");

        Assert.assertEquals("john", context.getData("param.user.name"));
        Assert.assertEquals(10, context.getData("outParam.rows[0].id"));
        Assert.assertEquals("first", context.getData("outParam.rows[0].name"));
        Assert.assertEquals(1, context.getDataLength("outParam.rows"));
    }

    @Test
    public void testGetDataWithElCache() {
        Context context = new Context();
        context.setData("param.users[0].name", "john");
        Map<String, Object> elCache = new HashMap<>();

        Assert.assertEquals("john", context.getData("param.users[0].name", elCache));
        Assert.assertTrue(elCache.containsKey("param.users"));
    }

    @Test
    public void testGetDataLength() {
        Context context = new Context();
        context.setParamData("names", Arrays.asList("john", "lily"));
        context.setParamData("ids", new int[]{1, 2, 3});
        context.setParamData("name", "john");

        Assert.assertEquals(2, context.getDataLength("param.names"));
        Assert.assertEquals(3, context.getDataLength("param.ids"));
        Assert.assertEquals(-1, context.getDataLength("param.name"));
        Assert.assertEquals(-1, context.getDataLength("param.missing"));
    }

    @Test
    public void testGetDataTypeConversion() throws Exception {
        Context context = new Context()
                .withParam("intValue", "10")
                .withParam("longValue", "100")
                .withParam("floatValue", "1.5")
                .withParam("doubleValue", 2)
                .withParam("booleanValue", "true")
                .withParam("shortValue", "3")
                .withParam("byteValue", "4")
                .withParam("dateValue", "2024-01-02");

        Assert.assertEquals(Integer.valueOf(10), context.getData("param.intValue", Integer.class));
        Assert.assertEquals(Long.valueOf(100), context.getData("param.longValue", Long.class));
        Assert.assertEquals(Float.valueOf(1.5f), context.getData("param.floatValue", Float.class));
        Assert.assertEquals(Double.valueOf(2), context.getData("param.doubleValue", Double.class));
        Assert.assertEquals(Boolean.TRUE, context.getData("param.booleanValue", Boolean.class));
        Assert.assertEquals(Short.valueOf((short) 3), context.getData("param.shortValue", Short.class));
        Assert.assertEquals(Byte.valueOf((byte) 4), context.getData("param.byteValue", Byte.class));
        Assert.assertEquals(new SimpleDateFormat(DBFoundConfig.getDateFormat()).parse("2024-01-02"),
                context.getData("param.dateValue", Date.class));
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSetDataRejectsNullName() {
        new Context().setData(null, "value");
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSetDataRejectsEmptyName() {
        new Context().setData("", "value");
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSetDataRejectsUnsupportedScope() {
        new Context().setData("cookie.token", "value");
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSetRequestDataRejectsNestedName() {
        new Context().setData("request.user.name", "john");
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSetSessionDataRejectsIndexedName() {
        boolean openSession = DBFoundConfig.isOpenSession();
        setDBFoundConfigField("openSession", true);
        try {
            new Context().setData("session.tokens[0]", "value");
        } finally {
            setDBFoundConfigField("openSession", openSession);
        }
    }

    @Test(expected = DBFoundRuntimeException.class)
    public void testSessionDataRequiresOpenSession() {
        boolean openSession = DBFoundConfig.isOpenSession();
        setDBFoundConfigField("openSession", false);
        try {
            new Context().setData("session.token", "value");
        } finally {
            setDBFoundConfigField("openSession", openSession);
        }
    }

    private static void setDBFoundConfigField(String name, Object value) {
        try {
            Field field = DBFoundConfig.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
