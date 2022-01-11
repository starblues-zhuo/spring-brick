package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

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
    List<Path> getPluginsRoots();

    /**
     * 加载配置目录中全部插件
     * @return 加载的插件信息
     */
    List<PluginDescriptor> loadPlugins();

    /**
     * 校验插件jar包
     * @param jarPath 插件jar包
     * @return 校验结果. true 成功, false 失败
     */
    boolean verify(Path jarPath);

    /**
     * 安装具体插件路径来加载插件
     * @param pluginPath 插件路径
     * @return 加载的插件信息
     * @throws PluginException 插件异常
     */
    PluginDescriptor load(Path pluginPath) throws PluginException;

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
    PluginDescriptor install(Path pluginPath) throws PluginException;

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
    void upgrade(Path pluginPath) throws PluginException;

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
     * 根据插件id获取插件描述信息
     * @param pluginId 插件id
     * @return PluginDescriptor
     */
    PluginDescriptor getPluginDescriptor(String pluginId);

    /**
     * 得到全部的插件信息
     * @return List PluginWrapper
     */
    List<PluginWrapper> getPluginWrappers();

    /**
     * 根据插件id获取插件 PluginWrapper
     * @param pluginId 插件id
     * @return PluginWrapper
     */
    PluginWrapper getPluginWrapper(String pluginId);

}
