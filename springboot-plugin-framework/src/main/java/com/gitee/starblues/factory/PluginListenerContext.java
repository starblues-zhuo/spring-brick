package com.gitee.starblues.factory;

import java.util.List;

/**
 * 插件bean监听者上下文
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginListenerContext {

    /**
     * 添加监听者
     * @param pluginListener 插件 bean 监听者
     */
    void addListener(PluginListener pluginListener);

    /**
     * 追加多个监听者
     * @param pluginListeners 插件 bean 监听者集合
     */
    void addListener(List<PluginListener> pluginListeners);


}
