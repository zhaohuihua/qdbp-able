package com.gitee.zhaohuihua.tools.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.gitee.zhaohuihua.core.exception.ResourceNotFoundException;
import com.gitee.zhaohuihua.tools.utils.VerifyTools;

/**
 * 资源工具类<br>
 *
 * @author zhaohuihua
 * @version 170625
 */
public abstract class PathTools {

    /** 斜杠/ **/
    private static final char SLASH = '/';
    /** 反斜杠\ **/
    private static final char BSLASH = '\\';

    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /** URL prefix for loading from the file system: "file:" */
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * 获取文件扩展名<br>
     * /image/abc.def.png --&gt; .png<br>
     * /image/abcdef/png --&gt; null<br>
     * /image/abc.def/png --&gt; null<br>
     *
     * @param fileName
     * @return
     */
    public static String getExtension(String fileName) {
        return getExtension(fileName, true);
    }

    /**
     * 获取文件扩展名
     *
     * @param path
     * @param dot 带不带点, 如true=.png, false=png
     * @return
     */
    public static String getExtension(String path, boolean dot) {
        if (path == null) {
            return null;
        }

        int i = path.lastIndexOf('.');
        if (i < 0 || i < path.lastIndexOf('/') || i < path.lastIndexOf('/')) {
            return null;
        }
        return path.substring(i + (dot ? 0 : 1));
    }

    /**
     * 清除文件扩展名<br>
     * /image/abc.def.png --&gt; /image/abc.def<br>
     * /image/abcdef/png --&gt; /image/abcdef/png<br>
     * /image/abc.def/png --&gt; /image/abc.def/png<br>
     *
     * @param path
     * @return
     */
    public static String removeExtension(String path) {
        if (path == null) {
            return null;
        }

        int i = path.lastIndexOf('.');
        if (i < 0 || i < path.lastIndexOf('/') || i < path.lastIndexOf('/')) {
            return null;
        }

        return path.substring(0, i);
    }

    /**
     * 替换文件扩展名<br>
     * /image/abc.def.png, .jpg --&gt; /image/abc.def.jpg<br>
     * /image/abcdef/xxx, .jpg --&gt; /image/abcdef/xxx.jpg<br>
     * /image/abc.def/xxx, .jpg --&gt; /image/abc.def/xxx.jpg<br>
     *
     * @param path
     * @return
     */
    public static String replaceExtension(String path, String extension) {
        String newpath = removeExtension(path);
        if (newpath == null || extension == null) {
            return newpath;
        } else if (extension.startsWith(".")) {
            return newpath + extension;
        } else {
            return newpath + "." + extension;
        }
    }

    /** 获取最后修改时间 **/
    public static Date getLastModified(URL url) {
        try {
            URLConnection connection = url.openConnection();
            return new Date(connection.getLastModified());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 解析资源路径<br>
     * 未指定前缀的绝对路径一律解析为file<br>
     * <div><b>prefix = null</b><div>
     * <li>mmm/nnn.txt</li>
     * <li>D:/mmm/nnn.txt(unix)</li> <div><b>prefix = "classpath:"</b><div>
     * <li>classpath:/mmm/nnn.txt</li>
     * <li>classpath:mmm/nnn.txt</li> <div><b>prefix = "file:"</b><div>
     * <li>file:/mmm/nnn.txt</li>
     * <li>file:mmm/nnn.txt</li>
     * <li>/mmm/nnn.txt</li>
     * <li>D:/mmm/nnn.txt(windows)</li> <div><b>other prefix</b><div>
     * <li>http://mmm/nnn.txt</li>
     * <li>ftp://mmm/nnn.txt</li>
     * <li>...<br>
     * 
     * @param resourceLocation 资源位置
     * @return 资源信息
     */
    private static ResourceBean parseResource(String resourceLocation) {
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            // classpath:/mmm/nnn.txt or classpath:mmm/nnn.txt
            return new ResourceBean(CLASSPATH_URL_PREFIX, resourceLocation);
        } else if (resourceLocation.startsWith(FILE_URL_PREFIX)) {
            // file:/mmm/nnn.txt or file:mmm/nnn.txt
            return new ResourceBean(FILE_URL_PREFIX, resourceLocation);
        } else {
            char[] chars = resourceLocation.toCharArray();
            for (int i = 0, len = chars.length; i < len; i++) {
                if (chars[i] == SLASH || chars[i] == BSLASH) {
                    if (i == 0) { // /mmm/nnn.txt
                        String path = concat(FILE_URL_PREFIX, resourceLocation);
                        return new ResourceBean(FILE_URL_PREFIX, path);
                    } else { // mmm/nnn.txt
                        return new ResourceBean(resourceLocation);
                    }
                } else if (chars[i] == ':') {
                    if (i == 0) { // :/mmm/nnn.txt ?
                        return new ResourceBean(resourceLocation);
                    } else if (i == 1) { // D:/mmm/nnn.txt -- windows是绝对路径, unix是相对路径
                        if (new File(resourceLocation).isAbsolute()) { // windows
                            String path = concat(FILE_URL_PREFIX, resourceLocation);
                            return new ResourceBean(FILE_URL_PREFIX, path);
                        } else { // unix
                            return new ResourceBean(resourceLocation);
                        }
                    } else { // http://mmm/nnn.txt or ftp://mmm/nnn.txt
                        String prefix = resourceLocation.substring(0, i);
                        return new ResourceBean(prefix, resourceLocation);
                    }
                }
            }
            // nnn.txt
            return new ResourceBean(resourceLocation);
        }
    }

    /**
     * 转换为URL对象(不会判断是否存在)<br>
     * http://mmm/nnn.txt or ftp://mmm/nnn.txt<br>
     * 
     * @param url URL路径(完整URL路径)
     * @return URL对象
     * @throws ResourceNotFoundException URL格式错误
     */
    private static URL toUrlInstance(String url) throws ResourceNotFoundException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Argument [" + url + "] is not a well-formed URL");
        }
    }

    /**
     * 查找classpath下的资源<br>
     * findResource("mmm/nnn.txt", com.xxx.XxxTest.class, com.yyy.YyyTest.class)<br>
     * 假如XxxTest, YyyTest分别位于xxx.jar和yyy.jar, 将会查找以下位置:<br>
     * 1. {classpath}/mmm/nnn.txt<br>
     * 2. xxx.jar!/mmm/nnn.txt<br>
     * 3. yyy.jar!/mmm/nnn.txt<br>
     * 
     * @param path 资源路径(不带classpath:前缀的路径)
     * @param classes 备选查找位置
     * @return 资源URL
     * @throws ResourceNotFoundException 资源不存在
     */
    private static URL findClasspathResource(String path, Class<?>... classes) throws ResourceNotFoundException {

        Set<String> locations = new LinkedHashSet<>();
        ClassLoader cl = getDefaultClassLoader();
        String temp = concat(".", path); // 通过ClassLoader获取, 如果路径是/开头的, 会获取失败
        URL url = cl != null ? cl.getResource(temp) : ClassLoader.getSystemResource(temp);
        if (url == null) {
            URL root = cl != null ? cl.getResource("") : ClassLoader.getSystemResource("");
            locations.add(toUriPath(resolve(root, temp)));
            for (Class<?> clazz : VerifyTools.nvl(classes, new Class<?>[0])) {
                url = clazz.getResource(path);
                if (url != null) {
                    break;
                } else {
                    locations.add(getClassResourceRealPath(clazz, path));
                }
            }
        }
        if (url != null) {
            return url;
        } else {
            String desc = "Resource location [" + path + "]";
            throw new ResourceNotFoundException(desc + " does not found. Found in " + locations);
        }
    }

    // 检查资源是否存在
    private static void checkConnect(URL url) throws ResourceNotFoundException {
        try {
            URLConnection connection = url.openConnection(); // 检查资源是否存在
            connection.connect();
        } catch (FileNotFoundException e) {
            String desc = "Resource location [" + toUriPath(url) + "]";
            throw new ResourceNotFoundException(desc + " do not exist");
        } catch (IOException e) {
            String desc = "Resource location [" + toUriPath(url) + "]";
            throw new ResourceNotFoundException(desc + " connection failed", e);
        }
    }

    /**
     * 查找资源<br>
     * classpath:/mmm/nnn.txt = classpath:mmm/nnn.txt -- 相对路径<br>
     * http://mmm/nnn.txt -- 绝对路径<br>
     * file:/mmm/nnn.txt -- 绝对路径<br>
     * file:mmm/nnn.txt -- 相对路径<br>
     * /mmm/nnn.txt = file:/mmm/nnn.txt -- 绝对路径<br>
     * mmm/nnn.txt = classpath:/mmm/nnn.txt -- 相对路径<br>
     * D:/mmm/nnn.txt -- windows是绝对路径, unix是相对路径<br>
     * <br>
     * findResource("classpath:mmm/nnn.txt", com.xxx.XxxTest.class, com.yyy.YyyTest.class)<br>
     * 先从当前classpath查找, 再从classes列表对应的jar的classpath查找<br>
     * 假如XxxTest, YyyTest分别位于xxx.jar和yyy.jar, 将会查找以下位置:<br>
     * 1. {classpath}/mmm/nnn.txt<br>
     * 2. xxx.jar!/mmm/nnn.txt<br>
     * 3. yyy.jar!/mmm/nnn.txt<br>
     * <br>
     * findResource("file:mmm/nnn.txt", com.xxx.XxxTest.class, com.yyy.YyyTest.class)<br>
     * 不管后面的classes参数, 只会查找{classpath}/mmm/nnn.txt<br>
     * 
     * @param resourceLocation 资源位置
     * @param classes 备选查找位置
     * @return 资源URL
     * @throws ResourceNotFoundException 资源不存在
     */
    public static URL findResource(String resourceLocation, Class<?>... classes) throws ResourceNotFoundException {
        Objects.requireNonNull(resourceLocation, "resourceLocation");

        ResourceBean resource = parseResource(resourceLocation);
        String prefix = resource.getPrefix();
        String path = resource.getPath();

        if (prefix == null) { // 相对路径
            // mmm/nnn.txt
            // D:/mmm/nnn.txt(unix)
            return findClasspathResource(path, classes);
        } else if (prefix.equals(CLASSPATH_URL_PREFIX)) {
            // classpath:/mmm/nnn.txt or classpath:mmm/nnn.txt
            String temp = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            return findClasspathResource(temp, classes);
        } else {
            // file:/mmm/nnn.txt or file:mmm/nnn.txt
            // /mmm/nnn.txt
            // D:/mmm/nnn.txt(windows)
            // http://mmm/nnn.txt or ftp://mmm/nnn.txt
            URL url = toUrlInstance(path);
            checkConnect(url);
            return url;
        }
    }

    /**
     * 获取指定资源的相对位置的另一个资源<br>
     * 如果需要获取的资源是绝对路径就返回该路径; 如果是相对路径, 则取指定资源的相对位置
     * 
     * @param url 指定资源
     * @param resourceLocation 需要获取的资源位置
     * @return 资源URL
     */
    public static URL findRelativeResource(URL url, String resourceLocation, Class<?>... classes)
            throws ResourceNotFoundException {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(resourceLocation, "resourceLocation");

        ResourceBean resource = parseResource(resourceLocation);
        String prefix = resource.getPrefix();
        String path = resource.getPath();

        if (prefix == null) { // 相对路径
            // mmm/nnn.txt
            // D:/mmm/nnn.txt(unix)
            URL newurl;
            try {
                // 这种写法不兼容jar:协议
                // url.toURI().resolve(new URI(resourceLocation)).toURL();
                newurl = resolveRelativePath(url, resourceLocation);
            } catch (MalformedURLException | URISyntaxException e) {
                String desc = "Resource location [" + resourceLocation + "] relative to [" + url + "]";
                throw new ResourceNotFoundException(desc + " is not a well-formed path");
            }

            checkConnect(newurl);
            return newurl;
        } else if (prefix.equals(CLASSPATH_URL_PREFIX)) {
            // classpath:/mmm/nnn.txt or classpath:mmm/nnn.txt
            String temp = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            return findClasspathResource(temp, classes);
        } else {
            // file:/mmm/nnn.txt or file:mmm/nnn.txt
            // /mmm/nnn.txt
            // D:/mmm/nnn.txt(windows)
            // http://mmm/nnn.txt or ftp://mmm/nnn.txt
            URL newurl = toUrlInstance(path);
            checkConnect(newurl);
            return newurl;
        }
    }

    private static URL resolveRelativePath(URL url, String path) throws MalformedURLException, URISyntaxException {
        if (isAbsolutePath(path)) {
            throw new IllegalArgumentException("Path [" + path + "] must be relative path");
        }

        String folder = url.toURI().toString();
        String newpath;
        if (folder.endsWith("/") || folder.endsWith("\\")) {
            newpath = concat(true, folder, path);
        } else {
            newpath = concat(true, folder, "..", path);
        }
        return new URL(newpath);
    }

    public static URL resolve(URL url, String path) {
        String desc = "Path [" + path + "] relative to [" + url + "]";
        try {
            return resolveRelativePath(url, path);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException(desc + " is not a well-formed path");
        }
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/" if name is absolute<br>
     * resolveClassResource(com.xxx.abc.Test.class, "mmm/nnn.txt") -- com/xxx/abc/mmm/nnn.txt<br>
     * resolveClassResource(com.xxx.abc.Test.class, "/mmm/nnn.txt") -- mmm/nnn.txt<br>
     */
    private static String resolveClassPath(Class<?> clazz, String path) {
        if (path == null) {
            return path;
        }
        if (!path.startsWith("/")) {
            Class<?> c = clazz;
            while (c.isArray()) {
                c = c.getComponentType();
            }
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                path = baseName.substring(0, index).replace('.', '/') + "/" + path;
            }
        } else {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 获取与指定资源同名但扩展名不同的另一个资源
     * 
     * @param url 指定资源
     * @param extension 另一个资源的扩展名
     * @return 资源URL
     */
    public static URL getSameNameResource(URL url, String extension) throws ResourceNotFoundException {
        String path;
        try {
            path = url.toURI().toString();
        } catch (URISyntaxException e) {
            throw new ResourceNotFoundException(toUriPath(url) + " is not a well-formed path");
        }

        String newpath = replaceExtension(path, extension);
        return toUrlInstance(newpath);
    }

    /**
     * 获取指定类相对位置的资源<br>
     * getClassResource()是查找指定类路径的相对位置的资源<br>
     * findResource()是查找classpath下的资源<br>
     * 如getClassResource(com.xxx.abc.Test.class, "mmm/nnn.txt")会查找以下位置:<br>
     * 1. {classpath}/com/xxx/abc/mmm/nnn.txt<br>
     * 2. jar!/com/xxx/abc/mmm/nnn.txt<br>
     * findResource("mmm/nnn.txt", com.xxx.abc.Test.class)会查找以下位置:<br>
     * 1. {classpath}/mmm/nnn.txt<br>
     * 2. jar!/mmm/nnn.txt<br>
     * 
     * @param resourceLocation 资源位置
     * @param classes 备选查找位置
     * @return 资源URL
     */
    public static URL findClassResource(Class<?> clazz, String resourceLocation) throws ResourceNotFoundException {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(resourceLocation, "resourceLocation");

        ResourceBean resource = parseResource(resourceLocation);
        String prefix = resource.getPrefix();
        String path = resource.getPath();

        if (prefix == null) { // 相对路径
            // mmm/nnn.txt
            // D:/mmm/nnn.txt(unix)

            // com.xxx.abc.Test.class, mmm/nnn.txt --> com/xxx/abc/mmm/nnn.txt
            String temp = resolveClassPath(clazz, path);
            temp = concat(".", temp); // 通过ClassLoader获取, 如果路径是/开头的, 会获取失败
            URL newurl = findClasspathResource(temp, clazz);
            return newurl;
        } else if (prefix.equals(CLASSPATH_URL_PREFIX)) {
            // classpath:/mmm/nnn.txt or classpath:mmm/nnn.txt
            String temp = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            return findClasspathResource(temp, clazz);
        } else {
            // file:/mmm/nnn.txt or file:mmm/nnn.txt
            // /mmm/nnn.txt
            // D:/mmm/nnn.txt(windows)
            // http://mmm/nnn.txt or ftp://mmm/nnn.txt
            URL newurl = toUrlInstance(path);
            checkConnect(newurl);
            return newurl;
        }

    }

    // 计算路径相对于class的实际路径
    private static String getClassResourceRealPath(Class<?> clazz, String path) {
        // clazz.getClassLoader().getResource(""); // 空路径取到的文件夹, 是当前classpath, 而不是clazz的classpath
        String current = toUriPath(clazz.getResource("")); // clazz的文件夹
        // 替换掉clazz的类路径的部分, 回到根文件夹
        String folder = current.replace(resolveClassPath(clazz, ""), "");
        // 再加上资源文件相对于类文件的路径
        return concat(true, folder, path);
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            // getClassLoader() returning null indicates the bootstrap ClassLoader
            try {
                cl = ClassLoader.getSystemClassLoader();
            } catch (Throwable ex) {
                // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
            }
        }
        return cl;
    }

    /** url.toString()对于中文/空格等显示的转义后的%开头的字符, 需要将url显示出来的时候都应该调这个方法 **/
    public static String toUriPath(URL url) {
        try {
            // url.getPath(), 文件路径中有空格的话会出问题, 被替换为%20
            // url.toURI(), 支持文件路径中有空格的情况
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                String p = url.toURI().getPath();
                if ((p.length() > 2) && (p.charAt(2) == ':')) {
                    // "/c:/foo/" --> "c:/foo/"
                    p = p.substring(1);
                }
                return p;
            } else {
                return url.toURI().toString();
            }
        } catch (URISyntaxException e) {
            return url.toString();
        }
    }

    private static URL getClassPathUrl() {
        return ClassLoader.getSystemResource("");
    }

    /**
     * 获取类路径
     *
     * @return 绝对路径
     */
    public static String getClassPath() {
        URL root = getClassPathUrl();
        return toUriPath(root);
    }

    /**
     * 计算输出文件夹路径<br>
     * 如果root不是文件, 则一律返回classpath对应文件夹
     * 
     * @param root 根目录
     * @param outputFolder 相对路径
     * @return 输出文件夹路径
     */
    public static String getOutputFolder(URL root, String outputFolder) {

        if (isAbsolutePath(outputFolder)) {
            String folder = new File(outputFolder).getAbsolutePath();
            return concat(true, folder, "/");
        }
        if ("file".equalsIgnoreCase(root.getProtocol())) {
            return toUriPath(resolve(root, outputFolder));
        }

        URL classpath = getClassPathUrl();

        if ("jar".equalsIgnoreCase(root.getProtocol())) {
            try {
                // jar://file:/D:/xxx/yyy.jar!abc/def/index.txt + ../xyz/code/ = {classpath}/abc/xyz/code/
                String path = root.toURI().getPath();
                int index = path.indexOf('!');
                if (index < 0) {
                    throw new IllegalArgumentException(root + " is not a well-formed jar path");
                }

                URL newroot = resolve(classpath, path.substring(index + 1));
                URL output = resolve(newroot, outputFolder);
                return toUriPath(output);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(root + " is not a well-formed path");
            }
        } else {
            try {
                // http://xxx.com/abc/def/index.html + ../xyz/code/ = {classpath}/abc/xyz/code/
                String path = root.toURI().getPath();

                URL newroot = resolve(classpath, path);
                URL output = resolve(newroot, outputFolder);
                return toUriPath(output);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(root + " is not a well-formed path");
            }
        }
    }

    /**
     * 连接
     *
     * @param folder 文件夹
     * @param paths 文件路径
     * @return
     */
    public static String concat(String folder, String... paths) {
        return concat(false, folder, paths);
    }

    /**
     * 连接
     *
     * @param format 要不要格式化
     * @param folder 文件夹
     * @param paths 文件路径
     * @return
     */
    public static String concat(boolean format, String folder, String... paths) {

        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(folder)) {
            buffer.append(folder);
        }
        for (String path : paths) {
            if (VerifyTools.isBlank(path)) {
                continue;
            }
            if (!endsWithSeparator(buffer) && !startsWithSeparator(path)) {
                buffer.append(SLASH).append(path);
            } else if (endsWithSeparator(folder) && startsWithSeparator(path)) {
                buffer.append(path.substring(1));
            } else {
                buffer.append(path);
            }
        }
        return format ? formatPath(buffer.toString()) : buffer.toString();
    }

    private static boolean endsWithSeparator(CharSequence string) {
        if (string == null || string.length() == 0) return false;
        char lastChar = string.toString().charAt(string.length() - 1);
        return lastChar == SLASH || lastChar == BSLASH;
    }

    private static boolean startsWithSeparator(CharSequence string) {
        if (string == null || string.length() == 0) return false;
        char firstChar = string.toString().charAt(0);
        return firstChar == SLASH || firstChar == BSLASH;
    }

    /**
     * 格式化路径, \替换为/, 并处理../和./的情况<br>
     * 路径作为URL时不能使用\, 但不论windows还是unix, Java环境都支持/<br>
     * 因此将所有的\替换为/<br>
     * 如 \\site\\home\\../include/head.tpl 处理为 /site/include/head.tpl
     *
     * @author zhaohuihua
     * @param path 原路径
     * @return 格式化之后的路径
     */
    public static String formatPath(String path) {
        if (VerifyTools.isBlank(path)) {
            return path;
        }

        StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
        LinkedList<String> list = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            String string = tokenizer.nextToken();
            if (VerifyTools.isBlank(string)) {
                continue;
            } else if (".".equals(string)) {
                continue;
            } else if ("..".equals(string)) {
                if (list.isEmpty()) {
                    continue;
                } else {
                    list.removeLast();
                }
            } else {
                list.addLast(string);
            }
        }

        StringBuilder buffer = new StringBuilder();
        for (String string : list) {
            if (buffer.length() > 0) {
                buffer.append(SLASH);
            } else {
                if (startsWithSeparator(path)) {
                    buffer.append(SLASH);
                }
            }
            buffer.append(string);
        }
        if (endsWithSeparator(path)) {
            buffer.append(SLASH);
        }
        return buffer.toString();
    }

    /**
     * 将path转换为相对于root的路径<br>
     * 不用Files.relativize()是为了兼容URL
     * 
     * <pre>
     * String root = "D:/domain/biz/"
     * relativize(root, "D:/domain/biz/index.html") -- index.html
     * relativize(root, "D:/domain/biz/html/homepage.html") -- html/homepage.html
     * relativize(root, "D:/domain/assets/libs/mui/mui.js") -- ../assets/libs/mui/mui.js
     * relativize(root, "D:/static/assets/libs/mui/mui.js") -- ../../static/assets/libs/mui/mui.js
     * </pre>
     * 
     * @param root 根路径
     * @param path 绝对路径
     * @return 相对路径
     */
    public static String relativize(String root, String path) {

        String[] roots = splitPath(root);
        String[] paths = splitPath(path);

        // 先找到第一个不相同的文件夹
        int len = Math.min(roots.length, paths.length);
        int idx = len;
        for (int i = 0; i < len; i++) {
            if (!roots[i].equals(paths[i])) {
                idx = i;
                break;
            }
        }
        StringBuilder buffer = new StringBuilder();
        // 从第一个不相同的开始, roots还有几级就加几个../
        for (int i = idx; i < roots.length; i++) {
            buffer.append("..").append("/");
        }
        // 从第一个不相同的开始, 加上paths剩下的路径
        for (int i = idx; i < paths.length; i++) {
            if (i > idx) {
                buffer.append("/");
            }
            buffer.append(paths[i]);
        }
        return buffer.toString();
    }

    private static Pattern separator = Pattern.compile("/+");
    private static Pattern trim = Pattern.compile("^/+|/+$");

    private static String[] splitPath(String path) {
        path = formatPath(path);
        path = trim.matcher(path).replaceAll("");
        return separator.split(path);
    }

    /** 判断是不是绝对路径 **/
    public static boolean isAbsolutePath(String path) {
        // 绝对路径: unix的/home/或window的D:/xxx/或http://url
        if (path == null || path.length() == 0) return false;

        char[] chars = path.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == SLASH || chars[i] == BSLASH) {
                if (i == 0) { // /mmm/nnn.txt
                    return true;
                } else { // mmm/nnn.txt
                    return false;
                }
            } else if (chars[i] == ':') {
                if (i == 0) { // :/mmm/nnn.txt ?
                    return false;
                } else if (i == 1) { // D:/mmm/nnn.txt -- windows是绝对路径, unix是相对路径
                    return new File(path).isAbsolute();
                } else { // http://mmm/nnn.txt or ftp://mmm/nnn.txt
                    return true;
                }
            }
        }
        // nnn.txt
        return false;
    }

    /** 资源信息类 **/
    protected static class ResourceBean implements Serializable {

        /** SerialVersionUID **/
        private static final long serialVersionUID = 1L;

        /** 前缀, 如file:|http:|classpath:, 带冒号 **/
        private String prefix;
        /** 路径, 带前缀 **/
        private String path;

        public ResourceBean() {
        }

        public ResourceBean(String path) {
            this.prefix = null;
            this.path = path;
        }

        public ResourceBean(String prefix, String path) {
            this.prefix = prefix;
            this.path = path;
        }

        /** 前缀, 如file:|http:|classpath:, 带冒号 **/
        public String getPrefix() {
            return prefix;
        }

        /** 前缀, 如file:|http:|classpath:, 带冒号 **/
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /** 路径, 带前缀 **/
        public String getPath() {
            return path;
        }

        /** 路径, 带前缀 **/
        public void setPath(String path) {
            this.path = path;
        }

    }
}
