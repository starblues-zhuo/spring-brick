package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象的资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractResourceLoader {

    protected final URL baseUrl;
    private final Map<String, Resource> resourceCache = new ConcurrentHashMap<>();

    protected AbstractResourceLoader(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

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
        String queryName = name;
        if(name.endsWith(ResourceUtils.PACKAGE_SPLIT)){
            queryName = name.substring(0, name.lastIndexOf(ResourceUtils.PACKAGE_SPLIT));
        }
        return resourceCache.get(queryName);
    }

    public InputStream getInputStream(final String name) {
        Resource resourceInfo = resourceCache.get(name);
        if (resourceInfo != null) {
            return new ByteArrayInputStream(resourceInfo.getBytes());
        } else {
            return null;
        }
    }

    public List<Resource> getResources(){
        return new ArrayList<>(resourceCache.values());
    }

    public void clear() {
        for (Resource resource : resourceCache.values()) {
            resource.tryCloseUrlSystemFile();
        }
        resourceCache.clear();
    }



}
