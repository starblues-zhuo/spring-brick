package com.plugin.development.context;

/**
 * @Description: 插件中的SpringBean 启用禁用监听者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-06-04 11:37
 * @Update Date Time: 2019-06-04 11:37
 * @see
 */
public interface PluginSpringBeanListener {


    /**
     * 注册插件中的bean事件
     * @param pluginId 插件id
     * @throws Exception
     */
    void registryEvent(String pluginId) throws Exception;

    /**
     * 卸载插件中的bean事件
     * @param pluginId 插件id
     * @throws Exception
     */
    void unRegistryEvent(String pluginId) throws Exception;

}
