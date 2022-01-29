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

package com.gitee.starblues.core.classloader;

import java.net.URL;
import java.nio.file.Paths;

/**
 * 资源信息
 * @author starBlues
 * @version 3.0.0
 */
public class Resource {

    private final String name;
    private final URL baseUrl;
    private final URL url;

    private byte[] bytes;

    public Resource(String name, URL baseUrl, URL url) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    void tryCloseUrlSystemFile(){
        try {
            Paths.get(baseUrl.toURI()).getFileSystem().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Paths.get(url.toURI()).getFileSystem().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
