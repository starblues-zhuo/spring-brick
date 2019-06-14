package com.plugin.development.exception;

/**
 * 插件安装异常
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginPlugException extends Exception{


    public PluginPlugException() {
        super();
    }

    public PluginPlugException(String message) {
        super(message);
    }

    public PluginPlugException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginPlugException(Throwable cause) {
        super(cause);
    }

    protected PluginPlugException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
