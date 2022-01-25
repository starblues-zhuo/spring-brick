package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;

/**
 * 默认的内部PluginWrapperInside实现
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginInsideInfo implements PluginInsideInfo {

    private final String pluginId;
    private final InsidePluginDescriptor pluginDescriptor;
    private PluginState pluginState;
    private boolean isFollowInitial = false;

    public DefaultPluginInsideInfo(InsidePluginDescriptor pluginDescriptor) {
        this.pluginId = pluginDescriptor.getPluginId();
        this.pluginDescriptor = pluginDescriptor;
    }

    @Override
    public void setPluginState(PluginState pluginState) {
        this.pluginState = pluginState;
    }

    @Override
    public void setFollowSystem() {
        isFollowInitial = true;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public PluginInfo toPluginInfo() {
        return new PluginInfoFace(this);
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
        return isFollowInitial;
    }


}
