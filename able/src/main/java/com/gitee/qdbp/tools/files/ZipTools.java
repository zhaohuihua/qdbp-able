package com.gitee.qdbp.tools.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import com.gitee.qdbp.able.exception.ResourceNotFoundException;
import com.gitee.qdbp.able.matches.StringMatcher;

/**
 * zip压缩工具
 *
 * @author zhaohuihua
 * @version 160223
 */
public class ZipTools {

    /**
     * ZIP文件解压
     * 
     * @param srcPath 源文件
     * @param saveFolder 保存文件的路径
     */
    public static void decompression(String srcPath, String saveFolder) throws IOException {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) { // 判断源文件是否存在
            throw new ResourceNotFoundException(srcPath);
        }
        String fileName = PathTools.removeExtension(new File(srcPath).getName()) + '/';
        // 开始解压
        try (ZipFile zipFile = new ZipFile(srcFile);) {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String relativePath = entry.getName();
                // 如果第一级路径与ZIP文件名相同, 则去掉这一级路径
                if (relativePath.startsWith(fileName)) {
                    relativePath = relativePath.substring(fileName.length());
                }
                if (entry.isDirectory()) {
                    // 如果是文件夹, 就创建文件夹
                    String folder = PathTools.concat(saveFolder, relativePath);
                    FileTools.mkdirsIfNotExists(new File(folder), false);
                    continue;
                }

                // 文件保存路径
                File file = new File(PathTools.concat(saveFolder, relativePath));
                FileTools.mkdirsIfNotExists(file, true);
                // 将压缩文件内容写入文件中
                try (InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream os = new FileOutputStream(file);) {
                    FileTools.copy(is, os);
                }
            }
        }
    }

    /**
     * 收集可压缩的文件
     *
     * @param rootFolder 文件夹路径
     * @param filter 文件过滤表达式, 如*.docx
     * @return 压缩项列表
     */
    public static List<FileItem> collectFiles(String rootFolder, String filter) {
        List<File> files = FileTools.collect(rootFolder, filter);
        return toFileItems(rootFolder, files);
    }

    /**
     * 收集可压缩的文件
     *
     * @param rootFolder 文件夹路径
     * @param relativePaths 相对路径, 可以是文件或文件夹(文件夹将会遍历所有子文件)
     * @return 压缩项列表
     */
    public static List<FileItem> collectFiles(String rootFolder, List<String> relativePaths) {
        List<File> files = FileTools.collect(rootFolder, relativePaths);
        return toFileItems(rootFolder, files);
    }

    /**
     * 收集可压缩的文件
     *
     * @param rootFolder 文件夹路径
     * @param usePath 按文件路径还是文件名匹配
     * @param matcher 匹配规则
     * @return 压缩项列表
     */
    public static List<FileItem> collectFiles(String rootFolder, boolean usePath, StringMatcher matcher) {
        List<File> files = FileTools.treelist(rootFolder, usePath, matcher);
        return toFileItems(rootFolder, files);
    }

    /**
     * 将文件列表转换为压缩项列表
     * 
     * @param rootFolder 文件夹路径
     * @param files 文件列表
     * @return 压缩项列表
     */
    public static List<FileItem> toFileItems(String rootFolder, List<File> files) {
        String rootAbsoluteFolder = PathTools.getAbsoluteFolder(rootFolder);
        if (files == null) {
            return null;
        }
        List<FileItem> items = new ArrayList<>();
        for (File file : files) {
            String relativePath = PathTools.relativize(rootAbsoluteFolder, file.getAbsolutePath());
            items.add(new FileItem(relativePath, file));
        }
        return items;
    }

    /**
     * 通过URL下载文件并压缩保存到指定位置
     *
     * @param items 待压缩的文件信息
     * @param savePath 保存路径
     * @throws IOException
     */
    //
    public static <T extends ZipItem> void compression(List<T> items, String savePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(savePath);) {
            compression(items, fos);
        }
    }

    /**
     * 通过URL下载文件并压缩
     *
     * @param items 待压缩的文件信息
     * @return 压缩文件的二进制数据
     * @throws IOException
     */
    public static <T extends ZipItem> byte[] compression(List<T> items) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            compression(items, baos);
            return baos.toByteArray();
        }
    }

    /**
     * 通过URL下载文件并压缩到OutputStream中
     *
     * @param items 待压缩的文件信息
     * @param original OutputStream
     * @throws IOException
     */
    private static <T extends ZipItem> void compression(List<T> items, OutputStream original) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(original);
                BufferedOutputStream output = new BufferedOutputStream(zos);) {
            for (ZipItem i : items) {
                ZipEntry entry = new ZipEntry(i.getPath());
                zos.putNextEntry(entry);
                if (i instanceof UrlItem) {
                    UrlItem item = (UrlItem) i;
                    URL url = item.getUrl();
                    try (BufferedInputStream input = new BufferedInputStream(url.openStream());) {
                        FileTools.copy(input, zos);
                    }
                } else if (i instanceof FileItem) {
                    FileItem item = (FileItem) i;
                    File file = item.getFile();
                    try (FileInputStream fis = new FileInputStream(file);
                            BufferedInputStream input = new BufferedInputStream(fis);) {
                        entry.setTime(file.lastModified());
                        FileTools.copy(input, zos);
                    }
                } else {
                    throw new IllegalArgumentException("UnsupportedZipItem: {}" + i.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * zip配置
     *
     * @author zhaohuihua
     * @version 160223
     */
    public static abstract class ZipItem {

        /** 保存路径 **/
        private String path;

        /** 保存路径 **/
        public String getPath() {
            return path;
        }

        /** 保存路径 **/
        public void setPath(String path) {
            this.path = path;
        }
    }

    /**
     * URL配置
     *
     * @author zhaohuihua
     * @version 160223
     */
    public static class UrlItem extends ZipItem {

        /** 下载地址 **/
        private URL url;

        public UrlItem() {
        }

        public UrlItem(String path, URL url) {
            super.path = path;
            this.url = url;
        }

        public UrlItem(String path, String url) {
            super.path = path;
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("url format error: " + url, e);
            }
        }

        /** 下载地址 **/
        public URL getUrl() {
            return url;
        }

        /** 下载地址 **/
        public void setUrl(URL url) {
            this.url = url;
        }

    }

    /**
     * File配置
     *
     * @author zhaohuihua
     * @version 160223
     */
    public static class FileItem extends ZipItem {

        /** 下载地址 **/
        private File file;

        public FileItem() {
        }

        public FileItem(String path, File file) {
            super.path = path;
            this.file = file;
        }

        public FileItem(String path, String file) {
            super.path = path;
            this.file = new File(file);
        }

        /** 下载地址 **/
        public File getFile() {
            return file;
        }

        /** 下载地址 **/
        public void setFile(File file) {
            this.file = file;
        }

    }
}
