package com.gitee.qdbp.tools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.gitee.qdbp.able.utils.DateTools;

public class DateToolsTest {

    public static void main(String[] args) throws Exception {
        test("2015-02-08 20:30:40.500");
        test("2016-02-08 20:30:40.500");
        test("2016-03-08 20:30:40.500");
        test("2016-04-08 20:30:40.500");
    }

    public static void test(String string) throws ParseException {
        System.out.println(string);
        System.out.println();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = sdf.parse(string);
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.YEAR)));
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.MONDAY)));
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.DAY_OF_MONTH)));
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.HOUR_OF_DAY)));
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.MINUTE)));
        System.out.println(sdf.format(DateTools.toFirstTime(date, Calendar.SECOND)));

        System.out.println();
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.YEAR)));
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.MONDAY)));
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.DAY_OF_MONTH)));
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.HOUR_OF_DAY)));
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.MINUTE)));
        System.out.println(sdf.format(DateTools.toLastTime(date, Calendar.SECOND)));
        System.out.println("------------------");
        System.out.println();
    }
}
