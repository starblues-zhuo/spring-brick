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

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.ResourceByteGetter;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 资源存储者
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface ResourceStorage extends AutoCloseable{

    /**
     * 添加资源
     * @param name 资源名称
     * @param url 资源url
     * @param byteGetter 资源字节获取者
     * @throws Exception 添加资源异常
     */
    void add(String name, URL url, ResourceByteGetter byteGetter) throws Exception;

    /**
     * 添加资源
     * @param name 资源名称
     * @param url 资源url
     * @throws Exception 添加资源异常
     */
    void add(String name, URL url) throws Exception;

    /**
     * 存在资源
     * @param name 资源名称
     * @return 存在 true, 不存在 false
     */
    boolean exist(String name);

    /**
     * 获取资源
     * @param name 资源名称
     * @return Resource
     */
    Resource get(String name);

    /**
     * 获取资源的 InputStream
     * @param name 资源名称
     * @return InputStream
     */
    InputStream getInputStream(String name);

    /**
     * 得到全部资源
     * @return 全部资源列表
     */
    List<Resource> getAll();

    /**
     * 是否为空
     * @return boolean
     */
    boolean isEmpty();


}
