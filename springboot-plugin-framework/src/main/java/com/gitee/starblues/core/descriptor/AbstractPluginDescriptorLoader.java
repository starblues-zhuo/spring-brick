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


import com.gitee.starblues.common.*;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ManifestUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.MANIFEST;
import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.utils.ManifestUtils.getValue;

/**
 * 抽象的 PluginDescriptorLoader
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        Manifest manifest = null;
        try {
            manifest = getManifest(location);
            if(manifest == null){
                logger.debug("路径[{}]没有发现[{}]", location, MANIFEST);
                return null;
            }
            return create(manifest, location);
        } catch (Exception e) {
            logger.error("路径[{}]中存在非法[{}]: {}", location, MANIFEST, e.getMessage());
            return null;
        }
    }

    @Override
    public void close() throws Exception {

    }

    /**
     * 子类获取 Properties
     * @param location properties 路径
     * @return Properties
     * @throws Exception 异常
     */
    protected abstract Manifest getManifest(Path location) throws Exception;

    protected DefaultInsidePluginDescriptor create(Manifest manifest, Path path) throws Exception{
        Attributes attributes = manifest.getMainAttributes();
        DefaultInsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(
                getValue(attributes, PLUGIN_ID),
                getValue(attributes, PLUGIN_VERSION),
                getValue(attributes, PLUGIN_BOOTSTRAP_CLASS),
                path
        );
        PluginResourcesConfig pluginResourcesConfig = getPluginResourcesConfig(path, attributes);

        descriptor.setPluginLibInfo(getPluginLibInfo(pluginResourcesConfig.getDependenciesIndex()));
        descriptor.setIncludeMainResourcePatterns(pluginResourcesConfig.getLoadMainResourceIncludes());
        descriptor.setExcludeMainResourcePatterns(pluginResourcesConfig.getLoadMainResourceExcludes());

        descriptor.setManifest(manifest);
        descriptor.setPluginClassPath(getValue(attributes, PLUGIN_PATH, false));
        descriptor.setDescription(getValue(attributes, PLUGIN_DESCRIPTION, false));
        descriptor.setRequires(getValue(attributes, PLUGIN_REQUIRES, false));
        descriptor.setProvider(getValue(attributes, PLUGIN_PROVIDER, false));
        descriptor.setLicense(getValue(attributes, PLUGIN_LICENSE, false));
        descriptor.setConfigFileName(getValue(attributes, PLUGIN_CONFIG_FILE_NAME, false));
        descriptor.setConfigFileLocation(getValue(attributes, PLUGIN_CONFIG_FILE_LOCATION, false));

        descriptor.setType(getPluginType(attributes));

        descriptor.setDependencyPlugins(getPluginDependency(attributes));
        return descriptor;
    }

    protected PluginType getPluginType(Attributes attributes){
        String packageType = ManifestUtils.getValue(attributes, PluginDescriptorKey.PLUGIN_PACKAGE_TYPE, false);
        if(Objects.equals(packageType, PackageType.PLUGIN_PACKAGE_TYPE_JAR)){
            return PluginType.JAR;
        } else if(Objects.equals(packageType, PackageType.PLUGIN_PACKAGE_TYPE_JAR_OUTER)){
            return PluginType.JAR_OUTER;
        } else if(Objects.equals(packageType, PackageType.PLUGIN_PACKAGE_TYPE_ZIP)){
            return PluginType.ZIP;
        } else if(Objects.equals(packageType, PackageType.PLUGIN_PACKAGE_TYPE_ZIP_OUTER)){
            return PluginType.ZIP_OUTER;
        } else {
            return null;
        }
    }

    protected List<DependencyPlugin> getPluginDependency(Attributes attributes){
        return AbstractDependencyPlugin.toList(getValue(attributes, PLUGIN_DEPENDENCIES, false),
                DefaultDependencyPlugin::new);
    }


    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Attributes attributes) throws Exception{
        String libIndex = getValue(attributes, PLUGIN_RESOURCES_CONFIG);
        if(ObjectUtils.isEmpty(libIndex)){
            return new PluginResourcesConfig();
        }
        File file = new File(libIndex);
        if(!file.exists()){
            // 如果绝对路径不存在, 则判断相对路径
            String pluginPath = getValue(attributes, PLUGIN_PATH);
            file = new File(FilesUtils.joiningFilePath(pluginPath, libIndex));
        }
        if(!file.exists()){
            // 都不存在, 则返回为空
            return new PluginResourcesConfig();
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return PluginResourcesConfig.parse(lines);
        } catch (IOException e) {
            throw new Exception("Load plugin lib index path failure. " + libIndex, e);
        }
    }

    protected Set<PluginLibInfo> getPluginLibInfo(Set<String> dependenciesIndex){
        if(ObjectUtils.isEmpty(dependenciesIndex)){
            return Collections.emptySet();
        }
        Set<PluginLibInfo> pluginLibInfos = new HashSet<>(dependenciesIndex.size());
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        for (String index : dependenciesIndex) {
            String libPath;
            boolean loadToMain;
            if(index.endsWith(Constants.LOAD_TO_MAIN_SIGN)){
                libPath = index.substring(0, index.lastIndexOf(Constants.LOAD_TO_MAIN_SIGN));
                loadToMain = true;
            } else {
                libPath = index;
                loadToMain = false;
            }
            pluginLibInfos.add(new PluginLibInfo(FilesUtils.resolveRelativePath(absolutePath, libPath), loadToMain));
        }
        return pluginLibInfos;
    }

    protected Manifest getManifest(InputStream inputStream) throws Exception{
        Manifest manifest = new Manifest();
        try {
            manifest.read(inputStream);
            return manifest;
        } finally {
            inputStream.close();
        }
    }

}
