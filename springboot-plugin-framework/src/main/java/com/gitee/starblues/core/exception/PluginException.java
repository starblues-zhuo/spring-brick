package com.gitee.starblues.core.exception;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.MsgUtils;

import java.util.function.Supplier;


/**
 * 插件异常
 * @author starBlues
 * @version 3.0.0
 */
public class PluginException extends RuntimeException{

    public PluginException() {
        super();
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(PluginDescriptor pluginDescriptor, String opType, Throwable cause) {
        this(MsgUtils.getPluginUnique(pluginDescriptor), opType,  cause);
    }

    public PluginException(PluginDescriptor pluginDescriptor, String message) {
        this(MsgUtils.getPluginUnique(pluginDescriptor), message);
    }

    public PluginException(String pluginId, String opType, Throwable cause) {
        super("插件[" + pluginId + "]" + opType + "失败. " + MsgUtils.getThrowableMsg(cause), cause);
    }

    public PluginException(String pluginId, String message) {
        super("插件[" + pluginId + "]" + MsgUtils.getThrowableMsg(message));
    }

    public static PluginException getPluginException(Throwable throwable, Supplier<PluginException> getException){
        if(throwable instanceof PluginException){
            return (PluginException) throwable;
        }
        return getException.get();
    }
}
