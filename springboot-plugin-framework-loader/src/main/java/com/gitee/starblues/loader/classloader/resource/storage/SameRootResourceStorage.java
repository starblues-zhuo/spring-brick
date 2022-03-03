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

import java.net.URL;

/**
 * 抽象的资源存储
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class SameRootResourceStorage implements ResourceStorage {

    protected final URL baseUrl;

    public SameRootResourceStorage(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 获取 base url
     * @return URL
     */
    public URL getBaseUrl() {
        return baseUrl;
    }


}
