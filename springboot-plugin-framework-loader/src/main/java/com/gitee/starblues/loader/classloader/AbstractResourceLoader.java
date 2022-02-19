/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.loader.classloader;


import com.gitee.starblues.loader.utils.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象的资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractResourceLoader {

    private static final String CLASS_FILE_EXTENSION = ".class";

    private boolean isInit = false;

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
    public final synchronized void init() throws Exception{
        if(isInit){
            throw new Exception(this.getClass().getName()+": 已经初始化了, 不能再初始化!");
        }
        try {
            // 添加root 路径
            Resource rootResource = new Resource("root", baseUrl, baseUrl);
            resourceCache.put("/", rootResource);
            initOfChild();
        } finally {
            isInit = true;
        }
    }

    /**
     * 子类初始化实现
     * @throws Exception 初始异常
     */
    protected void initOfChild() throws Exception{};


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
        if(!isClass(path)){
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

    private static boolean isClass(String path){
        if(path == null || "".equals(path)){
            return false;
        }
        return path.toLowerCase().endsWith(CLASS_FILE_EXTENSION);
    }

}
