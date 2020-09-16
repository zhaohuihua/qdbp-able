package com.gitee.qdbp.tools.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.gitee.qdbp.tools.files.FileTools;

/**
 * 测试结果比对工具
 *
 * @author zhaohuihua
 * @version 20200614
 */
public class AssertTools {

    /**
     * 比对两个对象是否深度一致
     * 
     * @param actual 实际对象
     * @param expected 期望对象
     */
    public static void assertDeepEquals(Object actual, Object expected) {
        String owner = "Root object";
        if (expected != null) {
            owner = expected.getClass().getSimpleName();
        } else if (actual != null) {
            owner = actual.getClass().getSimpleName();
        }
        DeepEqualsAssertion assertion = new DeepEqualsAssertion();
        assertion.assertDeepEquals(actual, expected, owner);
        List<String> errors = assertion.getErrors();
        if (!errors.isEmpty()) {
            String msg = "caught " + errors.size() + " exceptions!\n" + ConvertTools.joinToString(errors, "\n");
            throw new AssertionError(msg);
        }
    }

    /**
     * 按行比较两个文本文件是否一致
     * 
     * @param actual 实际的文本文件路径
     * @param expected 对比的文本文件路径
     * @throws IOException 文件不存在或文件读取失败
     */
    public static void assertTextFileEquals(String actual, String expected) throws IOException {
        String aContent = FileTools.readTextContent(new File(actual));
        String eContent = FileTools.readTextContent(new File(expected));
        assertTextLinesEquals(aContent, eContent, expected);
    }

    /**
     * 按行比较两个文本内容是否一致
     * 
     * @param actual 实际文本内容
     * @param expected 期望文本内容
     */
    public static void assertTextLinesEquals(String actual, String expected) {
        assertTextLinesEquals(actual, expected, null);
    }

    /**
     * 按行比较两个文本内容是否一致
     * 
     * @param actual 实际文本内容
     * @param expected 期望文本内容
     * @param desc 描述信息
     */
    public static void assertTextLinesEquals(String actual, String expected, String desc) {
        String[] actualLines = splitStringByNewline(actual);
        String[] expectLines = splitStringByNewline(expected);
        int aLines = actualLines.length;
        int eLines = expectLines.length;
        if (aLines != eLines) {
            String msg = "Number of text lines is not equals, expected = " + eLines + ", actual = " + aLines;
            throw new AssertionError(desc == null ? msg : (desc + ' ' + msg));
        }
        for (int i = 0; i < aLines; i++) {
            String aString = actualLines[i];
            String eString = expectLines[i];
            if (aString.equals(eString)) {
                continue;
            }

            StringBuilder msg = new StringBuilder();
            if (desc != null) {
                msg.append(msg).append(' ');
            }
            msg.append("The text on line " + (i + 1) + " is not equals");
            if (aString.length() <= 10 && eString.length() <= 10) {
                msg.append(", expected = " + eString + ", actual = " + aString);
            } else {
                msg.append("\n\texpected = " + eString + "\n\tactual   = " + aString);
            }
            throw new AssertionError(msg);
        }
    }

    private static String[] splitStringByNewline(String string) {
        // \r\n替换为\n, 单独的\r替换为\n
        string = StringTools.replace(string, "\r\n", "\n", "\r", "\n");
        return StringTools.split(string, false, '\n');
    }
}
