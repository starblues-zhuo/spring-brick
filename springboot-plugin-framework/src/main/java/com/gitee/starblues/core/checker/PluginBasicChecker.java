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

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;

/**
 * 插件基本检查者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginBasicChecker {

    /**
     * 根据路径检查
     * @param path path
     * @throws Exception 检查异常
     */
    void checkPath(Path path) throws Exception;

    /**
     * 检查插件描述是否合法
     * @param descriptor 插件信息
     * @throws PluginException 检查异常
     */
    void checkDescriptor(PluginDescriptor descriptor) throws PluginException;

}
