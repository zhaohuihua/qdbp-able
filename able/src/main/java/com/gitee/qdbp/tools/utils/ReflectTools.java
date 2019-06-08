package com.gitee.qdbp.tools.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射工具类
 *
 * @author zhaohuihua
 * @version 170526
 */
public abstract class ReflectTools {

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
        VerifyTools.requireNotBlank(clazz, "clazz");
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
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(field, "field");
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
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(field, "field");

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
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(field, "field");
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
     * @param throwOnNotFound 如果字段不存在是否抛出异常
     */
    public static void setFieldValue(Object target, String fieldName, Object value, boolean throwOnNotFound) {
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnNotFound);
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
     * @param throwOnNotFound 如果字段不存在是否抛出异常
     */
    public static void setFieldValueIfAbsent(Object target, String fieldName, Object value, boolean throwOnNotFound) {
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        if (VerifyTools.isBlank(value)) {
            return;
        }

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnNotFound);
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
     * @param throwOnNotFound 如果字段不存在是否抛出异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName, boolean throwOnNotFound) {
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(fieldName, "fieldName");

        Class<?> clazz = target.getClass();
        Field field = findField(clazz, fieldName, throwOnNotFound);
        if (field == null) {
            return null;
        }

        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access field '" + fieldName + "': " + e.getMessage());
        }
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
        VerifyTools.requireNotBlank(clazz, "clazz");
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
        VerifyTools.requireNotBlank(clazz, "clazz");
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
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(method, "method");

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
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(method, "method");

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
     * @param throwOnNotFound 如果方法不存在是否抛出异常
     * @return 方法返回结果
     */
    public static <T> T invokeMethod(Object target, String methodName, boolean throwOnNotFound) {
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(methodName, "methodName");

        Class<?> clazz = target.getClass();
        Class<?>[] types = new Class<?>[0];
        Method method = findMethod(clazz, methodName, throwOnNotFound, types);
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
     * @param throwOnNotFound 如果方法不存在是否抛出异常
     * @param args 参数
     * @return 方法返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, String methodName, boolean throwOnNotFound, Object... args) {
        VerifyTools.requireNotBlank(target, "target");
        VerifyTools.requireNotBlank(methodName, "methodName");

        int size = args == null ? 0 : args.length;
        Class<?>[] types = new Class<?>[size];
        for (int i = 0; i < size; i++) {
            types[i] = args[i] == null ? null : args[i].getClass();
        }
        Class<?> clazz = target.getClass();
        Method method = findMethod(clazz, methodName, throwOnNotFound, types);
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
}
