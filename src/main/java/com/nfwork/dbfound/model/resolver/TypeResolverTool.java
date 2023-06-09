package com.nfwork.dbfound.model.resolver;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeResolverTool {

    private static final Map<Class<?>,TypeResolver> resolverMap = new HashMap<>();

    private static final ObjectResolver OBJECT_RESOLVER = new ObjectResolver();

    static {
        resolverMap.put(String.class, new StringResolver());

        resolverMap.put(int.class, new IntegerResolver());
        resolverMap.put(Integer.class, resolverMap.get(int.class));

        resolverMap.put(long.class, new LongResolver());
        resolverMap.put(Long.class, resolverMap.get(long.class));

        resolverMap.put(double.class, new DoubleResolver());
        resolverMap.put(Double.class, resolverMap.get(double.class));

        resolverMap.put(float.class, new FloatResolver());
        resolverMap.put(Float.class, resolverMap.get(float.class));

        resolverMap.put(boolean.class, new BooleanResolver());
        resolverMap.put(Boolean.class, resolverMap.get(boolean.class));

        resolverMap.put(short.class, new ShortResolver());
        resolverMap.put(Short.class, resolverMap.get(short.class));

        resolverMap.put(byte.class, new ByteResolver());
        resolverMap.put(Byte.class, resolverMap.get(byte.class));

        resolverMap.put(java.sql.Date.class, new SqlDateResolver());

        resolverMap.put(Date.class, new DateResolver());

        resolverMap.put(BigDecimal.class, new BigDecimalResolver());

        resolverMap.put(byte[].class, new ByteArrayResolver());

        resolverMap.put(LocalDate.class, new LocalDateResolver());

        resolverMap.put(LocalTime.class, new LocalTimeResolver());

        resolverMap.put(LocalDateTime.class, new LocalDateTimeResolver());

    }

    public static Object getValue(Class<?> clazz, ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        TypeResolver resolver = resolverMap.get(clazz);
        if(resolver == null){
           resolver = OBJECT_RESOLVER;
        }
        return resolver.getValue(rs, index, defaultCalendar);
    }
}
