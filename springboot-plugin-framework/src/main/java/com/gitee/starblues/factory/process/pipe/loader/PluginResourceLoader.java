package com.gitee.starblues.factory.process.pipe.loader;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;

/**
 * 插件资源加载者统一定义的接口
 *
 * @author starBlues
 * @version 2.2.0
 */
public interface PluginResourceLoader {

    /**
     * 加载者的key
     * @return String
     */
    String key();


    /**
     * 加载资源
     * @param pluginRegistryInfo 插件注册者信息
     * @return 资源包装对象
     * @throws Exception Exception
     */
    ResourceWrapper load(PluginRegistryInfo pluginRegistryInfo) throws Exception;

    /**
     * 卸载时的操作
     * @param pluginRegistryInfo 插件对象
     * @param resourceWrapper 资源包装者
     * @throws Exception 卸载异常
     */
    void unload(PluginRegistryInfo pluginRegistryInfo, ResourceWrapper resourceWrapper) throws Exception;


    /**
     * 执行顺序
     * @return OrderPriority
     */
    OrderPriority order();

}
