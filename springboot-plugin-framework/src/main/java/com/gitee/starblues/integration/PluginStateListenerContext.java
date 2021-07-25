package com.gitee.starblues.integration;

import org.pf4j.PluginStateListener;

import java.util.List;

/**
 * 插件状态监听器上下文
 * @author starBlues
 * @version 2.4.4
 */
public interface PluginStateListenerContext {


    /**
     * 添加pf4j状态监听者
     * @param pluginListener 插件 bean 监听者
     */
    void addPf4jStateListener(PluginStateListener pluginListener);


    /**
     * 添加pf4j状态监听者
     * @param pluginListenerClass 插件监听者Class类
     * @param <T> 继承PluginListener的子类
     */
    <T extends PluginStateListener> void addPf4jStateListener(Class<T> pluginListenerClass);

    /**
     * 添加pf4j状态监听者
     * @param pluginListeners 插件 bean 监听者集合
     */
    void addPf4jStateListener(List<PluginStateListener> pluginListeners);


}
