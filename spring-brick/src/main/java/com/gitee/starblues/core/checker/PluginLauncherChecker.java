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

package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.core.exception.PluginException;

/**
 * 插件启动检查者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginLauncherChecker {

    /**
     * 检查是否能启动
     * @param pluginInfo 插件信息
     * @throws PluginException 不能启动, 抛出PluginException异常即可
     */
    void checkCanStart(PluginInfo pluginInfo) throws PluginException;

    /**
     * 检查是否能停止
     * @param pluginInfo 插件信息
     * @throws PluginException 不能停止, 抛出PluginException异常即可
     */
    void checkCanStop(PluginInfo pluginInfo) throws PluginException;


}
