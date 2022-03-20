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

package com.gitee.starblues.integration.listener;


import com.gitee.starblues.core.PluginInfo;

import java.nio.file.Path;

/**
 * 插件监听者
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginListener {

    /**
     * 加载插件成功
     * @param pluginInfo 插件信息
     */
    default void loadSuccess(PluginInfo pluginInfo){}

    /**
     * 加载失败
     * @param path 要加载的插件路径
     * @param throwable 异常信息
     */
    default void loadFailure(Path path, Throwable throwable){}

    /**
     * 卸载插件成功
     * @param pluginInfo 插件信息
     */
    default void unLoadSuccess(PluginInfo pluginInfo){}

    /**
     * 卸载失败
     * @param pluginInfo 插件信息
     * @param throwable 异常信息
     */
    default void unLoadFailure(PluginInfo pluginInfo, Throwable throwable){}

    /**
     * 注册插件成功
     * @param pluginInfo 插件信息
     */
    default void startSuccess(PluginInfo pluginInfo){}


    /**
     * 启动失败
     * @param pluginInfo 插件信息
     * @param throwable 异常信息
     */
    default void startFailure(PluginInfo pluginInfo, Throwable throwable){}

    /**
     * 卸载插件成功
     * @param pluginInfo 插件信息
     */
    default void stopSuccess(PluginInfo pluginInfo){}


    /**
     * 停止失败
     * @param pluginInfo 插件信息
     * @param throwable 异常信息
     */
    default void stopFailure(PluginInfo pluginInfo, Throwable throwable){}

}
