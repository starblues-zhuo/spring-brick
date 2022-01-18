package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;

/**
 * 外部 PluginWrapperFace
 * @author starBlues
 * @version 3.0.0
 */
public class PluginInfoFace implements PluginInfo {

    private final PluginDescriptor pluginDescriptor;
    private final PluginState pluginState;

    public PluginInfoFace(PluginInsideInfo pluginInsideInfo) {
        Assert.isNotNull(pluginInsideInfo, "参数 pluginWrapperInside 不能为空");
        this.pluginDescriptor = pluginInsideInfo.getPluginDescriptor().toPluginDescriptor();
        this.pluginState = pluginInsideInfo.getPluginState();
    }

    public PluginInfoFace(PluginDescriptor pluginDescriptor, PluginState pluginState) {
        Assert.isNotNull(pluginDescriptor, "参数 pluginDescriptor 不能为空");
        Assert.isNotNull(pluginState, "参数 pluginState 不能为空");
        if(pluginDescriptor instanceof  InsidePluginDescriptor){
            this.pluginDescriptor = ((InsidePluginDescriptor)pluginDescriptor).toPluginDescriptor();
        } else {
            this.pluginDescriptor = pluginDescriptor;
        }
        this.pluginState = pluginState;
    }

    @Override
    public String getPluginId() {
        return pluginDescriptor.getPluginId();
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public String getPluginPath() {
        return pluginDescriptor.getPluginPath();
    }

    @Override
    public PluginState getPluginState() {
        return pluginState;
    }
}
