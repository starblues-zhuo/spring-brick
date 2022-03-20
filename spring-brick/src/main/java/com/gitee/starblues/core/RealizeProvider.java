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

import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.scanner.PluginScanner;
import com.gitee.starblues.core.version.VersionInspector;
/**
 * 插件扩展配置
 * @author starBlues
 * @version 3.0.0
 */
public interface RealizeProvider {

    /**
     * 初始化
     */
    void init();

    /**
     * 当前运行环境
     * @return RuntimeMode
     */
    RuntimeMode getRuntimeMode();

    /**
     * 得到 PluginScanner 实现
     * @return PluginScanner
     */
    PluginScanner getPluginScanner();

    /**
     * 得到插件基本的检查者
     * @return PluginBasicChecker
     */
    PluginBasicChecker getPluginBasicChecker();

    /**
     * 得到 PluginDescriptorLoader 实现
     * @return PluginDescriptorLoader
     */
    PluginDescriptorLoader getPluginDescriptorLoader();

    /**
     * 得到 VersionInspector 实现
     * @return VersionInspector
     */
    VersionInspector getVersionInspector();

}
