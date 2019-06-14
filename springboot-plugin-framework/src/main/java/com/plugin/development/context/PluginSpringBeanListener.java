package com.plugin.development.context;

/**
 * 插件中的SpringBean 启用禁用监听者
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginSpringBeanListener {


    /**
     * 注册插件中的bean事件
     * @param pluginId 插件id
     * @throws Exception 抛出异常
     */
    void registryEvent(String pluginId) throws Exception;

    /**
     * 卸载插件中的bean事件
     * @param pluginId 插件id
     * @throws Exception 抛出异常
     */
    void unRegistryEvent(String pluginId) throws Exception;

}
