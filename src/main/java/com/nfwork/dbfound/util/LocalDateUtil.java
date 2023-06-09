package com.nfwork.dbfound.util;

import com.nfwork.dbfound.core.DBFoundConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalDateUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getDateTimeFormat());

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getDateFormat());

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getTimeFormat());

    public static String formatDate(LocalDate date){
        return dateFormatter.format(date);
    }

    public static String formatTime(LocalTime time){
        return timeFormatter.format(time);
    }

    public static String formatDateTime(LocalDateTime dateTime){
        return dateTimeFormatter.format(dateTime);
    }
}
