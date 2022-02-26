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
import com.gitee.starblues.loader.launcher.ResourceLoaderFactoryGetter;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的资源加载工厂
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultResourceLoaderFactory implements ResourceLoaderFactory{

    private final Map<URL, ResourceLoader> resourceLoaderMap = new ConcurrentHashMap<>();

    private final String classLoaderName;

    public DefaultResourceLoaderFactory(String classLoaderName) {
        this.classLoaderName = classLoaderName;
    }


    @Override
    public void addResource(String path) throws Exception{
        if(path == null || "".equals(path)){
            return;
        }
        addResource(Paths.get(path));
    }

    @Override
    public void addResource(File file) throws Exception{
        if(file == null){
            return;
        }
        addResource(file.toPath());
    }

    @Override
    public void addResource(Path path) throws Exception{
        if(path == null){
            return;
        }
        if(!Files.exists(path)){
            return;
        }
        addResource(path.toUri().toURL());
    }

    @Override
    public void addResource(URL url) throws Exception{
        AbstractResourceLoader resourceLoader = null;
        if(ResourceUtils.isJarFileUrl(url)) {
            if(ResourceUtils.isJarProtocolUrl(url)){
                resourceLoader = new JarResourceLoader(url,
                        ResourceLoaderFactoryGetter.getResourceStorage(classLoaderName));
            } else {
                resourceLoader = new JarResourceLoader(Paths.get(url.toURI()).toFile(),
                        ResourceLoaderFactoryGetter.getResourceStorage(classLoaderName));
            }
        } else if(ResourceUtils.isFileUrl(url)){
            resourceLoader = new ClassPathLoader(url,
                    ResourceLoaderFactoryGetter.getResourceStorage(classLoaderName));
        }
        if(resourceLoader != null){
            addResource(resourceLoader);
        }
    }

    @Override
    public void addResource(ResourceLoader resourceLoader) throws Exception {
        if(resourceLoader == null){
            return;
        }
        if (resourceLoaderMap.containsKey(resourceLoader.getBaseUrl())) {
            return;
        }
        resourceLoader.load();
        resourceLoaderMap.put(resourceLoader.getBaseUrl(), resourceLoader);
    }

    @Override
    public List<Resource> findResources(String name) {
        List<Resource> resources = new ArrayList<>();
        for (ResourceLoader resourceLoader : resourceLoaderMap.values()) {
            Resource resource = resourceLoader.findResource(name);
            if(resource != null){
                resources.add(resource);
            }
        }
        return resources;
    }

    @Override
    public URL getBaseUrl() {
        return null;
    }

    @Override
    public void load() throws Exception {
        // 忽略
    }

    @Override
    public Resource findResource(String name) {
        for (ResourceLoader resourceLoader : resourceLoaderMap.values()) {
            Resource resourceInfo = resourceLoader.findResource(name);
            if(resourceInfo != null){
                return resourceInfo;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(String name) {
        for (ResourceLoader resourceLoader : resourceLoaderMap.values()) {
            InputStream inputStream = resourceLoader.getInputStream(name);
            if(inputStream != null){
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<>();
        for (ResourceLoader resourceLoader : resourceLoaderMap.values()) {
            resources.addAll(resourceLoader.getResources());
        }
        return resources;
    }

    @Override
    public void close() throws Exception {
        for (ResourceLoader resourceLoader : resourceLoaderMap.values()) {
            IOUtils.closeQuietly(resourceLoader);
        }
        resourceLoaderMap.clear();
    }
}
