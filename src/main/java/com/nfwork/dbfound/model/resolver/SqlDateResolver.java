package com.nfwork.dbfound.model.resolver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class SqlDateResolver implements TypeResolver{

    @Override
    public Object getValue(ResultSet rs, int index, Calendar defaultCalendar) throws SQLException {
        return rs.getDate(index);
    }
}
