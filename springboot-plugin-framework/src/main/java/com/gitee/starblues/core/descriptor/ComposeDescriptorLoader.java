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

package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合插件描述加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ComposeDescriptorLoader implements PluginDescriptorLoader{
    
    private final List<PluginDescriptorLoader> pluginDescriptorLoaders = new ArrayList<>();
    
    private final PluginBasicChecker pluginChecker;

    public ComposeDescriptorLoader(PluginBasicChecker pluginChecker) {
        this.pluginChecker = pluginChecker;
        addDefaultLoader();
    }

    protected void addDefaultLoader(){
        addLoader(new DevPluginDescriptorLoader());
        addLoader(new ProdPluginDescriptorLoader());
    }


    public void addLoader(PluginDescriptorLoader descriptorLoader){
        if(descriptorLoader != null){
            pluginDescriptorLoaders.add(descriptorLoader);
        }
    }
    
    
    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        for (PluginDescriptorLoader pluginDescriptorLoader : pluginDescriptorLoaders) {
            try {
                InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(location);
                if(pluginDescriptor != null){
                    pluginChecker.checkDescriptor(pluginDescriptor);
                    return pluginDescriptor;
                }
            } catch (Exception e){
                // 忽略异常
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
