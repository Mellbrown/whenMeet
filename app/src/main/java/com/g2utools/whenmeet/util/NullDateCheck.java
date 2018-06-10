package com.g2utools.whenmeet.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by mlyg2 on 2018-02-13.
 */

public class NullDateCheck {
    public static boolean equalDATE(Date d1, Date d2){
        if(d1 != null && d2 != null){
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(d1);
            int preYear = cal.get(Calendar.YEAR);
            int preMonth = cal.get(Calendar.MONTH);
            int preDate = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(d2);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int date = cal.get(Calendar.DAY_OF_MONTH);
            return preYear == year && preMonth == month && preDate == date;
        } return false;
    }

    public static boolean equalTime(Date d1, Date d2){
        if(d1 != null && d2 != null){
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(d1);
            int preHour = cal.get(Calendar.HOUR);
            int preMinute = cal.get(Calendar.MINUTE);
            cal.setTime(d2);
            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);
            return preHour == hour && preMinute == minute;
        } return false;
    }
}
