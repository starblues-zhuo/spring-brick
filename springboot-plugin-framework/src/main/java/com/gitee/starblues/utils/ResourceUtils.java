package com.gitee.starblues.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 对资源解析的工具类
 *
 * @author starBlues
 * @version 2.4.4
 */
public class ResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    public static final String URL_PROTOCOL_FILE = "file";

    public final static String TYPE_FILE = "file";
    public final static String TYPE_CLASSPATH = "classpath";
    public final static String TYPE_PACKAGE = "package";

    public static final String ROOT_PLUGIN_SIGN = "~";

    public final static String TYPE_SPLIT = ":";

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    public static final String PACKAGE_SPLIT = "/";

    public static final String JAR_FILE_EXTENSION = ".jar";
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String CLASS_FILE_EXTENSION = ".class";
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    public static final String URL_PROTOCOL_VFS = "vfs";

    /**
     * 获取匹配路绝
     * @param locationMatch 原始匹配路径。规则为: file:xxx, classpath:xxx , package:xxx
     * @return 整合出完整的匹配路绝
     */
    public static String getMatchLocation(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return null;
        }
        String classPathType = TYPE_CLASSPATH + TYPE_SPLIT;
        if(isClasspath(locationMatch)){
            return locationMatch.replaceFirst(classPathType, "");
        }
        String fileType = TYPE_FILE + TYPE_SPLIT;
        if(isFile(locationMatch)){
            return locationMatch.replaceFirst(fileType, "");
        }
        String packageType = TYPE_PACKAGE + TYPE_SPLIT;
        if(isPackage(locationMatch)){
            String location = locationMatch.replaceFirst(packageType, "");
            return location.replace(".", "/");
        }
        LOGGER.error("locationMatch {} illegal", locationMatch);
        return null;
    }

    public static boolean isClasspath(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_CLASSPATH + TYPE_SPLIT);
    }

    public static boolean isFile(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_FILE + TYPE_SPLIT);
    }

    public static boolean isPackage(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_PACKAGE + TYPE_SPLIT);
    }

    public static boolean isClass(String path){
        if(ObjectUtils.isEmpty(path)){
            return false;
        }
        return path.toLowerCase().endsWith(CLASS_FILE_EXTENSION);
    }

    /**
     * 是否为 zip 文件
     * @param path 文件路径
     * @return boolean
     */
    public static boolean isZipFile(Path path) {
        return Files.isRegularFile(path) && isZip(path.toString());
    }

    /**
     * 是否为 jar 文件
     * @param path 文件路径
     * @return boolean
     */
    public static boolean isJarFile(Path path) {
        return Files.isRegularFile(path) && isJar(path.toString());
    }

    /**
     * 是否为 zip 文件
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isZip(String name) {
        return name != null && name.toLowerCase().endsWith(ZIP_FILE_EXTENSION);
    }

    /**
     * 是否为 jar 文件
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isJar(String name) {
        return name != null && name.toLowerCase().endsWith(JAR_FILE_EXTENSION);
    }

    public static boolean isDirFile(Path path) {
        return path.toFile().isDirectory();
    }

    public static boolean isJarFileURL(URL url) {
        return (URL_PROTOCOL_FILE.equals(url.getProtocol()) &&
                url.getPath().toLowerCase().endsWith(JAR_FILE_EXTENSION));
    }

    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }


    public static boolean existFile(File rootFile, String checkFileName){
        if(rootFile == null || !rootFile.exists()){
            return false;
        }
        final File[] listFiles = rootFile.listFiles();
        if(listFiles == null || listFiles.length == 0){
            return false;
        }
        for (File listFile : listFiles) {
            if(Objects.equals(listFile.getName(), checkFileName)){
                return true;
            }
            if(listFile.isDirectory()){
                return existFile(listFile, checkFileName);
            }
        }
        return false;
    }

    public static void listFile(File rootFile, Consumer<File> consumerFile){
        if(rootFile == null || !rootFile.exists()){
            return;
        }
        final File[] listFiles = rootFile.listFiles();
        if(listFiles == null || listFiles.length == 0){
            return;
        }
        for (File listFile : listFiles) {
            if(listFile.isDirectory()){
                listFile(listFile, consumerFile);
                continue;
            }
            consumerFile.accept(listFile);
        }
    }


//    /**
//     * 根据 ~ 标记获取, 得到绝对路径
//     * @param pluginRegistryInfo pluginRegistryInfo
//     * @param rootDir 根目录
//     * @return java.lang.String
//     **/
//    public static String getAbsolutePath(PluginRegistryInfo pluginRegistryInfo, String rootDir){
//        if(StringUtils.isNullOrEmpty(rootDir)){
//            return rootDir;
//        }
//        String home = null;
//        if(rootDir.startsWith(ResourceUtils.ROOT_PLUGIN_SIGN)){
//            String pluginRootDir;
//            PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
//            RuntimeMode runtimeMode = pluginWrapper.getRuntimeMode();
//            if(runtimeMode == RuntimeMode.DEVELOPMENT){
//                pluginRootDir = pluginWrapper.getPluginPath().toString();
//            } else {
//                pluginRootDir = System.getProperty("user.dir");
//            }
//            // 如果root路径中开始存在ROOT_PLUGIN_SIGN,则说明进行插件根路替换
//            home = rootDir.replaceFirst("\\" + ResourceUtils.ROOT_PLUGIN_SIGN, "");
//            home = CommonUtils.joiningFilePath(pluginRootDir, home);
//        } else {
//            home = rootDir;
//        }
//        return home;
//    }


    public static boolean isUrl(String resourceLocation) {
        if (resourceLocation == null) {
            return false;
        }
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            return true;
        }
        try {
            new URL(resourceLocation);
            return true;
        }
        catch (MalformedURLException ex) {
            return false;
        }
    }

}
