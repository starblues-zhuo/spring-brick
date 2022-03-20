/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.core.descriptor;

import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * 内部的PluginDescriptor
 * @author starBlues
 * @version 3.0.0
 */
public interface InsidePluginDescriptor extends PluginDescriptor{

    /**
     * 得到插件的 Properties 配置
     * @return Properties
     */
    Properties getProperties();

    /**
     * 获取插件配置文件名称。
     * 和 getConfigFileLocation 配置二选一, 如果都有值则默认使用 getConfigFileName
     * @return String
     */
    String getConfigFileName();

    /**
     * 获取插件配置文件路径。
     * 和 getConfigFileName 配置二选一, 如果都有值则默认使用 getConfigFileName
     * @return String
     */
    String getConfigFileLocation();

    /**
     * 得到插件启动时参数
     * @return String
     */
    String getArgs();

    /**
     * 得到内部的插件路径
     * @return Path
     */
    Path getInsidePluginPath();

    /**
     * 获取插件文件名称
     * @return String
     */
    String getPluginFileName();


    /**
     * 获取插件classes path路径
     * @return Path
     */
    String getPluginClassPath();

    /**
     * 获取插件依赖的路径
     * @return String
     */
    Set<PluginLibInfo> getPluginLibInfo();

    /**
     * 设置当前插件包含主程序加载资源的匹配
     * @return Set
     */
    Set<String> getIncludeMainResourcePatterns();

    /**
     * 设置当前插件排除从主程序加载资源的匹配
     * @return Set
     */
    Set<String> getExcludeMainResourcePatterns();

    /**
     * 转换为 PluginDescriptor
     * @return PluginDescriptor
     */
    PluginDescriptor toPluginDescriptor();

}
