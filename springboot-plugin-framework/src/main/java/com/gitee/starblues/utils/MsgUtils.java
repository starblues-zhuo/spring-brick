package com.gitee.starblues.utils;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.slf4j.Logger;

/**
 * @author starBlues
 * @version 3.0.0
 */
public abstract class MsgUtils {

    private MsgUtils(){}

    public static String getPluginUnique(PluginDescriptor pluginDescriptor){
        return pluginDescriptor.getPluginId() + "@" + pluginDescriptor.getPluginVersion();
    }

    public static String getPluginUnique(String pluginId, String version){
        if(ObjectUtils.isEmpty(version)){
            return pluginId;
        }
        return pluginId + "@" + version;
    }

    public static String getThrowableMsg(Throwable throwable){
        return ObjectUtils.isEmpty(throwable.getMessage()) ? "" : throwable.getMessage();
    }

    public static String getThrowableMsg(String message){
        return ObjectUtils.isEmpty(message) ? "" : message;
    }

    public static void info(Logger logger, PluginDescriptor pluginDescriptor, String msg){
        logger.info("插件[{}]{}", getPluginUnique(pluginDescriptor), msg);
    }

}
