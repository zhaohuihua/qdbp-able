package com.gitee.zhaohuihua.tools.utils;

import java.lang.reflect.Method;
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
     * 获取方法签名的简要描述
     * 
     * @param clazz 类名
     * @param method 方法名
     * @param types 方法参数
     * @return
     */
    public static String getMethodLogSignature(Class<?> clazz, String method, Class<?>... types) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(clazz.getSimpleName());
        buffer.append(".");
        buffer.append(method);
        buffer.append("(");
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(types[i].getSimpleName());
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
     * @param name 方法名
     * @param types 参数列表
     * @return 方法对象
     * @throws NoSuchMethodException
     */
    public static Method findMethod(Class<?> clazz, String name, Class<?>... types) throws NoSuchMethodException {

        try {
            return clazz.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            return eachFindMethod(clazz, name, types);
        }
    }

    private static Method eachFindMethod(Class<?> clazz, String name, Class<?>... types) throws NoSuchMethodException {

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

        if (!accepted.isEmpty()) { // 没有参数类型完全相同, 但有通过装箱/拆箱能匹配的或父子关系能匹配的
            if (accepted.size() == 1) { // 如果只有一个这种方法, 就选择这个啦
                return accepted.get(0);
            } else { // 如果不只一个, 那没办法, 报个错吧
                String signature = getMethodLogSignature(clazz, name, types);
                throw new NoSuchMethodException("The method " + signature + " is ambiguous.");
            }
        } else {
            String signature = getMethodLogSignature(clazz, name, types);
            throw new NoSuchMethodException("The method " + signature + " not found.");
        }
    }

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
}
