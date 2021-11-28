package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.operator.module.PluginInfo;

import java.nio.file.Path;
import java.util.List;

/**
 * 插件管理者
 * @author starBlues
 */
public interface PluginManager {

    /**
     * 得到插件root目录
     * @return List
     */
    List<Path> getPluginsRoot();

    /**
     * 加载配置目录中全部插件
     * @return 加载的插件信息
     */
    List<PluginDescriptor> loadPlugins();

    /**
     * 安装具体插件路径来加载插件
     * @param pluginPath 插件路径
     * @return 加载的插件信息
     * @throws PluginException 插件异常
     */
    PluginDescriptor load(String pluginPath) throws PluginException;

    /**
     * 卸载加载插件
     * @param pluginId 插件id
     */
    void unLoad(String pluginId);

    /**
     * 安装路径直接安装插件, 并启动
     * @param pluginPath 插件路径
     * @return 安装的插件信息
     * @throws PluginException 插件异常
     */
    PluginDescriptor install(String pluginPath) throws PluginException;

    /**
     * 卸载插件
     * @param pluginId 插件id
     * @throws PluginException 插件异常
     */
    void uninstall(String pluginId) throws PluginException;

    /**
     * 更新已经安装的插件
     * @param pluginPath 新版本插件路径
     * @throws PluginException 插件异常
     */
    void upgrade(String pluginPath) throws PluginException;

    /**
     * 启动处于 RESOLVED 状态的插件
     * @param pluginId 插件id
     * @return PluginDescriptor
     * @throws PluginException 插件异常
     */
    PluginDescriptor start(String pluginId) throws PluginException;

    /**
     * 停止启动的插件
     * @param pluginId 插件id
     * @return PluginDescriptor
     * @throws PluginException 插件异常
     */
    PluginDescriptor stop(String pluginId) throws PluginException;

    /**
     * 得到全部的插件信息
     * @return List PluginDescriptor
     */
    List<PluginDescriptor> getPluginDescriptors();

    /**
     * 得到全部的插件信息
     * @return List PluginWrapper
     */
    List<PluginWrapper> getPluginWrappers();

}
