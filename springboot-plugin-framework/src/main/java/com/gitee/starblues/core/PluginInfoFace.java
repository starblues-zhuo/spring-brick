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
    private final boolean followSystem;

    public PluginInfoFace(PluginInsideInfo pluginInsideInfo) {
        Assert.isNotNull(pluginInsideInfo, "参数 pluginWrapperInside 不能为空");
        this.pluginDescriptor = pluginInsideInfo.getPluginDescriptor().toPluginDescriptor();
        this.pluginState = pluginInsideInfo.getPluginState();
        this.followSystem = pluginInsideInfo.isFollowSystem();
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

    @Override
    public boolean isFollowSystem() {
        return followSystem;
    }
}
