package com.gitee.qdbp.able.beans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 深度路径Map, 用于Ognl取值<br>
 * a.b.c, a.b.c.d 不能共存, 保留最路径最深的a.b.c.d
 *
 * <pre>
 * DepthPathMap dpm = new DepthPathMap();
 * dpm.put("author", "xxx"); // 该设置无效, 会被后面的覆盖掉
 * dpm.put("author.code", 100);
 * dpm.put("author.name", "zhaohuihua");
 *
 * dpm.put("code.folder.service", "service");
 * dpm.put("code.folder.page", "views");
 * dpm.put("code.folder", "java"); // 该设置无效, 会被忽略掉
 *
 * Map<String, Object> map = dpm.map();
 *
 * Ognl.getValue("author", map); -- Map({code=100, name=zhaohuihua})
 * Ognl.getValue("author.code", map); -- 100
 * Ognl.getValue("author.name", map); -- zhaohuihua
 *
 * Ognl.getValue("code.folder", map); -- Map({service=service, page=views})
 * Ognl.getValue("code.folder.service", map); -- service
 * Ognl.getValue("code.folder.page", map); -- views
 * </pre>
 *
 * @author zhaohuihua
 * @version 151221
 */
public class DepthPathMap {

    private static final Pattern SEPARATOR = Pattern.compile("\\.");

    private Map<String, Object> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public DepthPathMap put(String key, Object value) {
        String[] keys = SEPARATOR.split(key);

        List<String> list = Arrays.asList(keys);
        Iterator<String> iterator = list.iterator();

        Map<String, Object> parent = map;
        while (iterator.hasNext()) {
            String name = iterator.next();

            Object older = parent.get(name);
            if (iterator.hasNext()) {
                // 有下一级
                if (older == null || !(older instanceof Map)) {
                    // !(older instanceof Map):
                    // 如果之前有a.b.c, 新加一个a.b.c.d, 覆盖掉a.b.c
                    older = new HashMap<>();
                    parent.put(name, older);
                }
            } else {
                // 没有下一级
                if (older != null && older instanceof Map) {
                    // 如果之前有a.b.c.d, 新加一个a.b.c, 忽略a.b.c
                    continue;
                }
                parent.put(name, value);
            }
            parent = (Map<String, Object>) older;
        }
        return this;
    }

    public Map<String, Object> map() {
        return map;
    }

    public DepthPathMap copy() {
        DepthPathMap n = new DepthPathMap();
        n.map.putAll(this.map);
        return n;
    }

    /**
     * 从资源文件中读取所有指定前缀的配置内容
     * 
     * @param setting 资源文件
     * @param prefix 前缀
     * @return
     */
    public static DepthPathMap load(Properties setting, String prefix) {

        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }

        DepthPathMap dpm = new DepthPathMap();
        for (Entry<Object, Object> e : setting.entrySet()) {
            String key = e.getKey().toString();
            if (key.startsWith(prefix)) {
                dpm.put(key.substring(prefix.length()), e.getValue());
            }
        }
        return dpm;
    }
}
