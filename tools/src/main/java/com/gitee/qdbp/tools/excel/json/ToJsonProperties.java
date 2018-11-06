package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.excel.utils.MetadataTools;
import com.gitee.qdbp.tools.utils.PropertyTools;

/**
 * 数据转换配置<br>
 * 有哪些配置项详见{@linkplain MetadataTools#parseProperties(Properties)}<br>
 * exceltojson.x.main增加了file.name, id.field, self.name, copy.concat<br>
 * exceltojson.x.merge.y.type = json|list|field<br>
 * json = {@linkplain MergeToJson}, 增加了file.name, id.field, self.name, self.with<br>
 * list = {@linkplain MergeToList}, 增加了file.name, id.field, self.name<br>
 * field = {@linkplain MergeToField}, 增加了file.name, id.field<br>
 * <pre>
 * exceltojson.1.main.self.name = users
 * exceltojson.1.main.file.name = user.xlsx
 * exceltojson.1.main.sheet.name = MainSheet
 * exceltojson.1.main.field.rows = 1
 * exceltojson.1.main.id.field = id
 * exceltojson.1.main.rule.map.gender = { "UNKNOWN":"未知|0", "MALE":"男|1", "FEMALE":"女|2" }
 * exceltojson.1.main.rule.date.birthday = yyyy/MM/dd
 * exceltojson.1.main.copy.concat = { keywords:"userName,nickName,deptName" }
 * exceltojson.1.merge.1.type = list
 * exceltojson.1.merge.1.sheet.name = AddressSheet
 * exceltojson.1.merge.1.self.name = address
 * exceltojson.1.merge.1.field.rows = 1
 * exceltojson.1.merge.1.id.field = id
 * exceltojson.1.merge.2.type = field
 * exceltojson.1.merge.2.sheet.name = AddressSheet
 * exceltojson.1.merge.2.field.rows = 1
 * exceltojson.1.merge.2.id.field = id
 * 
 * exceltojson.2.main.self.name = xxx
 * ...
 * </pre>
 *
 * @author zhaohuihua
 * @version 181101
 */
public class ToJsonProperties implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public static List<ToJsonMetadata> parseMetadata(String config) {
        Properties properties = PropertyTools.loadByString(config);
        return parseMetadata(properties);
    }

    public static List<ToJsonMetadata> parseMetadata(Properties properties) {
        // 先统计有哪些前缀
        Map<String, Map<String, Object>> prefixes = new HashMap<>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String[] keys = StringTools.split(key, false, '.');
                if (keys.length < 1 || !keys[0].equals("exceltojson")) {
                    continue;
                }
                String main = null;
                String merge = null;
                if (keys.length > 3 && keys[2].equals("merge")) { // exceltojson.a.merge.b
                    main = StringTools.concat('.', keys[0], keys[1], "main", "."); // exceltojson.a.main.
                    merge = StringTools.concat('.', keys[0], keys[1], keys[2], keys[3], "."); // exceltojson.a.merge.b.
                } else if (keys.length > 2 && keys[1].equals("merge")) { // exceltojson.merge.b
                    main = StringTools.concat('.', keys[0], "main", "."); // exceltojson.main.
                    merge = StringTools.concat('.', keys[0], keys[1], keys[2], keys[3], "."); // exceltojson.merge.b.
                } else if (keys.length > 1 && keys[1].equals("main")) { // exceltojson.main
                    main = StringTools.concat('.', keys[0], keys[1], "."); // exceltojson.main.
                } else if (keys.length > 2 && keys[2].equals("main")) { // exceltojson.a.main
                    merge = StringTools.concat('.', keys[0], keys[1], keys[2], "."); // exceltojson.a.merge.
                }
                if (merge != null && main != null) {
                    if (!prefixes.containsKey(main)) {
                        prefixes.put(main, new HashMap<>());
                    }
                    if (!prefixes.get(main).containsKey(merge)) {
                        prefixes.get(main).put(merge, true);
                    }
                } else if (main != null) {
                    if (!prefixes.containsKey(main)) {
                        prefixes.put(main, new HashMap<>());
                    }
                }
            }
        }
        if (prefixes.isEmpty()) {
            return null;
        }

        // 再根据前缀过滤配置项子集, 分别解析
        List<String> keys = new ArrayList<>(prefixes.keySet());
        Collections.sort(keys);
        List<ToJsonMetadata> results = new ArrayList<>();
        for (String mainKey : keys) {
            Properties mainProperties = PropertyTools.filter(properties, mainKey);
            ToJsonMetadata mainMetadata = parse(mainProperties);
            Map<String, Object> mergeMap = prefixes.get(mainKey);
            if (mergeMap != null) {
                List<String> mergeKeys = new ArrayList<>(mergeMap.keySet());
                Collections.sort(mergeKeys);
                for (String mergeKey : mergeKeys) {
                    Properties mergeProperties = PropertyTools.filter(properties, mergeKey);
                    MergeMetadata mergeMetadata = parseMergeParams(mergeProperties);
                    mainMetadata.addMergers(mergeMetadata);
                }
            }
            results.add(mainMetadata);
        }
        return results;
    }

    /** 解析JsonMetadata配置项 **/
    private static ToJsonMetadata parse(Properties properties) {
        XMetadata base = MetadataTools.parseProperties(properties);

        String fileName = PropertyTools.getString(properties, "file.name", false); // Excel文件路径
        String idField = PropertyTools.getString(properties, "id.field"); // ID字段名
        String selfName = PropertyTools.getString(properties, "self.name", false); // 自身字段名称

        ToJsonMetadata metadata = base.to(ToJsonMetadata.class);
        metadata.setFileName(fileName); // Excel文件路径
        metadata.setIdField(idField); // ID字段名
        metadata.setSelfName(selfName); // 自身字段名称
        return metadata;
    }

    /** 解析MergeMetadata配置项 **/
    private static MergeMetadata parseMergeParams(Properties properties) {
        XMetadata metadata = MetadataTools.parseProperties(properties);

        String sMergeType = PropertyTools.getString(properties, "type"); // 合并类型
        String fileName = PropertyTools.getString(properties, "file.name", false); // Excel文件路径
        String idField = PropertyTools.getString(properties, "id.field"); // ID字段名
        String selfName = PropertyTools.getString(properties, "self.name", false); // 自身字段名称
        String selfWith = PropertyTools.getString(properties, "self.with", false); // 自身字段名称所在的字段名

        MergeType mergeType = parseMergeType(sMergeType);
        if (mergeType == null) {
            return null;
        }
        switch (mergeType) {
        case json: {
            MergeToJson merge = metadata.to(MergeToJson.class);
            merge.setFileName(fileName); // Excel文件路径
            merge.setIdField(idField); // ID字段名
            merge.setSelfName(selfName); // 自身字段名称
            merge.setSelfWith(selfWith); // 自身名称所在的字段名
            return merge;
        }
        case list: {
            MergeToList merge = metadata.to(MergeToList.class);
            merge.setFileName(fileName); // Excel文件路径
            merge.setIdField(idField); // ID字段名
            merge.setSelfName(selfName); // 自身字段名称
            return merge;
        }
        case field: {
            MergeToField merge = metadata.to(MergeToField.class);
            merge.setFileName(fileName); // Excel文件路径
            merge.setIdField(idField); // ID字段名
            return merge;
        }
        default:
            return null;
        }
    }

    /** 解析MergeType **/
    private static MergeType parseMergeType(String mergeType) {
        for (MergeType item : MergeType.values()) {
            if (item.name().equals(mergeType)) {
                return item;
            }
        }
        return null;
    }
}
