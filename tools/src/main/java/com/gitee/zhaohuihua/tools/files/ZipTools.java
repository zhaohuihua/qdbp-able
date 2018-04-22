package com.gitee.zhaohuihua.tools.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip压缩工具
 *
 * @author zhaohuihua
 * @version 160223
 */
public class ZipTools {

    /**
     * 通过URL下载文件并压缩保存到指定位置
     *
     * @param urls 待压缩的文件信息
     * @param savePath 保存路径
     * @throws IOException
     */
    public static void compression(List<UrlItem> urls, String savePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(savePath);) {
            compression(urls, fos);
        }
    }

    /**
     * 通过URL下载文件并压缩
     *
     * @param urls 待压缩的文件信息
     * @return 压缩文件的二进制数据
     * @throws IOException
     */
    public static byte[] compression(List<UrlItem> urls) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            compression(urls, baos);
            return baos.toByteArray();
        }
    }

    /**
     * 通过URL下载文件并压缩到OutputStream中
     *
     * @param urls 待压缩的文件信息
     * @param original OutputStream
     * @throws IOException
     */
    private static void compression(List<UrlItem> urls, OutputStream original) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(original);
                BufferedOutputStream output = new BufferedOutputStream(zos);) {

            for (UrlItem i : urls) {
                zos.putNextEntry(new ZipEntry(i.getPath()));
                URL url = new URL(i.getUrl());
                try (BufferedInputStream input = new BufferedInputStream(url.openStream());) {
                    FileTools.copy(input, output);
                }
                output.flush();
            }
        }
    }

    /**
     * URL配置
     *
     * @author zhaohuihua
     * @version 160223
     */
    public static class UrlItem {

        /** 保存路径 **/
        private String path;
        /** 下载地址 **/
        private String url;

        public UrlItem() {
        }

        public UrlItem(String path, String url) {
            this.path = path;
            this.url = url;
        }

        /** 保存路径 **/
        public String getPath() {
            return path;
        }

        /** 保存路径 **/
        public void setPath(String path) {
            this.path = path;
        }

        /** 下载地址 **/
        public String getUrl() {
            return url;
        }

        /** 下载地址 **/
        public void setUrl(String url) {
            this.url = url;
        }

    }
}
