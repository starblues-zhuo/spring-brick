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

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PackageType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.jar.Manifest;

/**
 * 开发环境 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class DevPluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    @Override
    protected PluginMeta getPluginMetaInfo(Path location) throws Exception {
        String pluginMetaPath = location.toString() + File.separator + PackageStructure.PLUGIN_META_NAME;
        File file = new File(pluginMetaPath);
        if(!file.exists()){
            return null;
        }
        Path path = Paths.get(pluginMetaPath);
        Properties properties = super.getProperties(Files.newInputStream(path));
        if(properties.isEmpty()){
            return null;
        }
        return new PluginMeta(PackageType.PLUGIN_PACKAGE_TYPE_DEV, properties);
    }

    @Override
    protected DefaultInsidePluginDescriptor create(PluginMeta pluginMeta, Path path) throws Exception {
        final DefaultInsidePluginDescriptor descriptor = super.create(pluginMeta, path);
        descriptor.setType(PluginType.DEV);
        return descriptor;
    }

}
