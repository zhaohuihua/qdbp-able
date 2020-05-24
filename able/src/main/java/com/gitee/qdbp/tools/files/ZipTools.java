package com.gitee.qdbp.tools.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import com.gitee.qdbp.able.exception.ResourceNotFoundException;
import com.gitee.qdbp.able.matches.AntStringMatcher;
import com.gitee.qdbp.able.matches.StringMatcher;
import com.gitee.qdbp.tools.files.FileTools.AllFileVisitor;
import com.gitee.qdbp.tools.utils.VerifyTools;

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
     * @param relativePaths 相对路径, 可以是文件或文件夹(文件夹将会遍历所有子文件)
     * @return 压缩项列表
     */
    public static List<UrlItem> collectFiles(String rootFolder, String filter) {
        String rootAbsoluteFolder = PathTools.getAbsoluteFolder(rootFolder);
        CollectZipFileVisitor visitor = new CollectZipFileVisitor(rootAbsoluteFolder, filter);
        try {
            Path path = Paths.get(rootFolder);
            Files.walkFileTree(path, visitor);
            return visitor.getItems();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 收集可压缩的文件
     *
     * @param rootFolder 文件夹路径
     * @param relativePaths 相对路径, 可以是文件或文件夹(文件夹将会遍历所有子文件)
     * @return 压缩项列表
     */
    public static List<UrlItem> collectFiles(String rootFolder, List<String> relativePaths) {
        List<UrlItem> items = new ArrayList<>();
        String rootAbsoluteFolder = PathTools.getAbsoluteFolder(rootFolder);
        for (String relativePath : relativePaths) {
            if (PathTools.isPathOutOfBounds(relativePath)) {
                continue;
            }
            String absolutePath = PathTools.concat(rootAbsoluteFolder, relativePath);
            File file = new File(absolutePath);
            if (file.isFile()) {
                String url = fileToUrl(file);
                if (url != null) {
                    items.add(new UrlItem(relativePath, url));
                }
            } else if (file.isDirectory()) {
                CollectZipFileVisitor visitor = new CollectZipFileVisitor(rootAbsoluteFolder, "*.*");
                try {
                    Files.walkFileTree(file.toPath(), visitor);
                    items.addAll(visitor.getItems());
                } catch (IOException e) {
                    continue;
                }
            }
        }
        return items;
    }

    private static String fileToUrl(File file) {
        try {
            return file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static UrlItem newFileItem(String rootFolder, File file) {
        String relativePath = PathTools.relativize(rootFolder, file.getAbsolutePath());
        String url = fileToUrl(file);
        return url == null ? null : new UrlItem(relativePath, url);
    }

    /**
     * 从文件夹下递归查找可压缩的文件
     *
     * @author zhaohuihua
     * @version 20200519
     */
    private static class CollectZipFileVisitor extends AllFileVisitor {

        private String rootFolder;
        private StringMatcher fileNameMatcher;
        private List<UrlItem> items = new ArrayList<>();

        public CollectZipFileVisitor(String rootFolder, String filter) {
            super(null);
            this.rootFolder = rootFolder;
            this.fileNameMatcher = new AntStringMatcher(VerifyTools.nvl(filter, "*.*"));
        }

        public List<UrlItem> getItems() {
            return items;
        }

        @Override
        protected boolean onVisitFile(Path path) {
            File file = path.toFile();
            if (file.isDirectory() || !fileNameMatcher.matches(file.getName())) {
                return true; // 继续
            }

            UrlItem item = newFileItem(rootFolder, file);
            if (item != null) {
                items.add(item);
            }
            return true; // 继续
        }
    }

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

        @Override
        public String toString() {
            return this.path;
        }
    }
}
