package com.gitee.qdbp.tools.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Json工具类
 *
 * @author zhaohuihua
 * @version 180621
 */
public abstract class JsonTools {

    /**
     * 将对象转换为日志文本<br>
     * 如 toLogs(params, operator) 返回 \n\t{paramsJson} \n\t{operatorJson}<br>
     * 
     * @param objects
     * @return
     */
    public static String toLogString(Object... objects) {
        StringBuilder buffer = new StringBuilder();
        for (Object object : objects) {
            buffer.append("\n\t");
            if (object == null) {
                buffer.append("null");
            } else if (object instanceof String) {
                buffer.append(object);
            } else {
                buffer.append(object.getClass().getSimpleName()).append(": ");
                buffer.append(toJsonString(object));
            }
        }
        return buffer.toString();
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "null";
        }
        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(SerializerFeature.QuoteFieldNames, false);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.write(object);
            return out.toString();
        }
    }
}
