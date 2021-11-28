package com.gitee.starblues.core;

import com.gitee.starblues.utils.ObjectUtils;

/**
 * 插件异常
 * @author starBlues
 * @version 3.0.03
 */
public class PluginException extends Exception{

    public PluginException() {
        super();
    }

    public PluginException(String message) {
        this(null, message);
    }

    public PluginException(String message, Throwable cause) {
        this(null, message, cause);
    }

    public PluginException(String pluginId, String message) {
        this(null, message, null);
    }

    public PluginException(String pluginId, String message, Throwable cause) {
        super(ObjectUtils.isEmpty(pluginId) ? message : "插件[" + pluginId + "]操作异常. " + message,
                cause);
    }


}
