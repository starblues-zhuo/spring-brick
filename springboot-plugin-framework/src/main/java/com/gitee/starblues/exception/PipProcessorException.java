package com.gitee.starblues.exception;

/**
 * 插件pip处理异常
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PipProcessorException extends PluginFactoryException{

    public PipProcessorException() {
        super();
    }

    public PipProcessorException(String message) {
        super(message);
    }

    public PipProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public PipProcessorException(Throwable cause) {
        super(cause);
    }

    protected PipProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
