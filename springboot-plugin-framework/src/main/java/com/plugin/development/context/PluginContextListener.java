package com.plugin.development.context;

/**
 * @Description: 上下文监听者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-06-04 14:28
 * @Update Date Time: 2019-06-04 14:28
 * @see
 */
public interface PluginContextListener {

    /**
     * 添加监听者
     * @param pluginSpringBeanListener 插件spring bean 监听者
     */
    void addListener(PluginSpringBeanListener pluginSpringBeanListener);


}
