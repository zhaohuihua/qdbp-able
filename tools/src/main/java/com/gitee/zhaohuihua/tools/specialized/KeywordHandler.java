package com.gitee.zhaohuihua.tools.specialized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.gitee.zhaohuihua.core.utils.VerifyTools;
import com.gitee.zhaohuihua.tools.utils.ConvertTools;

/**
 * 关键字收集
 *
 * @author zhaohuihua
 * @version 180626
 */
public class KeywordHandler {

    private Set<String> container = new TreeSet<>();

    protected KeywordHandler() {
    }

    public static KeywordHandler newInstance() {
        return new KeywordHandler();
    }

    /** 增加普通关键字(不替换特殊字符) **/
    public void addPlain(String... keywords) {
        for (String string : keywords) {
            container.add(string);
        }
    }

    /** 增加文本关键字(替换特殊字符) **/
    public void addText(String... keywords) {
        for (String string : keywords) {
            container.addAll(doFormat(string));
        }
    }

    private static List<String> doFormat(String string) {
        if (VerifyTools.isBlank(string)) {
            return null;
        }
        char[] chars = string.toCharArray();
        List<String> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean space = false;
        for (int i = 0, s = chars.length; i < s; i++) {
            char c = chars[i];
            if (c >= 0 && c < 127) { // 符号替换为空格
                if (c == '.' || c == '@' || c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                    buffer.append(c);
                    space = false;
                } else {
                    if (!space) {
                        if (i > 0 && i < s - 1 && buffer.length() > 0) {
                            list.add(buffer.toString());
                            buffer.setLength(0);
                        }
                        space = true;
                    }
                }
            } else {
                if (replace.containsKey(c)) {
                    buffer.append(replace.get(c));
                    space = false;
                } else if (clear.containsKey(c)) {
                    if (!space) {
                        if (i > 0 && i < s - 1 && buffer.length() > 0) {
                            list.add(buffer.toString());
                            buffer.setLength(0);
                        }
                        space = true;
                    }
                } else {
                    buffer.append(c);
                    space = false;
                }
            }
        }
        if (buffer.length() > 0) {
            list.add(buffer.toString());
            buffer.setLength(0);
        }
        return list;
    }

    public static String format(String string) {
        List<String> list = doFormat(string);
        return VerifyTools.isBlank(list) ? null : ConvertTools.joinToString(list, " ");
    }

    public String toString() {
        return VerifyTools.isBlank(container) ? null : ConvertTools.joinToString(container, " ");
    }

    private static Map<Character, String> replace = new HashMap<>();
    private static Map<Character, Boolean> clear = new HashMap<>();
    static {
        replace.put('０', "0");
        replace.put('１', "1");
        replace.put('２', "2");
        replace.put('３', "3");
        replace.put('４', "4");
        replace.put('５', "5");
        replace.put('６', "6");
        replace.put('７', "7");
        replace.put('８', "8");
        replace.put('９', "9");
        replace.put('①', "1");
        replace.put('②', "2");
        replace.put('③', "3");
        replace.put('④', "4");
        replace.put('⑤', "5");
        replace.put('⑥', "6");
        replace.put('⑦', "7");
        replace.put('⑧', "8");
        replace.put('⑨', "9");
        replace.put('⑩', "10");
        replace.put('．', ".");
        replace.put('＠', "@");
        clear.put('　', true);
        clear.put('、', true);
        clear.put('，', true);
        clear.put('。', true);
        clear.put('？', true);
        clear.put('＋', true);
        clear.put('－', true);
        clear.put('〈', true);
        clear.put('〉', true);
        clear.put('《', true);
        clear.put('》', true);
        clear.put('（', true);
        clear.put('）', true);
        clear.put('｛', true);
        clear.put('｝', true);
        clear.put('［', true);
        clear.put('］', true);
        clear.put('～', true);
        clear.put('！', true);
        clear.put('＃', true);
        clear.put('＄', true);
        clear.put('％', true);
        clear.put('＾', true);
        clear.put('∧', true);
        clear.put('＆', true);
        clear.put('＊', true);
        clear.put('＝', true);
        clear.put('≈', true);
        clear.put('＿', true);
        clear.put('｜', true);
        clear.put('＼', true);
        clear.put('／', true);
        clear.put('＇', true);
        clear.put('＂', true);
        clear.put('：', true);
        clear.put('；', true);
        clear.put('￥', true);
        clear.put('…', true);
        clear.put('—', true);
    }
}
