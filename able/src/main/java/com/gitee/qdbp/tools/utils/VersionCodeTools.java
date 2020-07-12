package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本号工具类
 *
 * @author zhaohuihua
 * @version 20200711
 */
public class VersionCodeTools {

    /**
     * 比较版本号<br>
     * 1.0.0; 4.3.20.RELEASE; 1.2.8a; 1.2.8-SNAPSHOT; 1.0.234_20200708001; 1.0.0-R1<br>
     * 注意: 数字字母混合的, 将数字/字母拆分, 继续分段对比<br>
     * -- 如 8a 拆分为 [8,a]; V24R108 拆分为[V,24,R,108]<br>
     * -- 例如: 1.2.7 &lt; 1.2.8a; 1.2 &lt; 1.2.8a; 1.2.8 &gt; 1.2.8a; 1.2.8a &lt; 1.2.8b;<br>
     * ---- 1.2.8a &lt; 1.2.8a0; 1.2.8a &lt; 1.2.8ab; 1.2.8a1 = 1.2.8a01; 1.2.8a1 &lt; 1.2.8a012<br>
     * 注意: 两边级数不相等时, 如果多出来的是0则相等; 多出来的是数字, 则级数多的为大; <br>
     * -- 多出来的是字母, 则级数多的为小<br>
     * -- (即判定多出来的部分性质是SNAPSHOT,alpha,beta,build之类的临时或测试版本)<br>
     * -- 就是关于1.0与1.0.B001谁比较大的问题<br>
     * -- 对于1.0与1.0.1自然是后者比较大; 但B001,R001之类带字母的, 一律认为是正式版本之前的临时版本<br>
     * -- 例如: 1.0 = 1.0.0.0; 1.0.0 &lt; 1.0.0.1;<br>
     * ---- 多出来的是字母: 1.2.8 &gt; 1.2.8-alpha; 1.2.8 &gt; 1.2.8-SNAPSHOT; 1.2.8 &gt; 1.2.8.R1;<br>
     * ---- 1.0 &gt; 1.0.B001; V108 &gt; V108B001(等于V108是正式版本,B001是临时版本)<br>
     * 
     * @param source 当前版本号
     * @param target 目标版本号
     * @return 返回负数,0,正数, 分别代表source小于等于大于target
     */
    public static int compare(String source, String target) {
        VerifyTools.requireNotBlank(target, "versionString");
        VerifyTools.requireNotBlank(target, "versionString");
        String[] sources = splitVersionString(source);
        String[] targets = splitVersionString(target);
        return compareVersions(sources, targets, 0);
    }

    public static String[] splitVersionString(String versionString) {
        return StringTools.split(versionString, '.', '_', '-');
    }

    public static int compareVersions(String[] sources, String[] targets, int start) {
        int size = Math.max(sources.length, targets.length);
        for (int i = start; i < size; i++) {
            String sourceString = sources.length <= i ? null : sources[i];
            String targetString = targets.length <= i ? null : targets[i];
            if (VerifyTools.equals(sourceString, targetString)) {
                continue;
            }
            int result = compareVersionItem(sourceString, targetString);
            if (result == 0) {
                continue;
            } else {
                return result;
            }
        }
        return 0;
    }

    // 比较版本号(如果一侧是数字另一侧不是数字, 则数字为大)
    // 1 > alpha; 1 < 12a; 1 > 1a; 1a < 1a0; abc < abcd
    private static int compareVersionItem(String source, String target) {
        if (VerifyTools.equals(source, target)) {
            return 0;
        }
        // 两边级数不相等时, 如果多出来的是0则相等; 
        // 多出来的是数字, 则级数多的为大; 多出来的是字母, 则级数多的为小
        // 如果为空就变成0, 避免 1.0 < 1.0.0 的问题 (1.0与1.0.0应该相等)
        source = VerifyTools.nvl(source, "0");
        target = VerifyTools.nvl(target, "0");

        if (StringTools.isDigit(source) && StringTools.isDigit(target)) {
            // 都是数字
            return compareVersionNumber(source, target);
        }

        // 有一侧存在字母, 继续拆分比较
        // 将数字和字母分开, 如 8a 拆分为 [8,a]; V24R108 拆分为[V,24,R,108]
        String[] sources = splitAsciiVersion(source);
        String[] targets = splitAsciiVersion(target);
        int size = Math.max(sources.length, targets.length);
        for (int i = 0; i < size; i++) {
            String sourceString = sources.length <= i ? null : sources[i];
            String targetString = targets.length <= i ? null : targets[i];
            if (VerifyTools.equals(sourceString, targetString)) {
                continue;
            }
            boolean sourceIsBlank = VerifyTools.isBlank(sourceString);
            boolean targetIsBlank = VerifyTools.isBlank(targetString);
            if (sourceIsBlank && targetIsBlank) { // 都为空
                continue;
            }
            // 上面作了处理, 第1级如果为空就会变成0
            // -- 避免了 1.0 < 1.0.0的问题 (1.0与1.0.0应该相等)
            // 两边级数不相等时, 多出来的是数字, 则级数多的为大; 多出来的是字母, 则级数多的为小;
            // -- 多出来的是0, 也以级数多的为大(这与前面不同)
            // -- 因为 1a 应该小于 1a0, 而不是等于; 前面的情况是 1.0 = 1.0.0
            if (sourceIsBlank) { // 本方为空, 对方不为空
                // 对方是数字, 本方小; 对方不是数字, 本方大
                return StringTools.isDigit(targetString) ? -1 : 1;
            } else if (targetIsBlank) { // 本方不为空, 对方为空
                // 本方是数字, 本方大; 本方不是数字, 对方大
                return StringTools.isDigit(sourceString) ? 1 : -1;
            } else { // 都不为空
                int diff = compareVersionValue(sourceString, targetString);
                if (diff != 0) {
                    return diff;
                }
            }
        }
        return 0;
    }

    // 数字/数字以数值比较; 字母/字母以A-Z比较; 数字与字母比较时, 以数字为大
    private static int compareVersionValue(String source, String target) {
        if (source.equals(target)) {
            return 0;
        }
        boolean sourceIsDigit = StringTools.isDigit(source);
        boolean targetIsDigit = StringTools.isDigit(target);
        if (sourceIsDigit && targetIsDigit) { // 都是数字
            return compareVersionNumber(source, target);
        } else if (sourceIsDigit) { // 左侧是数字, 右侧不是数字
            return 1;
        } else if (targetIsDigit) { // 左侧不是数字, 右侧是数字
            return -1;
        } else { // 都不是数字
            return compareVersionAlphabet(source, target);
        }
    }

    private static int compareVersionNumber(String source, String target) {
        source = StringTools.removeLeft(source, '0');
        target = StringTools.removeLeft(target, '0');
        if (source.length() > target.length()) {
            return 1;
        } else if (source.length() < target.length()) {
            return -1;
        } else {
            return compareVersionAlphabet(source, target);
        }
    }

    private static int compareVersionAlphabet(String source, String target) {
        int diff = source.compareTo(target);
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    // 拆分带数字和字母的版本号, 将数字和字母分开
    // 如 8a 拆分为 [8,a]; V24R108 拆分为[V,24,R,108]
    private static String[] splitAsciiVersion(String string) {
        if (string == null || string.length() == 0) {
            return new String[0];
        }
        List<StringBuilder> buffers = new ArrayList<>();
        char[] chars = string.toCharArray();
        boolean lastIsDigit = Character.isDigit(chars[0]);
        buffers.add(new StringBuilder().append(chars[0]));
        for (int i = 1; i < chars.length; i++) {
            char c = chars[i];
            boolean currIsDigit = Character.isDigit(c);
            if (currIsDigit == lastIsDigit) {
                buffers.get(buffers.size() - 1).append(c);
            } else {
                buffers.add(new StringBuilder().append(c));
            }
            lastIsDigit = currIsDigit;
        }
        String[] result = new String[buffers.size()];
        for (int i = 0; i < buffers.size(); i++) {
            result[i] = buffers.get(i).toString();
        }
        return result;
    }
}
