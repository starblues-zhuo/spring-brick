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

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可缓存的资源存储者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class CacheResourceStorage extends DefaultResourceStorage{

    protected final Map<String, Resource> resourceStorage = new ConcurrentHashMap<>();

    public CacheResourceStorage(URL baseUrl) {
        super(baseUrl);
    }

    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception{
        name = formatResourceName(name);
        if(resourceStorage.containsKey(name)){
            return;
        }
        CacheResource cacheResource = new CacheResource(name, baseUrl, url);
        cacheResource.setBytes(byteGetter);
        addResource(name, cacheResource);
    }

    private static class CacheResource extends DefaultResource {

        private byte[] bytes;

        public CacheResource(String name, URL baseUrl, URL url) {
            super(name, baseUrl, url);
        }

        @Override
        public void setBytes(ResourceByteGetter byteGetter) throws Exception{
            if(byteGetter == null){
                return;
            }
            // 忽略
            bytes = byteGetter.get();
        }

        @Override
        public byte[] getBytes() {
            return bytes;
        }
    }


}
