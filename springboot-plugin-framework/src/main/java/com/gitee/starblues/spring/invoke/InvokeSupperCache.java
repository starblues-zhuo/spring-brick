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

package com.gitee.starblues.spring.invoke;


/**
 * 插件调用提供者缓存
 * @author starBlues
 * @version 3.0.0
 */
public interface InvokeSupperCache {

    /**
     * 获取提供者bean
     * @param pluginId 插件id
     * @param supperKey 提供者key
     * @return Object
     */
    Object getSupperBean(String pluginId, String supperKey);

    /**
     * 获取提供者bean
     * @param supperKey 提供者key
     * @return Object
     */
    Object getSupperBean(String supperKey);

    /**
     * 添加提供者
     * @param pluginId 插件id
     * @param cache 提供者缓存
     */
    void add(String pluginId, SupperCache cache);

    /**
     * 移除插件提供者
     * @param pluginId 插件id
     */
    void remove(String pluginId);



}
