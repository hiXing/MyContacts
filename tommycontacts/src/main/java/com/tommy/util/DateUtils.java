
package com.tommy.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @Description 时间格式化
 */
public class DateUtils {
    private static Calendar mCalendar = Calendar.getInstance();
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat();

    private static final String HHMM_FORMAT_STRING = "HH:mm";
    private static final String MMDDHHMM_FORMAT_STRING = "MM-dd HH:mm";
    private static final String YYYYMMDDHHMM_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

   

    /**
     * 获取当前时间
     * 
     * @return
     */
    public static String getCurrentTime() {
        mSimpleDateFormat.applyPattern(YYYYMMDDHHMM_FORMAT_STRING);
        return mSimpleDateFormat.format(System.currentTimeMillis());
    }
}
