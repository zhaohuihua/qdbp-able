package com.gitee.qdbp.tools.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 反射工具类
 *
 * @author zhaohuihua
 * @version 170526
 */
public abstract class ReflectTools {

    /** 或运算符 **/
    private static Pattern FIELD_OR_SPLITOR = Pattern.compile("\\s*\\|\\|\\s*");
    /** 数组或点正则表达式 **/
    private static Pattern FIELD_PART_SPLITOR = Pattern.compile("[\\.\\[\\]]+");

    /**
     * 从对象中获取字段值, 支持多级字段名<br>
     * 如 target = { domain:{ text:"baidu", url:"http://baidu.com" } }<br>
     * -- findFieldValue(target, "domain.text"); 返回 "baidu"<br>
     * 如 target = { domain:{ text:"baidu", url:"http://baidu.com" } }<br>
     * -- findFieldValue(target, "domain.name || domain.text"); 返回 "baidu"<br>
     * 如 list = [{ domain:{ text:"baidu", url:"http://baidu.com" } },<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;{ domain:{ text:"bing", url:"http://cn.bing.com" } }]<br>
     * -- findFieldValue(list, "[1].domain.text"); 返回 "bing"<br>
     * 如 data = { data: [<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;{ domain:{ text:"baidu", url:"http://baidu.com" } },<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;{ domain:{ text:"bing", url:"http://cn.bing.com" } }<br>
     * ] }<br>
     * -- findFieldValue(list, "data[1].domain.text"); 返回 "bing"<br>
     * 
     * @param target 目标对象, 支持数组/List/Map/Bean
     * @param fieldNames 字段名, 支持带点的多级字段名和数组下标
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDepthValue(Object target, String fieldNames) {
        VerifyTools.requireNotBlank(fieldNames, "field");
        if (target == null) {
            return null;
        }
        String[] orFields = FIELD_OR_SPLITOR.split(fieldNames);
        Object value = null;
        for (String fields : orFields) {
            value = target;
            // 将表达式以.或[]拆分为数组
            String[] list = FIELD_PART_SPLITOR.split(fields);
            // 逐层取值
            for (int j = 0; value != null && j < list.length; j++) {
                String field = list[j];
                if (VerifyTools.isNotBlank(field) && !field.equals("this")) {
                    if (value.getClass().isArray()) {
                        value = getArrayFieldValue((Object[]) value, field);
                    } else if (value instanceof Collection) {
                        value = getListFieldValue((Collection<?>) value, field);
                    } else if (value instanceof Map) {
                        value = getMapFieldValue((Map<?, ?>) value, field);
                    } else {
                        value = getFieldValue(value, field, false);
                    }
                }
            }
            if (value != null) {
                break;
            }
        }
        return (T) value;
    }

    private static Object getMapFieldValue(Map<?, ?> map, String fieldName) {
        return map.get(fieldName);
    }

    private static Object getArrayFieldValue(Object[] array, String fieldName) {
        Integer index = ConvertTools.toInteger(fieldName, null);
        if (index == null) {
            return null;
        } else {
            return index >= array.length ? null : array[index];
        }
    }

    private static Object getListFieldValue(Collection<?> list, String fieldName) {
        Integer index = ConvertTools.toInteger(fieldName, null);
        if (index == null) {
            return null;
        }
        if (list instanceof List) {
            List<?> temp = (List<?>) list;
            return index >= temp.size() ? null : temp.get(index);
        } else {
            int i = 0;
            for (Object item : list) {
                if (i++ == index) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * 通过反射查找字段, 优先查找当前类, 并循继承树向上查找父类
     * 
     * @param clazz 目标类
     * @param fieldName 字段名
     * @return 字段对象, 如果未找到则返回null
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        return findField(clazz, fieldName, false);
    }

    /**
     * 通过反射查找字段, 优先查找当前类, 并循继承树向上查找父类
     * 
     * @param clazz 目标类
     * @param fieldName 字段名
     * @return 字段对象
     * @param throwOnNotFound 如果字段不存在是否抛出异常
     */
    public static Field findField(Class<?> clazz, String fieldName, boolean throwOnNotFound) {
        VerifyTools.requireNonNull(clazz, "clazz");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        Class<?> temp = clazz;
        while (temp != null && temp != Object.class) {
            Field[] fields = temp.getDeclaredFields();
            for (Field field : fields) {
                if (fieldName.equals(field.getName())) {
                    return field;
                }
            }
            temp = temp.getSuperclass();
        }

        if (throwOnNotFound) {
            throw new IllegalArgumentException(clazz.getSimpleName() + "." + fieldName + " not found.");
        } else {
            return null;
        }
    }

    /**
     * 通过反射设置字段值
     * 
     * @param target 目标对象
     * @param field 字段对象
     * @param value 字段值
     */
    public static void setFieldValue(Object target, Field field, Object value) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNonNull(field, "field");
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field '" + field.getName() + "': " + e.getMessage());
        }
    }

    /**
     * 如果原字段值为空则设置新的字段值
     * 
     * @param target 目标对象
     * @param field 字段对象
     * @param value 字段值
     */
    public static void setFieldValueIfAbsent(Object target, Field field, Object value) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNonNull(field, "field");

        if (VerifyTools.isBlank(value)) {
            return;
        }

        Object original = ReflectTools.getFieldValue(target, field);
        if (VerifyTools.isBlank(original)) {
            ReflectTools.setFieldValue(target, field, value);
        }
    }

    /**
     * 通过反射获取字段值
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param field 字段对象
     * @return 字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, Field field) {
        VerifyTools.requireNonNull(field, "field");
        if (target == null) {
            return null;
        }
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field '" + field.getName() + "': " + e.getMessage());
        }
    }

    /**
     * 通过反射设置字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名称, 如果字段不存在将抛出IllegalArgumentException异常
     * @param value 字段值
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        setFieldValue(target, fieldName, value, true);
    }

    /**
     * 通过反射设置字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @param throwOnFieldNotFound 如果字段不存在是否抛出异常
     */
    public static void setFieldValue(Object target, String fieldName, Object value, boolean throwOnFieldNotFound) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnFieldNotFound);
        if (field == null) {
            return;
        }

        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * 如果原字段值为空则设置新的字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名称, 如果字段不存在将抛出IllegalArgumentException异常
     * @param value 字段值
     */
    public static void setFieldValueIfAbsent(Object target, String fieldName, Object value) {
        setFieldValueIfAbsent(target, fieldName, value, true);
    }

    /**
     * 如果原字段值为空则设置新的字段值
     * 
     * @param target 目标对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @param throwOnFieldNotFound 如果字段不存在是否抛出异常
     */
    public static void setFieldValueIfAbsent(Object target, String fieldName, Object value,
            boolean throwOnFieldNotFound) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        if (VerifyTools.isBlank(value)) {
            return;
        }

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnFieldNotFound);
        if (field == null) {
            return;
        }

        Object original = ReflectTools.getFieldValue(target, field);
        if (VerifyTools.isBlank(original)) {
            ReflectTools.setFieldValue(target, field, value);
        }
    }

    /**
     * 通过反射获取字段值
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param fieldName 字段名称, 如果字段不存在将抛出IllegalArgumentException异常
     * @return 字段值
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        return getFieldValue(target, fieldName, true);
    }

    /**
     * 通过反射获取字段值
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param fieldName 字段名称
     * @return 字段值
     * @param throwOnFieldNotFound 如果字段不存在是否抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName, boolean throwOnFieldNotFound) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnFieldNotFound);
        if (field == null) {
            return null;
        }

        try {
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * 获取指定类的所有字段<br>
     * 将不会包含静态字段, 会包含父类字段, 如果父类和子类有相同名称的字段, 则只取子类的字段
     * 
     * @param clazz 目标类
     * @return 字段列表
     * @since 4.1.0
     */
    public static Field[] getAllFields(Class<?> clazz) {
        return getAllFields(clazz, false, true, true);
    }

    /**
     * 获取指定类的所有字段(排序为字段声明顺序, 先父类字段后子类字段)
     * 
     * @param clazz 目标类
     * @param includeStatic 是否包含静态字段
     * @param includeSuper 是否包含父类字段
     * @param distinct 是否根据字段名去重(去重时, 如果父类和子类有相同名称的字段, 则只取子类的字段)
     * @return 字段列表
     * @since 4.1.0
     */
    public static Field[] getAllFields(Class<?> clazz, boolean includeStatic, boolean includeSuper, boolean distinct) {
        VerifyTools.requireNonNull(clazz, "clazz");

        List<Field> allFields = new ArrayList<>();
        Map<String, ?> fieldNames = new HashMap<>();
        Class<?> temp = clazz;
        while (temp != null && temp != Object.class) {
            Field[] declaredFields = temp.getDeclaredFields();
            List<Field> fields = new ArrayList<>();
            for (Field field : declaredFields) {
                if (!includeStatic && Modifier.isStatic(field.getModifiers())) {
                    continue; // 不包含静态字段
                }
                if (distinct) { // 去重
                    if (fieldNames.containsKey(field.getName())) {
                        continue;
                    }
                    fieldNames.put(field.getName(), null);
                }
                fields.add(field);
            }
            if (!fields.isEmpty()) {
                // 父类字段放在前面
                allFields.addAll(0, fields);
            }
            if (!includeSuper) {
                break; // 不包含父类字段
            }
            temp = temp.getSuperclass();
        }
        return ConvertTools.toArray(allFields, Field.class);
    }

    /**
     * 获取所有setter方法
     * 
     * @param clazz 目标类
     * @return 方法列表
     * @since 5.0.0
     */
    public static Method[] getAllSetter(Class<?> clazz) {
        VerifyTools.requireNonNull(clazz, "clazz");

        List<Method> allSetters = new ArrayList<>();
        Class<?> temp = clazz;
        while (temp != null && temp != Object.class) {
            Method[] declaredMethods = temp.getDeclaredMethods();
            List<Method> methods = new ArrayList<>();
            for (Method method : declaredMethods) {
                if (Modifier.isStatic(method.getModifiers())) {
                    continue; // 去掉静态方法
                }
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue; // 去掉非公开的方法
                }
                if (method.getParameterTypes().length != 1) {
                    continue; // Setter方法只能有一个参数
                }
                // 判断方法名是否符合setter特征
                String name = method.getName();
                if (name.startsWith("set") && name.length() > 3) {
                    char c = name.charAt(3);
                    if (Character.isUpperCase(c) || c == '_' || c == '$') {
                        methods.add(method);
                    }
                }
            }
            if (!methods.isEmpty()) {
                // 父类字段放在前面
                allSetters.addAll(0, methods);
            }
            temp = temp.getSuperclass();
        }
        return ConvertTools.toArray(allSetters, Method.class);
    }

    /**
     * 获取所有getter方法
     * 
     * @param clazz 目标类
     * @return 方法列表
     * @since 5.0.0
     */
    public static Method[] getAllGetter(Class<?> clazz) {
        VerifyTools.requireNonNull(clazz, "clazz");

        List<Method> allSetters = new ArrayList<>();
        Class<?> temp = clazz;
        while (temp != null && temp != Object.class) {
            Method[] declaredMethods = temp.getDeclaredMethods();
            List<Method> methods = new ArrayList<>();
            for (Method method : declaredMethods) {
                if (Modifier.isStatic(method.getModifiers())) {
                    continue; // 去掉静态方法
                }
                if (!Modifier.isPublic(method.getModifiers())) {
                    continue; // 去掉非公开的方法
                }
                if (method.getParameterTypes().length > 0) {
                    continue; // Getter方法不能有参数
                }
                Class<?> returnType = method.getReturnType();
                if (returnType == void.class) {
                    continue; // Getter方法必须有返回值
                }
                // 判断方法名是否符合getter特征
                String name = method.getName();
                if (name.startsWith("is") && name.length() > 2) {
                    char c = name.charAt(2);
                    if (Character.isUpperCase(c) || c == '_' || c == '$') {
                        if (returnType == boolean.class || returnType == Boolean.class) {
                            methods.add(method);
                        }
                    }
                } else if (name.startsWith("get") && name.length() > 3) {
                    char c = name.charAt(3);
                    if (Character.isUpperCase(c) || c == '_' || c == '$') {
                        methods.add(method);
                    }
                }
            }
            if (!methods.isEmpty()) {
                // 父类字段放在前面
                allSetters.addAll(0, methods);
            }
            temp = temp.getSuperclass();
        }
        return ConvertTools.toArray(allSetters, Method.class);
    }

    /**
     * 获取方法签名的简要描述
     * 
     * @param clazz 类名
     * @param methodName 方法名
     * @param types 方法参数
     * @return 方法签名描述
     */
    public static String getMethodLogSignature(Class<?> clazz, String methodName, Class<?>... types) {
        VerifyTools.requireNonNull(clazz, "clazz");
        VerifyTools.requireNotBlank(methodName, "methodName");

        StringBuilder buffer = new StringBuilder();
        buffer.append(clazz.getSimpleName());
        buffer.append(".");
        buffer.append(methodName);
        buffer.append("(");
        int size = types == null ? 0 : types.length;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            if (types[i] == null) {
                buffer.append("null");
            } else {
                buffer.append(types[i].getSimpleName());
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * 优先选择参数类型完全相同的, 其次选择通过装箱/拆箱能匹配的或父子关系能匹配的<br>
     * 总之:<br>
     * 如果有一个方法xxxMethod(List&lt;?&gt; list, Integer i)<br>
     * 通过eachFindMethod(clazz, "xxxMethod", List.class, Integer.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", List.class, int.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", ArrayList.class, Integer.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", ArrayList.class, int.class);也能找到<br>
     * 
     * @param clazz 类
     * @param methodName 方法名
     * @param types 参数列表
     * @return 方法对象, 如果未找到则返回null
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... types) {
        return findMethod(clazz, methodName, false, types);
    }

    /**
     * 优先选择参数类型完全相同的, 其次选择通过装箱/拆箱能匹配的或父子关系能匹配的<br>
     * 总之:<br>
     * 如果有一个方法xxxMethod(List&lt;?&gt; list, Integer i)<br>
     * 通过eachFindMethod(clazz, "xxxMethod", List.class, Integer.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", List.class, int.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", ArrayList.class, Integer.class);能找到<br>
     * 通过eachFindMethod(clazz, "xxxMethod", ArrayList.class, int.class);也能找到<br>
     * 
     * @param clazz 类
     * @param methodName 方法名
     * @param throwOnNotFound 如果方法不存在是否抛出异常
     * @param types 参数列表, 如果其中某个参数为null, 则不检查类型
     * @return 方法对象
     */
    public static Method findMethod(Class<?> clazz, String methodName, boolean throwOnNotFound, Class<?>... types) {
        VerifyTools.requireNonNull(clazz, "clazz");
        VerifyTools.requireNotBlank(methodName, "methodName");

        try {
            return clazz.getMethod(methodName, types);
        } catch (NoSuchMethodException e) {
            return eachFindMethod(clazz, methodName, throwOnNotFound, types);
        }
    }

    private static Method eachFindMethod(Class<?> clazz, String name, boolean throwOnNotFound, Class<?>... types) {

        // 直接采用clazz.getMethod(name, types)的方式
        // 会出现根据int找不到clazz.method(Integer), 根据Integer找不到clazz.method(Object)的情况

        List<Method> accepted = new ArrayList<>(); // 可接受的(通过装箱/拆箱能匹配的或父子关系能匹配的)

        int length = types.length;
        Method[] methods = clazz.getMethods();

        for (Method m : methods) {
            if (!name.equals(m.getName())) {
                continue; // 方法名不符
            }
            Class<?>[] actuals = m.getParameterTypes();
            if (actuals.length != types.length) {
                continue; // 参数个数不符
            }
            boolean equals = true;
            boolean accept = true;
            for (int i = 0; i < length; i++) {
                Class<?> a = actuals[i];
                Class<?> t = types[i];
                if (t == null) {
                    equals = false;
                    continue;
                }
                if (a != t) {
                    equals = false;
                }
                if (a != t && !a.isAssignableFrom(t) && !isCompatible(a, t)) {
                    accept = false;
                }
            }
            if (equals) {
                return m; // 参数类型完全相同, 直接返回
            }
            if (accept) {
                accepted.add(m);
            }
        }

        if (accepted.isEmpty()) {
            if (throwOnNotFound) {
                String signature = getMethodLogSignature(clazz, name, types);
                throw new IllegalArgumentException(signature + " not found.");
            } else {
                return null;
            }
        } else { // 没有参数类型完全相同, 但有通过装箱/拆箱能匹配的或父子关系能匹配的
            if (accepted.size() == 1) { // 如果只有一个这种方法, 就选择这个啦
                return accepted.get(0);
            } else { // 如果不只一个, 那没办法, 报个错吧
                String signature = getMethodLogSignature(clazz, name, types);
                throw new IllegalArgumentException(signature + " is ambiguous.");
            }
        }
    }

    /**
     * 执行无参数的方法
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param method 方法对象
     * @return 方法返回结果
     */
    public static <T> T invokeMethod(Object target, Method method) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNonNull(method, "method");

        return invokeMethod(target, method, new Object[0]);
    }

    /**
     * 执行对象方法
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param method 方法对象
     * @return 方法返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, Method method, Object... args) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNonNull(method, "method");

        try {
            return (T) method.invoke(target, args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw renewMethodRuntimeException(e);
        }
    }

    /**
     * 执行无参数的方法
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param methodName 方法名, 如果方法不存在将抛出IllegalArgumentException异常
     * @return 方法返回结果
     */
    public static <T> T invokeMethod(Object target, String methodName) {
        return invokeMethod(target, methodName, true);
    }

    /**
     * 执行无参数的方法
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param methodName 方法名
     * @param throwOnMethodNotFound 如果方法不存在是否抛出异常
     * @return 方法返回结果
     */
    public static <T> T invokeMethod(Object target, String methodName, boolean throwOnMethodNotFound) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNotBlank(methodName, "methodName");

        Class<?> clazz = target.getClass();
        Class<?>[] types = new Class<?>[0];
        Method method = findMethod(clazz, methodName, throwOnMethodNotFound, types);
        if (method == null) {
            return null;
        }
        return invokeMethod(target, method, new Object[0]);
    }

    /**
     * 执行对象方法<br>
     * 如果根据方法名和参数类型找到多个方法将抛出IllegalArgumentException异常<br>
     * 在args中如果包含null对象将不会判断参数类型, 如果有多个同名且参数个数相同的方法将有可能因无法确定目标方法而报错
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param methodName 方法名, 如果方法不存在将抛出IllegalArgumentException异常
     * @param args 参数
     * @return 方法返回结果
     */
    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        return invokeMethod(target, methodName, true, args);
    }

    /**
     * 执行对象方法<br>
     * 如果根据方法名和参数类型找到多个方法将抛出IllegalArgumentException异常<br>
     * 在args中如果包含null对象将不会判断参数类型, 如果有多个同名且参数个数相同的方法将有可能因无法确定目标方法而报错
     * 
     * @param <T> 返回结果类型, 如果类型不符将抛出ClassCastException异常
     * @param target 目标对象
     * @param methodName 方法名, 如果方法不存在将抛出IllegalArgumentException异常
     * @param throwOnMethodNotFound 如果方法不存在是否抛出异常
     * @param args 参数
     * @return 方法返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, String methodName, boolean throwOnMethodNotFound, Object... args) {
        VerifyTools.requireNonNull(target, "target");
        VerifyTools.requireNotBlank(methodName, "methodName");

        int size = args == null ? 0 : args.length;
        Class<?>[] types = new Class<?>[size];
        for (int i = 0; i < size; i++) {
            types[i] = args[i] == null ? null : args[i].getClass();
        }
        Class<?> clazz = target.getClass();
        Method method = findMethod(clazz, methodName, throwOnMethodNotFound, types);
        if (method == null) {
            return null;
        }

        try {
            return (T) method.invoke(target, args);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw renewMethodRuntimeException(e);
        }
    }

    /** 判断参数是否能够匹配 **/
    private static boolean isCompatible(Class<?> a, Class<?> b) {
        if (a == b) {
            return true;
        } else if (a == boolean.class && b == Boolean.class || b == boolean.class && a == Boolean.class) {
            return true;
        } else if (a == char.class && b == Character.class || b == char.class && a == Character.class) {
            return true;
        } else if (a == byte.class && b == Byte.class || b == byte.class && a == Byte.class) {
            return true;
        } else if (a == short.class && b == Short.class || b == short.class && a == Short.class) {
            return true;
        } else if (a == int.class && b == Integer.class || b == int.class && a == Integer.class) {
            return true;
        } else if (a == long.class && b == Long.class || b == long.class && a == Long.class) {
            return true;
        } else if (a == float.class && b == Float.class || b == float.class && a == Float.class) {
            return true;
        } else if (a == double.class && b == Double.class || b == double.class && a == Double.class) {
            return true;
        }
        return false;
    }

    private static RuntimeException renewMethodRuntimeException(Exception e) {
        if (e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Method not found: " + e.getMessage());
        }
        if (e instanceof IllegalAccessException) {
            return new IllegalArgumentException("Could not access method: " + e.getMessage());
        }
        Throwable throwable = e;
        if (e instanceof InvocationTargetException) {
            throwable = ((InvocationTargetException) e).getTargetException();
        }
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        return new UndeclaredThrowableException(throwable);
    }

    /**
     * 判断是不是原始类型及其包装类型
     * 
     * @param clazz 目标类型
     * @return 判断结果
     * @since 4.1.0
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return isPrimitive(clazz, true);
    }

    /**
     * 判断是不是原始类型及其包装类型<br>
     * 如果strict=false, 则字符串/枚举/日期及一切Number的子类都算原始类型
     * 
     * @param clazz 目标类型
     * @param strict 是否严格模式
     * @return 判断结果
     * @since 4.1.0
     */
    public static boolean isPrimitive(Class<?> clazz, boolean strict) {
        // @formatter:off
        return clazz.isPrimitive()
            || clazz == Boolean.class
            || clazz == Integer.class
            || clazz == Long.class
            || clazz == Double.class
            || clazz == Float.class
            || clazz == Character.class
            || clazz == Byte.class
            || ( !strict && (
                clazz == String.class
                || clazz.isEnum()
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
            ) );
        // @formatter:on
    }
}
