package com.gitee.starblues.exception;

/**
 * 插件工厂异常
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginFactoryException extends Exception{

    public PluginFactoryException() {
        super();
    }

    public PluginFactoryException(String message) {
        super(message);
    }

    public PluginFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginFactoryException(Throwable cause) {
        super(cause);
    }

    protected PluginFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
