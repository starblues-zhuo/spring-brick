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


import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.loader.classloader.AbstractResourceLoader;
import com.gitee.starblues.loader.classloader.JarResourceLoader;
import com.gitee.starblues.loader.classloader.Resource;
import com.gitee.starblues.loader.classloader.ResourceLoaderFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * 嵌套插件jar加载者
 * @author starBlues
 * @version 3.0.0
 */
public class NestedPluginJarResourceLoader extends AbstractResourceLoader {

    private final InsidePluginDescriptor pluginDescriptor;
    private final ResourceLoaderFactory resourceLoaderFactory;

    public NestedPluginJarResourceLoader(InsidePluginDescriptor pluginDescriptor,
                                         ResourceLoaderFactory resourceLoaderFactory) throws Exception {
        super(new URL("jar:" + pluginDescriptor.getInsidePluginPath().toUri().toURL() + "!/"));
        this.pluginDescriptor = pluginDescriptor;
        this.resourceLoaderFactory = resourceLoaderFactory;
    }

    @Override
    public void init() throws Exception {
        super.init();
        try (JarFile jarFile = new JarFile(pluginDescriptor.getInsidePluginPath().toFile())) {
            addClassPath(jarFile);
            addLib(jarFile);
        }
    }

    private void addClassPath(JarFile jarFile) throws Exception{
        String classesPath = pluginDescriptor.getPluginClassPath();
        Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry jarEntry;
        while (entries.hasMoreElements()){
            jarEntry = entries.nextElement();
            if(!jarEntry.getName().startsWith(classesPath)){
                continue;
            }
            String realName = jarEntry.getName().replace(classesPath, "");
            URL url = new URL(baseUrl.toString() + jarEntry.getName());
            Resource resource = new Resource(realName, baseUrl, url);
            resource.setBytes(getClassBytes(realName, jarFile.getInputStream(jarEntry), true));
            addResource(realName, resource);
        }
    }

    private void addLib(JarFile jarFile) throws Exception {
        JarEntry jarEntry = null;
        Set<String> pluginLibPaths = pluginDescriptor.getPluginLibPaths();
        for (String pluginLibPath : pluginLibPaths) {
            jarEntry = jarFile.getJarEntry(pluginLibPath);
            if (jarEntry.getMethod() != ZipEntry.STORED) {
                throw new PluginException("插件依赖压缩方式错误, 必须是: 存储(stored)压缩方式");
            }
            InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
            URL url = new URL(baseUrl.toString() + pluginLibPath + "!/");
            JarResourceLoader jarResourceLoader = new JarResourceLoader(url, new JarInputStream(jarFileInputStream));
            jarResourceLoader.init();
            resourceLoaderFactory.addResourceLoader(jarResourceLoader);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }
}
