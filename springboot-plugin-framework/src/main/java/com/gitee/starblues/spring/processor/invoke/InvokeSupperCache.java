package com.gitee.starblues.spring.processor.invoke;

import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class InvokeSupperCache {

    private static final Map<String, Map<String, Cache>> INVOKE_SUPPLIER_CACHE = new ConcurrentHashMap<>();

    static Object getSupperBean(String pluginId, String supperKey){
        if(!ObjectUtils.isEmpty(pluginId)){
            Map<String, Cache> cacheMap = INVOKE_SUPPLIER_CACHE.get(pluginId);
            if(cacheMap == null){
                return null;
            }
            return getSupperBean(cacheMap.get(supperKey));
        }
        for (Map<String, Cache> value : INVOKE_SUPPLIER_CACHE.values()) {
            Object supperBean = getSupperBean(value.get(supperKey));
            if(supperBean != null){
                return supperBean;
            }
        }
        return null;
    }

    private static Object getSupperBean(Cache cache){
        if(cache == null){
            return null;
        }
        GenericApplicationContext applicationContext = cache.getApplicationContext();
        if(applicationContext.containsBean(cache.getBeanName())){
            return applicationContext.getBean(cache.getBeanName());
        }
        return null;
    }

    public static void add(String pluginId, Cache cache){
        Map<String, Cache> supperCache = INVOKE_SUPPLIER_CACHE.computeIfAbsent(pluginId, k -> new HashMap<>());
        supperCache.put(cache.getSupperKey(), cache);
    }

    public static void remove(String pluginId){
        INVOKE_SUPPLIER_CACHE.remove(pluginId);
    }

    public static class Cache{
        private final String supperKey;
        private final String beanName;
        private final GenericApplicationContext applicationContext;

        public Cache(String supperKey, String beanName, GenericApplicationContext applicationContext) {
            this.supperKey = supperKey;
            this.beanName = beanName;
            this.applicationContext = applicationContext;
        }

        public String getSupperKey() {
            return supperKey;
        }

        public String getBeanName() {
            return beanName;
        }

        public GenericApplicationContext getApplicationContext() {
            return applicationContext;
        }
    }


}
