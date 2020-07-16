package com.gitee.qdbp.tools.utils;

/**
 * 缩进工具类<br>
 * \r=CARRIAGE RETURN,回到行首; \n=LINE FEED,换行; \t=HORIZONTAL TABULATION,水平制表位<br>
 * \f=FORM FEED,换页, 这个不作处理, 相当于一个不可见字符
 *
 * @author zhaohuihua
 * @version 20200716
 */
public class IndentTools {

    /** 获取缩进字符 **/
    public static char[] getIndenTabs(int size) {
        if (size == 1) {
            return new char[] { '\t' };
        } else {
            char[] tabs = new char[size];
            for (int i = 0; i < size; i++) {
                tabs[i] = '\t';
            }
            return tabs;
        }
    }

    /**
     * 在最后一个换行符之后插入信息<pre>
     * 例如: \n=换行符, \t=TAB符, \s=空格
     * \n\tABC\n\t\t --> \n\tABC\n[\tsMESSAGE\n]\t\tDEF // 换行模式(插入的信息与上一行缩进对齐)
     * \n\tABC\s     --> \n\tABC\s[\sMESSAGE\s]DEF      // 非换行模式
     * \n\tABC       --> \n\tABC[\ssMESSAGE\s]DEF       // 非换行模式</pre>
     * 
     * @param string 原字符串
     * @param message 待插入的信息
     */
    public static void insertMessageAfterLastNewline(StringBuilder string, StringBuilder message) {
        int last = string.length() - 1;
        int position = -1;
        // 查找插入点: 最后一个换行符之后的那个位置
        // 如果还没找到换行符就遇到了非空字符, 就是非换行模式
        for (int i = last; i >= 0; i--) {
            char c = string.charAt(i);
            if (c == ' ' || c == '\t') {
                continue;
            } else if (c == '\r' || c == '\n') {
                position = i;
            } else {
                break; // 还没找到换行符就遇到了非空字符
            }
        }
        if (position < 0) { // 非换行模式
            string.append(' ').append(message).append(' ');
        } else { // 换行模式
            int indent = findLastIndentSize(string, position);
            message.insert(0, getIndenTabs(indent));
            message.append('\n');
            string.insert(position, message);
        }
    }

    /**
     * 查找缩进量(从字符串最后向前查找最近一行的缩进量)<pre>
     * 例如: \r\n=换行符, \t=TAB符, \s=空格
     * \n\tABC\n\t\tDEF\t\t\t --> 这里要找的是DEF之前的那个换行符之后的空白字符, 即缩进量为2
     * \n\tABC\n\t\tDEF\n     --> 最后一个字符就是换行符, 即刚刚换行完, 要找的仍然是DEF之前的那个换行符
     * \n\tABC\n\t\tDEF\n\n   --> 最后连续多个换行符, 要找的仍然是DEF之前的那个换行符
     * \n\tABC\n\t\t          --> 这里应返回ABC后面的换行符之后的缩进量2
     * \tABC --> 这里应返回首行的缩进量1</pre>
     * 
     * @param string 字符串
     * @return 缩进量
     */
    public static int findLastIndentSize(CharSequence string) {
        int lastIndex = string.length() - 1;
        return findLastIndentSize(string, lastIndex);
    }

    /**
     * 查找缩进量(从指定位置向前查找最近一行的缩进量)<pre>
     * 例如: \r\n=换行符, \t=TAB符, \s=空格
     * \n\tABC\n\t\tDEF\t\t\t --> 这里要找的是DEF之前的那个换行符之后的空白字符, 即缩进量为2
     * \n\tABC\n\t\tDEF\n     --> 最后一个字符就是换行符, 即刚刚换行完, 要找的仍然是DEF之前的那个换行符
     * \n\tABC\n\t\tDEF\n\n   --> 最后连续多个换行符, 要找的仍然是DEF之前的那个换行符
     * \n\tABC\n\t\t          --> 这里应返回ABC后面的换行符之后的缩进量2
     * \tABC --> 这里应返回首行的缩进量1</pre>
     * 
     * @param string 字符串
     * @param lastIndex 从哪个位置开始向前查找
     * @return 缩进量
     */
    public static int findLastIndentSize(CharSequence string, int lastIndex) {
        for (int i = lastIndex; i >= 0; i--) {
            char c = string.charAt(i);
            if (c == '\r' || c == '\n') {
                // 移除最后连续的换行符
                lastIndex--;
            } else {
                break;
            }
        }
        // 查找最后一个换行符
        int position = -1;
        for (int i = lastIndex; i >= 0; i--) {
            char c = string.charAt(i);
            if (c == '\r' || c == '\n') {
                position = i;
                break;
            }
        }
        // 从换行符位置的下一个字符开始, 查找前置空白字符
        // 计算空白字符等于多少缩进量
        return calcSpacesToTabSize(string, position + 1, lastIndex + 1);
    }

    /** 计算空白字符等于多少缩进量(余1个空格不算缩进,余2个空格算1个缩进) **/
    public static int calcSpacesToTabSize(CharSequence string) {
        return calcSpacesToTabSize(string, 0, string.length());
    }

    /** 计算空白字符等于多少缩进量(余1个空格不算缩进,余2个空格算1个缩进) **/
    public static int calcSpacesToTabSize(CharSequence string, int start, int end) {
        int size = 0;
        int pending = 0;
        for (int i = start; i < end; i++) {
            char c = string.charAt(i);
            if (c == ' ') {
                pending++;
            } else if (c == '\t') {
                if (pending > 0) {
                    // TAB之前的空格, 不够4个的不计缩进
                    size += pending / 4;
                    pending = 0;
                }
                size++;
            } else {
                break;
            }
        }
        if (pending > 0) {
            // 余1个空格不算缩进,余2个空格算1个缩进
            size += (pending + 2) / 4;
        }
        return size;
    }

    /** 统计文本中有多少个换行符 **/
    public static int countNewlineChars(String string) {
        // 单独的\r或\n算一行, 连续的\r\n算一行, \n\r算两行
        int count = 0;
        int size = string == null ? 0 : string.length();
        for (int i = 0; i < size; i++) {
            char c = string.charAt(i);
            if (c == '\n') {
                count++;
            } else if (c == '\r') {
                if (i == size - 1 || string.charAt(i + 1) != '\n') {
                    count++;
                }
            }
        }
        return count;
    }

}
