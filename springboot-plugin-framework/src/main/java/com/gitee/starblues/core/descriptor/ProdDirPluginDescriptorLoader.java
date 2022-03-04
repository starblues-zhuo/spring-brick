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
import com.gitee.starblues.common.PackageType;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.utils.PropertiesUtils;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;


/**
 * 生产环境目录式插件 PluginDescriptorLoader 加载者
 * 解析生产的dir
 * @author starBlues
 * @version 3.0.0
 */
public class ProdDirPluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    @Override
    protected PluginMeta getPluginMetaInfo(Path location) throws Exception {
        File file = new File(FilesUtils.joiningFilePath(location.toString(), resolvePath(PROD_MANIFEST_PATH)));
        if(!file.exists()){
            return null;
        }
        Manifest manifest = new Manifest();
        String packageType = null;
        String pluginMetaPath = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            manifest.read(fileInputStream);
            Attributes attributes = manifest.getMainAttributes();
            packageType = ManifestKey.getValue(attributes, ManifestKey.PLUGIN_PACKAGE_TYPE);
            pluginMetaPath = ManifestKey.getValue(attributes, ManifestKey.PLUGIN_META_PATH);
        }
        if(packageType == null || pluginMetaPath == null){
            return null;
        }

        File pluginMetaFile = new File(FilesUtils.joiningFilePath(location.toString(), pluginMetaPath));
        if(!pluginMetaFile.exists()){
            return null;
        }
        Properties properties = super.getProperties(new FileInputStream(pluginMetaFile));
        if(properties.isEmpty()){
            return null;
        }
        return new PluginMeta(packageType, properties);
    }

    @Override
    protected DefaultInsidePluginDescriptor create(PluginMeta pluginMeta, Path path) throws Exception {
        DefaultInsidePluginDescriptor descriptor = super.create(pluginMeta, path);
        String pathStr = path.toFile().getPath();
        descriptor.setPluginClassPath(FilesUtils.joiningFilePath(
                pathStr, descriptor.getPluginClassPath()
        ));
        return descriptor;
    }

    @Override
    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Properties properties) throws Exception {
        String pathStr = path.toFile().getPath();
        String libIndexFile = getExistResourcesConfFile(
                pathStr, PropertiesUtils.getValue(properties, PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG)
        );

        if(libIndexFile == null){
            return new PluginResourcesConfig();
        }
        File libFile = new File(libIndexFile);
        List<String> lines = FileUtils.readLines(libFile, CHARSET_NAME);
        PluginResourcesConfig pluginResourcesConfig = PluginResourcesConfig.parse(lines);

        Set<String> dependenciesIndex = pluginResourcesConfig.getDependenciesIndex();
        Set<String> pluginLibPaths = new HashSet<>();
        for (String index : dependenciesIndex) {
            index = resolvePath(index);
            File file = new File(index);
            if(!file.exists()){
                // 如果直接读取的路径不存在, 则从相对路径读取
                file = new File(FilesUtils.joiningFilePath(
                        pathStr, index
                ));
            }
            if(file.exists()){
                pluginLibPaths.add(file.getPath());
            }
        }
        pluginResourcesConfig.setDependenciesIndex(pluginLibPaths);
        return pluginResourcesConfig;
    }

    protected String getExistResourcesConfFile(String rootPath, String libIndexPath){
        libIndexPath = resolvePath(libIndexPath);
        if(ObjectUtils.isEmpty(libIndexPath)){
            // 如果配置为空, 直接从默认路径读取
            libIndexPath = FilesUtils.joiningFilePath(rootPath, resolvePath(PROD_RESOURCES_DEFINE_PATH));
        } else {
            if(Files.exists(Paths.get(libIndexPath))){
                return libIndexPath;
            }
            libIndexPath = FilesUtils.joiningFilePath(rootPath, libIndexPath);
        }
        if(Files.exists(Paths.get(libIndexPath))){
            return libIndexPath;
        }
        return null;
    }



}
