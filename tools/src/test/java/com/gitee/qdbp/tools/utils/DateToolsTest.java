package com.gitee.qdbp.tools.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import com.gitee.qdbp.able.utils.DateTools;

public class DateToolsTest {

    public static void main(String[] args) throws Exception {
        testCalculate();

        testTime("2015-02-08 20:30:40.500");
        testTime("2016-02-08 20:30:40.500");
        testTime("2016-03-08 20:30:40.500");
        testTime("2016-04-08 20:30:40.500");
    }

    private static void testCalculate() {
        Date date = DateTools.parse("2016-03-01");
        System.out.println(DateTools.toNormativeString(DateTools.calculate(date, "-1d"))); // 2016-02-29
        System.out.println(DateTools.toNormativeString(DateTools.calculate(date, "-2M"))); // 2016-01-01
        System.out.println(DateTools.toNormativeString(DateTools.calculate(date, "-3y"))); // 2013-03-01
        System.out.println(DateTools.toNormativeString(DateTools.calculate(date, "+2d"))); // 2016-03-03
        System.out.println(DateTools.toNormativeString(DateTools.calculate(date, "-2M+3d"))); // 2016-01-04
        System.out.println("------------------");
        System.out.println();
    }

    public static void testTime(String string) throws ParseException {
        System.out.println(string);
        System.out.println();
        Date date = DateTools.parse(string);
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.YEAR)));
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.MONDAY)));
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.DAY_OF_MONTH)));
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.HOUR_OF_DAY)));
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.MINUTE)));
        System.out.println(DateTools.toNormativeString(DateTools.toFirstTime(date, Calendar.SECOND)));

        System.out.println();
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.YEAR)));
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.MONDAY)));
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.DAY_OF_MONTH)));
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.HOUR_OF_DAY)));
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.MINUTE)));
        System.out.println(DateTools.toNormativeString(DateTools.toLastTime(date, Calendar.SECOND)));
        System.out.println("------------------");
        System.out.println();
    }
}
