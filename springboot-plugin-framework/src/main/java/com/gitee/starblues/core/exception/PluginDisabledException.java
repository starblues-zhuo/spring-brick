package com.gitee.starblues.core.exception;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
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

    /**
     * 检查插件是否被禁用
     * @param pluginInsideInfo PluginInfo
     */
    public static void checkDisabled(PluginInfo pluginInsideInfo, IntegrationConfiguration configuration, String opType){
        if(pluginInsideInfo.getPluginState() == PluginState.DISABLED
                || configuration.isDisabled(pluginInsideInfo.getPluginId())
                || !configuration.isEnable(pluginInsideInfo.getPluginId())){
            throw new PluginDisabledException(pluginInsideInfo.getPluginDescriptor(), opType);
        }
    }
}
