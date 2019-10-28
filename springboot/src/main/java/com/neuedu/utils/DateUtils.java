package com.neuedu.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

//joda-time时间格式化
public class DateUtils {

    public static final String FORMAT_DEFAULT="yyyy-MM-dd HH:mm:ss";

    /**
     * 将字符串日期转出Date
     * yyyy-MM-dd HH:mm:ss
     * */
    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(FORMAT_DEFAULT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 将Date转字符串
     * */

    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return null;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static String dateToStr(Date date){
        if(date == null){
            return null;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(FORMAT_DEFAULT);
    }


}