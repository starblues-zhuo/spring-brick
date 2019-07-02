package com.gitee.starblues.exception;

/**
 * 插件bean注册异常
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginBeanFactoryException extends PluginFactoryException{

    public PluginBeanFactoryException() {
        super();
    }

    public PluginBeanFactoryException(String message) {
        super(message);
    }

    public PluginBeanFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginBeanFactoryException(Throwable cause) {
        super(cause);
    }

    protected PluginBeanFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
