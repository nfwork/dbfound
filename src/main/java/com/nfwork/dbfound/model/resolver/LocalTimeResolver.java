package com.nfwork.dbfound.model.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class LocalTimeResolver implements TypeResolver{

    @Override
    public Object getValue(ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        return rs.getTime(index, defaultCalendar).toLocalTime();
    }
}
