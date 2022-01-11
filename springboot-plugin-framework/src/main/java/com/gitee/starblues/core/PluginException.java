package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.MsgUtils;


/**
 * 插件异常
 * @author starBlues
 * @version 3.0.0
 */
public class PluginException extends RuntimeException{

    public PluginException() {
        super();
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(String opType, PluginDescriptor pluginDescriptor, Throwable cause) {
        this(opType, MsgUtils.getPluginUnique(pluginDescriptor), cause);
    }

    public PluginException(String opType, PluginDescriptor pluginDescriptor, String message) {
        this(opType, MsgUtils.getPluginUnique(pluginDescriptor), message);
    }

    public PluginException(String opType, String pluginId, Throwable cause) {
        super(opType + "插件[" + pluginId + "]失败. " + MsgUtils.getThrowableMsg(cause), cause);
    }

    public PluginException(String opType, String pluginId, String message) {
        super(opType + "插件[" + pluginId + "]失败. " + MsgUtils.getThrowableMsg(message));
    }


}
