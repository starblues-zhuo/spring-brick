/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core;

import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;
import java.util.List;

/**
 * 插件管理者
 * @author starBlues
 * @version 3.0.0
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
     * 校验插件jar包
     * @param pluginPath 插件包的路径
     * @return  成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 校验异常
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
     * 通过路径安装插件(会启用), 该插件文件必须存在于服务器 [适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件包. (如果插件包为压缩包时生效)
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
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
