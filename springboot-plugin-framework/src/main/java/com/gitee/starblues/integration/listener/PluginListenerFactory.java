package com.gitee.starblues.integration.listener;

import java.util.List;

/**
 * 插件监听工厂
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginListenerFactory extends PluginListener {

    /**
     * 添加监听者
     *
     * @param pluginListener 插件监听者
     */
    void addPluginListener(PluginListener pluginListener);

    /**
     * 得到监听者
     *
     * @return 监听者集合
     */
    List<PluginListener> getListeners();

}
