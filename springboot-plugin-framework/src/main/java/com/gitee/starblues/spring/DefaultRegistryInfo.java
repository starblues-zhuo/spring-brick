package com.gitee.starblues.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultRegistryInfo implements RegistryInfo{

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
    public void removeRegistryInfo(String key) {
        registryInfo.remove(key);
    }

    @Override
    public void clearRegistryInfo() {
        registryInfo.clear();
    }
}
