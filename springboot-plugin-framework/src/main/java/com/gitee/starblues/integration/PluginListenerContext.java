package com.gitee.starblues.integration;


import com.gitee.starblues.integration.listener.PluginListener;

import java.util.List;

/**
 * 插件bean监听者上下文。
 *  注意: 监听者必须在初始化插件前添加，否则在初始化阶段可能无法触发添加的监听者。
 * @author zhangzhuo
 * @version 2.2.0
 */
public interface PluginListenerContext {

    /**
     * 添加监听者
     * @param pluginListener 插件 bean 监听者
     */
    void addListener(PluginListener pluginListener);


    /**
     * 添加监听者
     * @param pluginListenerClass 插件监听者Class类
     * @param <T> 继承PluginListener的子类
     */
    <T extends PluginListener> void addListener(Class<T> pluginListenerClass);

    /**
     * 追加多个监听者
     * @param pluginListeners 插件 bean 监听者集合
     */
    void addListener(List<PluginListener> pluginListeners);


}
