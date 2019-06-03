package com.plugin.development.exception;

/**
 * @Description: 插件bean注册异常
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-28 16:23
 * @Update Date Time:
 * @see
 */
public class PluginBeanFactoryException extends Exception{

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
