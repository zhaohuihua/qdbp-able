package com.gitee.zhaohuihua.common.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.gitee.qdbp.tools.files.ImageTools;

public class ImageToolsTest {

    public static void main(String[] args) throws Exception {
        testThumbnail();
        testQrCode();
    }

    private static void testQrCode() throws Exception {
        String content = "http://www.baidu.com";
        int size = 100;
        try (OutputStream os = new FileOutputStream(new File("D:/baidu." + size + ".png"))) {
            ImageTools.generateQrCode(content, size, os);
        }
    }

    private static void testThumbnail() throws IOException {
        int width = 400;
        int height = 400;

        String folder = "D:/image/";
        // System.out.println(Arrays.toString(ImageIO.getWriterFormatNames()));
        String[] files = new String[] { "1.png", "2.png", "he.jpg", "hw.jpg" };
        for (String src : files) {
            int index = src.lastIndexOf('.');
            String dest = src.substring(0, index) + "." + width + "x" + height + src.substring(index);

            try (InputStream is = new FileInputStream(new File(folder + src));
                    OutputStream os = new FileOutputStream(new File(folder + dest))) {
                ImageTools.thumbnail(is, os, width, height);
            }
        }

    }
}
