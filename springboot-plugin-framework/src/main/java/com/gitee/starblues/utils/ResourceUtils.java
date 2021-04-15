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


    /**
     * 获取匹配路绝
     * @param locationMatch 原始匹配路径。规则为: file:xxx, classpath:xxx , package:xxx
     * @return 整合出完整的匹配路绝
     */
    public static String getMatchLocation(String locationMatch){
        if(StringUtils.isNullOrEmpty(locationMatch)){
            return null;
        }
        String[] split = locationMatch.split(":");
        if(split.length != 2){
            return null;
        }
        String type = split[0];
        String location = split[1];
        if(TYPE_CLASSPATH.equalsIgnoreCase(type) || TYPE_FILE.equalsIgnoreCase(type)){
            return locationMatch;
        } else if(TYPE_PACKAGE.equalsIgnoreCase(type)){
            return location.replace(".", "/");
        } else {
            LOGGER.error("locationMatch {} illegal", locationMatch);
            return null;
        }
    }

}
