package com.gitee.qdbp.tools.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ResourceNotFoundException;
import com.gitee.qdbp.able.matches.AntFileMatcher;
import com.gitee.qdbp.able.matches.FileMatcher;
import com.gitee.qdbp.able.matches.FileMatcher.Target;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 路径处理工具类<br>
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
        if (i < 0 || i < path.lastIndexOf('/') || i < path.lastIndexOf('\\')) {
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
     * @param path 原始路径
     * @return 新路径
     */
    public static String removeExtension(String path) {
        if (path == null) {
            return null;
        }

        int i = path.lastIndexOf('.');
        if (i < 0 || i < path.lastIndexOf('/') || i < path.lastIndexOf('\\')) {
            return path;
        }

        return path.substring(0, i);
    }

    /**
     * 替换文件扩展名<br>
     * /image/abc.def.png, .jpg --&gt; /image/abc.def.jpg<br>
     * /image/abcdef/xxx, .jpg --&gt; /image/abcdef/xxx.jpg<br>
     * /image/abc.def/xxx, .jpg --&gt; /image/abc.def/xxx.jpg<br>
     *
     * @param path 原始路径
     * @return 新路径
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

    /**
     * 清除文件名, 只保留路径<br>
     * /image/abc.def.png, .jpg --&gt; /image/<br>
     * /image/abcdef/png --&gt; /image/abcdef/<br>
     * /image/abc.def/png --&gt; /image/abc.def/<br>
     * /image --&gt; "/"<br>
     * image --&gt; ""<br>
     * 
     * @param path 原始路径
     * @return 不带文件名的路径
     */
    public static String removeFileName(String path) {
        if (path == null) {
            return null;
        }

        int i = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return i < 0 ? "" : i == 0 ? "/" : path.substring(0, i + 1);
    }

    /**
     * 获取文件名, 即获取最后的路径分隔符之后的字符串<br>
     * /image/abc.def.png, .jpg --&gt; abc.def.png<br>
     * /image/abcdef/png --&gt; png<br>
     * /image/abc.def/png --&gt; png<br>
     * /image --&gt; image<br>
     * image --&gt; image<br>
     * /image/ --&gt; ""<br>
     * 
     * @param path 原始路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        if (path == null) {
            return null;
        }
        int i = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return i < 0 ? path : path.substring(i + 1);
    }

    /**
     * 替换文件名<br>
     * /image/abc.def.png, new.jpg --&gt; /image/new.jpg<br>
     * /image/abcdef/xxx, yyy --&gt; /image/abcdef/yyy<br>
     * /image/abc.def/, yyy.jpg --&gt; /image/abc.def/yyy.jpg<br>
     * /xxx.jpg, yyy.jpg --&gt; /yyy.jpg<br>
     * xxx.jpg, yyy.jpg --&gt; yyy.jpg<br>
     *
     * @param path 原始路径
     * @param fileName 新的文件名
     * @return 新的文件路径
     */
    public static String replaceFileName(String path, String fileName) {
        String newpath = removeFileName(path);
        if (newpath == null || fileName == null) {
            return newpath;
        } else {
            return concat(newpath, fileName);
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
        File pathToFile = new File(path);
        if (pathToFile.exists()) {
            // 如果根据文件路径能直接获取到文件, 则直接返回此文件(一般是命令行模式)
            URI uri = pathToFile.toURI();
            try {
                return uri.toURL();
            } catch (MalformedURLException e) {
                String desc = "Resource location [" + uri.toString() + "]";
                throw new ResourceNotFoundException(desc + " is not a well-formed path");
            }
        }

        Set<String> locations = new LinkedHashSet<>();
        String temp = path;
        if (path.startsWith("/") || path.startsWith("\\")) {
            temp = "." + path; // 通过ClassLoader获取, 如果路径是/开头的, 会获取失败
        }
        URL resource = getClassLoaderResource(temp);
        if (resource != null) {
            return resource;
        }

        URL root = getClassPathUrl();
        locations.add(toUriPath(resolve(root, temp)));
        for (Class<?> clazz : VerifyTools.nvl(classes, new Class<?>[0])) {
            // URL url = clazz.getResource(temp); // jar包不在classpath下时会获取失败
            String classResourcePath = getClassResourceRealPath(clazz, temp);
            URL url = toUrlInstance(classResourcePath);
            try {
                checkConnect(url);
                return url;
            } catch (ResourceNotFoundException e) {
                locations.add(toUriPath(url));
                continue;
            }
        }

        String desc = "Resource location [" + path + "]";
        throw new ResourceNotFoundException(desc + " does not found. Found in " + locations);
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
     * 查找所有classpath或jar包中, 指定文件夹下的资源
     * 
     * @param folder 文件夹
     * @param filter 匹配规则
     * @return 资源列表
     * @throws ResourceNotFoundException 资源查找失败, 资源不存在
     */
    public static List<URL> scanResources(String folder, String filter) throws ResourceNotFoundException {
        FileMatcher matcher = new AntFileMatcher(filter, Target.FileName);
        return scanResources(folder, matcher);
    }

    /**
     * 查找所有classpath或jar包中, 指定文件夹下的资源
     * 
     * @param folder 文件夹
     * @param matcher 匹配规则
     * @return 资源列表
     * @throws ResourceNotFoundException 资源查找失败, 资源不存在
     */
    public static List<URL> scanResources(String folder, FileMatcher matcher) throws ResourceNotFoundException {
        Enumeration<URL> roots;
        try {
            roots = getClassLoaderResources(folder);
        } catch (IOException e) {
            String desc = "Failed to scan resource in [" + folder + "], " + matcher.toString();
            throw new ResourceNotFoundException(desc + ", " + e.getMessage(), e);
        }
        List<URL> urls = new ArrayList<>();
        while (roots.hasMoreElements()) {
            URL url = roots.nextElement();
            if ("file".equalsIgnoreCase(url.getProtocol())) {
                String rootFolder = toUriPath(url);
                List<File> files = FileTools.treelist(rootFolder, matcher);
                for (File file : files) {
                    try {
                        urls.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        String desc = "Resource location [" + formatPath(file.getAbsolutePath()) + "]";
                        throw new ResourceNotFoundException(desc + " is not a well-formed path");
                    }
                }
            } else if ("jar".equalsIgnoreCase(url.getProtocol())) {
                // jar:file:/E:/repository/qdbp-jdbc-core-3.0.0.jar!/settings/
                JarFile jar;
                try {
                    jar = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch (IOException e) {
                    throw new ResourceNotFoundException("Failed to open resource [" + url + "]", e);
                }
                String folderPath = PathTools.concat(true, "/", folder, "/");
                String rootPrefix = StringTools.removeSuffix(url.toString(), folderPath);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.isDirectory()) {
                        continue;
                    }
                    // entry.getName()是相对路径, 如: settings/spring/qdbc.xml
                    String entryPath = entry.getName();
                    String filePath = PathTools.concat("/", entryPath);
                    if (!filePath.startsWith(folderPath)) {
                        continue;
                    }
                    File file = new File(filePath);
                    if (matcher.matches(file)) {
                        try {
                            // jar:file:/E:/repository/qdbp-jdbc-core-3.0.0.jar!/settings/spring/qdbc.xml
                            urls.add(new URL(PathTools.concat(rootPrefix, entryPath)));
                        } catch (MalformedURLException e) {
                            String desc = "Resource location [" + formatPath(file.getAbsolutePath()) + "]";
                            throw new ResourceNotFoundException(desc + " is not a well-formed path");
                        }
                    }
                }
            }
        }
        return urls;
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
        VerifyTools.requireNotBlank(resourceLocation, "resourceLocation");

        ResourceBean resource = parseResource(resourceLocation);
        String prefix = resource.getPrefix();
        String path = resource.getPath();

        if (prefix == null) { // 相对路径
            // mmm/nnn.txt
            // D:/mmm/nnn.txt(unix)
            File pathToFile = new File(path);
            if (pathToFile.exists()) {
                // 如果根据文件路径能直接获取到文件, 则直接返回此文件(一般是命令行模式)
                URI uri = pathToFile.toURI();
                try {
                    return uri.toURL();
                } catch (MalformedURLException e) {
                    String desc = "Resource location [" + uri.toString() + "]";
                    throw new ResourceNotFoundException(desc + " is not a well-formed path");
                }
            } else {
                return findClasspathResource(path, classes);
            }
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
        VerifyTools.requireNotBlank(url, "url");
        VerifyTools.requireNotBlank(resourceLocation, "resourceLocation");

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
            } catch (MalformedURLException e) {
                String desc = "Resource location [" + resourceLocation + "] relative to [" + url + "]";
                throw new ResourceNotFoundException(desc + " is not a well-formed path");
            } catch (URISyntaxException e) {
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

    /**
     * 计算path相对于url的路径, 返回绝对路径<br>
     * 如: resolve("http://xxx.com/a-module/page.html", "../b-module/index.html")<br>
     * 返回: http://xxx.com/b-module/index.html
     * 
     * @param url 基准URL路径
     * @param path 相对路径
     * @return 绝对路径
     */
    public static URL resolve(URL url, String path) {
        String desc = "Path [" + path + "] relative to [" + url + "]";
        try {
            return resolveRelativePath(url, path);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(desc + " is not a well-formed path");
        } catch (URISyntaxException e) {
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
     * findClassResource()是查找指定类路径的相对位置的资源<br>
     * findResource()是查找classpath下的资源<br>
     * 如findClassResource(com.xxx.abc.Test.class, "mmm/nnn.txt")会查找以下位置:<br>
     * 1. {classpath}/com/xxx/abc/mmm/nnn.txt<br>
     * 2. jar!/com/xxx/abc/mmm/nnn.txt<br>
     * findResource("mmm/nnn.txt", com.xxx.abc.Test.class)会查找以下位置:<br>
     * 1. {classpath}/mmm/nnn.txt<br>
     * 2. jar!/mmm/nnn.txt<br>
     * 
     * @param resourceLocation 资源位置
     * @param clazz 指定类
     * @return 资源URL
     */
    public static URL findClassResource(Class<?> clazz, String resourceLocation) throws ResourceNotFoundException {
        VerifyTools.requireNotBlank(clazz, "clazz");
        VerifyTools.requireNotBlank(resourceLocation, "resourceLocation");

        ResourceBean resource = parseResource(resourceLocation);
        String prefix = resource.getPrefix();
        String path = resource.getPath();

        if (prefix == null) { // 相对路径
            // mmm/nnn.txt
            // D:/mmm/nnn.txt(unix)

            // com.xxx.abc.Test.class, mmm/nnn.txt --> com/xxx/abc/mmm/nnn.txt
            String temp = resolveClassPath(clazz, path);
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

    /** 计算路径相对于class的实际路径 **/
    // clazz.getClassLoader().getResource("");
    // -- ClassLoader空路径取到的文件夹, 是当前环境classpath, 而不是clazz的classpath
    // clazz.getResource("");
    // -- 在jar(zip)体系中:文件是文件,文件夹是文件夹,很多开源的jar包并没有把文件夹打进去, 会返回null的
    // -- 解决办法就是用clazz.getResource(clazz.getSimpleName() + ".class")来clazz所在的路径
    // -- see https://blog.csdn.net/sunyujia/article/details/2957481
    // 假定:
    // -- clazz = com.package.SimpleName, path = settings/xxx.txt
    // A -- clazz位于D:/path/file.jar
    // B -- clazz位于D:/path/classpath/
    private static String getClassResourceRealPath(Class<?> clazz, String path) {
        // 当前类的文件名
        // -- SimpleName.class
        String simpleName = clazz.getSimpleName() + ".class";
        // 当前class的路径
        // A -- jar:file:/D:/path/file.jar!/com/package/SimpleName.class
        // B -- file:/D:/path/classpath/com/package/SimpleName.class
        String fullPath = clazz.getResource(simpleName).toString();
        // 当前类的全名
        // -- com.package.SimpleName.class
        String fullName = clazz.getName() + ".class";
        // 去掉clazz的类路径的部分, 回到根文件夹
        // A -- jar:file:/D:/path/file.jar!/
        // B -- file:/D:/path/classpath/
        String folder = fullPath.substring(0, fullPath.length() - fullName.length());
        // 再加上资源文件相对于类文件的路径
        // A -- jar:file:/D:/path/file.jar!/settings/xxx.txt
        // B -- file:/D:/path/classpath/settings/xxx.txt
        return concat(folder, formatPath(path));
    }

    /** url.toString()对于空格会显示为%20, 需要将url转换为文件路径或将url显示出来的时候都应该调这个方法 **/
    // 中文没有问题, 只是空格需要转换
    // URL url = new File("D:/abc def/中文.txt").toURI().toURL();
    // url.toString() --> url.toURI().toString() --> file:/D:/abc%20def/中文.txt
    // url.toURI().getPath() --> /D:/abc def/中文.txt
    public static String toUriPath(URL url) {
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            try {
                // url.toString(), url.getPath(), 文件路径中有空格会显示为%20
                // url.toURI().getPath(), 支持文件路径中有空格的情况
                String p = url.toURI().getPath();
                if ((p.length() > 2) && (p.charAt(2) == ':')) {
                    // "/c:/foo/" --> "c:/foo/"
                    p = p.substring(1);
                }
                return p;
            } catch (URISyntaxException e) {
                return url.toString();
            }
        } else {
            return url.toString(); // url.toURI().toString();
        }
    }

    private static URL getClassLoaderResource(String path) {
        ClassLoader cl = getDefaultClassLoader();
        String temp = path;
        if (path.startsWith("/") || path.startsWith("\\")) {
            temp = "." + path; // 通过ClassLoader获取, 如果路径是/开头的, 会获取失败
        }
        return cl != null ? cl.getResource(temp) : ClassLoader.getSystemResource(temp);
    }

    private static Enumeration<URL> getClassLoaderResources(String path) throws IOException {
        ClassLoader cl = getDefaultClassLoader();
        String temp = path;
        if (path.startsWith("/") || path.startsWith("\\")) {
            temp = "." + path; // 通过ClassLoader获取, 如果路径是/开头的, 会获取失败
        }
        return cl != null ? cl.getResources(temp) : ClassLoader.getSystemResources(temp);
    }

    private static URL getClassPathUrl() {
        ClassLoader cl = getDefaultClassLoader();
        return cl != null ? cl.getResource("") : ClassLoader.getSystemResource("");
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
     * 获取绝对路径文件
     * 
     * @param path 文件路径
     * @param paths 文件路径列表
     * @return 绝对路径文件
     */
    public static String getAbsoluteFile(String path, String... paths) {
        return PathTools.concat(true, new File(PathTools.concat(true, path, paths)).getAbsolutePath());
    }

    /**
     * 获取绝对路径文件夹
     * 
     * @param path 文件路径
     * @param paths 文件路径列表
     * @return 绝对路径文件夹
     */
    public static String getAbsoluteFolder(String path, String... paths) {
        return PathTools.concat(true, new File(PathTools.concat(true, path, paths)).getAbsolutePath(), "/");
    }

    /**
     * 连接
     *
     * @param folder 文件夹
     * @param paths 文件路径
     * @return 连接后的路径
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
     * @return 连接后的路径
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
            if (buffer.length() == 0) {
                buffer.append(path);
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
        if (string == null || string.length() == 0) {
            return false;
        }
        char lastChar = string.toString().charAt(string.length() - 1);
        return lastChar == SLASH || lastChar == BSLASH;
    }

    private static boolean startsWithSeparator(CharSequence string) {
        if (string == null || string.length() == 0) {
            return false;
        }
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
     * 判断路径是否超出范围(用于检查拼接的路径是否会超出限定范围)<br>
     * 例如: String serverPaht = PathTools.concat(rootPath, relativePath)<br>
     * 此时如果relativePath超出范围就是一个安全隐患<br>
     * <br>
     * isPathOutOfBounds("../ccc/ddd.txt") -- true<br>
     * isPathOutOfBounds("aaa/bbb/../ccc/ddd.txt") -- false<br>
     * isPathOutOfBounds("aaa/bbb/../../ccc/ddd.txt") -- false<br>
     * isPathOutOfBounds("aaa/bbb/../../../ccc/ddd.txt") -- true<br>
     * 
     * @param path 目标路径
     * @return 是否超出范围
     */
    public static boolean isPathOutOfBounds(String path) {
        return formatPath(path).startsWith("../");
    }

    /**
     * 将path转换为相对于root的路径<br>
     * 不用Path.relativize()是为了兼容URL
     * 
     * <pre>
     * String root = "D:/domain/biz/";
     * relativize(root, "D:/domain/biz/index.html"); // index.html
     * relativize(root, "D:/domain/biz/html/homepage.html"); // html/homepage.html
     * relativize(root, "D:/domain/assets/libs/mui/mui.js"); // ../assets/libs/mui/mui.js
     * relativize(root, "D:/static/assets/libs/mui/mui.js"); // ../../static/assets/libs/mui/mui.js
     * relativize(root, "D:/domain/biz/"); // ./
     * relativize(root, "D:/domain/biz/html/"); // html/
     * relativize("", "home/index.html"); // home/index.html
     * </pre>
     * 
     * @param root 根路径, <b>如果是文件夹, 必须以/结尾</b>
     * @param path 绝对路径
     * @return 相对路径
     */
    public static String relativize(String root, String path) {

        String[] roots = splitPath(removeFileName(root));
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
        if (path.trim().endsWith("/") || path.trim().endsWith("\\")) {
            if (buffer.length() == 0) {
                buffer.append("./");
            } else if (!buffer.toString().endsWith("/")) {
                buffer.append("/");
            }
        }
        return buffer.toString();
    }

    private static Pattern separator = Pattern.compile("/+");
    private static Pattern trim = Pattern.compile("^\\s*/+|/+\\s*$");

    private static String[] splitPath(String path) {
        path = formatPath(path);
        path = trim.matcher(path).replaceAll("");
        if (VerifyTools.isBlank(path)) {
            return new String[0];
        } else {
            return separator.split(path);
        }
    }

    /** 判断是不是绝对路径 **/
    public static boolean isAbsolutePath(String path) {
        // 绝对路径: unix的/home/或window的D:/xxx/或http://url
        if (path == null || path.length() == 0) {
            return false;
        }

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

    /**
     * 通过网络下载文件
     *
     * @param url URL
     * @return 文件内容
     * @throws IOException 失败
     */
    public static byte[] download(String url) throws IOException {
        return download(new URL(url));
    }

    /**
     * 通过网络下载文件
     *
     * @param url URL
     * @return 文件内容
     * @throws IOException 失败
     */
    public static byte[] download(URL url) throws IOException {
        try (InputStream input = url.openStream(); ByteArrayOutputStream output = new ByteArrayOutputStream();) {
            // Files.copy不支持HTTP协议
            // Files.copy(Paths.get(URI.create(url)), output);
            FileTools.copy(input, output);
            return output.toByteArray();
        }
    }

    /**
     * 通过网络下载文件<br>
     * 将会自动识别文本编码格式
     *
     * @param url URL
     * @return 文件内容
     * @throws IOException 失败
     */
    public static String downloadString(String url) throws IOException {
        return downloadString(url, "UTF-8");
    }

    /**
     * 通过网络下载文件<br>
     * 将会自动识别文本编码格式
     *
     * @param url URL
     * @param defaultCharset 默认的字符编码格式(识别失败时使用)
     * @return 文件内容
     * @throws IOException 失败
     */
    public static String downloadString(String url, String defaultCharset) throws IOException {
        return downloadString(new URL(url), defaultCharset);
    }

    /**
     * 通过网络下载文件<br>
     * 将会自动识别文本编码格式
     *
     * @param url URL
     * @return 文件内容
     * @throws IOException 失败
     */
    public static String downloadString(URL url) throws IOException {
        return downloadString(url, "UTF-8");
    }

    /**
     * 通过网络下载文件<br>
     * 将会自动识别文本编码格式
     *
     * @param url URL
     * @param defaultCharset 默认的字符编码格式(识别失败时使用)
     * @return 文件内容
     * @throws IOException 失败
     */
    public static String downloadString(URL url, String defaultCharset) throws IOException {
        return FileTools.bytesToString(download(url), Charset.forName(defaultCharset));
    }

    /**
     * 通过网络下载文件
     *
     * @param url URL
     * @param saveAs 保存的文件路径
     * @throws IOException 失败
     */
    public static void downloadSave(String url, String saveAs) throws IOException {
        downloadSave(new URL(url), saveAs);
    }

    /**
     * 通过网络下载文件
     *
     * @param url URL
     * @param saveAs 保存的文件路径
     * @throws IOException 失败
     */
    public static void downloadSave(URL url, String saveAs) throws IOException {
        try (InputStream input = url.openStream(); OutputStream output = new FileOutputStream(new File(saveAs))) {
            // Files.copy不支持HTTP协议
            // Files.copy(Paths.get(URI.create(url)), output);
            FileTools.copy(input, output);
        }
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
