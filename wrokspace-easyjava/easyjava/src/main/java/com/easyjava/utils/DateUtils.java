package com.easyjava.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final String PATTEN_DATE = "yyyy-MM-dd";
    public static final String _PATTEN_DATE = "yyyy/MM/dd";
    public static final String PATTEN_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String _PATTEN_TIME = "yyyy/MM/dd HH:mm:ss";

    public static String format(Date date,String patten){
        return new SimpleDateFormat(patten).format( date);
    }
    public static Date parse(String date,String patten){
        try {
            return new SimpleDateFormat(patten).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
