package com.gitee.qdbp.tools.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * UI样式工具类
 *
 * @author zhaohuihua
 * @version 20200215
 */
public class UiStyleTools {

    /** 默认黑体字体表 **/
    // @formatter:off
    private static String[] DEFAULT_HEITI_FONT_FAMILY = new String[] {
            // windows
            "Microsoft YaHei", "SimSun", "NSimSun", "STXihei",
            // linux
            "Source Han Sans CN", "WenQuanYi Micro Hei", "WenQuanYi Zen Hei", "Source Han Serif SC", 
            // Mac OS
            "PingFang SC", "Hiragino Sans GB", "Heiti SC", "STHeiti"
        };
    // @formatter:on

    /** 获取默认黑体字体名称 **/
    public static String getDefaultHeitiFontName() {
        return DefaultHeitiFont.NAME;
    }

    private static class DefaultHeitiFont {

        private static String NAME = findAvailableFontName(DEFAULT_HEITI_FONT_FAMILY);
    }

    /** 查找可用的字体名称 **/
    public static String findAvailableFontName(String[] chooseNames) {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font font : fonts) {
            for (String fontName : chooseNames) {
                if (fontName.equalsIgnoreCase(font.getFontName(Locale.ENGLISH))
                        || fontName.equalsIgnoreCase(font.getFontName(Locale.CHINESE))) {
                    return fontName;
                }
            }
        }
        throw new IllegalArgumentException("Font not found: " + Arrays.toString(chooseNames));
    }

    /** 解析16进制表示的颜色列表 **/
    public static Color[] parseHexColors(String... strings) {
        VerifyTools.requireNotBlank(strings, "HexColorString");
        List<Color> colors = new ArrayList<>();
        for (String string : strings) {
            if (string.indexOf(',') < 0) {
                colors.add(Color.decode("0x" + string));
            } else {
                String[] words = StringTools.split(string, ',');
                for (String word : words) {
                    colors.add(Color.decode("0x" + word));
                }
            }
        }
        return colors.toArray(new Color[0]);
    }
}
