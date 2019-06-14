package com.plugin.development.context;

/**
 * 上下文监听者
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginContextListener {

    /**
     * 添加监听者
     * @param pluginSpringBeanListener 插件spring bean 监听者
     */
    void addListener(PluginSpringBeanListener pluginSpringBeanListener);


}
