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
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;

import java.io.File;
import java.util.Set;

/**
 * 插件资源加载者工厂
 * @author starBlues
 * @version 3.0.0
 */
public class PluginResourceLoaderFactory extends ResourceLoaderFactory{


    public synchronized void addResource(InsidePluginDescriptor pluginDescriptor) throws Exception{
        PluginDescriptor.Type type = pluginDescriptor.getType();
        if(type == PluginDescriptor.Type.JAR || type == PluginDescriptor.Type.ZIP){
            NestedJarResourceLoader resourceLoader = new NestedJarResourceLoader(pluginDescriptor, this);
            resourceLoader.init();
            addResourceLoader(resourceLoader);
        } else if(type == PluginDescriptor.Type.DIR || type == PluginDescriptor.Type.DEV){
            addClasspath(pluginDescriptor);
            addLibFile(pluginDescriptor);
        }
    }

    private void addClasspath(InsidePluginDescriptor pluginDescriptor) throws Exception {
        String pluginClassPath = pluginDescriptor.getPluginClassPath();
        File existFile = PluginFileUtils.getExistFile(pluginClassPath);
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
            File existFile = PluginFileUtils.getExistFile(pluginLibPath);
            if(existFile != null){
                addResource(existFile);
            }
        }
    }

}
