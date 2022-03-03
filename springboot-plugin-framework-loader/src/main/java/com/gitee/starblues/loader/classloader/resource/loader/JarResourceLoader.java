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

import com.gitee.starblues.loader.classloader.filter.ExcludeResource;
import com.gitee.starblues.loader.classloader.filter.IncludeResource;
import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;

import java.io.File;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * jar 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class JarResourceLoader extends AbstractResourceLoader {

    private final JarInputStream jarInputStream;

    private ExcludeResource excludeResource = (jarEntry)->false;
    private IncludeResource includeResource = (jarEntry)->true;

    public JarResourceLoader(File file)  throws Exception{
        super(new URL("jar:" + file.toURI().toURL() + "!/"));
        URL url = file.toURI().toURL();
        this.jarInputStream = new JarInputStream(url.openStream());
    }

    public JarResourceLoader(URL url)  throws Exception{
        super(url);
        this.jarInputStream = new JarInputStream(url.openStream());
    }

    public JarResourceLoader(URL url, JarInputStream jarInputStream)  throws Exception{
        super(url);
        this.jarInputStream = jarInputStream;
    }

    public void setExcludeResource(ExcludeResource excludeResource) {
        if(excludeResource == null){
            return;
        }
        this.excludeResource = excludeResource;
    }

    public void setIncludeResource(IncludeResource includeResource) {
        if(includeResource == null){
            return;
        }
        this.includeResource = includeResource;
    }

    @Override
    protected void loadOfChild(ResourceStorage resourceStorage) throws Exception {
        // 解析
        try {
            JarEntry jarEntry = null;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                if(excludeResource.exclude(jarEntry)){
                    continue;
                }
                if(includeResource.include(jarEntry)){
                    String name = resolveName(jarEntry.getName());
                    URL url = new URL(baseUrl.toString() + name);
                    resourceStorage.add(name, url, ()->{
                        return getClassBytes(name, jarInputStream, false);
                    });
                    jarInputStream.closeEntry();
                }
            }
        } finally {
            jarInputStream.close();
        }
    }

    protected String resolveName(String name){
        return name;
    }

}
