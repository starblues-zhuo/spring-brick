package com.gitee.starblues.core.exception;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.MsgUtils;

/**
 * 插件禁止停止异常
 * @author starBlues
 * @version 3.0.0
 */
public class PluginProhibitStopException extends PluginException{

    public PluginProhibitStopException(PluginDescriptor pluginDescriptor, String message) {
        super(pluginDescriptor, "被禁止卸载. " + MsgUtils.getThrowableMsg(message));
    }
}
