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
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;

import java.net.URL;

/**
 * 默认的资源信息
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultResource implements Resource {

    private final String name;
    private final URL baseUrl;
    private final URL url;

    public DefaultResource(String name, URL baseUrl, URL url) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public byte[] getBytes() {
        return null;
    }

    @Override
    public void setBytes(ResourceByteGetter byteGetter) throws Exception{
        // 忽略
    }

    @Override
    public void close() throws Exception {

    }
}
