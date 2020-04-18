package com.gitee.qdbp.tools.chart;

import java.awt.Font;

/**
 * 黑体字体, 从系统安装的字体中选择可用的字体<br>
 * new PlainHeiti(14);<br>
 * new BoldHeiti(14);<br>
 * new ItalicHeiti(14);<br>
 *
 * @author zhaohuihua
 * @version 20200214
 */
public class HeitiFont extends Font {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    public HeitiFont(int style, int size) {
        super(UiStyleTools.getDefaultHeitiFontName(), style, size);
    }

    public static class PlainHeiti extends HeitiFont {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        public PlainHeiti(int size) {
            super(Font.PLAIN, size);
        }
    }

    public static class BoldHeiti extends HeitiFont {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        public BoldHeiti(int size) {
            super(Font.BOLD, size);
        }
    }

    public static class ItalicHeiti extends HeitiFont {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        public ItalicHeiti(int size) {
            super(Font.ITALIC, size);
        }
    }
}
