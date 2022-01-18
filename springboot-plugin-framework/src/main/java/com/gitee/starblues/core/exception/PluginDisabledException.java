package com.gitee.starblues.core.exception;

import com.gitee.starblues.core.PluginException;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.ObjectUtils;

/**
 * 插件被禁用异常
 * @author starBlues
 * @version 3.0.0
 */
public class PluginDisabledException extends PluginException {

    public PluginDisabledException(PluginDescriptor pluginDescriptor) {
        this(pluginDescriptor, null);
    }

    public PluginDisabledException(PluginDescriptor pluginDescriptor, String opType) {
        super("插件[" + MsgUtils.getPluginUnique(pluginDescriptor) + "]已被禁用, 不能"
                + (!ObjectUtils.isEmpty(opType) ? opType : "操作"));
    }
}
