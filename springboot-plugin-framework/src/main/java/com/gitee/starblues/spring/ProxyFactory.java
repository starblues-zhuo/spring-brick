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

package com.gitee.starblues.spring;

/**
 * 代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface ProxyFactory {

    /**
     * 获取代理类
     * @param interfacesClass 需代理的接口
     * @param <T> 代理实现的泛型
     * @return 代理实现
     */
    <T> T getObject(Class<T> interfacesClass);

}
