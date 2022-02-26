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

package com.gitee.starblues.loader.classloader.resource.storage;

import com.gitee.starblues.loader.classloader.resource.loader.DefaultResource;
import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;
import com.gitee.starblues.loader.utils.IOUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的资源存储者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultResourceStorage implements ResourceStorage{

    protected final Map<String, Resource> resourceStorage = new ConcurrentHashMap<>();

    @Override
    public void add(String name, URL baseUrl, URL url, ResourceByteGetter byteGetter) throws Exception{
        if(resourceStorage.containsKey(name)){
            return;
        }
        DefaultResource defaultResource = new DefaultResource(name, baseUrl, url);
        addResource(name, defaultResource);
    }

    @Override
    public void add(String name, URL baseUrl, URL url) throws Exception{
        this.add(name, baseUrl, url, null);
    }

    @Override
    public boolean exist(String name) {
        return resourceStorage.containsKey(name);
    }

    protected void addResource(String name, Resource resource){
        resourceStorage.put(name, resource);
    }

    @Override
    public Resource get(String name) {
        return resourceStorage.get(name);
    }

    @Override
    public List<Resource> getAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @Override
    public void close() throws Exception {
        for (Resource resource : resourceStorage.values()) {
            IOUtils.closeQuietly(resource);
        }
        resourceStorage.clear();
    }
}
