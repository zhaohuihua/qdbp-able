package com.gitee.qdbp.tools.excel;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.gitee.qdbp.able.jdbc.paging.PageList;
import com.gitee.qdbp.able.jdbc.paging.Paging;
import com.gitee.qdbp.tools.excel.json.ExcelToJson;
import com.gitee.qdbp.tools.excel.json.ToJsonMetadata;
import com.gitee.qdbp.tools.excel.json.ToJsonProperties;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.JsonTools;
import com.gitee.qdbp.tools.utils.PropertyTools;
import com.gitee.qdbp.tools.utils.QueryTools;

public class ExcelToJsonTest {

    public static void main(String[] args) {
        URL path = PathTools.findClassResource(ExcelToJsonTest.class, "ExcelToJsonTest.txt");
        System.out.println(path);
        String folder = PathTools.getOutputFolder(path, "./");
        Properties properties = PropertyTools.load(path);
        List<ToJsonMetadata> metadata = ToJsonProperties.parseMetadata(properties);
        {
            long start = System.currentTimeMillis();
            Map<String, ?> result = ExcelToJson.convert(folder, metadata);
            System.out.println(JsonTools.toJsonString(result));
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            Map<String, List<Map<String, Object>>> result = ExcelToJson.convert(folder, metadata);
            System.out.println(JsonTools.toJsonString(result));
            System.out.println(System.currentTimeMillis() - start);

            {
                System.out.println("paging: 5");
                List<Map<String, Object>> users = result.get("users");
                PageList<Map<String, Object>> list = QueryTools.filter(users, null, new Paging(1, 5));
                System.out.println("users.size=" + users.size() + ", paged.size=" + list.size());
                System.out.println(JsonTools.toJsonString(list));
            }
            {
                System.out.println("nameLike:路人");
                List<Map<String, Object>> users = result.get("users");
                Map<String, Object> where = new HashMap<>();
                where.put("nameLike", "路人");
                PageList<Map<String, Object>> list = QueryTools.filter(users, where, null);
                System.out.println(JsonTools.toJsonString(list));
            }
            {
                System.out.println("heightBetween:170");
                List<Map<String, Object>> users = result.get("users");
                Map<String, Object> where = new HashMap<>();
                where.put("heightBetween", "170");
                PageList<Map<String, Object>> list = QueryTools.filter(users, where, null);
                System.out.println(JsonTools.toJsonString(list));
            }
            {
                System.out.println("heightBetween:160,170");
                List<Map<String, Object>> users = result.get("users");
                Map<String, Object> where = new HashMap<>();
                where.put("heightBetween", "160,170");
                PageList<Map<String, Object>> list = QueryTools.filter(users, where, null);
                System.out.println(JsonTools.toJsonString(list));
            }
            {
                System.out.println("skillsExists:jQuery");
                List<Map<String, Object>> users = result.get("users");
                Map<String, Object> where = new HashMap<>();
                where.put("skillsExists", "jQuery");
                PageList<Map<String, Object>> list = QueryTools.filter(users, where, null);
                System.out.println(JsonTools.toJsonString(list));
            }
        }
    }
}
