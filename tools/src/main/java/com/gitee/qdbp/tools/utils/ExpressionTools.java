package com.gitee.qdbp.tools.utils;

import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * 表达式工具类
 *
 * @author zhaohuihua
 * @version 180611
 */
public abstract class ExpressionTools {

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static int parseIntegerExpression(String expression) throws NumberFormatException {
        if (VerifyTools.isBlank(expression)) {
            throw new NumberFormatException("null");
        }
        Integer number = ExpressionTools.parseIntegerExpression(expression, null);
        if (number == null) {
            throw new NumberFormatException(expression);
        } else {
            return number;
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Integer parseIntegerExpression(String expression, Integer defaults) {
        if (VerifyTools.isBlank(expression)) {
            return defaults;
        }

        expression = expression.trim();

        try {
            return Integer.parseInt(expression);
        } catch (NumberFormatException nfe) {
            Number value = calculateNumberOgnlExpression(expression, defaults);
            return value == null ? null : (int) Math.round(value.doubleValue());
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static long parseLongExpression(String expression) throws NumberFormatException {
        if (VerifyTools.isBlank(expression)) {
            throw new NumberFormatException("null");
        }
        Long number = ExpressionTools.parseLongExpression(expression, null);
        if (number == null) {
            throw new NumberFormatException(expression);
        } else {
            return number;
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Long parseLongExpression(String expression, Long defaults) {
        if (VerifyTools.isBlank(expression)) {
            return defaults;
        }

        expression = expression.trim();

        try {
            return Long.parseLong(expression);
        } catch (NumberFormatException nfe) {
            Number value = calculateNumberOgnlExpression(expression, defaults);
            return value == null ? null : Math.round(value.doubleValue());
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static float parseFloatExpression(String expression) throws NumberFormatException {
        if (VerifyTools.isBlank(expression)) {
            throw new NumberFormatException("null");
        }
        Float number = ExpressionTools.parseFloatExpression(expression, null);
        if (number == null) {
            throw new NumberFormatException(expression);
        } else {
            return number;
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Float parseFloatExpression(String expression, Float defaults) {
        if (VerifyTools.isBlank(expression)) {
            return defaults;
        }

        expression = expression.trim();

        try {
            return Float.parseFloat(expression);
        } catch (NumberFormatException nfe) {
            Number value = calculateNumberOgnlExpression(expression, defaults);
            return value == null ? null : value.floatValue();
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static double parseDoubleExpression(String expression) throws NumberFormatException {
        if (VerifyTools.isBlank(expression)) {
            throw new NumberFormatException("null");
        }
        Double number = ExpressionTools.parseDoubleExpression(expression, null);
        if (number == null) {
            throw new NumberFormatException(expression);
        } else {
            return number;
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Double parseDoubleExpression(String expression, Double defaults) {
        if (VerifyTools.isBlank(expression)) {
            return defaults;
        }

        expression = expression.trim();

        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException nfe) {
            Number value = calculateNumberOgnlExpression(expression, defaults);
            return value == null ? null : value.doubleValue();
        }
    }

    /**
     * 解析Boolean表达式
     * 
     * @param expression 表达式, 支持运算符
     * @return 解析结果
     * @throws IllegalArgumentException 表达式格式错误
     */
    public static boolean parseBooleanExpression(String expression) throws IllegalArgumentException {
        if (VerifyTools.isBlank(expression)) {
            throw new IllegalArgumentException("null");
        }
        Boolean number = ExpressionTools.parseBooleanExpression(expression, null);
        if (number == null) {
            throw new IllegalArgumentException(expression);
        } else {
            return number;
        }
    }

    /**
     * 解析Boolean表达式
     * 
     * @param expression 表达式, 支持运算符
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Boolean parseBooleanExpression(String expression, Boolean defaults) {
        if (VerifyTools.isBlank(expression)) {
            return defaults;
        }

        expression = expression.trim();

        if (StringTools.isPositive(expression, false)) {
            return true;
        } else if (StringTools.isNegative(expression, false)) {
            return false;
        } else {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                if (defaults != null) {
                    return defaults;
                } else {
                    throw new IllegalArgumentException(expression + ", " + e.getMessage());
                }
            }
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else if (result instanceof Number) {
                return ((Number) result).doubleValue() != 0;
            } else {
                if (defaults != null) {
                    return defaults;
                } else {
                    throw new IllegalArgumentException(expression);
                }
            }
        }
    }

    /**
     * 解析表达式
     * 
     * @param expression 表达式, 支持运算符
     * @return 解析结果
     */
    public static Object parseExpression(String expression) {
        try {
            return Ognl.getValue(expression, null);
        } catch (OgnlException e) {
            throw new IllegalArgumentException(expression + ", " + e.getMessage());
        }
    }

    private static Number calculateNumberOgnlExpression(String expression, Number defaults) {
        Object result;
        try {
            result = Ognl.getValue(expression, null);
        } catch (OgnlException e) {
            if (defaults != null) {
                return defaults;
            } else {
                throw new NumberFormatException(expression + ", " + e.getMessage());
            }
        }
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        } else {
            if (defaults != null) {
                return defaults;
            } else {
                throw new NumberFormatException(expression);
            }
        }
    }
}
