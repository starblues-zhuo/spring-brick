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

import com.gitee.starblues.common.DependencyPlugin;

import java.util.List;

/**
 * 插件信息
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginDescriptor {

    /**
     * 获取插件id
     * @return String
     */
    String getPluginId();

    /**
     * 获取插件版本
     * @return String
     */
    String getPluginVersion();

    /**
     * 获取插件引导类
     * @return String
     */
    String getPluginBootstrapClass();

    /**
     * 获取插件路径
     * @return Path
     */
    String getPluginPath();

    /**
     * 获取插件描述
     * @return String
     */
    String getDescription();

    /**
     * 获取插件所能安装到主程序的版本
     * @return String
     */
    String getRequires();

    /**
     * 获取插件提供开发者
     * @return String
     */
    String getProvider();

    /**
     * 获取插件 license
     * @return String
     */
    String getLicense();

    /**
     * 获取当前插件依赖
     * @return List
     */
    List<DependencyPlugin> getDependencyPlugin();

    /**
     * 得到插件类型
     * @return 插件类型
     */
    PluginType getType();


}
