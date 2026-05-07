package com.nfwork.dbfound.model.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class LocalDateTimeResolver implements TypeResolver{

    @Override
    public Object getValue(ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index, defaultCalendar);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
