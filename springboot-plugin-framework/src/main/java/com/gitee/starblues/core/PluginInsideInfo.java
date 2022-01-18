package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;

/**
 * 内部的 PluginWrapper
 * @version 3.0.0
 * @author starBlues
 */
public interface PluginInsideInfo extends PluginInfo {

    /**
     * 设置插件状态
     * @param pluginState 插件状态
     */
    void setPluginState(PluginState pluginState);

    /**
     * 得到插件描述
     * @return PluginDescriptor
     */
    @Override
    InsidePluginDescriptor getPluginDescriptor();

    /**
     * 转换为插件信息
     * @return PluginInfo
     */
    PluginInfo toPluginInfo();

}
