package com.gitee.qdbp.able.matches;

/**
 * 字符串替换接口
 *
 * @author zhaohuihua
 * @version 20200217
 */
public interface StringReplacer {

    /**
     * 替换字符串
     * 
     * @param string 待替换的源字符串
     * @return 替换后的字符串
     */
    String replace(String string);
}
