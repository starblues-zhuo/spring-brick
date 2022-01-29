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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 可缓存的注册信息
 * @author starBlues
 * @version 3.0.0
 */
public class CacheRegistryInfo implements RegistryInfo {

    private final Map<String, Object> registryInfo = new ConcurrentHashMap<>();

    @Override
    public void addRegistryInfo(String key, Object value) {
        registryInfo.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getRegistryInfo(String key) {
        Object o = registryInfo.get(key);
        if(o == null){
            return null;
        }
        return (T) o;
    }

    @Override
    public <T> T getRegistryInfo(String key, Supplier<T> notExistCreate) {
        T t = getRegistryInfo(key);
        if(t != null){
            return t;
        }
        t = notExistCreate.get();
        registryInfo.put(key, t);
        return t;
    }

    @Override
    public void removeRegistryInfo(String key) {
        registryInfo.remove(key);
    }

    @Override
    public void clearRegistryInfo() {
        registryInfo.clear();
    }
}
