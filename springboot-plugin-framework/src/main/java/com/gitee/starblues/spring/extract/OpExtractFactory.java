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

package com.gitee.starblues.spring.extract;

/**
 * 可操作的扩展工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface OpExtractFactory extends ExtractFactory{

    /**
     * 添加main中的扩展
     * @param extractObject extractObject
     */
    void addOfMain(Object extractObject);

    /**
     * 添加插件中的扩展
     * @param pluginId 插件
     * @param extractObject 扩展对象
     */
    void add(String pluginId, Object extractObject);

    /**
     * 移除插件中的扩展
     * @param pluginId 插件id
     */
    void remove(String pluginId);

}
