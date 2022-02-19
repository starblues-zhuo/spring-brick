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

package com.gitee.starblues.loader.classloader;

import com.gitee.starblues.loader.classloader.filter.ExcludeResource;

import java.net.URL;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * jar 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class JarResourceLoader extends AbstractResourceLoader{

    private final JarInputStream jarInputStream;

    private ExcludeResource excludeResource = (name)->false;


    private Function<String, String> function = name->name;

    public JarResourceLoader(URL url)  throws Exception{
        super(new URL("jar:" + url.toString() + "!/"));
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

    public void setFunction(Function<String, String> function) {
        this.function = function;
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 解析
        try {
            JarEntry jarEntry = null;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                String name = jarEntry.getName();
                name = function.apply(name);
                if(excludeResource.exclude(name)){
                    continue;
                }
                URL url = new URL(baseUrl.toString() + name);
                Resource resource = new Resource(name, baseUrl, url);
                resource.setBytes(getClassBytes(name, jarInputStream, false));
                addResource(name, resource);
                jarInputStream.closeEntry();
            }
        } finally {
            jarInputStream.close();
        }
    }


}
