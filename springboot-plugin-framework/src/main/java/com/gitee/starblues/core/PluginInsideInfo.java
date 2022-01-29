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

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;

/**
 * 内部的 PluginInfo
 * @version 3.0.0
 * @author starBlues
 */
public interface PluginInsideInfo extends PluginInfo {

    /**
     * 设置插件状态
     * @param pluginState 插件状态
     */
    void setPluginState(PluginState pluginState);

    /**
     * 设置是跟随系统启动而启动的插件
     */
    void setFollowSystem();

    /**
     * 得到插件描述
     * @return PluginDescriptor
     */
    @Override
    InsidePluginDescriptor getPluginDescriptor();

    /**
     * 转换为外部插件信息
     * @return PluginInfo
     */
    PluginInfo toPluginInfo();

}
