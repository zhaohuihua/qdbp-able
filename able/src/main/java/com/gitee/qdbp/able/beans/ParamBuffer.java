package com.gitee.qdbp.able.beans;

import java.util.regex.Pattern;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * 收集参数
 *
 * @author zhaohuihua
 * @version 180814
 */
public class ParamBuffer implements Cloneable {

    private static final Pattern TAB = Pattern.compile("\\t");
    private static final Pattern RETURN = Pattern.compile("\\r");
    private static final Pattern NEWLINE = Pattern.compile("\\n");

    private String separator;
    private int valueStringMaxLength;
    private StringBuffer buffer;

    public ParamBuffer() {
        this("&", 0);
    }

    public ParamBuffer(String separator, int valueStringMaxLength) {
        this.buffer = new StringBuffer();
        this.valueStringMaxLength = valueStringMaxLength;
    }

    public ParamBuffer newline(boolean indent) {
        if (buffer.length() > 0) {
            buffer.append('\n');
        }
        if (indent) {
            buffer.append("----");
        }
        return this;
    }

    public <T> ParamBuffer append(T value) {
        if (buffer.length() > 0) {
            this.buffer.append(separator);
        }
        this.buffer.append(convertToString(value));
        return this;
    }

    public <T> ParamBuffer append(String key, T value) {
        if (buffer.length() > 0) {
            this.buffer.append(separator);
        }
        this.buffer.append(key).append('=').append(convertToString(value));
        return this;
    }

    private String convertToString(Object value) {
        if (value == null) {
            return "";
        }
        String string = value.toString();
        // 替换掉\t\r\n
        if (string.indexOf('\t') >= 0) {
            string = TAB.matcher(string).replaceAll("\\t");
        }
        if (string.indexOf('\r') >= 0) {
            string = RETURN.matcher(string).replaceAll("\\r");
        }
        if (string.indexOf('\n') >= 0) {
            string = NEWLINE.matcher(string).replaceAll("\\n");
        }
        // 裁切过长的字符串
        return StringTools.ellipsis(string, valueStringMaxLength);
    }

    @Override
    public ParamBuffer clone() {
        ParamBuffer copy = new ParamBuffer();
        copy.separator = this.separator;
        copy.valueStringMaxLength = this.valueStringMaxLength;
        if (buffer.length() > 0) {
            copy.buffer = new StringBuffer(this.buffer.toString());
        } else {
            copy.buffer = new StringBuffer();
        }
        return copy;
    }

    @Override
    public String toString() {
        return this.buffer.toString();
    }

    public static ParamBuffer newLogBuffer() {
        return new ParamBuffer(", ", 0);
    }

    public static ParamBuffer newDefBuffer() {
        return new ParamBuffer("&", 0);
    }

    public static ParamBuffer newTabBuffer() {
        return new ParamBuffer("\t", 0);
    }

    public static ParamBuffer newLogBuffer(int valueStringMaxLength) {
        return new ParamBuffer(", ", valueStringMaxLength);
    }

    public static ParamBuffer newDefBuffer(int valueStringMaxLength) {
        return new ParamBuffer("&", valueStringMaxLength);
    }

    public static ParamBuffer newTabBuffer(int valueStringMaxLength) {
        return new ParamBuffer("\t", valueStringMaxLength);
    }
}
