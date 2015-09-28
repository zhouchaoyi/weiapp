package com.intel.assist.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ecic Chen on 2015/8/4.
 */
public class DateUtil {
    public static String date2String(Date date) {
        return date2String(date, Consts.SIMPLE_DATE_FORMAT_STRING);
    }

    public static String date2String(Date date, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date string2Date(String date, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date string2Date(String date) {
        return string2Date(date, Consts.SIMPLE_DATE_FORMAT_STRING);
    }

    public static String formatDateString(Date date){
        Date currentDate = new Date();

        if((currentDate.getTime() - date.getTime())/1000/60 <= 2  ){
            return "刚刚";
        }else if((currentDate.getTime() - date.getTime())/1000/60 < 60){
            return (currentDate.getTime() - date.getTime())/1000/60 +"分钟前";
        }else if((currentDate.getTime() - date.getTime())/1000/60/60 < 24){
            return(currentDate.getTime() - date.getTime())/1000/60/60 +"小时前";
        }else if((currentDate.getTime() - date.getTime())/1000/60/60/24 < 31){
            return (currentDate.getTime() - date.getTime())/1000/60/60/24 +"天前";
        }else{
            return new SimpleDateFormat("yyyy/MM/dd").format(date);
        }
    }

}
