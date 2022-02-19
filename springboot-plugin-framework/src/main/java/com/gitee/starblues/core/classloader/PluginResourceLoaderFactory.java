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

import com.gitee.starblues.core.descriptor.PluginType;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.loader.classloader.ResourceLoaderFactory;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.util.Set;

/**
 * 插件资源加载者工厂
 * @author starBlues
 * @version 3.0.0
 */
public class PluginResourceLoaderFactory extends ResourceLoaderFactory {


    public synchronized void addResource(InsidePluginDescriptor descriptor) throws Exception{
        PluginType pluginType = descriptor.getType();
        if(pluginType == PluginType.JAR || pluginType == PluginType.ZIP){
            NestedPluginJarResourceLoader resourceLoader =
                    new NestedPluginJarResourceLoader(descriptor, this);
            resourceLoader.init();
            addResourceLoader(resourceLoader);
        } else if(pluginType == PluginType.DIR || pluginType == PluginType.DEV){
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
        Set<String> pluginLibPaths = pluginDescriptor.getPluginLibPaths();
        if(ObjectUtils.isEmpty(pluginLibPaths)){
            return;
        }
        for (String pluginLibPath : pluginLibPaths) {
            File existFile = FilesUtils.getExistFile(pluginLibPath);
            if(existFile != null){
                addResource(existFile);
            }
        }
    }

}
