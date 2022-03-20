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


import com.gitee.starblues.common.ManifestKey;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.utils.PropertiesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
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

    private PluginResourcesConfig pluginResourcesConfig;

    public ProdPackagePluginDescriptorLoader() {
    }

    @Override
    protected PluginMeta getPluginMetaInfo(Path location) throws Exception {
        try (JarFile jarFile = new JarFile(location.toFile())){
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String packageType = ManifestKey.getValue(attributes, ManifestKey.PLUGIN_PACKAGE_TYPE);
            String pluginMetaPath = ManifestKey.getValue(attributes, ManifestKey.PLUGIN_META_PATH);
            if(packageType == null || pluginMetaPath == null){
                return null;
            }
            JarEntry jarEntry = jarFile.getJarEntry(pluginMetaPath);
            if(jarEntry == null){
                return null;
            }
            Properties properties = super.getProperties(jarFile.getInputStream(jarEntry));
            if(properties.isEmpty()){
                return null;
            }
            pluginResourcesConfig = getPluginResourcesConfig(jarFile, properties);
            return new PluginMeta(packageType, properties);
        }
    }

    @Override
    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Properties properties) throws Exception {
        return pluginResourcesConfig;
    }

    protected PluginResourcesConfig getPluginResourcesConfig(JarFile jarFile, Properties properties) throws Exception {
        String pluginResourcesConf = PropertiesUtils.getValue(properties, PLUGIN_RESOURCES_CONFIG);
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
