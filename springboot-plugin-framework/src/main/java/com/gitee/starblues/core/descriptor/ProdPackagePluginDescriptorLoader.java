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
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.ManifestUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG;


/**
 * 生产环境打包好的插件 PluginDescriptorLoader 加载者
 * 解析 jar、zip
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPackagePluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    private final PluginType type;
    private PluginResourcesConfig pluginResourcesConfig;

    public ProdPackagePluginDescriptorLoader(PluginType type) {
        this.type = type;
    }

    @Override
    protected Manifest getManifest(Path location) throws Exception {
        try (JarFile jarFile = new JarFile(location.toFile())){
            Manifest manifest = jarFile.getManifest();
            pluginResourcesConfig = getPluginResourcesConfig(jarFile, manifest);
            return manifest;
        }
    }

    @Override
    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Attributes attributes) throws Exception {
        return pluginResourcesConfig;
    }

    @Override
    protected DefaultInsidePluginDescriptor create(Manifest manifest, Path path) throws Exception {
        DefaultInsidePluginDescriptor descriptor = super.create(manifest, path);
        PluginType manifestPluginType = descriptor.getType();
        if(manifestPluginType == null){
            descriptor.setType(type);
            return descriptor;
        }
        return descriptor;
    }

    protected PluginResourcesConfig getPluginResourcesConfig(JarFile jarFile, Manifest manifest) throws Exception {
        Attributes attributes = manifest.getMainAttributes();
        String pluginResourcesConf = ManifestUtils.getValue(attributes, PLUGIN_RESOURCES_CONFIG);
        if(ObjectUtils.isEmpty(pluginResourcesConf)){
            return new PluginResourcesConfig();
        }
        JarEntry jarEntry = jarFile.getJarEntry(pluginResourcesConf);
        if(jarEntry == null){
            return new PluginResourcesConfig();
        }
        InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
        List<String> lines = IOUtils.readLines(jarFileInputStream, PackageStructure.CHARSET_NAME);
        return PluginResourcesConfig.parse(lines);
    }

}
