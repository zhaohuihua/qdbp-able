package com.gitee.qdbp.tools.excel.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.DateTools;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * 规则注册工厂
 *
 * @author zhaohuihua
 * @version 190317
 */
public class RuleFactory {

    public static final RuleFactory global = new RuleFactory();

    // @FunctionalInterface
    public static interface RuleBuilder {

        CellRule build(Object options);
    }

    public RuleFactory() {
        this.init();
    }

    private Map<String, RuleBuilder> builders = new HashMap<>();

    public void register(String type, RuleBuilder builder) {
        this.builders.put(type, builder);
    }

    public CellRule build(String type, Object options) {
        RuleBuilder builder = builders.get(type);
        if (builder == null) {
            throw new IllegalArgumentException("CellRuleError, unsupported type[" + type + "]");
        }
        return builder.build(options);
    }

    private static String illegalArgumentType(String rule, Object value) {
        String type = value == null ? "null" : value.toString() + '(' + value.getClass().getSimpleName() + ')';
        return "Rule '" + rule + "' can't supported argument type for " + type;
    }

    private void init() {
        // this.register("xxx", options -> { });
        this.register("ignoreIllegalValue", new RuleBuilder() {

            public CellRule build(Object options) {
                if (Boolean.TRUE.equals(options)) {
                    return new IgnoreIllegalValue();
                } else if (Boolean.TRUE.equals(options)) {
                    return null;
                }
                try {
                    Boolean value = TypeUtils.castToBoolean(options);
                    return value ? new IgnoreIllegalValue() : null;
                } catch (Exception e) {
                    throw new IllegalArgumentException(illegalArgumentType("ignoreIllegalValue", options));
                }
            }
        });
        this.register("clear", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options instanceof String) {
                    return new ClearRule((String) options);
                } else if (options instanceof Pattern) {
                    return new ClearRule((Pattern) options);
                } else {
                    throw new IllegalArgumentException(illegalArgumentType("clear", options));
                }
            }
        });
        this.register("date", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options == null) {
                    return new DateRule(DateTools.PATTERN_GENERAL_DATETIME);
                } else if (options instanceof String) {
                    return new DateRule((String) options);
                } else {
                    throw new IllegalArgumentException(illegalArgumentType("date", options));
                }
            }
        });
        this.register("number", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options == null) {
                    return new NumberRule();
                } else if (options instanceof String) {
                    return new NumberRule((String) options);
                } else {
                    throw new IllegalArgumentException(illegalArgumentType("number", options));
                }
            }
        });
        this.register("rate", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options == null) {
                    return new RateRule();
                } else if (options instanceof Number) {
                    return new RateRule(((Number) options).doubleValue());
                } else if (options instanceof String) {
                    return new RateRule(ConvertTools.toDouble((String) options));
                } else {
                    throw new IllegalArgumentException(illegalArgumentType("rate", options));
                }
            }
        });
        this.register("split", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options instanceof String) {
                    return new SplitRule(((String) options).toCharArray());
                } else {
                    throw new IllegalArgumentException(illegalArgumentType("split", options));
                }
            }
        });
        this.register("map", new RuleBuilder() {

            public CellRule build(Object options) {
                if (options == null) {
                    throw new IllegalArgumentException(illegalArgumentType("map", options));
                }
                if (options instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) options;
                    Map<String, Object> json = new HashMap<>();
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getKey() != null) {
                            json.put(entry.getKey().toString(), entry.getValue());
                        }
                    }
                    return new MapRule(json);
                }
                if (!(options instanceof String)) {
                    throw new IllegalArgumentException(illegalArgumentType("map", options));
                }
                String string = (String) options;
                if (string.startsWith("{")) {
                    JSONObject json = (JSONObject) JSON.parse(string);
                    return new MapRule(json);
                } else {
                    // 0:未知, 1:男, 2:女
                    // true:是|Y|1, false:否|N|0
                    // DEBX:等额本息,DEBJ:等额本金,DQHB:到期还本,FQHB:分期还本,GSZF:过手支付,GDTH:固定摊还
                    Map<String, Object> map = new HashMap<>();
                    String[] array = StringTools.split(string, ',');
                    for (int i = 0; i < array.length; i++) {
                        String item = array[i];
                        int colonIndex = item.indexOf(':');
                        if (colonIndex < 0) {
                            map.put(String.valueOf(i + 1), item);
                        } else if (colonIndex == 0) {
                            map.put(String.valueOf(i + 1), item.substring(1));
                        } else {
                            map.put(item.substring(0, colonIndex), item.substring(colonIndex + 1));
                        }
                    }
                    return new MapRule(map);
                }
            }
        });
    }

}
