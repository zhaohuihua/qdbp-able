package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.model.paging.Paging;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 查询工具, 根据查询条件过滤, 分页
 *
 * @author zhaohuihua
 * @version 181102
 */
public class QueryTools {

    /**
     * 根据查询条件过滤, 分页<br>
     * list = [ { userId:"10001", nickName:"我是老大", scoreBetween:150, tags:["T01", "T06", "T08"] } ]<br>
     * where = { userIdEquals:"10001", nickNameLike:"大", scoreBetween:"100|200", tagsExists:"T06" }<br>
     * 
     * @param list 原始数据
     * @param where 查询条件
     * @param paging 分页条件
     * @return 过滤后的结果集
     */
    public static List<Map<String, Object>> filter(List<Map<String, Object>> list, Map<String, Object> where,
            Paging paging) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        if (where == null || where.isEmpty()) {
            return paginate(list, paging);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> data : list) {
            if (matches(data, where)) {
                result.add(data); // 只有能匹配所有条件才加入结果集
            }
        }

        return paging == null ? result : paginate(result, paging);
    }

    private static Pattern NUMBER = Pattern.compile("^[\\+\\-]?[\\d,]*(\\.\\d+)?$");
    private static Pattern DATE = Pattern.compile("^[\\d\\-:\\. ]+$");

    /**
     * 判断数据是否匹配条件
     * 
     * @param data 数据
     * @param where 条件
     * @return 是否匹配
     */
    public static boolean matches(Map<String, Object> data, Map<String, Object> where) {
        if (where == null || where.isEmpty()) {
            return true;
        }
        if (data == null || data.isEmpty()) {
            return false;
        }
        for (Entry<String, Object> entry : where.entrySet()) {
            String key = entry.getKey();
            Object expectValue = entry.getValue();
            if (VerifyTools.isBlank(expectValue)) {
                continue;
            }
            String type;
            if (key.endsWith(type = "Equals")) {
                String field = StringTools.removeSuffix(key, type);
                Object actualValue = data.get(field);
                if (!equals(actualValue, expectValue)) {
                    return false;
                }
            } else if (key.endsWith(type = "Like")) {
                String field = StringTools.removeSuffix(key, type);
                Object actualValue = data.get(field);
                if (!like(actualValue, expectValue)) {
                    return false;
                }
            } else if (key.endsWith(type = "Between")) {
                String field = StringTools.removeSuffix(key, type);
                Object actualValue = data.get(field);
                if (VerifyTools.isBlank(actualValue)) {
                    return false;
                }
                // Between只支持Number和Date
                Object realActualValue = actualValue;
                if (actualValue instanceof CharSequence) {
                    try {
                        if (NUMBER.matcher(actualValue.toString()).matches()) {
                            realActualValue = TypeUtils.castToDouble(actualValue);
                        } else if (DATE.matcher(actualValue.toString()).matches()) {
                            realActualValue = TypeUtils.castToDate(actualValue);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                if (realActualValue instanceof Number || realActualValue instanceof Date) {
                    Object minValue = null;
                    Object maxValue = null;
                    // Between需要解析minValue和maxValue两个参数
                    if (expectValue instanceof CharSequence) {
                        String[] array = StringTools.split(expectValue.toString(), true, ',', '|');
                        minValue = array.length >= 1 ? array[0] : null;
                        maxValue = array.length >= 2 ? array[1] : null;
                    } else if (expectValue.getClass().isArray()) {
                        Object[] array = (Object[]) expectValue;
                        minValue = array.length >= 1 ? array[0] : null;
                        maxValue = array.length >= 2 ? array[1] : null;
                    } else if (expectValue instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) expectValue;
                        minValue = VerifyTools.nvl(map.get("min"), map.get("minValue"));
                        maxValue = VerifyTools.nvl(map.get("max"), map.get("maxValue"));
                    } else if (expectValue instanceof Collection) {
                        Object[] array = ((Collection<?>) expectValue).toArray();
                        minValue = array.length >= 1 ? array[0] : null;
                        maxValue = array.length >= 2 ? array[1] : null;
                    } else {
                        return false;
                    }
                    if (realActualValue instanceof Number) {
                        if (!between((Number) realActualValue, minValue, maxValue)) {
                            return false;
                        }
                    } else if (realActualValue instanceof Date) {
                        if (!between((Date) realActualValue, minValue, maxValue)) {
                            return false;
                        }
                    }
                }
            } else if (key.endsWith(type = "Exists")) {
                String field = StringTools.removeSuffix(key, type);
                Object actualValue = data.get(field);
                if (VerifyTools.isBlank(actualValue)) {
                    return false;
                }
                // Exists的actualValue需要解析为数组
                Collection<?> actualValues = null;
                if (expectValue instanceof CharSequence) {
                    String[] array = StringTools.split(expectValue.toString(), true, ',', '|');
                    actualValues = ConvertTools.toList(array);
                } else if (expectValue.getClass().isArray()) {
                    Object[] array = (Object[]) expectValue;
                    actualValues = ConvertTools.toList(array);
                } else if (expectValue instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) expectValue;
                    actualValues = map.values();
                } else if (expectValue instanceof Collection) {
                    actualValues = (Collection<?>) expectValue;
                } else {
                    return false;
                }
                if (!exists(actualValues, expectValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 分页
     * 
     * @param list 数据列表
     * @param paging 分页参数
     * @return 分页后的数据列表
     */
    public static <T> List<T> paginate(List<T> list, Paging paging) {
        if (list == null || list.isEmpty() || paging == null) {
            return list;
        }
        int start = paging.getStart();
        int end = paging.getEnd();
        List<T> result = new ArrayList<>();
        for (int i = start; i < end && i < list.size(); i++) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 相等判断
     * 
     * @param actualValue 实际值
     * @param expectValue 期望值
     * @return 是否相等
     */
    public static boolean equals(Object actualValue, Object expectValue) {
        if (VerifyTools.isBlank(expectValue)) {
            return true;
        } else if (VerifyTools.isBlank(actualValue)) {
            return false;
        } else {
            if (actualValue instanceof CharSequence) {
                return VerifyTools.equals(actualValue.toString().toLowerCase(), expectValue.toString().toLowerCase());
            } else {
                return VerifyTools.equals(actualValue, expectValue);
            }
        }
    }

    /**
     * 包含判断
     * 
     * @param actualValue 实际值
     * @param expectValue 期望值
     * @return 是否包含
     */
    public static boolean like(Object actualValue, Object expectValue) {
        if (VerifyTools.isBlank(expectValue)) {
            return true;
        } else if (VerifyTools.isBlank(actualValue)) {
            return false;
        } else {
            return actualValue.toString().toLowerCase().contains(expectValue.toString().toLowerCase());
        }
    }

    /**
     * 数字范围判断
     * 
     * @param actualValue 实际值
     * @param minValue 最小值
     * @param maxValue 最大值
     * @return 是否在范围内(>= minValue and < maxValue)
     */
    public static boolean between(Number actualValue, Object minValue, Object maxValue) {
        if (minValue == null && maxValue == null) {
            return true;
        } else if (actualValue == null) {
            return false;
        } else {
            try {
                double actuval = actualValue.doubleValue();
                Number minNumber = VerifyTools.isBlank(minValue) ? null : TypeUtils.castToDouble(minValue);
                Number maxNumber = VerifyTools.isBlank(maxValue) ? null : TypeUtils.castToDouble(maxValue);
                if (minNumber == null) {
                    return actuval < maxNumber.doubleValue();
                } else if (maxNumber == null) {
                    return actuval >= minNumber.doubleValue();
                } else {
                    return actuval >= minNumber.doubleValue() && actuval < maxNumber.doubleValue();
                }
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 日期范围判断
     * 
     * @param actualValue 实际值
     * @param minValue 最小值
     * @param maxValue 最大值
     * @return 是否在范围内(>= minValue and < maxValue)
     */
    public static boolean between(Date actualValue, Object minValue, Object maxValue) {
        if (minValue == null && maxValue == null) {
            return true;
        } else if (actualValue == null) {
            return false;
        } else {
            try {
                long actuval = actualValue.getTime();
                Date minDate = VerifyTools.isBlank(minValue) ? null : TypeUtils.castToDate(minValue);
                Date maxDate = VerifyTools.isBlank(maxValue) ? null : TypeUtils.castToDate(maxValue);
                if (minDate == null) {
                    return actuval < maxDate.getTime();
                } else if (maxDate == null) {
                    return actuval >= minDate.getTime();
                } else {
                    return actuval >= minDate.getTime() && actuval < maxDate.getTime();
                }
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 列表内容判断
     * 
     * @param actualValues 实际值
     * @param expectValue 目标值
     * @return 目标值是否在列表中
     */
    public static boolean exists(Collection<?> actualValues, Object expectValue) {
        if (VerifyTools.isBlank(expectValue)) {
            return true;
        } else if (VerifyTools.isBlank(actualValues)) {
            return false;
        } else {
            for (Object i : actualValues) {
                if (expectValue.equals(i)) {
                    return true;
                }
            }
            return false;
        }
    }

}
