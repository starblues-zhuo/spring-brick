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

package com.gitee.starblues.bootstrap.realize;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * 插件被停止监听者。用于自定义关闭资源
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginCloseListener {

    /**
     * 关闭时调用
     * @param descriptor 当前插件描述者
     */
    void close(PluginDescriptor descriptor);

}
