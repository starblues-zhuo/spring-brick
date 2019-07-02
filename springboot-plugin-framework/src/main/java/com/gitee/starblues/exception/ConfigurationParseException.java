package com.gitee.starblues.exception;

/**
 * 配置文件解析异常
 * @author zhangzhuo
 * @version 1.0
 */
public class ConfigurationParseException extends Exception{

    public ConfigurationParseException() {
        super();
    }

    public ConfigurationParseException(String message) {
        super(message);
    }

    public ConfigurationParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationParseException(Throwable cause) {
        super(cause);
    }

    protected ConfigurationParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
