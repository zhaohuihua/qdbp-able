package com.gitee.qdbp.db.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlFormatter {

    private static Pattern KEYWORDS =
            Pattern.compile("\\b(FROM|INNER JOIN|LEFT JOIN|RIGHT JOIN|VALUES|WHERE|ORDER BY|GROUP BY|HAVING|LIMIT)\\b",
                Pattern.CASE_INSENSITIVE);

    public static String format(String sql) {
        StringBuilder temp = new StringBuilder();
        Matcher matcher = KEYWORDS.matcher(sql.toString());
        int index = 0;
        while (matcher.find()) {
            temp.append(sql.substring(index, matcher.start()));
            String key = matcher.group(1);
            if (!bufferIsEmpty(temp)) {
                if ("FROM".equalsIgnoreCase(key)) {
                    if (getLastLineLength(temp) > 20) {
                        appendNewline(temp, 1);
                    }
                } else {
                    appendNewline(temp, 1);
                }
            }
            temp.append(key);
            index = matcher.end();
        }
        if (index < sql.length()) {
            temp.append(sql.substring(index));
        }
        return temp.toString();
    }

    private static void appendNewline(StringBuilder buffer, int indent) {
        if (indent <= 0) {
            buffer.append('\n');
        } else if (indent == 1) {
            buffer.append('\n').append('\t');
        } else {
            buffer.append('\n');
            for (int i = 0; i < indent; i++) {
                buffer.append('\t');
            }
        }
    }

    private static boolean bufferIsEmpty(StringBuilder buffer) {
        if (buffer.length() == 0) {
            return true;
        }
        for (int i = 0, len = buffer.length(); i < len; i++) {
            char c = buffer.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    private static int getLastLineLength(StringBuilder buffer) {
        int lastNewlineIndex = buffer.lastIndexOf("\n");
        if (lastNewlineIndex < 0) {
            return buffer.length();
        } else {
            return buffer.length() - lastNewlineIndex;
        }
    }
}
