package com.gitee.starblues.utils;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * @author starBlues
 * @version 3.0.0
 */
public abstract class MsgUtils {

    private MsgUtils(){}

    public static String getPluginUnique(PluginDescriptor pluginDescriptor){
        return pluginDescriptor.getPluginId() + "@" + pluginDescriptor.getPluginVersion();
    }

    public static String getThrowableMsg(Throwable throwable){
        return ObjectUtils.isEmpty(throwable.getMessage()) ? "" : throwable.getMessage();
    }

    public static String getThrowableMsg(String message){
        return ObjectUtils.isEmpty(message) ? "" : message;
    }

}
