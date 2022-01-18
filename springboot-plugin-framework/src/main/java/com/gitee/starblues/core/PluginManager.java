package com.gitee.starblues.core;

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
    List<String> getPluginsRoots();

    /**
     * 得到插件默认的根路径
     * @return String
     */
    String getDefaultPluginRoot();

    /**
     * 加载配置目录中全部插件
     * @return 加载的插件信息
     */
    List<PluginInfo> loadPlugins();

    /**
     * 校验插件包
     * @param pluginPath 插件包路径
     * @return 校验结果. true 成功, false 失败
     */
    boolean verify(Path pluginPath);

    /**
     * 解析插件包
     * @param pluginPath 插件包路基
     * @return 解析的插件信息
     * @throws PluginException 插件异常
     */
    PluginInfo parse(Path pluginPath) throws PluginException;

    /**
     * 根据具体插件路径来加载插件.
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件文件
     * @return 加载的插件信息
     * @throws PluginException 插件异常
     */
    PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 卸载加载插件
     * @param pluginId 插件id
     */
    void unLoad(String pluginId);

    /**
     * 安装路径直接安装插件, 并启动
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件文件
     * @return 安装的插件信息
     * @throws PluginException 插件异常
     */
    PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 卸载插件
     * @param pluginId 插件id
     * @throws PluginException 插件异常
     */
    void uninstall(String pluginId) throws PluginException;

    /**
     * 更新已经安装的插件
     * @param pluginPath 新版本插件路径
     * @param unpackPlugin 是否解压要更新的插件文件
     * @throws PluginException 插件异常
     * @return PluginInfo 更新的插件信息
     */
    PluginInfo upgrade(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 启动处于 RESOLVED 状态的插件
     * @param pluginId 插件id
     * @return PluginDescriptor
     * @throws PluginException 插件异常
     */
    PluginInfo start(String pluginId) throws PluginException;

    /**
     * 停止启动的插件
     * @param pluginId 插件id
     * @return PluginDescriptor
     * @throws PluginException 插件异常
     */
    PluginInfo stop(String pluginId) throws PluginException;

    /**
     * 根据插件id获取插件描述信息
     * @param pluginId 插件id
     * @return PluginDescriptor
     */
    PluginInfo getPluginInfo(String pluginId);

    /**
     * 得到全部的插件信息
     * @return List PluginWrapper
     */
    List<PluginInfo> getPluginInfos();

}
