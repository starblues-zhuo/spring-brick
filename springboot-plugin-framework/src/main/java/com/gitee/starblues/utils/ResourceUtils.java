package com.gitee.starblues.utils;

import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 对资源解析的工具类
 *
 * @author starBlues
 * @version 2.2.3
 */
public class ResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    public final static String TYPE_FILE = "file";
    public final static String TYPE_CLASSPATH = "classpath";
    public final static String TYPE_PACKAGE = "package";

    public final static String TYPE_SPLIT = ":";

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


}
