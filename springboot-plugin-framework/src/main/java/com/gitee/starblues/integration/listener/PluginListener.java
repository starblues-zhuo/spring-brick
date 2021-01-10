package com.gitee.starblues.integration.listener;


/**
 * 插件bean监听者
 *
 * @author starBlues
 * @version 2.3.1
 */
public interface PluginListener {


    /**
     * 注册插件
     * @param pluginId 插件id
     * @param isStartInitial 是否随着系统启动时而进行的插件注册
     */
    void registry(String pluginId, boolean isStartInitial);

    /**
     * 卸载插件
     * @param pluginId 插件id
     */
    void unRegistry(String pluginId);

    /**
     * 失败监听
     * @param pluginId 插件id
     * @param throwable 异常信息
     */
    void failure(String pluginId, Throwable throwable);


}
