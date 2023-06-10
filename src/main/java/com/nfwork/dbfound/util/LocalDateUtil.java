package com.nfwork.dbfound.util;

import com.nfwork.dbfound.core.DBFoundConfig;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getDateTimeFormat());

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getDateFormat());

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DBFoundConfig.getTimeFormat());

    public static String formatDate(LocalDate date){
        return dateFormatter.format(date);
    }

    public static String formatDate(Date date){
        if(date instanceof java.sql.Date) {
            return dateFormatter.format(((java.sql.Date)date).toLocalDate());
        }else if(date instanceof Timestamp){
            return formatDateTime((Timestamp) date);
        }else if(date instanceof Time){
            return formatTime((Time) date);
        }else{
            return formatDateTime(new Timestamp(date.getTime()));
        }
    }

    public static String formatTime(LocalTime time){
        return timeFormatter.format(time);
    }

    public static String formatTime(Time time){
        return timeFormatter.format(time.toLocalTime());
    }

    public static String formatDateTime(LocalDateTime dateTime){
        return dateTimeFormatter.format(dateTime);
    }

    public static String formatDateTime(Timestamp timestamp){
        return dateTimeFormatter.format(timestamp.toLocalDateTime());
    }

    public static LocalDate parseDate(String value){
        return LocalDate.parse(value,dateFormatter);
    }

    public static LocalDateTime parseDateTime(String value){
        return LocalDateTime.parse(value, dateTimeFormatter);
    }

    public static LocalTime parseTime(String value){
        return LocalTime.parse(value, timeFormatter);
    }
}
