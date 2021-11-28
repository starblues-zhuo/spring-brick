package com.gitee.starblues.core.classloader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象的资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractResourceLoader {


    private final Map<String, Resource> resourceCache = new ConcurrentHashMap<>();

    protected void addResource(String name, Resource resource) {
        if(resourceCache.containsKey(name)){
            return;
        }
        resourceCache.put(name, resource);
    }

    /**
     * 初始化 resource
     * @throws Exception 初始异常
     */
    public void init() throws Exception{

    }

    protected boolean existResource(String name){
        return resourceCache.containsKey(name);
    }

    public Resource findResource(final String name) {
        return resourceCache.get(name);
    }

    public InputStream getInputStream(final String name) {
        Resource resourceInfo = resourceCache.get(name);
        if (resourceInfo != null) {
            return new ByteArrayInputStream(resourceInfo.getBytes());
        } else {
            return null;
        }
    }

    public void clear() {
        for (Resource resource : resourceCache.values()) {
            resource.tryCloseUrlSystemFile();
        }
        resourceCache.clear();
    }



}
