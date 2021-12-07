package com.gitee.starblues.spring;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
