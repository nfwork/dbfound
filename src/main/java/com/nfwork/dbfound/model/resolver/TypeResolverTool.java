package com.nfwork.dbfound.model.resolver;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeResolverTool {

    private static final Map<String,TypeResolver> resolverMap = new HashMap<>();

    private static final ObjectResolver OBJECT_RESOLVER = new ObjectResolver();

    static {
        resolverMap.put(String.class.getName(), new StringResolver());

        resolverMap.put(int.class.getName(), new IntegerResolver());
        resolverMap.put(Integer.class.getName(), resolverMap.get(int.class.getName()));

        resolverMap.put(long.class.getName(), new LongResolver());
        resolverMap.put(Long.class.getName(), resolverMap.get(long.class.getName()));

        resolverMap.put(double.class.getName(), new DoubleResolver());
        resolverMap.put(Double.class.getName(), resolverMap.get(double.class.getName()));

        resolverMap.put(float.class.getName(), new FloatResolver());
        resolverMap.put(Float.class.getName(), resolverMap.get(float.class.getName()));

        resolverMap.put(boolean.class.getName(), new BooleanResolver());
        resolverMap.put(Boolean.class.getName(), resolverMap.get(boolean.class.getName()));

        resolverMap.put(short.class.getName(), new ShortResolver());
        resolverMap.put(Short.class.getName(), resolverMap.get(short.class.getName()));

        resolverMap.put(byte.class.getName(), new ByteResolver());
        resolverMap.put(Byte.class.getName(), resolverMap.get(byte.class.getName()));

        resolverMap.put(java.sql.Date.class.getName(), new SqlDateResolver());

        resolverMap.put(Date.class.getName(), new DateResolver());
        resolverMap.put(Timestamp.class.getName(), resolverMap.get(Date.class.getName()));

        resolverMap.put(Time.class.getName(), new TimeResolver());

        resolverMap.put(BigDecimal.class.getName(), new BigDecimalResolver());

        resolverMap.put(byte[].class.getName(), new ByteArrayResolver());

        resolverMap.put(LocalDate.class.getName(), new LocalDateResolver());

        resolverMap.put(LocalTime.class.getName(), new LocalTimeResolver());

        resolverMap.put(LocalDateTime.class.getName(), new LocalDateTimeResolver());

    }

    public static Object getValue(Class<?> clazz, ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        TypeResolver resolver = resolverMap.get(clazz.getName());
        if(resolver == null){
           resolver = OBJECT_RESOLVER;
        }
        return resolver.getValue(rs, index, defaultCalendar);
    }

    public static boolean isSupport(Class<?> clazz){
       return resolverMap.get(clazz.getName()) != null;
    }
}
