package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本号测试类
 *
 * @author zhaohuihua
 * @version 20200711
 */
public class VersionCodeToolsTest {

    public static void main(String[] args) {
        List<Item> testcases = new ArrayList<>();
        testcases.add(new Item("1.0.0", "1.0.0", 0));
        testcases.add(new Item("1.0.1", "1.0.0001", 0));
        testcases.add(new Item("1.0.0", "1.0.1", -1));
        testcases.add(new Item("4.3.20.RELEASE", "4.3.20.RELEASE", 0));
        testcases.add(new Item("1.2.8a", "1.2.8a", 0));
        testcases.add(new Item("1.0.234_20200708001", "1.0.234_20200708001", 0));
        testcases.add(new Item("1.0.234_20200708001", "1.0.234_20200708002", -1));
        testcases.add(new Item("1.0.0-R1", "1.0.0-R1", 0));
        testcases.add(new Item("1.0", "1.0.0.0", 0));
        testcases.add(new Item("1.0.0", "1.0.0.1", -1));
        testcases.add(new Item("1.2.8", "1.2.8-alpha", 1));
        testcases.add(new Item("1.2.8", "1.2.8-SNAPSHOT", 1));
        testcases.add(new Item("1.2.8", "1.2.8.R1", 1));
        testcases.add(new Item("1.2.7", "1.2.8a", -1));
        testcases.add(new Item("1.2", "1.2.8a", -1));
        testcases.add(new Item("1.2.8", "1.2.8a", 1));
        testcases.add(new Item("1.2.8a", "1.2.8b", -1));
        testcases.add(new Item("1.2.8a", "1.2.8a0", -1));
        testcases.add(new Item("1.2.8a", "1.2.8ab", -1));
        testcases.add(new Item("1.2.8a1", "1.2.8a01", 0));
        testcases.add(new Item("1.2.8a1", "1.2.8a012", -1));
        testcases.add(new Item("V24R108", "V24R108", 0));
        testcases.add(new Item("V24R108B002", "V24R108B003", -1));
        // 套用1.0>1.0-SNAPSHOT的逻辑; V24R108是正式版本,B001是临时版本, 所以V24R108>V24R108B001
        testcases.add(new Item("V24R108", "V24R108B001", 1));
        testItems(testcases);
    }

    private static void testItems(List<Item> testcases) {
        List<String> errors = new ArrayList<>();
        for (Item item : testcases) {
            { // 正向测试
                String result = testItem(item.source, item.target, item.expected);
                if (result != null) {
                    errors.add(result);
                }
            }
            { // 反向测试
                String result = testItem(item.target, item.source, -item.expected);
                if (result != null) {
                    errors.add(result);
                }
            }
        }
        if (errors.isEmpty()) {
            System.out.println("The test passed!");
        } else {
            System.out.println(ConvertTools.joinToString(errors, '\n'));
            throw new RuntimeException("The test failed!");
        }
    }

    private static String testItem(String source, String target, int expected) {
        int actual = VersionCodeTools.compare(source, target);
        if (actual == expected) {
            return null;
        } else {
            char flag = expected > 0 ? '>' : expected < 0 ? '<' : '=';
            return source + ' ' + flag + ' ' + target + ", but returns " + actual + '.';
        }
    }

    private static class Item {

        private String source;
        private String target;
        private int expected;

        public Item(String source, String target, int expected) {
            this.source = source;
            this.target = target;
            this.expected = expected;
        }
    }

}
