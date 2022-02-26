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

package com.gitee.starblues.loader.classloader.resource.loader;


import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;
import com.gitee.starblues.loader.utils.IOUtils;

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
public abstract class AbstractResourceLoader implements ResourceLoader{

    private static final String CLASS_FILE_EXTENSION = ".class";

    private boolean loaded = false;

    protected final URL baseUrl;

    protected final ResourceStorage resourceStorage;

    protected AbstractResourceLoader(URL baseUrl, ResourceStorage resourceStorage) {
        this.baseUrl = baseUrl;
        this.resourceStorage = resourceStorage;
    }

    @Override
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * 初始化 resource
     * @throws Exception 初始异常
     */
    @Override
    public final synchronized void load() throws Exception{
        if(loaded){
            throw new Exception(this.getClass().getName()+": 已经初始化了, 不能再初始化!");
        }
        try {
            // 添加root 路径
            resourceStorage.add("/", baseUrl, baseUrl);
            loadOfChild();
        } finally {
            loaded = true;
        }
    }

    /**
     * 子类初始化实现
     * @throws Exception 初始异常
     */
    protected void loadOfChild() throws Exception{};

    @Override
    public Resource findResource(final String name) {
        return resourceStorage.get(name);
    }

    @Override
    public InputStream getInputStream(final String name) {
        Resource resourceInfo = resourceStorage.get(name);
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

    @Override
    public List<Resource> getResources(){
        return resourceStorage.getAll();
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

    @Override
    public void close() throws Exception {
        resourceStorage.close();
    }

    private static boolean isClass(String path){
        if(path == null || "".equals(path)){
            return false;
        }
        return path.toLowerCase().endsWith(CLASS_FILE_EXTENSION);
    }

}
