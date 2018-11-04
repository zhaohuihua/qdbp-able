package com.gitee.qdbp.tools.excel;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.gitee.qdbp.tools.excel.json.ExcelToJson;
import com.gitee.qdbp.tools.excel.json.ToJsonMetadata;
import com.gitee.qdbp.tools.excel.json.ToJsonProperties;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.JsonTools;
import com.gitee.qdbp.tools.utils.PropertyTools;

public class ExcelToJsonTest {

    public static void main(String[] args) {
        URL path = PathTools.findClassResource(ExcelToJsonTest.class, "ExcelToJsonTest.txt");
        System.out.println(path);
        String folder = PathTools.getOutputFolder(path, "./");
        Properties properties = PropertyTools.load(path);
        List<ToJsonMetadata> metadata = ToJsonProperties.parseMetadata(properties);
        {
            long start = System.currentTimeMillis();
            Map<String, Object> users = ExcelToJson.convert(folder, metadata);
            System.out.println(JsonTools.toJsonString(users));
            System.out.println(System.currentTimeMillis() - start);
        }
        {
            long start = System.currentTimeMillis();
            Map<String, Object> users = ExcelToJson.convert(folder, metadata);
            System.out.println(JsonTools.toJsonString(users));
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}
