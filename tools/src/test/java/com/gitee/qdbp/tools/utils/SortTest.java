package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.gitee.qdbp.able.instance.ComplexComparator;
import com.gitee.qdbp.able.instance.MapFieldComparator;
import com.gitee.qdbp.able.jdbc.ordering.Orderings;

public class SortTest {

    public static void main(String[] args) {

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(JSON.parseObject("{id:6,name:'kkk',dept:'BB'}"));
        list.get(list.size() - 1).put("birthday", DateTools.parse("1988-09-09"));
        list.add(JSON.parseObject("{id:1,name:'bbb',dept:'AA'}"));
        list.get(list.size() - 1).put("birthday", DateTools.parse("1988-08-08"));
        list.add(JSON.parseObject("{id:3,name:'hhh',dept:'AA'}"));
        list.get(list.size() - 1).put("birthday", DateTools.parse("1988-11-11"));
        list.add(JSON.parseObject("{id:5,name:'eee',dept:'CC'}"));
        list.get(list.size() - 1).put("birthday", DateTools.parse("1988-10-10"));
        list.add(JSON.parseObject("{id:4,name:'ddd',dept:'BB'}"));
        list.get(list.size() - 1).put("birthday", DateTools.parse("1988-08-08"));
        list.add(JSON.parseObject("{id:7,name:'fff'}"));
        list.add(JSON.parseObject("{id:2,name:'aaa'}"));
        System.out.println(JsonTools.toJsonString(list));

        {
            List<Map<String, Object>> temp = new ArrayList<>();
            temp.addAll(list);
            Collections.sort(temp, new MapFieldComparator<String, Object>("id"));
            System.out.println(JsonTools.toJsonString(temp));
        }

        {
            List<Map<String, Object>> temp = new ArrayList<>();
            temp.addAll(list);
            Collections.sort(temp, new MapFieldComparator<String, Object>("birthday"));
            System.out.println(JsonTools.toJsonString(temp));
        }

        {
            List<Map<String, Object>> temp = new ArrayList<>();
            temp.addAll(list);
            Collections.sort(temp, new MapFieldComparator<String, Object>("birthday", false));
            System.out.println(JsonTools.toJsonString(temp));
        }

        {
            List<Map<String, Object>> temp = new ArrayList<>();
            temp.addAll(list);

            // dept desc, birthday asc, name desc
            ComplexComparator<Map<String, Object>> comparator = new ComplexComparator<>();
            comparator.addComparator(new MapFieldComparator<String, Object>("dept", false));
            comparator.addComparator(new MapFieldComparator<String, Object>("birthday"));
            comparator.addComparator(new MapFieldComparator<String, Object>("name", false));

            Collections.sort(temp, comparator);
            System.out.println(JsonTools.toJsonString(temp));
        }
        {
            List<Map<String, Object>> temp = new ArrayList<>();
            temp.addAll(list);

            QueryTools.sort(temp, new Orderings("dept desc, birthday asc, name desc"));
            System.out.println(JsonTools.toJsonString(temp));
        }
    }
}
