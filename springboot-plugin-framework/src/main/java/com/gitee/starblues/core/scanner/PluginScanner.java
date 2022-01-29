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

package com.gitee.starblues.core.scanner;

import java.nio.file.Path;
import java.util.List;

/**
 * 插件扫描者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginScanner {

    /**
     * 从 rootDir 集合中扫描出插件路径
     * @param rootDir 根目录
     * @return 扫描出的目录
     */
    List<Path> scan(List<String> rootDir);


}
