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
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;
import com.gitee.starblues.loader.utils.Assert;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 共享资源存储者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class ShareResourceStorage extends DefaultResourceStorage{

    private final String key;

    public ShareResourceStorage(String key, URL baseUrl) {
        super(baseUrl);
        this.key = key;
    }

    @Override
    public void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception{
        name = formatResourceName(name);
        if(resourceStorage.containsKey(name)){
            return;
        }
        ShareResource shareResource = new ShareResource(key, name, baseUrl, url);
        shareResource.setBytes(byteGetter);
        super.addResource(name, shareResource);
    }

    private static class ShareResource extends DefaultResource {

        private final static Map<String, ByteStore> BYTE_STORE_MAP = new ConcurrentHashMap<>();
        private final String key;

        public ShareResource(String key, String name, URL baseUrl, URL url) {
            super(name, baseUrl, url);
            this.key = key;
        }

        @Override
        public void setBytes(ResourceByteGetter byteGetter) throws Exception{
            if(byteGetter == null){
                return;
            }
            byte[] bytes = byteGetter.get();
            String name = getName();
            ByteStore byteStore = BYTE_STORE_MAP.get(name);
            if(byteStore == null){
                byteStore = new ByteStore(name);
                byteStore.addByte(key, bytes);
                BYTE_STORE_MAP.put(getName(), byteStore);
            } else {
                byteStore.addByte(key, bytes);
            }
        }

        @Override
        public byte[] getBytes() {
            String name = getName();
            ByteStore byteStore = BYTE_STORE_MAP.get(name);
            if(byteStore == null){
                return null;
            }
            return byteStore.getByte(key);
        }

        @Override
        public void close() throws Exception {
            String name = getName();
            ByteStore byteStore = BYTE_STORE_MAP.get(name);
            if(byteStore == null){
                return;
            }
            if(byteStore.remove(key)){
                BYTE_STORE_MAP.remove(name);
            }
        }
    }

    private static class ByteStore{

        private final String commonByteKey;

        private final Map<String, byte[]> bytesMap = new HashMap<>();

        private ByteStore(String resourceName) {
            this.commonByteKey = resourceName + "_ByteStoreCommon";
        }

        public synchronized void addByte(String key, byte[] bytes){
            Assert.isNotEmpty(key, "classLoaderName 不能为空");
            if(bytes == null || bytes.length == 0){
                return;
            }
            byte[] bytesOfMap = bytesMap.get(commonByteKey);
            if(bytesOfMap != null && bytesOfMap.length > 0){
                if(Arrays.equals(bytesOfMap, bytes)){
                    bytesMap.put(key, new byte[]{});
                } else {
                    bytesMap.put(key, bytes);
                }
                return;
            }
            // common 不存在, 则往 common 存储一份
            bytesMap.put(commonByteKey, bytes);
            bytesMap.put(key, new byte[]{});
        }

        public synchronized byte[] getByte(String classLoaderName){
            byte[] bytes = bytesMap.get(classLoaderName);
            if(bytes == null || bytes.length == 0){
                bytes = bytesMap.get(commonByteKey);
            }
            return bytes;
        }

        public boolean remove(String classLoaderName){
            bytesMap.remove(classLoaderName);
            if(bytesMap.size() == 1 && bytesMap.containsKey(commonByteKey)){
                // 只存在一个common
                bytesMap.clear();
                return true;
            } else {
                return false;
            }
        }
    }

}
