package com.gitee.qdbp.db.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.utils.NamingTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.db.condition.DbCondition;
import com.gitee.qdbp.db.condition.DbField;
import com.gitee.qdbp.db.condition.DbUpdate;
import com.gitee.qdbp.db.condition.DbWhere;
import com.gitee.qdbp.db.condition.OrderType;
import com.gitee.qdbp.db.condition.Ordering;
import com.gitee.qdbp.db.condition.SubWhere;
import com.gitee.qdbp.db.exception.UnsupportFieldExeption;
import com.gitee.qdbp.db.model.ColumnInfo;
import com.gitee.qdbp.db.model.PrimaryKey;
import com.gitee.qdbp.db.sql.SqlBuffer;
import com.joyintech.entity.IdEntity;

/**
 * 生成SQL的工具类
 *
 * @author 赵卉华
 * @version 181218
 */
public abstract class DbTools {

    private static Logger log = LoggerFactory.getLogger(DbTools.class);
    /** 分页/排序对象的通用字段 **/
    private static List<String> COMMON_FIELDS = Arrays.asList("_", "extra", "offset", "pageSize", "skip", "rows",
        "page", "needCount", "paging", "ordering");
    /** 允许数组的字段名后缀 **/
    private static List<String> WHERE_ARRAY_FIELDS = Arrays.asList("In", "NotIn", "Between", "NotBetween");

    /**
     * 将Map转换为Java对象<br>
     * map的key是数据表的列名, 会根据class的注解转换为字段名
     * 
     * @param map 数据
     * @param clazz 目标类型
     * @return Java对象
     * @author 赵卉华
     */
    public static <T> T mapToJavaBean(Map<String, Object> map, Class<T> clazz) {
        if (map == null || clazz == null) {
            return null;
        }

        // 1. 从bean.getClass()通过注释获取列名与字段名的对应关系
        List<ColumnInfo> columns = DbTools.parseColumnList(clazz);
        return mapToJavaBean(map, columns, clazz);
    }

    /**
     * 将Map转换为Java对象<br>
     * map的key是数据表的列名, 会根据columns信息转换为字段名
     * 
     * @param map 数据
     * @param columns 字段列表信息
     * @param clazz 目标类型
     * @return Java对象
     * @author 赵卉华
     */
    public static <T> T mapToJavaBean(Map<String, Object> map, List<ColumnInfo> columns, Class<T> clazz) {
        if (map == null || clazz == null || columns == null || columns.isEmpty()) {
            return null;
        }

        Map<String, String> columnFieldMaps = DbTools.toColumnFieldMap(columns);
        // 2. properties是列名与字段值的对应关系, 转换为字段名与字段值的对应关系
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String columnName = entry.getKey();
            if (columnFieldMaps.containsKey(columnName)) {
                String fieldName = columnFieldMaps.get(columnName);
                fieldValues.put(fieldName, entry.getValue());
            }
        }
        // 3. 利用fastjson工具进行Map到JavaObject的转换
        return TypeUtils.castToJavaBean(map, clazz);
    }

    /**
     * 将Java对象转换为Where对象
     * 
     * @param entity Java对象
     * @return Where对象
     */
    public static DbWhere parseWhereFromEntity(Object entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> map = (JSONObject) JSON.toJSON(entity);
        return DbWhere.from(map);
    }

    /**
     * 将Java对象转换为Update对象
     * 
     * @param entity Java对象
     * @return Update对象
     */
    public static DbUpdate parseUpdateFromEntity(Object entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> map = (JSONObject) JSON.toJSON(entity);
        return DbUpdate.from(map);
    }

    /**
     * 从请求参数中构建Where对象<br>
     * 只会包含clazz注解中通过@JoyInColumn指定的字段名
     * 
     * @param params 请求参数
     * @param clazz 实体类
     * @return Where对象
     */
    public static <T> DbWhere parseWhereFromParams(Map<String, String[]> params, Class<T> clazz) {
        List<ColumnInfo> columns = parseColumnList(clazz);
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);
        List<String> fieldNames = new ArrayList<String>(fieldColumnMap.keySet());
        Map<String, Object> map = parseMapWithWhitelist(params, fieldNames, WHERE_ARRAY_FIELDS);
        return DbWhere.from(map);
    }

    /**
     * 从请求参数中构建Where对象<br>
     * <pre>
     * 转换规则:
        fieldName$Equals(=), fieldName$NotEquals(!=), 
        fieldName$LessThen(<), fieldName$LessEqualsThen(<=), 
        fieldName$GreaterThen(>), fieldName$GreaterEqualsThen(>=), 
        fieldName$IsNull, fieldName$IsNotNull, 
        fieldName$Like, fieldName$NotLike, fieldName$Starts, fieldName$Ends, 
        fieldName$In, fieldName$NotIn, fieldName$Between
     * </pre>
     * 
     * @param params 请求参数
     * @param excludeDefault 是否排除默认的公共字段<br>
     *            extra, offset, pageSize, skip, rows, page, needCount, paging, orderings
     * @param excludeFields 排除的字段名, optional
     * @return Where对象
     */
    public static DbWhere parseWhereFromParams(Map<String, String[]> params, boolean excludeDefault,
            String... excludeFields) {
        List<String> realExcludeFields = new ArrayList<String>();
        if (excludeDefault) {
            realExcludeFields.addAll(COMMON_FIELDS);
        }
        if (VerifyTools.isNotBlank(excludeFields)) {
            for (String string : excludeFields) {
                realExcludeFields.add(string);
            }
        }
        Map<String, Object> map = parseMapWithBlacklist(params, realExcludeFields, WHERE_ARRAY_FIELDS);
        return DbWhere.from(map);
    }

    /**
     * 从请求参数中构建Update对象<br>
     * 只会包含clazz注解中通过@JoyInColumn指定的字段名
     * <pre>
     * 转换规则:
        fieldName 或 fieldName$Equals(=)
        fieldName$Add(增加值)
        fieldName$ToNull(转换为空)
     * </pre>
     * 
     * @param params 请求参数
     * @param clazz 实体类
     * @return Update对象
     */
    public static <T> DbUpdate parseUpdateFromParams(Map<String, String[]> params, Class<T> clazz) {
        List<ColumnInfo> columns = parseColumnList(clazz);
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);
        List<String> fieldNames = new ArrayList<String>(fieldColumnMap.keySet());
        Map<String, Object> map = parseMapWithWhitelist(params, fieldNames, null);
        return DbUpdate.from(map);
    }

    /**
     * 从请求参数中构建Update对象
     * 
     * @param params 请求参数
     * @param excludeDefault 是否排除默认的公共字段<br>
     *            extra, offset, pageSize, skip, rows, page, needCount, paging, orderings
     * @param excludeFields 排除的字段名, optional
     * @return Update对象
     */
    public static DbUpdate parseUpdateFromParams(Map<String, String[]> params, boolean excludeDefault,
            String... excludeFields) {
        List<String> realExcludeFields = new ArrayList<String>();
        if (excludeDefault) {
            realExcludeFields.addAll(COMMON_FIELDS);
        }
        if (VerifyTools.isNotBlank(excludeFields)) {
            for (String string : excludeFields) {
                realExcludeFields.add(string);
            }
        }
        Map<String, Object> map = parseMapWithBlacklist(params, realExcludeFields, null);
        return DbUpdate.from(map);
    }

    /**
     * 将请求参数转换为Map对象
     * 
     * @param params 请求参数, required
     * @param excludeFields 排除的字段名, optional
     * @param allowArraySuffixes 允许数组的字段名后缀, optional
     * @return Map对象
     */
    public static Map<String, Object> parseMapWithBlacklist(Map<String, String[]> params, List<String> excludeFields,
            List<String> allowArraySuffixes) {
        if (params == null) {
            return null;
        }

        // 需要排除的字段名
        Map<String, Void> blacklistMap = new HashMap<String, Void>();
        if (VerifyTools.isNotBlank(excludeFields)) {
            for (String field : excludeFields) {
                blacklistMap.put(field, null);
            }
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (VerifyTools.isAnyBlank(entry.getKey(), entry.getValue())) {
                continue;
            }
            if (blacklistMap.containsKey(entry.getKey())) {
                continue;
            }
            String fieldName = entry.getKey();
            if (fieldName.endsWith("[]")) {
                fieldName = fieldName.substring(0, fieldName.length() - 2);
            }
            if (allowArraySuffixes != null && isEndsWith(fieldName, allowArraySuffixes)) {
                resultMap.put(fieldName, entry.getValue());
            } else {
                resultMap.put(fieldName, entry.getValue()[0]);
            }
        }
        return resultMap;
    }

    /**
     * 将请求参数转换为Map对象
     * 
     * @param params 请求参数, required
     * @param includeFields 有效的字段名, required
     * @param allowArraySuffixes 允许数组的字段名后缀, optional
     * @return Map对象
     */
    public static Map<String, Object> parseMapWithWhitelist(Map<String, String[]> params, List<String> includeFields,
            List<String> allowArraySuffixes) {
        if (params == null) {
            return null;
        }

        // 有效的字段名
        Map<String, Void> whitelistMap = new HashMap<String, Void>();
        if (VerifyTools.isNotBlank(includeFields)) {
            for (String field : includeFields) {
                whitelistMap.put(field, null);
            }
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (VerifyTools.isAnyBlank(entry.getKey(), entry.getValue())) {
                continue;
            }
            String fieldName = entry.getKey();
            if (fieldName.endsWith("[]")) {
                fieldName = fieldName.substring(0, fieldName.length() - 2);
            }
            String realFieldName = fieldName;
            int dollarLastIndex = fieldName.lastIndexOf('$');
            if (dollarLastIndex > 0) {
                realFieldName = fieldName.substring(0, dollarLastIndex);
            }
            if (!whitelistMap.containsKey(realFieldName)) {
                continue;
            }
            if (allowArraySuffixes != null && isEndsWith(fieldName, allowArraySuffixes)) {
                resultMap.put(fieldName, entry.getValue());
            } else {
                resultMap.put(fieldName, entry.getValue()[0]);
            }
        }
        return resultMap;
    }

    private static boolean isEndsWith(String fieldName, List<String> suffixes) {
        if (VerifyTools.isBlank(suffixes)) {
            return false;
        }
        for (String suffix : suffixes) {
            if (fieldName.endsWith('$' + suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成Where SQL语句
     * 
     * @param where 查询条件
     * @param clazz 类型
     * @return SQL语句
     */
    public static SqlBuffer buildWhereSql(DbWhere where, Class<?> clazz) {
        Map<String, String> fieldColumnMap = parseFieldColumnMap(clazz);
        return buildWhereSql(where, fieldColumnMap);
    }

    /**
     * 生成Where SQL语句
     * 
     * @param where 查询条件
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @return SQL语句
     */
    public static SqlBuffer buildWhereSql(DbWhere where, List<ColumnInfo> columns) {
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);
        return buildWhereSql(where, fieldColumnMap);
    }

    /**
     * 生成Where SQL语句
     * 
     * @param where 查询条件
     * @param fieldColumnMap 字段名与列名映射表
     */
    public static SqlBuffer buildWhereSql(DbWhere where, Map<String, String> fieldColumnMap) {
        if (VerifyTools.isBlank(fieldColumnMap)) {
            throw new IllegalArgumentException("column is empty");
        }
        List<DbCondition> items = where == null ? null : where.items();
        if (VerifyTools.isBlank(items)) {
            return null;
        }

        try {
            SqlBuffer buffer = toWhereSql(where, fieldColumnMap);
            if (buffer != null && !buffer.isEmpty()) {
                buffer.prepend("WHERE", ' ');
            }
            return buffer;
        } catch (UnsupportFieldExeption e) {
            // 此处必须报错, 否则将可能由于忽略导致严重的问题
            // 由于前面的判断都是基于where.isEmpty(), 逻辑只要where不是空就必定会生成where语句
            // 如果不报错, 那么有可能因为字段名写错导致where语句为空, 从而导致表记录被全部删除!
            // 例如delete操作where.on("id", "=", "xxx");的字段名写成idd, 生成的语句就是DELETE FROM tableName
            e.setMessage("Where sql unsupported fields");
            throw e;
        }
    }

    /**
     * DbWhere转换为Where SQL语句
     * 
     * @param where 查询条件
     * @param fieldColumnMap 字段名与列名映射表
     */
    public static SqlBuffer toWhereSql(DbWhere where, Map<String, String> fieldColumnMap) {
        if (where == null || where.isEmpty()) {
            return null;
        }

        String logicType = "AND";
        if (where instanceof SubWhere) {
            logicType = ((SubWhere) where).getLogicType();
        }

        SqlBuffer buffer = new SqlBuffer();
        List<String> unsupported = new ArrayList<String>();
        List<DbCondition> items = where.items();
        boolean first = true;
        for (DbCondition condition : items) {
            if (condition.isEmpty()) {
                continue;
            }

            if (first) {
                first = false;
            } else {
                buffer.append(' ', logicType, ' ');
            }

            if (condition instanceof SubWhere) {
                SubWhere subWhere = (SubWhere) condition;
                try {
                    SqlBuffer subSql = toWhereSql(subWhere, fieldColumnMap);
                    buffer.append(subSql);
                } catch (UnsupportFieldExeption e) {
                    unsupported.addAll(e.getFields());
                }
            } else {
                DbField item = (DbField) condition;

                try {
                    SqlBuffer fieldSql = toWhereSql(item, fieldColumnMap);
                    buffer.append(fieldSql);
                } catch (UnsupportFieldExeption e) {
                    unsupported.addAll(e.getFields());
                }
            }
        }
        if (unsupported.isEmpty()) {
            if (!buffer.isEmpty()) {
                if (where instanceof SubWhere) {
                    // 子SQL要用括号括起来
                    buffer.prepend("( ").append(" )");
                    if (!((SubWhere) where).isPositive()) {
                        buffer.prepend("NOT", ' ');
                    }
                }
            }
            return buffer;
        } else {
            throw new UnsupportFieldExeption("Unsupported fields", unsupported);
        }
    }

    /**
     * DbField转换为Where SQL语句
     * 
     * @param item 字段条件
     * @param fieldColumnMap 字段名与列名映射表
     */
    public static SqlBuffer toWhereSql(DbField item, Map<String, String> fieldColumnMap) throws UnsupportFieldExeption {

        String operateType = VerifyTools.nvl(item.getOperateType(), "Equals");
        String fieldName = item.getFieldName();
        Object fieldValue = item.getFieldValue();
        String columnName = fieldColumnMap.get(fieldName);
        if (VerifyTools.isBlank(fieldName)) {
            throw new UnsupportFieldExeption("Unsupported fields", Arrays.asList("fieldName#IsBlank"));
        }
        if (VerifyTools.isBlank(columnName)) {
            throw new UnsupportFieldExeption("Unsupported fields", Arrays.asList(fieldName));
        }

        SqlBuffer buffer = new SqlBuffer();
        if ("In".equals(operateType) || "NotIn".equals(operateType) || "Between".equals(operateType)) {
            if (VerifyTools.isBlank(fieldValue)) {
                List<String> unsupported = Arrays.asList(fieldName + '(' + fieldValue + "#IsBlank" + ')');
                throw new UnsupportFieldExeption("Unsupported fields", unsupported);
            }
            List<Object> values;
            if (fieldValue.getClass().isArray()) {
                values = Arrays.asList((Object[]) fieldValue);
            } else if (fieldValue instanceof Collection) {
                values = new ArrayList<Object>((Collection<?>) fieldValue);
            } else if (fieldValue instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) fieldValue;
                values = new ArrayList<Object>(map.values());
            } else if (fieldValue instanceof Iterable) {
                values = new ArrayList<Object>();
                Iterable<?> iterable = (Iterable<?>) fieldValue;
                for (Object temp : iterable) {
                    values.add(temp);
                }
            } else {
                values = Arrays.asList(fieldValue);
            }
            if ("Between".equals(operateType)) {
                if (values.size() < 2) {
                    List<String> unsupported = Arrays.asList(fieldName + '(' + operateType + "#MissVars" + ')');
                    throw new UnsupportFieldExeption("Unsupported fields", unsupported);
                }
                buffer.append(columnName);
                buffer.append(' ', "BETWEEN", ' ');
                buffer.addVariable(fieldName, values.get(0));
                buffer.append(' ', "AND", ' ');
                buffer.addVariable(fieldName, values.get(1));
            } else {
                if (values.size() == 1) {
                    buffer.append(columnName).append('=').addVariable(fieldName, values.get(0));
                } else {
                    String operate = "In".equals(operateType) ? "IN" : "NOT IN";
                    buffer.append(columnName).append(' ', operate, ' ').append('(');
                    for (int i = 0; i < values.size(); i++) {
                        if (i > 0) {
                            buffer.append(',');
                        }
                        buffer.addVariable(fieldName, values.get(i));
                    }
                    buffer.append(')');
                }
            }
        } else if ("IsNull".equals(operateType)) {
            buffer.append(columnName).append(' ', "IS NULL");
        } else if ("IsNotNull".equals(operateType)) {
            buffer.append(columnName).append(' ', "IS NOT NULL");
        } else {
            if ("GreaterThen".equals(operateType)) {
                buffer.append(columnName).append(">").addVariable(fieldName, fieldValue);
            } else if ("GreaterEqualsThen".equals(operateType)) {
                buffer.append(columnName).append(">=").addVariable(fieldName, fieldValue);
            } else if ("LessThen".equals(operateType)) {
                buffer.append(columnName).append('<').addVariable(fieldName, fieldValue);
            } else if ("LessEqualsThen".equals(operateType)) {
                buffer.append(columnName).append("<=").addVariable(fieldName, fieldValue);
            } else if ("Starts".equals(operateType)) {
                buffer.append(columnName, ' ').append(DbDialect.toStartsWithSql(fieldName, fieldValue));
            } else if ("Ends".equals(operateType)) {
                buffer.append(columnName, ' ').append(DbDialect.toEndsWithSql(fieldName, fieldValue));
            } else if ("Like".equals(operateType)) {
                buffer.append(columnName, ' ').append(DbDialect.toLikeSql(fieldName, fieldValue));
            } else if ("NotLike".equals(operateType)) {
                buffer.append(columnName, ' ').append("NOT", ' ').append(DbDialect.toLikeSql(fieldName, fieldValue));
            } else if ("NotEquals".equals(operateType)) {
                buffer.append(columnName).append("!=").addVariable(fieldName, fieldValue);
            } else if ("Equals".equals(operateType)) {
                buffer.append(columnName).append('=').addVariable(fieldName, fieldValue);
            } else {
                List<String> unsupported = Arrays.asList(fieldName + '(' + operateType + ')');
                throw new UnsupportFieldExeption("Unsupported fields", unsupported);
            }
        }
        return buffer;
    }

    /**
     * 生成OrderBy SQL语句
     * 
     * @param orderings 排序条件
     * @param clazz 类型
     * @return SQL语句
     */
    public static SqlBuffer buildOrderBySql(List<Ordering> orderings, Class<?> clazz) {
        Map<String, String> fieldColumnMap = parseFieldColumnMap(clazz);
        return buildOrderBySql(orderings, fieldColumnMap);
    }

    /**
     * 生成OrderBy SQL语句
     * 
     * @param orderings 排序条件
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @return SQL语句
     */
    public static SqlBuffer buildOrderBySql(List<Ordering> orderings, List<ColumnInfo> columns) {
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);
        return buildOrderBySql(orderings, fieldColumnMap);
    }

    private static String PINYIN_SUFFIX = "(PINYIN)";

    /**
     * 生成OrderBy SQL语句
     * 
     * @param orderings 排序条件
     * @param fieldColumnMap 字段名与列名映射表
     * @return SQL语句
     */
    public static SqlBuffer buildOrderBySql(List<Ordering> orderings, Map<String, String> fieldColumnMap) {
        if (VerifyTools.isAnyBlank(orderings, fieldColumnMap)) {
            return null;
        }
        SqlBuffer buffer = new SqlBuffer();
        buffer.append("ORDER BY");
        List<String> unsupported = new ArrayList<String>();
        boolean first = true;
        for (Ordering item : orderings) {
            String fieldName = item.getOrderBy();
            // 汉字按拼音排序: userName(PINYIN)
            boolean usePinyin = false;
            if (fieldName.toUpperCase().endsWith(PINYIN_SUFFIX)) {
                usePinyin = true;
                fieldName = fieldName.substring(0, fieldName.length() - PINYIN_SUFFIX.length()).trim();
            }
            String columnName = fieldColumnMap.get(fieldName);
            if (VerifyTools.isBlank(columnName)) {
                unsupported.add(fieldName);
                continue;
            }
            if (usePinyin) { // 根据数据库类型转换为拼音排序表达式
                columnName = DbDialect.toPinyinOrderByExpression(columnName);
            }
            if (first) {
                first = false;
            } else {
                buffer.append(',');
            }
            buffer.append(' ', columnName);
            OrderType orderType = item.getOrderType();
            if (orderType == OrderType.ASC) {
                buffer.append(' ', "ASC");
            } else if (orderType == OrderType.DESC) {
                buffer.append(' ', "DESC");
            }
        }
        if (!unsupported.isEmpty() && log.isWarnEnabled()) {
            throw new IllegalArgumentException("Order by sql unsupported fields: " + unsupported);
        }
        return buffer;
    }

    /**
     * 生成Select/Insert字段列表SQL语句
     * 
     * @param clazz 类型
     * @return SQL语句
     */
    public static SqlBuffer buildFieldsSql(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        return buildFieldsSql(parseColumnList(clazz));
    }

    /**
     * 生成Select/Insert字段列表SQL语句
     * 
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @return SQL语句
     */
    public static SqlBuffer buildFieldsSql(List<ColumnInfo> columns) {
        if (VerifyTools.isBlank(columns)) {
            throw new IllegalArgumentException("columns is empty");
        }

        SqlBuffer buffer = new SqlBuffer();
        for (ColumnInfo item : columns) {
            if (!buffer.isEmpty()) {
                buffer.append(',');
            }
            buffer.append(item.getColumnName());
        }
        return buffer;
    }

    /**
     * 生成Select/Insert字段列表SQL语句
     * 
     * @param fields 只包含指定字段名
     * @param clazz 类型
     * @return SQL语句
     */
    public static SqlBuffer buildFieldsSql(Set<String> fields, Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        return buildFieldsSql(fields, parseColumnList(clazz));
    }

    /**
     * 生成Select/Insert字段列表SQL语句
     * 
     * @param fields 只包含指定字段名
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @return SQL语句
     */
    public static SqlBuffer buildFieldsSql(Set<String> fields, List<ColumnInfo> columns) {
        if (VerifyTools.isBlank(columns)) {
            throw new IllegalArgumentException("columns is empty");
        }
        if (VerifyTools.isBlank(fields)) {
            throw new IllegalArgumentException("fields is empty");
        }

        // Field-Column
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);

        // 字段名映射
        Map<String, Void> fieldMap = new HashMap<String, Void>();
        List<String> unsupported = new ArrayList<String>();
        for (String item : fields) {
            if (fieldColumnMap.containsKey(item)) {
                fieldMap.put(item, null);
            } else {
                unsupported.add(item);
            }
        }
        if (!unsupported.isEmpty() && log.isWarnEnabled()) {
            log.warn("Unsupported fields: {}", unsupported);
        }

        // 根据列顺序生成SQL
        SqlBuffer buffer = new SqlBuffer();
        for (ColumnInfo item : columns) {
            if (!fieldMap.containsKey(item.getFieldName())) {
                continue;
            }
            if (!buffer.isEmpty()) {
                buffer.append(',');
            }
            buffer.append(item.getColumnName());
        }
        return buffer;
    }

    /**
     * 生成Insert字段值占位符列表SQL语句
     * 
     * @param clazz 类型
     * @param fields 只包含指定字段名
     * @return SQL语句
     */
    public static SqlBuffer buildInsertValuesSql(Map<String, Object> entity, Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        return buildInsertValuesSql(entity, parseColumnList(clazz));
    }

    /**
     * 生成Insert字段值占位符列表SQL语句
     * 
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @param fields 只包含指定字段名
     * @return SQL语句
     */
    public static SqlBuffer buildInsertValuesSql(Map<String, Object> entity, List<ColumnInfo> columns) {
        if (VerifyTools.isBlank(entity)) {
            throw new IllegalArgumentException("entity is empty");
        }
        if (VerifyTools.isBlank(columns)) {
            throw new IllegalArgumentException("columns is empty");
        }

        // Field-Column
        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);

        List<String> unsupported = new ArrayList<String>();
        for (String item : entity.keySet()) {
            if (!fieldColumnMap.containsKey(item)) {
                unsupported.add(item);
            }
        }
        if (!unsupported.isEmpty() && log.isWarnEnabled()) {
            throw new IllegalArgumentException("Insert values sql unsupported fields: " + unsupported);
        }

        // 根据列顺序生成SQL
        SqlBuffer buffer = new SqlBuffer();
        for (ColumnInfo item : columns) {
            if (!entity.containsKey(item.getFieldName())) {
                continue;
            }
            if (!buffer.isEmpty()) {
                buffer.append(',');
            }
            buffer.addVariable(item.getFieldName(), entity.get(item.getFieldName()));
        }
        return buffer;
    }

    /**
     * 生成Update字段值占位符列表SQL语句<br>
     * 格式: COLUMN_NAME1=:fieldName$U$1, COLUMN_NAME2=:fieldName$U$2<br>
     * 
     * @param entity Update对象
     * @param clazz 类型
     * @return SQL语句
     */
    public static SqlBuffer buildUpdateValuesSql(DbUpdate entity, Class<?> clazz) {
        List<ColumnInfo> columns = parseColumnList(clazz);
        return buildUpdateValuesSql(entity, columns);
    }

    /**
     * 生成Update字段值占位符列表SQL语句<br>
     * 格式: COLUMN_NAME1=:fieldName$U$1, COLUMN_NAME2=:fieldName$U$2<br>
     * 
     * @param entity Update对象
     * @param columns 字段名和数据库列名的映射表, ColumnInfo: fieldName - columnName
     * @return SQL语句
     */
    public static SqlBuffer buildUpdateValuesSql(DbUpdate entity, List<ColumnInfo> columns) {
        if (VerifyTools.isBlank(entity)) {
            throw new IllegalArgumentException("entity is empty");
        }
        if (VerifyTools.isBlank(columns)) {
            throw new IllegalArgumentException("columns is empty");
        }

        Map<String, String> fieldColumnMap = toFieldColumnMap(columns);

        List<String> unsupported = new ArrayList<String>();
        SqlBuffer buffer = new SqlBuffer();
        for (DbField item : entity.fields()) {
            String operateType = VerifyTools.nvl(item.getOperateType(), "Set");
            String fieldName = item.getFieldName();
            Object fieldValue = item.getFieldValue();
            if (VerifyTools.isAnyBlank(fieldName, fieldValue)) {
                continue;
            }
            String columnName = fieldColumnMap.get(fieldName);
            if (VerifyTools.isBlank(columnName)) {
                unsupported.add(fieldName);
                continue;
            }

            if (!buffer.isEmpty()) {
                buffer.append(',');
            }
            if ("ToNull".equals(operateType)) {
                buffer.append(columnName).append('=').append("NULL");
            } else if ("Add".equals(operateType)) {
                if (fieldValue instanceof Number && ((Number) fieldValue).doubleValue() < 0) {
                    buffer.append(columnName).append('=');
                    buffer.append(columnName).append('-');
                    buffer.addVariable(fieldName, fieldValue);
                } else {
                    buffer.append(columnName).append('=');
                    buffer.append(columnName).append('+');
                    buffer.addVariable(fieldName, fieldValue);
                }
            } else if ("Set".equals(operateType)) {
                buffer.append(columnName).append('=');
                buffer.addVariable(fieldName, fieldValue);
            } else {
                unsupported.add(fieldName + '(' + operateType + ')');
            }
        }
        if (!unsupported.isEmpty() && log.isWarnEnabled()) {
            throw new IllegalArgumentException("Update values sql unsupported fields: " + unsupported);
        }
        return buffer;
    }

    /**
     * 通过类注解获取表名
     * 
     * @param clazz 类名
     * @return 表名
     */
    public static String parseTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation != null) {
            return annotation.name();
        } else {
            return NamingTools.toUnderlineString(clazz.getSimpleName()).toUpperCase();
        }
    }

    /**
     * 通过类注解获取表名
     * 
     * @param clazz 类名
     * @return 表名
     */
    public static PrimaryKey parsePrimaryKey(Class<?> clazz) {
        Class<?> temp = clazz;
        while (temp != null) {
            Field[] fields = temp.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                JoyInId annotation = field.getAnnotation(JoyInId.class);
                if (annotation != null) {
                    PrimaryKey pk = new PrimaryKey();
                    pk.setFieldName(field.getName());
                    pk.setColumnName(annotation.name());
                    pk.setColumnText(annotation.alias());
                    pk.setType(annotation.genType());
                    return pk;
                }
            }
            temp = temp.getSuperclass();
        }
        return null;
    }

    /**
     * 通过注解获取字段名和数据库列名的映射表, 如果没有注解则不返回
     * 
     * @param clazz 类型
     * @return ColumnInfo: fieldName - columnName
     */
    public static List<ColumnInfo> parseColumnList(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        // 字段顺序: ID放在最前面, 然后按继承顺序排序, 最后放公共的字段(创建人/创建时间/更新人/更新时间/逻辑删除标记)
        ColumnInfo idColumn = null;
        List<ColumnInfo> commColumns = new ArrayList<ColumnInfo>();
        List<ColumnInfo> all = new ArrayList<ColumnInfo>();
        Map<String, String> map = new HashMap<String, String>();
        Class<?> temp = clazz;
        while (temp != null) {
            Field[] fields = temp.getDeclaredFields();
            List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
            for (Field field : fields) {
                if (map.containsKey(field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                String fieldName = field.getName();
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (idColumn == null) {
                    Id idAnnotation = field.getAnnotation(Id.class);
                    if (idAnnotation != null) {
                        if (columnAnnotation == null) {
                            String columnName = idAnnotation.name();
                            String columnText = idAnnotation.alias();
                            idColumn = new ColumnInfo(fieldName, columnName, columnText);
                        } else {
                            String columnName = columnAnnotation.name();
                            String columnText = columnAnnotation.alias();
                            idColumn = new ColumnInfo(fieldName, columnName, columnText);
                        }
                        continue;
                    }
                }
                if (columnAnnotation != null) {
                    String columnName = columnAnnotation.name();
                    String columnText = columnAnnotation.alias();
                    columns.add(new ColumnInfo(fieldName, columnName, columnText));
                }
            }
            if (!columns.isEmpty()) {
                if (temp.getPackage() == IdEntity.class.getPackage()) {
                    commColumns.addAll(0, columns); // 公共字段
                } else {
                    all.addAll(0, columns);
                }
            }
            temp = temp.getSuperclass();
        }
        if (idColumn != null) {
            all.add(0, idColumn);
        }
        if (!commColumns.isEmpty()) {
            all.addAll(commColumns);
        }
        return all;
    }

    /**
     * 通过注解获取字段名和数据库列名的映射表, 如果没有注解则不返回
     * 
     * @param clazz 类型
     * @return map: fieldName - columnName
     */
    public static Map<String, String> parseFieldColumnMap(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<ColumnInfo> columns = parseColumnList(clazz);
        return toFieldColumnMap(columns);
    }

    /**
     * 列表转换为Field-Column映射表
     * 
     * @param columns 字段列表信息
     * @return Field-Column映射表
     */
    public static Map<String, String> toFieldColumnMap(List<ColumnInfo> columns) {
        if (columns == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (ColumnInfo item : columns) {
            map.put(item.getFieldName(), item.getColumnName());
        }
        return map;
    }

    /**
     * 通过注解获取数据库列名和字段名的映射表, 如果没有注解则不返回
     * 
     * @param clazz 类型
     * @return map: columnName - fieldName
     */
    public static Map<String, String> parseColumnFieldMap(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<ColumnInfo> columns = parseColumnList(clazz);
        return toColumnFieldMap(columns);
    }

    /**
     * 列表转换为Field-Column映射表
     * 
     * @param columns 字段列表信息
     * @return Column-Field映射表
     */
    public static Map<String, String> toColumnFieldMap(List<ColumnInfo> columns) {
        if (columns == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (ColumnInfo item : columns) {
            map.put(item.getColumnName(), item.getFieldName());
        }
        return map;
    }

    /**
     * 清除Map中的空值
     * 
     * @param map Map对象
     * @param emptyOnNull 如果map==null, 是否返回空Map. 如果是, 返回空Map, 否则返回null
     * @return 清空后的Map对象
     */
    public static Map<String, Object> clearBlankValue(Map<String, Object> map, boolean emptyOnNull) {
        if (map == null) {
            return emptyOnNull ? new HashMap<String, Object>() : null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (VerifyTools.isNotBlank(entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 将Java对象转换为Map
     * 
     * @param object Java对象
     * @return Map
     */
    public static Map<String, Object> toMap(Object object) {
        return toMap(object, false);
    }

    /**
     * 将Java对象转换为Map
     * 
     * @param object Java对象
     * @param clearBlankValue 是否清除空值
     * @return Map
     */
    public static Map<String, Object> toMap(Object object, boolean clearBlankValue) {
        if (object == null) {
            return null;
        }
        Map<String, Object> map = (JSONObject) JSON.toJSON(object);
        return clearBlankValue ? clearBlankValue(map, false) : map;
    }

}
