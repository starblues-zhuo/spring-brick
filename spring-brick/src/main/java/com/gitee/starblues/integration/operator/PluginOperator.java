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

package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.upload.UploadByInputStreamParam;
import com.gitee.starblues.integration.operator.upload.UploadByMultipartFileParam;
import com.gitee.starblues.integration.operator.upload.UploadParam;

import java.nio.file.Path;
import java.util.List;

/**
 * 操作插件的接口
 * @author starBlues
 * @version 3.0.0
 * @see DefaultPluginOperator
 */
public interface PluginOperator {

    /**
     * 初始化插件 [适用于 dev、prod 环境]
     * 该方法只能执行一次。因程序启动时已经调用了该方法，在使用时不要再调用该方法。
     * @param pluginInitializerListener 插件初始化监听者
     * @return true初始化成功, false初始化失败
     * @throws PluginException 抛出异常说明初始化失败
     */
    boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException;

    /**
     * 校验插件jar包 [适用于 dev、prod 环境]
     * @param pluginPath 插件包绝对路径
     * @return  true校验成功, false校验失败
     * @throws PluginException 抛出异常说明校验插件包失败
     */
    boolean verify(Path pluginPath) throws PluginException;

    /**
     * 解析插件包 [适用于 dev、prod 环境]
     * @param pluginPath 插件包路基
     * @return 解析的插件信息
     * @throws PluginException 抛出异常说明解析插件包失败
     */
    PluginInfo parse(Path pluginPath) throws PluginException;

    /**
     * 通过路径安装启动插件 [适用于 dev、prod 环境]
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件包. (如果插件包为压缩包时生效)
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 卸载插件 [适用于 dev、prod 环境]
     * @param pluginId 插件id
     * @param isDelete 卸载后是否删除插件文件. [适用于 prod 环境]
     * @param isBackup 删除插件文件前, 是否备份插件文件。备份文件命名规则为；[uninstall][时间]_原jar名.jar [适用于 prod 环境]
     * @throws PluginException 异常信息
     */
    void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException;

    /**
     * 加载插件, 但不启动 [适用于 dev、prod 环境]
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件包. (如果插件包为压缩包时生效)
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo load(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 配合load使用. 针对load的插件进行unload [适用于 dev、prod 环境]
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean unload(String pluginId) throws PluginException;

    /**
     * 启用插件 [适用于 dev、prod 环境]
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean start(String pluginId) throws PluginException;


    /**
     * 停止插件 [适用于 dev、prod 环境]
     * @param pluginId 插件id
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean stop(String pluginId) throws PluginException;

    /**
     * 上传插件. [适用于 dev、prod 环境]
     * dev模式:
     *   如果不存在相同插件(插件id不相同), 则正常上传到临时目录, 然后加载、启动。
     *   如果存在相同插件(插件id相同)
     *      相同插件在启动状态, 则进入更新模式(满足上传的插件包版本必须大于已启动的插件版本), 系统会自动卸载旧版本, 然后对临时目录中的插件进行安装、启动。
     *      相同插件不在启动状态, 则直接对临时目录中的插件进行安装、启动。
     * prod模式:
     *   如果不存在相同插件(插件id不相同), 则正常上传到插件目录, 然后加载、启动。
     *   如果存在相同插件(插件id相同)
     *      相同插件在启动状态, 则进入更新模式(满足上传的插件包版本必须大于已启动的插件版本), 系统会自动卸载旧版本, 安装新版本。
     *      相同插件不在启动状态, 则进入覆盖模式, 会对旧插件进行备份(可根据isBackOldPlugin配置不备份), 然后上传新插件包到插件目录, 然后安装、启动。
     *   如果在插件根目录存在同文件名称插件, 系统会抛出异常, 建议重命名插件名称, 再上传。
     *
     * @param uploadParam 上传参数
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     * @see UploadByInputStreamParam
     * @see UploadByMultipartFileParam
     */
    PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException;

    /**
     * 通过路径备份插件文件。[适用于 prod 环境]
     * @param backDirPath 备份的目录路径
     * @param sign 备份文件的自定义标识
     * @return 备份插件的路径
     * @throws PluginException 异常信息
     */
    Path backupPlugin(Path backDirPath, String sign) throws PluginException;

    /**
     * 通过插件id备份插件。 [适用于 prod 环境]
     * @param pluginId 插件id
     * @param sign 备份文件的自定义标识
     * @return 备份插件的路径
     * @throws PluginException 异常信息
     */
    Path backupPlugin(String pluginId, String sign) throws PluginException;

    /**
     * 获取插件信息 [适用于 dev、prod 环境]
     * @return 返回插件信息列表
     */
    List<PluginInfo> getPluginInfo();

    /**
     * 根据插件id获取插件信息 [适用于 dev、prod 环境]
     * @param pluginId 插件id
     * @return 插件信息
     */
    PluginInfo getPluginInfo(String pluginId);

}
