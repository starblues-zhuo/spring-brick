package com.gitee.starblues.bootstrap.processor;

/**
 * 处理者异常
 * @author starBlues
 * @version 3.0.0
 */
public class ProcessorException extends RuntimeException{

    public ProcessorException() {
        super();
    }

    public ProcessorException(String message) {
        super(message);
    }

    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessorException(Throwable cause) {
        super(cause);
    }

    protected ProcessorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
