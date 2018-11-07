package com.gitee.qdbp.tools.excel.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.ExcelErrorCode;
import com.gitee.qdbp.tools.excel.ImportCallback;
import com.gitee.qdbp.tools.excel.XExcelParser;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.excel.condition.CellValueCondition;
import com.gitee.qdbp.tools.excel.condition.CellValueContainsTextCondition;
import com.gitee.qdbp.tools.excel.condition.CellValueEqualsTextCondition;
import com.gitee.qdbp.tools.excel.condition.IndexListCondition;
import com.gitee.qdbp.tools.excel.condition.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.condition.NameListCondition;
import com.gitee.qdbp.tools.excel.condition.Required;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.rule.ClearRule;
import com.gitee.qdbp.tools.excel.rule.DateRule;
import com.gitee.qdbp.tools.excel.rule.IgnoreIllegalValue;
import com.gitee.qdbp.tools.excel.rule.MapRule;
import com.gitee.qdbp.tools.excel.rule.NumberRule;
import com.gitee.qdbp.tools.excel.rule.RateRule;
import com.gitee.qdbp.tools.excel.rule.SplitRule;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.JsonTools;

/**
 * Excel数据转换为JSON格式数据<br>
 * <pre>
 * <b>Excel数据</b>
 * user.xlsx[MainSheet]
 *     id name   gender
 *     1  jack   male
 * user.xlsx[AddressSheet]
 *     id name   city     details
 *     1  home   hefei    xxxxx
 *     1  office nanjing  yyyyy
 * user.xlsx[ExtraSheet]
 *     id intro      description
 *     1  jack-intro jack-description
 * 
 * <b>ToJsonMetadata</b> = { selfName:users, fileName:user.xlsx, sheetName:MainSheet, idColun:1, headerRows:1 }
 * 
 * <b>MergeToJson</b>, 一对多合并, 将子数据以selfWith指定列的字段内容作为字段名合并至主数据, 
 *     如下示例的主数据多了home/office两个字段
 *     MergeToJson = { selfWith:2, sheetName:AddressSheet, idField:1, headerRows:1 }
 *     users:[ { id:1, name:jack, home:{ city:hefei, details:xxxxx }, office:{ city:nanjing, details:yyyyy } } ]
 * 
 * <b>MergeToJson</b>, 一对一合并, 未指定selfWith而是指定了selfName, 则将子数据列表以selfName指定的字段名合并至主数据, 
 *     如果子数据有多条后出现的会覆盖前面的, 如下示例的主数据多了address字段, home数据被office覆盖了
 *     MergeToJson = { selfName:address, sheetName:AddressSheet, idField:1, headerRows:1 }
 *     users:[ { id:1, name:jack, address:{ name:office, city:nanjing, details:yyyyy } } ]
 *
 * <b>MergeToList</b>, 一对多合并, 将子数据列表以selfName指定的字段名合并至主数据, 
 *     如下示例的主数据多了address字段, 内容为子数据列表
 *     MergeToJson = { selfName:address, sheetName:AddressSheet, idField:1, headerRows:1 }
 *     users:[ { id:1, name:jack, address:[{ name:home, city:hefei, details:xxxxx }, { name:office, city:nanjing, details:yyyyy }] } ]
 * 
 * <b>MergeToField</b>, 一对一合并, 将子数据所有字段合并至主数据, 如下示例的主数据会具有子数据的所有字段
 *     MergeToField = { sheetName:ExtraShee, idField:1, headerRows:1 }
 *     users:[ { id:1, name:jack, intro:jack-intro, description:jack-description } ]
 * </pre>
 *
 * @author zhaohuihua
 * @version 181027
 */
public class ExcelToJson {

    private static Logger log = LoggerFactory.getLogger(ExcelToJson.class);

    /**
     * 执行数据转换 <br>
     * List<ToJsonMetadata> metadata = ToJsonProperties.parseMetadata(Properties)<br>
     * Map<String, Object> result = ExcelToJson.convert(folder, metadata);<br>
     * 
     * @param folder 文件夹路径
     * @param metadata 数据转换参数
     * @return JSON数据列表
     * @throws ServiceException
     */
    public static Map<String, List<Map<String, Object>>> convert(String folder, List<ToJsonMetadata> metadata)
            throws ServiceException {
        if (VerifyTools.isBlank(metadata)) {
            return null;
        }
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (int i = 0; i < metadata.size(); i++) {
            ToJsonMetadata item = metadata.get(i);
            // fileName为必填参数, idField如果为空就不合并子数据, selfName如果为空就用index作为自身字段名称
            if (VerifyTools.isBlank(item.getFileName())) {
                log.warn("Failed to load excel rows. ToJsonMetadata fileName is null");
                continue;
            }

            List<Map<String, Object>> result = loadAndMergeData(folder, item);
            if (result != null) {
                String selfName = VerifyTools.nvl(item.getSelfName(), String.valueOf(i));
                map.put(selfName, result);
            }
        }
        return map;
    }

    /**
     * 执行数据转换 <br>
     * List&lt;Map<String, Object>&gt; result = ExcelToJson.convert(folder, metadata);<br>
     * 
     * @param folder 文件夹路径
     * @param metadata 数据转换参数
     * @return JSON数据列表
     * @throws ServiceException
     */
    public static List<Map<String, Object>> convert(String folder, ToJsonMetadata metadata) throws ServiceException {
        // fileName为必填参数, idField如果为空就不合并子数据
        if (VerifyTools.isBlank(metadata.getFileName())) {
            log.warn("Failed to load excel rows. ToJsonMetadata fileName is null");
            return null;
        }

        return loadAndMergeData(folder, metadata);
    }

    /** 导入及合并Excel数据 **/
    private static List<Map<String, Object>> loadAndMergeData(String folder, ToJsonMetadata metadata)
            throws ServiceException {

        // 导入主数据
        List<Map<String, Object>> mainRows = loadExcelRows(folder, metadata.getFileName(), metadata);

        // 准备导入合并数据
        List<MergeMetadata> mergers = metadata.getMergers();
        if (VerifyTools.isBlank(mergers)) {
            return mainRows;
        }
        String mainIdField = metadata.getIdField();
        if (VerifyTools.isBlank(mainIdField)) { // idField如果为空就不导入合并子数据
            log.warn("ToJsonMetadata idField is null");
            return mainRows;
        }

        // 导入合并数据
        for (MergeMetadata merge : mergers) {
            if (merge == null) {
                continue;
            }
            if (VerifyTools.isBlank(merge.getIdField())) {
                log.warn("MergeMetadata({}) idField is null", merge.getClass().getSimpleName());
                continue;
            }
            // 导入数据
            String fileName = VerifyTools.nvl(merge.getFileName(), metadata.getFileName());
            List<Map<String, Object>> subRows = loadExcelRows(folder, fileName, merge);
            if (VerifyTools.isBlank(subRows)) {
                continue;
            }
            // 合并数据
            if (merge instanceof MergeToField) {
                mergeData(mainRows, mainIdField, subRows, (MergeToField) merge);
            } else if (merge instanceof MergeToList) {
                mergeData(mainRows, mainIdField, subRows, (MergeToList) merge);
            } else if (merge instanceof MergeToJson) {
                mergeData(mainRows, mainIdField, subRows, (MergeToJson) merge);
            } else {
                log.warn("MergeMetadata({}) can't supportted", merge.getClass().getSimpleName());
            }
        }
        return mainRows;
    }

    /** 导入Excel数据 **/
    private static List<Map<String, Object>> loadExcelRows(String folder, String fileName, XMetadata metadata)
            throws ServiceException {
        String msg = "Failed to load excel rows. ";

        String filePath = PathTools.concat(folder, fileName);
        File excelFile = new File(filePath);
        if (!excelFile.exists()) {
            log.error(msg + "file not found, filePath={}", filePath);
            throw new ServiceException(ExcelErrorCode.FILE_NOT_FOUND);
        }
        long lastModified = excelFile.lastModified();
        String cacheKey = filePath + generateCacheKey(metadata);
        ExcelRowsItem cacheItem = EXCEL_ROWS_CACHE.get(cacheKey);
        if (cacheItem != null && cacheItem.lastModified == lastModified) {
            return cacheItem.rows;
        }

        XExcelParser parser = new XExcelParser(metadata);
        try (InputStream is = new FileInputStream(excelFile)) {
            Callback callback = new Callback();
            parser.parse(is, callback);
            if (VerifyTools.isNotBlank(callback.getFailed())) {
                log.warn("Parse excel fail data: {}", JsonTools.toJsonString(callback.getFailed()));
            }
            List<Map<String, Object>> rows = callback.rows;
            EXCEL_ROWS_CACHE.put(cacheKey, new ExcelRowsItem(lastModified, rows));
            return rows;
        } catch (IOException e) {
            log.error(msg + "filePath={}\n\t{}", filePath, JsonTools.toJsonString(metadata), e);
            throw new ServiceException(ExcelErrorCode.FILE_READ_ERROR, e);
        }
    }

    private static class Callback extends ImportCallback {

        /** 版本序列号 **/
        private static final long serialVersionUID = 1L;

        private List<Map<String, Object>> rows = new ArrayList<>();

        @Override
        public void callback(Map<String, Object> map, RowInfo row) throws ServiceException {
            try {
                rows.add(map);
            } catch (JSONException e) {
                throw new ServiceException(ExcelErrorCode.EXCEL_DATA_FORMAT_ERROR, e);
            }
        }

    }

    /** 数据缓存 **/
    private static Map<String, ExcelRowsItem> EXCEL_ROWS_CACHE = new HashMap<>();

    private static class ExcelRowsItem implements Serializable {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        private long lastModified;
        private List<Map<String, Object>> rows;

        public ExcelRowsItem(long lastModified, List<Map<String, Object>> rows) {
            this.lastModified = lastModified;
            this.rows = rows;
        }

    }

    private static SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();
    static {
        SERIALIZE_CONFIG.put(FieldInfo.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(CellValueCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(CellValueContainsTextCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(CellValueEqualsTextCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(IndexListCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(IndexRangeCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(NameListCondition.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(Required.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(ClearRule.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(DateRule.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(IgnoreIllegalValue.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(MapRule.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(NumberRule.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(RateRule.class, ToStringSerializer.instance);
        SERIALIZE_CONFIG.put(SplitRule.class, ToStringSerializer.instance);
    }

    /** 根据查询条件生成CacheKey **/
    private static String generateCacheKey(XMetadata metadata) {
        if (metadata == null) {
            return "null";
        }

        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out, SERIALIZE_CONFIG);
            serializer.config(SerializerFeature.QuoteFieldNames, false);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            if (metadata instanceof ToJsonMetadata) {
                ToJsonMetadata copy = ((ToJsonMetadata) metadata).to(ToJsonMetadata.class);
                copy.setMergers(null);
                serializer.write(copy);
            } else {
                serializer.write(metadata);
            }
            return out.toString();
        }
    }

    /** 合并数据(MergeToField) **/
    private static void mergeData(List<Map<String, Object>> mainRows, String mainIdField,
            List<Map<String, Object>> subRows, MergeToField merge) {
        if (VerifyTools.isBlank(merge.getIdField())) {
            log.warn("MergeToField idField is null");
            return;
        }
        // Map<id, Map<field, value>>
        Map<String, Map<String, Object>> subData = new HashMap<>();
        for (Map<String, Object> item : subRows) {
            Object id = item.get(merge.getIdField());
            if (VerifyTools.isNotBlank(id)) {
                subData.put(id.toString(), item);
            }
        }
        for (Map<String, Object> mainItem : mainRows) {
            Object id = mainItem.get(mainIdField);
            if (VerifyTools.isNotBlank(id)) {
                Map<String, Object> subItem = subData.get(id.toString());
                if (subItem != null) {
                    mainItem.putAll(subItem);
                }
            }
        }
    }

    /** 合并数据(MergeToList) **/
    private static void mergeData(List<Map<String, Object>> mainRows, String mainIdField,
            List<Map<String, Object>> subRows, MergeToList merge) {
        if (VerifyTools.isBlank(merge.getIdField())) {
            log.warn("MergeToList idField is null");
            return;
        }
        if (VerifyTools.isBlank(merge.getSelfName())) {
            log.warn("MergeToList selfName is null");
            return;
        }
        // Map<id, Map<field, value>>
        Map<String, List<Map<String, Object>>> subData = new HashMap<>();
        for (Map<String, Object> item : subRows) {
            Object id = item.get(merge.getIdField());
            if (VerifyTools.isNotBlank(id)) {
                if (!subData.containsKey(id.toString())) {
                    subData.put(id.toString(), new ArrayList<>());
                }
                subData.get(id.toString()).add(item);
            }
        }
        for (Map<String, Object> mainItem : mainRows) {
            Object id = mainItem.get(mainIdField);
            if (VerifyTools.isNotBlank(id)) {
                List<Map<String, Object>> subItems = subData.get(id.toString());
                if (subItems != null) {
                    mainItem.put(merge.getSelfName(), subItems);
                }
            }
        }
    }

    /** 合并数据(MergeToJson) **/
    private static void mergeData(List<Map<String, Object>> mainRows, String mainIdField,
            List<Map<String, Object>> subRows, MergeToJson merge) {
        if (VerifyTools.isBlank(merge.getIdField())) {
            log.warn("MergeToJson idField is null");
            return;
        }
        if (VerifyTools.isAllBlank(merge.getSelfWith(), merge.getSelfName())) {
            log.warn("MergeToJson selfName and selfWith is null");
            return;
        }
        if (VerifyTools.isNotBlank(merge.getSelfWith())) {
            // 一对多合并, 将子数据以selfWith指定列的字段内容作为字段名合并至主数据
            // Map<id, Map<field, value>>
            Map<String, List<Map<String, Object>>> subData = new HashMap<>();
            for (Map<String, Object> item : subRows) {
                Object id = item.get(merge.getIdField());
                if (VerifyTools.isNotBlank(id)) {
                    if (!subData.containsKey(id.toString())) {
                        subData.put(id.toString(), new ArrayList<>());
                    }
                    subData.get(id.toString()).add(item);
                }
            }
            for (Map<String, Object> mainItem : mainRows) {
                Object id = mainItem.get(mainIdField);
                if (VerifyTools.isNotBlank(id)) {
                    List<Map<String, Object>> subItems = subData.get(id);
                    for (Map<String, Object> subItem : subItems) {
                        Object selfName = subItem.get(merge.getSelfWith());
                        if (VerifyTools.isNotBlank(selfName)) {
                            mainItem.put(selfName.toString(), subItem);
                        }
                    }
                }
            }
        } else if (VerifyTools.isNotBlank(merge.getSelfName())) {
            // 一对一合并, 则将子数据列表以selfName指定的字段名合并至主数据
            // Map<id, Map<field, value>>
            Map<String, Map<String, Object>> subData = new HashMap<>();
            for (Map<String, Object> item : subRows) {
                Object id = item.get(merge.getIdField());
                if (VerifyTools.isNotBlank(id)) {
                    subData.put(id.toString(), item);
                }
            }
            for (Map<String, Object> mainItem : mainRows) {
                Object id = mainItem.get(mainIdField);
                if (VerifyTools.isNotBlank(id)) {
                    Map<String, Object> subItem = subData.get(id.toString());
                    if (subItem != null) {
                        mainItem.put(merge.getSelfName(), subItem);
                    }
                }
            }
        }
    }
}
