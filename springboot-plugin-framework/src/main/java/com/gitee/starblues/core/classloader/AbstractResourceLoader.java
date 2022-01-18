package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.ResourceUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
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
        // 添加root 路径
        Resource rootResource = new Resource("root", baseUrl, baseUrl);
        resourceCache.put("/", rootResource);
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
            try (InputStream inputStream = resourceInfo.getUrl().openStream();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
                IOUtils.copy(inputStream, byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Resource> getResources(){
        return new ArrayList<>(resourceCache.values());
    }

    public void clear() {
        resourceCache.clear();
    }

    protected byte[] getClassBytes(String path, InputStream inputStream, boolean isClose) throws Exception{
        if(!ResourceUtils.isClass(path)){
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } finally {
            if(isClose){
                IOUtils.closeQuietly(inputStream);
            }
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

}
