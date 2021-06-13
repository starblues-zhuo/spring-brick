package com.gitee.starblues.integration.listener;


/**
 * 插件bean监听者
 *
 * @author starBlues
 * @version 2.4.4
 */
public interface PluginListener {


    /**
     * 注册插件成功
     * @param pluginId 插件id
     * @param isStartInitial 是否随着系统启动时而进行的插件注册
     */
    void registry(String pluginId, boolean isStartInitial);

    /**
     * 卸载插件成功
     * @param pluginId 插件id
     */
    void unRegistry(String pluginId);

    /**
     * 注册错误
     * @param pluginId 插件id
     * @param throwable 异常信息
     */
    void registryFailure(String pluginId, Throwable throwable);

    /**
     * 注册错误
     * @param pluginId 插件id
     * @param throwable 异常信息
     */
    void unRegistryFailure(String pluginId, Throwable throwable);

}
