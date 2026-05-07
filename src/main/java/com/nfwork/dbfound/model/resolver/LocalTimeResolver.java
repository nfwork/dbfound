package com.nfwork.dbfound.model.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

public class LocalTimeResolver implements TypeResolver{

    @Override
    public Object getValue(ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        Time time = rs.getTime(index, defaultCalendar);
        return time == null ? null : time.toLocalTime();
    }
}
