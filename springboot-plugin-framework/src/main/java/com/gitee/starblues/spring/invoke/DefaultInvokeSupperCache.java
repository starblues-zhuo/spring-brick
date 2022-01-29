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

import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的 InvokeSupperCache
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultInvokeSupperCache implements InvokeSupperCache{

    private final Map<String, Map<String, SupperCache>> invokeSupplierCache = new ConcurrentHashMap<>();

    @Override
    public Object getSupperBean(String supperKey){
        return getSupperBean(null, supperKey);
    }

    @Override
    public Object getSupperBean(String pluginId, String supperKey){
        if(!ObjectUtils.isEmpty(pluginId)){
            Map<String, SupperCache> cacheMap = invokeSupplierCache.get(pluginId);
            if(cacheMap == null){
                return null;
            }
            return getSupperBean(cacheMap.get(supperKey));
        }
        for (Map<String, SupperCache> value : invokeSupplierCache.values()) {
            Object supperBean = getSupperBean(value.get(supperKey));
            if(supperBean != null){
                return supperBean;
            }
        }
        return null;
    }

    @Override
    public void add(String pluginId, SupperCache cache){
        Map<String, SupperCache> supperCache = invokeSupplierCache.computeIfAbsent(pluginId, k -> new HashMap<>());
        supperCache.put(cache.getSupperKey(), cache);
    }

    @Override
    public void remove(String pluginId){
        invokeSupplierCache.remove(pluginId);
    }

    private static Object getSupperBean(SupperCache cache){
        if(cache == null){
            return null;
        }
        ApplicationContext applicationContext = cache.getApplicationContext();
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        if(springBeanFactory.containsBean(cache.getBeanName())){
            return springBeanFactory.getBean(cache.getBeanName());
        }
        return null;
    }
}
