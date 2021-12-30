package com.gitee.starblues.spring.invoke;

import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
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
