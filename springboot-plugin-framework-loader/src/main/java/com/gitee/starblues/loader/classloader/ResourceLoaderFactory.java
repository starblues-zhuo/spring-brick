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


import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源加载工厂
 * @author starBlues
 * @version 3.0.0
 */
public class ResourceLoaderFactory extends AbstractResourceLoader {

    private final List<AbstractResourceLoader> resourceLoaders = new ArrayList<>();

    public ResourceLoaderFactory() {
        super(null);
    }

    public void addResourceLoader(AbstractResourceLoader resourceLoader){
        if(resourceLoader != null){
            resourceLoaders.add(resourceLoader);
        }
    }

    public void addResource(String path) throws Exception{
        if(path == null || "".equals(path)){
            return;
        }
        addResource(Paths.get(path));
    }

    public void addResource(File file) throws Exception{
        if(file == null){
            return;
        }
        addResource(file.toPath());
    }

    public void addResource(Path path) throws Exception{
        if(path == null){
            return;
        }
        if(!Files.exists(path)){
            return;
        }
        URL url = path.toUri().toURL();
        addResource(url);
    }

    public void addResource(URL url) throws Exception{
        AbstractResourceLoader resourceLoader = null;
        if(ResourceUtils.isJarFileUrl(url)) {
            resourceLoader = new JarResourceLoader(url);
        } else if(ResourceUtils.isFileUrl(url)){
            resourceLoader = new ClassPathLoader(url);
        }
        if(resourceLoader != null){
            resourceLoader.init();
            resourceLoaders.add(resourceLoader);
        }
    }

    public void addResource(AbstractResourceLoader resourceLoader) throws Exception {
        resourceLoader.init();
        resourceLoaders.add(resourceLoader);
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<>();
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            resources.addAll(resourceLoader.getResources());
        }
        return resources;
    }

    @Override
    public synchronized Resource findResource(String name) {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            Resource resourceInfo = resourceLoader.findResource(name);
            if(resourceInfo != null){
                return resourceInfo;
            }
        }
        return null;
    }

    public synchronized List<Resource> findResources(String name) {
        List<Resource> resourceInfos = new ArrayList<>();
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            Resource resource = resourceLoader.findResource(name);
            if(resource != null){
                resourceInfos.add(resource);
            }
        }
        return resourceInfos;
    }

    @Override
    public synchronized InputStream getInputStream(String name) {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            InputStream inputStream = resourceLoader.getInputStream(name);
            if(inputStream != null){
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public synchronized void clear() {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            try {
                resourceLoader.clear();
            } catch (Exception e){
                // 忽略
            }
        }
        resourceLoaders.clear();
    }
}
