package com.gitee.qdbp.tools.files;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ResourceNotFoundException;

/**
 * 文件工具类
 *
 * @author zhaohuihua
 * @version 151221
 */
public abstract class FileTools {

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(FileTools.class);

    public static void copy(InputStream input, OutputStream output) throws IOException {
        int length;
        int bz = 2048;
        byte[] buffer = new byte[bz];
        while ((length = input.read(buffer, 0, buffer.length)) > 0) {
            output.write(buffer, 0, length);
        }
    }

    /**
     * 将byte数据保存到文件
     *
     * @param data 数据
     * @param path 文件路径
     * @throws IOException 失败
     */
    public static void saveFile(byte[] data, String path) throws IOException {
        Path target = Paths.get(path);
        mkdirsIfNotExists(target);
        try (InputStream in = new ByteArrayInputStream(data)) {
            Files.copy(in, target);
        }
    }

    /**
     * 将InputStream数据保存到文件
     *
     * @param data 数据
     * @param path 文件路径
     * @throws IOException 失败
     */
    public static void saveFile(InputStream input, String path) throws IOException {

        Path target = Paths.get(path);
        mkdirsIfNotExists(target);
        Files.copy(input, target);
    }

    /**
     * 如果文件的文件夹不存在则创建
     * 
     * @param file 必须是文件, 不能是文件夹
     */
    public static void mkdirsIfNotExists(File file) {
        mkdirsIfNotExists(file.toPath());
    }

    /**
     * 如果文件夹不存在则创建
     * 
     * @param file
     * @param toParent 是判断file自身还是判断file的父级文件夹
     */
    public static void mkdirsIfNotExists(File file, boolean toParent) {
        mkdirsIfNotExists(file.toPath(), toParent);
    }

    /**
     * 如果文件夹不存在则创建
     * 
     * @param path
     * @param toParent 是判断file自身还是判断file的父级文件夹
     */
    private static void mkdirsIfNotExists(Path path) {
        mkdirsIfNotExists(path, true);
    }

    /**
     * 如果文件夹不存在则创建
     * 
     * @param path 文件路径
     * @param toParent 是判断file自身还是判断file的父级文件夹
     */
    private static void mkdirsIfNotExists(Path path, boolean toParent) {
        Path folder = toParent ? path.getParent() : path;
        if (!Files.exists(folder)) {
            folder.toFile().mkdirs();
        }
    }

    /**
     * 移动文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    public static void move(String source, String destination) throws IOException {
        move(Paths.get(source), Paths.get(destination));
    }

    /**
     * 移动文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    public static void move(File source, File destination) throws IOException {
        move(source.toPath(), destination.toPath());
    }

    /**
     * 移动文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    private static void move(Path source, Path destination) throws IOException {
        // normalize()清除路径中的.和..
        Path spath = source.normalize();
        Path dpath = destination.normalize();

        if (!Files.exists(spath)) {
            // 源文件不存在
            dpath.toFile().mkdirs();
            return;
        }

        if (spath.toString().equals(dpath.toString())) {
            return; // 源路径和目标路径相同
        }

        if (Files.isDirectory(spath) && (Files.exists(dpath) || dpath.startsWith(spath))) {
            // 是文件夹, 目标路径已经存在或目标路径在源路径之中, 则只能遍历一个个的移动
            File sfile = spath.toFile();
            File[] files = sfile.listFiles();
            for (File next : files) {
                move(next.toPath(), dpath.resolve(next.getName()));
            }
            if (sfile.exists() && sfile.listFiles().length == 0) {
                // 删除空文件夹
                sfile.delete();
            }
        } else {
            // 目标路径的上级文件夹必须存在, 否则会报错
            mkdirsIfNotExists(dpath, true);
            // 开始移动
            try {
                Files.move(spath, dpath);
            } catch (IOException e) {
                log.error("Move file failed: {} --> {}\n\t{}", spath, dpath, e.toString());
            }
        }
    }

    /**
     * 复制文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    public static void copy(String source, String destination) throws IOException {
        copy(Paths.get(source), Paths.get(destination));
    }

    /**
     * 复制文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    public static void copy(File source, File destination) throws IOException {
        copy(source.toPath(), destination.toPath());
    }

    /**
     * 复制文件或文件夹
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @throws IOException
     */
    private static void copy(Path source, Path destination) throws IOException {

        if (!Files.exists(source)) {
            // 源文件不存在
            throw new ResourceNotFoundException(source.toString());
        }

        // 目标路径的上级文件夹必须存在, 否则会报错
        mkdirsIfNotExists(destination, true);

        if (Files.isDirectory(source)) { // 复制文件夹
            Files.walkFileTree(source, new CopyFileVisitor(source, destination));
        } else { // 复制文件
            Files.copy(source, destination);
        }
    }

    /**
     * 删除文件或文件夹
     * 
     * @param source 待删除的文件
     * @throws IOException
     */
    public static void delete(String source) throws IOException {
        delete(Paths.get(source));
    }

    /**
     * 删除文件或文件夹
     * 
     * @param source 待删除的文件
     * @throws IOException
     */
    public static void delete(File source) throws IOException {
        delete(source.toPath());
    }

    /**
     * 删除文件或文件夹
     * 
     * @param source 待删除的文件
     * @throws IOException
     */
    private static void delete(Path source) throws IOException {
        if (!Files.exists(source)) {
            return; // 源文件不存在
        }

        if (Files.isDirectory(source)) { // 删除文件夹
            Files.walkFileTree(source, new DeleteFileVisitor());
        } else { // 删除文件
            try {
                Files.deleteIfExists(source);
            } catch (IOException e) {
                log.error("Delete file failed: {}\n\t{}", source, e.toString());
            }
        }
    }

    /**
     * 文件Visitor
     *
     * @author zhaohuihua
     * @version 161224
     */
    private static abstract class AllFileVisitor implements FileVisitor<Path> {

        @Override
        public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) throws IOException {
            onPreVisitDirectory(directory);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException e) throws IOException {
            onPostVisitDirectory(directory);
            if (e != null) {
                log.error("Visit directory failed: {}\n\t{}", directory.toString(), e.toString());
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            onVisitFile(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
            log.error("Visit file failed: {}\n\t{}", file.toString(), e.toString());
            return FileVisitResult.CONTINUE;
        }

        protected void onVisitFile(Path file) {
        }

        protected void onPreVisitDirectory(Path directory) {
        }

        protected void onPostVisitDirectory(Path directory) {
        }
    }

    private static class CopyFileVisitor extends AllFileVisitor {

        private Path source;
        private Path destination;

        public CopyFileVisitor(Path source, Path destination) {
            this.source = source;
            this.destination = destination;
        }

        protected void onVisitFile(Path file) {
            Path relative = source.relativize(file);
            Path absolute = destination.resolve(relative);
            try {
                mkdirsIfNotExists(absolute);
                Files.copy(file, absolute);
            } catch (IOException e) {
                log.error("Copy file failed: {} --> {}\n\t{}", file, absolute, e.toString());
            }
        }
    }

    private static class DeleteFileVisitor extends AllFileVisitor {

        protected void onVisitFile(Path file) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                log.error("Delete file failed: {}\n\t{}", file, e.toString());
            }
        }

        protected void onPostVisitDirectory(Path directory) {
            try {
                Files.deleteIfExists(directory);
            } catch (IOException e) {
                log.error("Delete directory failed: {}\n\t{}", directory, e.toString());
            }
        }
    }
}
