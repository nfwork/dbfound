package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.base.SimpleItemList;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.model.bean.SqlEntity;
import com.nfwork.dbfound.util.CollectionUtil;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LocalDateUtil;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

public class SqlEntityTest extends SqlEntity {

    @Test
    public void testParam(){
        Map<String,Object> elCache = new HashMap<>();
        Context context = new Context();
        context.setParamData("user_name","lily");
        context.setParamData("user_code","10010");
        Param userName = new Param();
        userName.setName("user_name");
        setParam(userName,context,"param", context.getParamDatas(), elCache);
        assert userName.getStringValue().equals("lily");
        assert userName.getSourcePathHistory().equals("param.user_name");

        Param userCode = new Param();
        userCode.setName("uc");
        userCode.setSourcePath("param.user_code");
        setParam(userCode,context,"param", context.getParamDatas(), elCache);
        assert userCode.getStringValue().equals("10010");
        assert userCode.getSourcePathHistory().equals("param.user_code");

        userCode = new Param();
        userCode.setName("user_code");
        userCode.setScope("param");
        setParam(userCode,context,"request", context.getRequestDatas(), elCache);
        assert userCode.getStringValue().equals("10010");
        assert userCode.getSourcePathHistory().equals("param.user_code");

        context.setParamData("user_code","");
        userCode = new Param();
        userCode.setName("user_code");
        setParam(userCode,context,"param", context.getParamDatas(), elCache);
        assert userCode.getStringValue() == null;
        userCode.setEmptyAsNull(false);
        setParam(userCode,context,"param", context.getParamDatas(), elCache);
        assert userCode.getStringValue().isEmpty();
    }

    @Test
    public void testGetExecuteSql(){
        Param ids = new Param();
        ids.setName("ids");
        ids.setDataType(DataType.COLLECTION);
        String[] value = new String[]{"1","2","3"};
        ids.setValue(value);

        Map<String,Param> params = new HashMap<>();
        params.put(ids.getName(),ids);
        List<Object> exeParam = new ArrayList<>();

        String sql = getExecuteSql("id in (${@ids})",params,exeParam);
        assert sql.equals("id in (?,?,?)");
        assert exeParam.size() == 3;
        assert "3".equals(exeParam.get(2));

        exeParam.clear();
        sql = getExecuteSql("id in (#{@ids})",params,exeParam);
        assert sql.equals("id in (1,2,3)");
        assert exeParam.isEmpty();

        Param tmp = new Param();
        tmp.setName("tmp");
        tmp.setValue("id in (${@ids})");
        params.put(tmp.getName(),tmp);
        sql = getExecuteSql("select * from dual where #{@tmp}",params,exeParam);
        System.out.println(sql);
        assert sql.equals("select * from dual where id in (?,?,?)");
        assert exeParam.size() == 3;
        assert "2".equals(exeParam.get(1));

        Param fields = new Param();
        fields.setName("fields");
        fields.setDataType(DataType.COLLECTION);
        fields.setValue(new String[]{"user_id","user_name",null});
        params.put("fields",fields);
        Param limit = new Param();
        limit.setName("limit");
        limit.setValue(10);
        limit.setDataType(DataType.NUMBER);
        params.put("limit",limit);
        exeParam.clear();
        sql = getExecuteSql("select #{@fields} from user where #{@tmp} limit ${@limit}",params,exeParam);
        System.out.println(sql);
        assert sql.equals("select user_id,user_name from user where id in (?,?,?) limit ?");
        assert exeParam.size() == 4;
        System.out.println(exeParam);
        assert Objects.equals(10,exeParam.get(3));
    }

    @Test
    public void testInitParamValue(){
        Param param = new Param();
        param.setDataType(DataType.VARCHAR);
        Object value = new String[]{"1","2","3"};
        param.setValue(value);
        initParamValue(param);
        assert param.getValue().toString().equals(JsonUtil.toJson(value));

        param = new Param();
        param.setDataType(DataType.DATE);
        value = System.currentTimeMillis();
        param.setValue(value);
        initParamValue(param);
        assert param.getValue() instanceof Date;

        param.setValue(value.toString());
        initParamValue(param);
        assert param.getValue() instanceof Date;

        param.setValue("2024-08-25");
        param.setName("date");
        initParamValue(param);
        assert param.getValue() instanceof LocalDate;

        Param ids = new Param();
        ids.setName("user_ids");
        ids.setDataType(DataType.COLLECTION);
        value = new String[]{"1","2","3",null,""};
        ids.setValue(value);

        Map<String,Param> params = new HashMap<>();
        params.put(ids.getName(),ids);
        params.put(param.getName(),param);
        String result = staticParamParse("user_id in (#{@user_ids}) and date='#{@date}'",params);
        assert ids.getValue() instanceof SimpleItemList;
        assert result.equals("user_id in (1,2,3,) and date='2024-08-25'");
        result = getExecuteSql("user_id in (#{@user_ids}) and date='#{@date}'",params,new ArrayList<>());
        assert result.equals("user_id in (1,2,3,) and date='2024-08-25'");

        params.clear();
        LocalDate date1 = LocalDateUtil.parseDate("2024-09-06");
        LocalDate date2 = LocalDateUtil.parseDate("2024-09-07");
        Date date3 = java.sql.Date.valueOf(LocalDateUtil.parseDate("2024-09-08"));
        Param dates = new Param();
        dates.setDataType(DataType.COLLECTION);
        dates.setName("dates");
        dates.setValue(CollectionUtil.asList(date1,date2,date3));
        params.put(dates.getName(),dates);
        result = staticParamParse("date find_in_set ('#{@dates}')",params);
        assert result.equals("date find_in_set ('2024-09-06,2024-09-07,2024-09-08')");
        result = getExecuteSql("date find_in_set ('#{@dates}')",params, new ArrayList<>());
        assert result.equals("date find_in_set ('2024-09-06,2024-09-07,2024-09-08')");
    }

    @Test
    public void testCollectionParam(){
        String[] empty = new String[]{};
        Param fields = new Param();
        fields.setName("fields");
        fields.setValue(empty);
        fields.setDataType(DataType.UNKNOWN);
        initParamType(fields);
        Map<String,Param> params = new HashMap<>();
        params.put(fields.getName(),fields);
        String result = staticParamParse("find_in_set(fields,'#{@fields}')",params);
        assert result.equals("find_in_set(fields,'')");
        result = getExecuteSql("find_in_set(fields,'#{@fields}')",params, new ArrayList<>());
        assert result.equals("find_in_set(fields,'')");
        result = getExecuteSql("field in (${@fields})", params, new ArrayList<>());
        assert result.equals("field in ()");

        List<Integer> list = CollectionUtil.asList(1,2,3,null,5);
        fields.setValue(list);
        result = getExecuteSql("field in (${@fields})", params, new ArrayList<>());
        assert result.equals("field in (?,?,?,?,?)");
        result = getExecuteSql("find_in_set(fields,'#{@fields}')",params, new ArrayList<>());
        assert result.equals("find_in_set(fields,'1,2,3,5')");
    }

    @Test
    public void testInitParamSetNull() throws Exception {
        List<Object> calls = new ArrayList<>();
        PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                (proxy, method, args) -> {
                    if ("setNull".equals(method.getName())) {
                        calls.add(args[0]);
                        calls.add(args[1]);
                    }
                    return null;
                });

        initParam(statement, Collections.singletonList(null));

        assert calls.size() == 2;
        assert Objects.equals(1, calls.get(0));
        assert Objects.equals(Types.NULL, calls.get(1));
    }

    @Test
    public void testGetDataBigDecimalAndNull() throws Exception {
        BigDecimal decimal = new BigDecimal("1234567890.123456789");
        ResultSet decimalResultSet = resultSet(decimal);
        Object value = getData(Types.NUMERIC, decimalResultSet, 1, Calendar.getInstance());
        assert value instanceof BigDecimal;
        assert decimal.equals(value);

        ResultSet nullResultSet = resultSet(null);
        value = getData(Types.INTEGER, nullResultSet, 1, Calendar.getInstance());
        assert value == null;
    }

    private ResultSet resultSet(Object value) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class[]{ResultSet.class},
                new java.lang.reflect.InvocationHandler() {
                    boolean wasNull;

                    @Override
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                        String name = method.getName();
                        if ("wasNull".equals(name)) {
                            return wasNull;
                        }
                        if ("getBigDecimal".equals(name)) {
                            wasNull = value == null;
                            return value;
                        }
                        if ("getInt".equals(name)) {
                            wasNull = value == null;
                            return value == null ? 0 : ((Number) value).intValue();
                        }
                        if ("getString".equals(name)) {
                            wasNull = value == null;
                            return value == null ? null : value.toString();
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == void.class) {
            return null;
        }
        return 0;
    }

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {
    }
}
