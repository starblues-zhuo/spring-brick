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

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.loader.classloader.*;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * 插件 classLoader
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassLoader extends GenericClassLoader {

    private final GenericClassLoader parentClassLoader;
    private MainResourceMatcher mainResourceMatcher;

    public PluginClassLoader(String name, GenericClassLoader parentClassLoader, MainResourcePatternDefiner patternDefiner,
                             ResourceLoaderFactory resourceLoaderFactory) {
        super(name, parentClassLoader, resourceLoaderFactory);
        this.parentClassLoader = parentClassLoader;
        if(patternDefiner != null){
            setMainResourceMatcher(new CacheMainResourceMatcher(patternDefiner));
        } else {
            setMainResourceMatcher(new ProhibitMainResourceMatcher());
        }
    }

    public void setMainResourceMatcher(MainResourceMatcher mainResourceMatcher){
        this.mainResourceMatcher = Assert.isNotNull(mainResourceMatcher, "参数 mainResourceMatcher 不能为空");
    }

    public void addResource(InsidePluginDescriptor descriptor) throws Exception {
        PluginType pluginType = descriptor.getType();
        if(pluginType == PluginType.JAR || pluginType == PluginType.ZIP){
            NestedPluginJarResourceLoader resourceLoader =
                    new NestedPluginJarResourceLoader(descriptor, parentClassLoader, resourceLoaderFactory);
            resourceLoaderFactory.addResource(resourceLoader);
        } else {
            addClasspath(descriptor);
            addLibFile(descriptor);
        }
    }

    private void addClasspath(InsidePluginDescriptor pluginDescriptor) throws Exception {
        String pluginClassPath = pluginDescriptor.getPluginClassPath();
        File existFile = FilesUtils.getExistFile(pluginClassPath);
        if(existFile != null){
            addResource(existFile);
        }
    }

    private void addLibFile(InsidePluginDescriptor pluginDescriptor) throws Exception {
        Set<PluginLibInfo> pluginLibInfos = pluginDescriptor.getPluginLibInfo();
        if(ObjectUtils.isEmpty(pluginLibInfos)){
            return;
        }
        for (PluginLibInfo pluginLibInfo : pluginLibInfos) {
            File existFile = FilesUtils.getExistFile(pluginLibInfo.getPath());
            if(existFile != null){
                if(pluginLibInfo.isLoadToMain()){
                    // 加载到主程序中
                    parentClassLoader.addResource(existFile);
                } else {
                    addResource(existFile);
                }
            }
        }
    }


    @Override
    protected Class<?> findClassFromParent(String className) throws ClassNotFoundException {
        if(mainResourceMatcher.match(className.replace(".", "/"))){
            try {
                return super.findClassFromParent(className);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected InputStream findInputStreamFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            try {
                return super.findInputStreamFromParent(name);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected URL findResourceFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            return super.findResourceFromParent(name);
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResourcesFromParent(String name) throws IOException {
        if(mainResourceMatcher.match(name)){
            return super.findResourcesFromParent(name);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if(mainResourceMatcher instanceof AutoCloseable){
            try {
                ((AutoCloseable) mainResourceMatcher).close();
            } catch (Exception e){
                throw new IOException(e);
            }
        }
    }

}
