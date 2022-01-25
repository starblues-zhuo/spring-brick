package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * 插件包装
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginInfo {

    /**
     * 得到插件id
     * @return String
     */
    String getPluginId();

    /**
     * 得到插件描述
     * @return PluginDescriptor
     */
    PluginDescriptor getPluginDescriptor();

    /**
     * 得到插件路径
     * @return Path
     */
    String getPluginPath();

    /**
     * 得到插件状态
     * @return PluginState
     */
    PluginState getPluginState();

    /**
     * 是否跟随系统启动而加载的插件
     * @return true: 是, false: 否
     */
    boolean isFollowSystem();

}
