package com.gitee.qdbp.tools.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateToolsTest {

    public static void main(String[] args) throws Exception {
        testParse();
        testCalculate();
        testTime("2015-02-08 20:30:40.500");
        testTime("2016-02-08 20:30:40.500");
        testTime("2016-03-08 20:30:40.500");
        testTime("2016-04-08 20:30:40.500");
    }

    private static void testParse() {
        testParseDate("2018/0/0");
        testParseDate("2018/0/8");
        testParseDate("2018/7/8");
        testParseDate("2018/7/20");
        testParseDate("2018/12/24");
        testParseDate("2016/2/29");
        testParseDate("2018/2/29");
        testParseDate("7/8/2018");
        testParseDate("7/20/2018");
        testParseDate("12/24/2018");
        testParseDate("6/7/8");
        testParseDate("0006/7/8");
        testParseDate("7/8/0006");
        testParseTime("0:0:0");
        testParseTime("4:5:6");
        testParseTime("20:30:40");
        testParseTime("23:59:59");
        testParseTime("4:5:6.999");
        testParseTime("23:59:59.999999");
        testParseDateTime("2018/7/8 20:30:40");
        testParseDateTime("2018/7/8 4:5:6.999");
        testParseDateTime("2018/7/8 23:59:59.999999");
        System.out.println("------------------");
        System.out.println();
    }

    private static void testParseDate(String string) {
        System.out.println(StringTools.pad(string, ' ', false, 16) + DateTools.toDateString(DateTools.parse(string)));
    }

    private static void testParseTime(String string) {
        System.out.println(StringTools.pad(string, ' ', false, 16) + DateTools.toNormativeString(DateTools.parse(string)));
    }

    private static void testParseDateTime(String string) {
        System.out.println(StringTools.pad(string, ' ', false, 25) + DateTools.toNormativeString(DateTools.parse(string)));
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
