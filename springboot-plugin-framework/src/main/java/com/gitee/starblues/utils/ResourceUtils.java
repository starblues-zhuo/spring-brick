package com.gitee.starblues.utils;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * 对资源解析的工具类
 *
 * @author starBlues
 * @version 2.4.4
 */
public class ResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    public final static String TYPE_FILE = "file";
    public final static String TYPE_CLASSPATH = "classpath";
    public final static String TYPE_PACKAGE = "package";

    public static final String ROOT_PLUGIN_SIGN = "~";

    public final static String TYPE_SPLIT = ":";

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    public static final String PACKAGE_SPLIT = "/";

    /**
     * 获取匹配路绝
     * @param locationMatch 原始匹配路径。规则为: file:xxx, classpath:xxx , package:xxx
     * @return 整合出完整的匹配路绝
     */
    public static String getMatchLocation(String locationMatch){
        if(StringUtils.isNullOrEmpty(locationMatch)){
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
        return locationMatch.startsWith(TYPE_CLASSPATH + TYPE_SPLIT);
    }

    public static boolean isFile(String locationMatch){
        return locationMatch.startsWith(TYPE_FILE + TYPE_SPLIT);
    }

    public static boolean isPackage(String locationMatch){
        return locationMatch.startsWith(TYPE_PACKAGE + TYPE_SPLIT);
    }

    /**
     * 根据 ~ 标记获取, 得到绝对路径
     * @param pluginRegistryInfo pluginRegistryInfo
     * @param rootDir 根目录
     * @return java.lang.String
     **/
    public static String getAbsolutePath(PluginRegistryInfo pluginRegistryInfo, String rootDir){
        if(StringUtils.isNullOrEmpty(rootDir)){
            return rootDir;
        }
        String home = null;
        if(rootDir.startsWith(ResourceUtils.ROOT_PLUGIN_SIGN)){
            String pluginRootDir;
            PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
            RuntimeMode runtimeMode = pluginWrapper.getRuntimeMode();
            if(runtimeMode == RuntimeMode.DEVELOPMENT){
                pluginRootDir = pluginWrapper.getPluginPath().toString();
            } else {
                pluginRootDir = System.getProperty("user.dir");
            }
            // 如果root路径中开始存在ROOT_PLUGIN_SIGN,则说明进行插件根路替换
            home = rootDir.replaceFirst("\\" + ResourceUtils.ROOT_PLUGIN_SIGN, "");
            home = CommonUtils.joiningFilePath(pluginRootDir, home);
        } else {
            home = rootDir;
        }
        return home;
    }


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
