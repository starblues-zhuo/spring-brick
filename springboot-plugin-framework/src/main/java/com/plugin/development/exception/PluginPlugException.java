package com.plugin.development.exception;

/**
 * @Description: 插件安装异常
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 10:01
 * @Update Date Time:
 * @see
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

    protected PluginPlugException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
