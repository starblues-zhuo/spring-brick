package com.plugin.development.exception;

/**
 * @Description: 配置文件解析异常
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 14:04
 * @Update Date Time:
 * @see
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
