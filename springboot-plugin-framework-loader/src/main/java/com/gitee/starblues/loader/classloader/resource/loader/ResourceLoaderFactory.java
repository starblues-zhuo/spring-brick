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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;

/**
 * 资源加载工厂
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface ResourceLoaderFactory extends AutoCloseable{

    /**
     * 根据路径字符串添加资源
     * @param path 路径字符串
     * @throws Exception 添加资源异常
     */
    void addResource(String path) throws Exception;

    /**
     * 根据文件添加资源
     * @param file 文件
     * @throws Exception 添加资源异常
     */
    void addResource(File file) throws Exception;

    /**
     * 根据路径添加资源
     * @param path 路径
     * @throws Exception 添加资源异常
     */
    void addResource(Path path) throws Exception;

    /**
     * 根据 URL 添加资源
     * @param url URL
     * @throws Exception 添加资源异常
     */
    void addResource(URL url) throws Exception;

    /**
     * 根据资源加载器添加资源
     * @param resourceLoader 资源加载者
     * @throws Exception 添加资源异常
     */
    void addResource(ResourceLoader resourceLoader) throws Exception;

    /**
     * 根据资源名称获取第一个资源
     * @param name 资源名称
     * @return Resource
     */
    Resource findResource(String name);

    /**
     * 根据资源名称获取资源集合
     * @param name 资源名称
     * @return Resource
     */
    Enumeration<Resource> findResources(String name);

    /**
     * 根据资源名称获取第一个资源的 InputStream
     * @param name 资源名称
     * @return Resource
     */
    InputStream getInputStream(String name);

    /**
     * 获取所有URL
     * @return URL
     */
    List<URL> getUrls();


}
