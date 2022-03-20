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

package com.gitee.starblues.core.launcher.plugin;
import java.util.function.Supplier;

/**
 * 注册的信息接口
 * @author starBlues
 * @version 3.0.0
 */
public interface RegistryInfo {


    /**
     * 添加注册的信息
     * @param key 注册信息key
     * @param value 注册信息值
     */
    void addRegistryInfo(String key, Object value);

    /**
     * 得到注册信息
     * @param key 注册信息key
     * @param <T> 返回类型泛型
     * @return 注册信息的值
     */
    <T> T getRegistryInfo(String key);

    /**
     * 得到注册信息
     * @param key 注册信息key
     * @param notExistCreate 不存在的话, 进行创建操作
     * @param <T> 返回类型泛型
     * @return 注册信息的值
     */
    <T> T getRegistryInfo(String key, Supplier<T> notExistCreate);


    /**
     * 移除注册信息
     * @param key 注册信息key
     */
    void removeRegistryInfo(String key);

    /**
     * 清除全部的注册信息
     */
    void clearRegistryInfo();

}
