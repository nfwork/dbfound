package dbfound.test.core;

import com.nfwork.dbfound.model.reflector.ReflectorUtil;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReflectorUtilTest {

    @Test
    public void testParseSimpleListNull() throws Exception {
        ResultSet rs = resultSet(new Object[][]{{null}, {5}}, "value");

        List<Integer> values = ReflectorUtil.parseSimpleList(Integer.class, rs);

        assert values.size() == 2;
        assert values.get(0) == null;
        assert values.get(1) == 5;
    }

    @Test
    public void testParseResultListSkipNullSetter() throws Exception {
        ResultSet rs = resultSet(new Object[][]{{null, null}, {0, 1}}, "count", "boxed_count");

        List<NullSetterBean> values = ReflectorUtil.parseResultList(NullSetterBean.class, rs);

        NullSetterBean nullValue = values.get(0);
        assert nullValue.count == 9;
        assert nullValue.countSetCount == 0;
        assert nullValue.boxedCount == 8;
        assert nullValue.boxedCountSetCount == 0;

        NullSetterBean zeroValue = values.get(1);
        assert zeroValue.count == 0;
        assert zeroValue.countSetCount == 1;
        assert zeroValue.boxedCount == 1;
        assert zeroValue.boxedCountSetCount == 1;
    }

    @Test
    public void testParseSimpleListTemporalNull() throws Exception {
        ResultSet dateRs = resultSet(new Object[][]{{null}}, "value");
        assert ReflectorUtil.parseSimpleList(LocalDate.class, dateRs).get(0) == null;

        ResultSet timeRs = resultSet(new Object[][]{{null}}, "value");
        assert ReflectorUtil.parseSimpleList(LocalTime.class, timeRs).get(0) == null;

        ResultSet dateTimeRs = resultSet(new Object[][]{{null}}, "value");
        assert ReflectorUtil.parseSimpleList(LocalDateTime.class, dateTimeRs).get(0) == null;
    }

    private ResultSet resultSet(Object[][] rows, String... labels) {
        ResultSetMetaData metaData = (ResultSetMetaData) Proxy.newProxyInstance(
                ResultSetMetaData.class.getClassLoader(),
                new Class[]{ResultSetMetaData.class},
                (proxy, method, args) -> {
                    if ("getColumnCount".equals(method.getName())) {
                        return labels.length;
                    }
                    if ("getColumnLabel".equals(method.getName()) || "getColumnName".equals(method.getName())) {
                        return labels[((Integer) args[0]) - 1];
                    }
                    return defaultValue(method.getReturnType());
                });

        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class[]{ResultSet.class},
                new java.lang.reflect.InvocationHandler() {
                    int row = -1;
                    boolean wasNull;

                    @Override
                    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
                        String name = method.getName();
                        if ("next".equals(name)) {
                            row++;
                            return row < rows.length;
                        }
                        if ("getMetaData".equals(name)) {
                            return metaData;
                        }
                        if ("wasNull".equals(name)) {
                            return wasNull;
                        }
                        if (name.startsWith("get")) {
                            Object value = rows[row][((Integer) args[0]) - 1];
                            wasNull = value == null;
                            return readValue(name, value);
                        }
                        return defaultValue(method.getReturnType());
                    }
                });
    }

    private Object readValue(String methodName, Object value) {
        if ("getString".equals(methodName)) {
            return value == null ? null : value.toString();
        }
        if ("getInt".equals(methodName)) {
            return value == null ? 0 : ((Number) value).intValue();
        }
        if ("getDate".equals(methodName)) {
            return value == null ? null : java.sql.Date.valueOf((LocalDate) value);
        }
        if ("getTime".equals(methodName)) {
            return value == null ? null : Time.valueOf((LocalTime) value);
        }
        if ("getTimestamp".equals(methodName)) {
            return value == null ? null : Timestamp.valueOf((LocalDateTime) value);
        }
        return value;
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

    public static class NullSetterBean {
        int count = 9;
        int countSetCount;
        Integer boxedCount = 8;
        int boxedCountSetCount;

        public void setCount(int count) {
            this.count = count;
            countSetCount++;
        }

        public void setBoxedCount(Integer boxedCount) {
            this.boxedCount = boxedCount;
            boxedCountSetCount++;
        }
    }
}
