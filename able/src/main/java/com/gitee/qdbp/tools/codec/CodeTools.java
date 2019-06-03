package com.gitee.qdbp.tools.codec;

import java.util.ArrayList;
import java.util.List;

/**
 * 编号处理工具类<br>
 * 用于维护每段固定长度,下级前缀等于上级的上下级编号, 便于like查询
 *
 * @author zhaohuihua
 * @version 170802
 */
public class CodeTools {

    /** start 开始位置(前缀长度) */
    private int start;
    /** 每一段的长度 **/
    private int length;

    public CodeTools(int start, int length) {
        this.start = start;
        this.length = length;
    }

    /** 拆分编号 **/
    public List<String> split(String code, boolean self) {
        return split(code, start, length, self);
    }

    /** 获取编号的级数 **/
    public int level(String code) {
        return level(code, start, length);
    }

    /** 获取顶级编号 **/
    public String top(String code) {
        return top(code, start, length);
    }

    /** 获取上级编号 **/
    public String parent(String code) {
        return parent(code, start, length);
    }

    /** 获取前缀编号 **/
    public String prefix(String code) {
        return prefix(code, start, length);
    }

    /**
     * 拆分编号<br>
     * 如split("500100220033", 4, true) --&gt; [5001, 50010022, 500100220033]<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如split("8500100220033", 4, true) --&gt; [85001, 850010022, 8500100220033]<br>
     * 如果code长度未达到最小长度, 返回空数组, split("85001", 4, 4, true) --&gt; []<br>
     * 
     * @param code 编号
     * @param length 每一段的长度
     * @param self 结果集是否包含编号本身
     * @return
     */
    public static List<String> split(String code, int length, boolean self) {
        return split(code, 0, length, self);
    }

    /**
     * 拆分编号<br>
     * 如split("500100220033", 4, true) --&gt; [5001, 50010022, 500100220033]<br>
     * 如split("500100220033", 4, 4, true) --&gt; [50010022, 500100220033]<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如split("8500100220033", 4, true) --&gt; [85001, 850010022, 8500100220033]<br>
     * 如split("8500100220033", 4, 4, true) --&gt; [850010022, 8500100220033]<br>
     * 如果code长度未达到最小长度, 返回空数组, split("85001", 4, 4, true) --&gt; []<br>
     * 
     * @param code 编号
     * @param start 开始位置
     * @param length 每一段的长度
     * @param self 结果集是否包含编号本身
     * @return
     */
    public static List<String> split(String code, int start, int length, boolean self) {
        List<String> list = new ArrayList<>();
        if (code == null || code.length() < start + length) {
            return list;
        }

        int m = code.length() % length; // 除不尽的都分给第一级
        for (int i = start + m + length; i < code.length(); i += length) {
            list.add(code.substring(0, i));
        }
        if (self) {
            list.add(code);
        }
        return list;
    }

    /** 获取编号的级数 **/
    public static int level(String code, int length) {
        return split(code, 0, length, true).size();
    }

    /** 获取编号的级数 **/
    public static int level(String code, int start, int length) {
        return split(code, start, length, true).size();
    }

    /**
     * 获取上级编号<br>
     * 如parent("500100220033", 4, 4) --&gt; 50010022<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如parent("8500100220033", 4, 4) --&gt; 850010022<br>
     * 如果code长度未达到最小长度, 返回空, parent("85001", 4, 4) --&gt; null<br>
     * 
     * @param code 编号
     * @param length 每一段的长度
     * @return 上级编号
     */
    public static String parent(String code, int start, int length) {
        if (code == null || code.length() < start + length * 2) { // 只有一级
            return null;
        }
        return code.substring(0, code.length() - length);
    }

    /**
     * 获取顶级编号<br>
     * 如top("500100220033", 4) --&gt; 5001<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如top("8500100220033", 4) --&gt; 85001<br>
     * 
     * @param code 编号
     * @param length 每一段的长度
     * @return 顶级编号
     */
    public static String top(String code, int length) {
        return top(code, 0, length);
    }

    /**
     * 获取顶级编号<br>
     * 如top("500100220033", 4) --&gt; 5001<br>
     * 如top("500100220033", 4, 4) --&gt; 50010022<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如top("8500100220033", 4) --&gt; 85001<br>
     * 如top("8500100220033", 4, 4) --&gt; 850010022<br>
     * 
     * @param code 编号
     * @param start 开始位置(前缀长度)
     * @param length 每一段的长度
     * @return 顶级编号
     */
    public static String top(String code, int start, int length) {
        if (code == null || code.length() < start + length) {
            return null;
        } else if (code.length() < start + length * 2) { // 只有一级
            return code;
        } else {
            int m = code.length() % length; // 除不尽的都分给第一级
            return code.substring(0, start + m + length);
        }
    }

    /**
     * 获取前缀编号<br>
     * 如prefix("500100220033", 4, 4) --&gt; 5001<br>
     * 如果长度不能整除, 多余的部分都给第一级<br>
     * 如prefix("8500100220033", 4, 4) --&gt; 85001<br>
     * 如果start=0, 返回null
     * 
     * @param code 编号
     * @param start 开始位置(前缀长度)
     * @param length 每一段的长度
     * @return 前缀编号
     */
    public static String prefix(String code, int start, int length) {
        if (start == 0 || code == null || code.length() < start) {
            return null;
        } else if (code.length() == start) {
            return code;
        } else {
            int m = code.length() % length; // 除不尽的都分给第一级
            return code.substring(0, m + start);
        }
    }
}
