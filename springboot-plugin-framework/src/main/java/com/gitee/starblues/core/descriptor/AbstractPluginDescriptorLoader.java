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
import com.gitee.starblues.utils.PropertiesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.MANIFEST;
import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.utils.PropertiesUtils.getValue;

/**
 * 抽象的 PluginDescriptorLoader
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        PluginMeta pluginMeta = null;
        try {
            pluginMeta = getPluginMetaInfo(location);
            if(pluginMeta == null || pluginMeta.getPluginMetaInfo() == null){
                logger.debug("路径[{}]没有发现插件配置信息", location);
                return null;
            }
            return create(pluginMeta, location);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() throws Exception {

    }

    /**
     * 子类获取插件信息
     * @param location 路径
     * @return Properties
     * @throws Exception 异常
     */
    protected abstract PluginMeta getPluginMetaInfo(Path location) throws Exception;

    protected DefaultInsidePluginDescriptor create(PluginMeta pluginMeta, Path path) throws Exception{
        Properties properties = pluginMeta.getPluginMetaInfo();
        DefaultInsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(
                getValue(properties, PLUGIN_ID),
                getValue(properties, PLUGIN_VERSION),
                getValue(properties, PLUGIN_BOOTSTRAP_CLASS),
                path
        );
        descriptor.setType(PluginType.byName(pluginMeta.getPackageType()));

        PluginResourcesConfig pluginResourcesConfig = getPluginResourcesConfig(path, properties);

        descriptor.setPluginLibInfo(getPluginLibInfo(pluginResourcesConfig.getDependenciesIndex()));
        descriptor.setIncludeMainResourcePatterns(pluginResourcesConfig.getLoadMainResourceIncludes());
        descriptor.setExcludeMainResourcePatterns(pluginResourcesConfig.getLoadMainResourceExcludes());

        descriptor.setProperties(properties);
        descriptor.setPluginClassPath(getValue(properties, PLUGIN_PATH, false));
        descriptor.setDescription(getValue(properties, PLUGIN_DESCRIPTION, false));
        descriptor.setRequires(getValue(properties, PLUGIN_REQUIRES, false));
        descriptor.setProvider(getValue(properties, PLUGIN_PROVIDER, false));
        descriptor.setLicense(getValue(properties, PLUGIN_LICENSE, false));
        descriptor.setConfigFileName(getValue(properties, PLUGIN_CONFIG_FILE_NAME, false));
        descriptor.setConfigFileLocation(getValue(properties, PLUGIN_CONFIG_FILE_LOCATION, false));
        descriptor.setArgs(getValue(properties, PLUGIN_ARGS, false));

        descriptor.setDependencyPlugins(getPluginDependency(properties));
        return descriptor;
    }


    protected List<DependencyPlugin> getPluginDependency(Properties properties){
        return AbstractDependencyPlugin.toList(getValue(properties, PLUGIN_DEPENDENCIES, false),
                DefaultDependencyPlugin::new);
    }

    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Properties properties) throws Exception{
        String libIndex = getValue(properties, PLUGIN_RESOURCES_CONFIG);
        if(ObjectUtils.isEmpty(libIndex)){
            return new PluginResourcesConfig();
        }
        File file = new File(libIndex);
        if(!file.exists()){
            // 如果绝对路径不存在, 则判断相对路径
            String pluginPath = getValue(properties, PLUGIN_PATH);
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

    protected Properties getProperties(InputStream inputStream) throws Exception{
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);){
            properties.load(reader);
            return properties;
        }
    }

    @AllArgsConstructor
    @Getter
    public static class PluginMeta{
        private final String packageType;
        private final Properties pluginMetaInfo;
    }

}
