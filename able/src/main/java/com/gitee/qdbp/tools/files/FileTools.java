package com.gitee.qdbp.tools.files;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import com.gitee.qdbp.able.exception.ExceptionWatcher;
import com.gitee.qdbp.able.exception.FileOversizeException;
import com.gitee.qdbp.tools.utils.ConvertTools;

/**
 * 文件工具类
 *
 * @author zhaohuihua
 * @version 151221
 */
public abstract class FileTools {

    /** buffer size used for reading and writing **/
    private static final int BUFFER_SIZE = 8192;

    private static String CHARSET = "UTF-8";

    /**
     * 读取文件的文本内容
     * 
     * @param file 待读取的文件
     * @return 文本内容
     */
    public static String readTextContent(File file) throws IOException {
        byte[] bytes;
        try (FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            copy(fis, bos);
            bytes = bos.toByteArray();
        }

        try {
            String charset = getEncoding(file);
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes, Charset.forName(CHARSET));
        }
    }

    /**
     * 读取文件的文本内容
     * 
     * @param file 待读取的文件
     * @param maxSize 允许读取的最大字节数, 0表示无限制
     * @return 文本内容
     */
    public static String readTextContent(File file, long maxSize) throws IOException {
        byte[] bytes;
        try (FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            copy(fis, bos, maxSize);
            bytes = bos.toByteArray();
        }

        try {
            String charset = getEncoding(file);
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes, Charset.forName(CHARSET));
        }
    }

    /**
     * 从输入流复制到输出流
     * 
     * @param input 输入流
     * @param output 输出流
     * @return 复制的字节数
     * @throws IOException IO异常
     */
    public static long copy(InputStream input, OutputStream output) throws IOException {
        int length;
        byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0;
        while ((length = input.read(buffer, 0, buffer.length)) > 0) {
            output.write(buffer, 0, length);
            total += length;
        }
        return total;
    }

    /**
     * 从输入流复制到输出流
     * 
     * @param input 输入流
     * @param output 输出流
     * @param maxSize 允许的最大字节数, 0表示无限制
     * @return 复制的字节数
     * @throws FileOversizeException 超出了大小限制
     * @throws IOException IO异常
     */
    public static long copy(InputStream input, OutputStream output, long maxSize)
            throws FileOversizeException, IOException {
        int length;
        byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0;
        while ((length = input.read(buffer, 0, buffer.length)) > 0) {
            if (maxSize > 0 && total + length > maxSize) {
                String max = ConvertTools.toByteString(maxSize);
                String msg = "File exceeds maximum permitted size of [" + max + "]";
                throw new FileOversizeException(msg);
            }
            output.write(buffer, 0, length);
            total += length;
        }
        return total;
    }

    /**
     * 将byte数据保存到文件
     *
     * @param data 数据
     * @param path 文件路径
     * @throws IOException IO异常
     */
    public static void saveFile(byte[] data, String path) throws IOException {
        Path target = Paths.get(path);
        mkdirsIfNotExists(target);
        try (InputStream in = new ByteArrayInputStream(data)) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 将InputStream数据保存到文件
     *
     * @param data 数据
     * @param path 文件路径
     * @return 复制的字节数
     * @throws IOException IO异常
     */
    public static long saveFile(InputStream input, String path) throws IOException {

        Path target = Paths.get(path);
        mkdirsIfNotExists(target);
        return Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 将InputStream数据保存到文件
     *
     * @param data 数据
     * @param path 文件路径
     * @param maxSize 允许的最大字节数
     * @return 复制的字节数
     * @throws FileOversizeException 超出了大小限制
     * @throws IOException IO异常
     */
    public static long saveFile(InputStream input, String path, long maxSize)
            throws FileOversizeException, IOException {

        Path target = Paths.get(path);
        mkdirsIfNotExists(target);
        return copy(input, target, maxSize, StandardCopyOption.REPLACE_EXISTING);
    }

    // code copy from Files.copy(InputStream, Path, CopyOption)
    private static long copy(InputStream in, Path target, long maxSize, CopyOption... options) throws IOException {
        // ensure not null before opening file
        Objects.requireNonNull(in);

        // check for REPLACE_EXISTING
        boolean replaceExisting = false;
        for (CopyOption opt : options) {
            if (opt == StandardCopyOption.REPLACE_EXISTING) {
                replaceExisting = true;
            } else {
                if (opt == null) {
                    throw new NullPointerException("options contains 'null'");
                } else {
                    throw new UnsupportedOperationException(opt + " not supported");
                }
            }
        }

        // attempt to delete an existing file
        SecurityException se = null;
        if (replaceExisting) {
            try {
                Files.deleteIfExists(target);
            } catch (SecurityException x) {
                se = x;
            }
        }

        // attempt to create target file. If it fails with
        // FileAlreadyExistsException then it may be because the security
        // manager prevented us from deleting the file, in which case we just
        // throw the SecurityException.
        OutputStream ostream;
        try {
            ostream = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException x) {
            if (se != null) throw se;
            // someone else won the race and created the file
            throw x;
        }

        // do the copy
        try (OutputStream out = ostream) {
            return copy(in, out, maxSize);
        }
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
     * 移动文件或递归移动文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.copy(source, destination, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.copy(source, destination, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @return 是否全部成功
     */
    public static boolean move(String source, String destination) {
        CountWatcher counter = new CountWatcher();
        move(Paths.get(source), Paths.get(destination), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 移动文件或递归移动文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.move(source, destination, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.move(source, destination, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @return 是否全部成功
     */
    public static boolean move(File source, File destination) {
        CountWatcher counter = new CountWatcher();
        move(source.toPath(), destination.toPath(), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 移动文件或递归移动文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     */
    public static void move(String source, String destination, ExceptionWatcher exceptionWatcher) {
        move(Paths.get(source), Paths.get(destination), exceptionWatcher);
    }

    /**
     * 移动文件或递归移动文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     */
    public static void move(File source, File destination, ExceptionWatcher exceptionWatcher) {
        move(source.toPath(), destination.toPath(), exceptionWatcher);
    }

    /**
     * 移动文件或递归移动文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     * @return 是否全部成功(捕获到未处理的异常就返回失败)
     */
    private static boolean move(Path source, Path destination, ExceptionWatcher exceptionWatcher) {

        // normalize()清除路径中的.和..
        Path spath = source.normalize();
        Path dpath = destination.normalize();

        if (!Files.exists(spath)) {
            // 源文件不存在时, 直接返回成功
            return true;
        }

        if (spath.toString().equals(dpath.toString())) {
            return true; // 源路径和目标路径相同, 直接返回成功
        }

        if (Files.isDirectory(spath) && (Files.exists(dpath) || dpath.startsWith(spath))) {
            // 是文件夹, 目标路径已经存在或目标路径在源路径之中(移到源路径下面), 则只能遍历一个个的移动
            File sfile = spath.toFile();
            File[] files = sfile.listFiles();
            for (File next : files) {
                boolean continues = move(next.toPath(), dpath.resolve(next.getName()), exceptionWatcher);
                if (!continues) {
                    return false;
                }
            }
            if (sfile.exists() && sfile.listFiles().length == 0) {
                // 删除空文件夹
                sfile.delete();
            }
            return true;
        } else {
            // 目标路径的上级文件夹必须存在, 否则会报错
            mkdirsIfNotExists(dpath, true);
            // 开始移动
            try {
                Files.move(spath, dpath);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on move file: [%s] --> [%s]. %s", spath, dpath, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }
    }

    /**
     * 复制文件或递归复制文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.copy(source, destination, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.copy(source, destination, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @return 是否全部成功
     */
    public static boolean copy(String source, String destination) {
        CountWatcher counter = new CountWatcher();
        copy(Paths.get(source), Paths.get(destination), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 复制文件或递归复制文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.copy(source, destination, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.copy(source, destination, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @return 是否全部成功
     */
    public static boolean copy(File source, File destination) {
        CountWatcher counter = new CountWatcher();
        copy(source.toPath(), destination.toPath(), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 复制文件或递归复制文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     */
    public static void copy(String source, String destination, ExceptionWatcher exceptionWatcher) {
        copy(Paths.get(source), Paths.get(destination), exceptionWatcher);
    }

    /**
     * 复制文件或递归复制文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     */
    public static void copy(File source, File destination, ExceptionWatcher exceptionWatcher) {
        copy(source.toPath(), destination.toPath(), exceptionWatcher);
    }

    /**
     * 复制文件或递归复制文件夹<br>
     * 源文件不存在时, 不会抛异常, 直接返回成功
     * 
     * @param source 源文件
     * @param destination 目标文件
     * @param exceptionWatcher 异常处理类
     * @return 是否全部成功(捕获到未处理的异常就返回失败)
     */
    private static boolean copy(Path source, Path destination, ExceptionWatcher exceptionWatcher) {
        // normalize()清除路径中的.和..
        Path spath = source.normalize();
        Path dpath = destination.normalize();

        if (!Files.exists(spath)) {
            // 源文件不存在时, 直接返回成功
            return true;
        }

        if (spath.toString().equals(dpath.toString())) {
            return true; // 源路径和目标路径相同, 直接返回成功
        }

        // 目标路径的上级文件夹必须存在, 否则会报错
        mkdirsIfNotExists(dpath, true);

        if (Files.isDirectory(spath)) { // 复制文件夹
            try {
                Files.walkFileTree(spath, new CopyFileVisitor(spath, dpath, exceptionWatcher));
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on copy directory: [%s] --> [%s]. %s", spath, dpath, e);
                return handleException(msg, e, exceptionWatcher);
            }
        } else { // 复制文件
            try {
                Files.copy(spath, dpath);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on copy file: [%s] --> [%s]. %s", spath, dpath, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }
    }

    /**
     * 删除文件或递归删除文件夹<br>
     * 源文件不存在时, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.delete(source, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.delete(source, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 待删除的文件
     * @return 是否全部成功
     */
    public static boolean delete(String source) {
        CountWatcher counter = new CountWatcher();
        delete(Paths.get(source), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 删除文件或递归删除文件夹<br>
     * 源文件不存在时, 直接返回成功<br>
     * <br>
     * 如果不关注是否成功建议使用ExceptionLogger记录日志:<br>
     * FileTools.delete(source, ExceptionLogger.SIMPLE);<br>
     * 如果既要记录日志又要知道是否成功:<br>
     * CountLogWatcher counter = ExceptionLogger.newCountWatcher();<br>
     * FileTools.delete(source, counter);<br>
     * boolean success = counter.getFailedTimes() == 0;<br>
     * 
     * @param source 待删除的文件
     * @return 是否全部成功
     */
    public static boolean delete(File source) {
        CountWatcher counter = new CountWatcher();
        delete(source.toPath(), counter);
        return counter.getFailedTimes() == 0;
    }

    /**
     * 删除文件或递归删除文件夹<br>
     * 源文件不存在时, 直接返回成功
     * 
     * @param source 待删除的文件
     * @param exceptionWatcher 异常处理类
     */
    public static void delete(String source, ExceptionWatcher exceptionWatcher) {
        delete(Paths.get(source), exceptionWatcher);
    }

    /**
     * 删除文件或递归删除文件夹<br>
     * 源文件不存在时, 直接返回成功
     * 
     * @param source 待删除的文件
     * @param exceptionWatcher 异常处理类
     */
    public static void delete(File source, ExceptionWatcher exceptionWatcher) {
        delete(source.toPath(), exceptionWatcher);
    }

    /**
     * 删除文件或递归删除文件夹<br>
     * 源文件不存在时, 直接返回成功
     * 
     * @param source 待删除的文件
     * @param exceptionWatcher 异常处理类
     * @return 是否全部成功(捕获到未处理的异常就返回失败)
     */
    private static boolean delete(Path source, ExceptionWatcher exceptionWatcher) {
        if (!Files.exists(source)) {
            // 源文件不存在时, 直接返回成功
            return true;
        }

        if (Files.isDirectory(source)) { // 删除文件夹
            try {
                Files.walkFileTree(source, new DeleteFileVisitor(exceptionWatcher));
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on delete directory: [%s]. %s", source, e);
                return handleException(msg, e, exceptionWatcher);
            }
        } else { // 删除文件
            try {
                Files.deleteIfExists(source);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on delete file: [%s]. %s", source, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }
    }

    /**
     * 统计失败次数的Watcher
     *
     * @author zhaohuihua
     * @version 190622
     */
    private static class CountWatcher implements ExceptionWatcher {

        private AtomicInteger counter = new AtomicInteger();

        public boolean onCaughtException(String message, Throwable e) {
            counter.getAndIncrement();
            return true;
        }

        public int getFailedTimes() {
            return counter.get();
        }
    }

    /** 发生异常时是否继续 **/
    protected static boolean handleException(String details, Throwable e, ExceptionWatcher exceptionWatcher) {
        if (exceptionWatcher == null) {
            return true;
        } else {
            return exceptionWatcher.onCaughtException(details, e);
        }
    }

    /**
     * 文件Visitor
     *
     * @author zhaohuihua
     * @version 161224
     */
    public static abstract class AllFileVisitor implements FileVisitor<Path> {

        protected ExceptionWatcher exceptionWatcher;

        public AllFileVisitor(ExceptionWatcher exceptionWatcher) {
            this.exceptionWatcher = exceptionWatcher;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attrs) {
            boolean continues = onPreVisitDirectory(directory);
            return continues ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException e) {
            if (e != null) {
                String msg = String.format("Caught exception on post visit directory: [%s]. %s", directory, e);
                if (!handleException(msg, e, exceptionWatcher)) {
                    return FileVisitResult.TERMINATE;
                }
            }
            boolean continues = onPostVisitDirectory(directory);
            return continues ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            boolean continues = onVisitFile(file);
            return continues ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
            String msg = String.format("Caught exception on visit file: [%s]. %s", file, e);
            if (!handleException(msg, e, exceptionWatcher)) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        protected boolean onVisitFile(Path file) {
            return true;
        }

        protected boolean onPreVisitDirectory(Path directory) {
            return true;
        }

        protected boolean onPostVisitDirectory(Path directory) {
            return true;
        }
    }

    private static class CopyFileVisitor extends AllFileVisitor {

        private Path source;
        private Path destination;

        public CopyFileVisitor(Path source, Path destination, ExceptionWatcher exceptionWatcher) {
            super(exceptionWatcher);
            this.source = source;
            this.destination = destination;
        }

        protected boolean onVisitFile(Path file) {
            Path relative = source.relativize(file);
            Path absolute = destination.resolve(relative);
            try {
                mkdirsIfNotExists(absolute);
                Files.copy(file, absolute);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on copy file: [%s] --> [%s]. %s", file, absolute, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }
    }

    private static class DeleteFileVisitor extends AllFileVisitor {

        public DeleteFileVisitor(ExceptionWatcher exceptionWatcher) {
            super(exceptionWatcher);
        }

        protected boolean onVisitFile(Path file) {
            try {
                Files.deleteIfExists(file);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on delete file: [%s]. %s", file, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }

        protected boolean onPostVisitDirectory(Path directory) {
            try {
                Files.deleteIfExists(directory);
                return true;
            } catch (IOException e) {
                String msg = String.format("Caught exception on delete directory: [%s]. %s", directory, e);
                return handleException(msg, e, exceptionWatcher);
            }
        }
    }

    /**
     * 判断文件的编码格式<br>
     * 来源: http://www.chsi.com.cn/xy/com/200902/20090218/17570775.html<br>
     * BOM参考: http://www.bitscn.com/pdb/java/200605/20811.html
     * 
     * @param file 指定文件
     * @return 编码格式
     * @version 2010-10-05
     */
    public static String getEncoding(File file) {
        String encoding = "GBK";
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.mark(0);

            // 带BOM信息的情况, 根据前三位判断编码
            byte[] bytes = new byte[3];
            int read = bis.read(bytes, 0, 3);
            if (read == -1) {
                return encoding;
            }
            if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
                return "UTF-16LE"; // Little-Endian
            } else if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
                return "UTF-16BE"; // Big-Endian
            } else if (bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                return "UTF-8";
            }

            // 根据BOM信息未判断出编码的情况
            bis.reset();
            while ((read = bis.read()) != -1) {
                if (read >= 0xF0) {
                    break;
                }

                if (0x80 <= read && read <= 0xBF) { // 单独出现BF以下的, 也算是GBK
                    break;
                }
                if (0xC0 <= read && read <= 0xDF) {
                    read = bis.read();
                    // 双字节 (0xC0 - 0xDF)(0x80 - 0xBF), 也可能在GB编码内
                    if (0x80 <= read && read <= 0xBF) {
                        continue;
                    } else {
                        break;
                    }
                }
                // 也有可能出错, 但是几率较小
                else if (0xE0 <= read && read <= 0xEF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            encoding = "UTF-8";
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
        return encoding;
    }
}
