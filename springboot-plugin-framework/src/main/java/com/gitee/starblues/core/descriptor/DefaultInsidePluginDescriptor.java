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

import lombok.Setter;

import java.nio.file.Path;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * 内部的默认插件描述者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultInsidePluginDescriptor extends DefaultPluginDescriptor implements InsidePluginDescriptor {

    private final Path pluginPath;
    private final String pluginFileName;

    @Setter
    private String pluginClassPath;
    @Setter
    private Manifest manifest;
    @Setter
    private String configFileName;
    @Setter
    private String configFileLocation;
    @Setter
    private Set<PluginLibInfo> pluginLibInfo;
    @Setter
    private Set<String> includeMainResourcePatterns;
    @Setter
    private Set<String> excludeMainResourcePatterns;

    public DefaultInsidePluginDescriptor(String pluginId, String pluginVersion, String pluginClass, Path pluginPath) {
        super(pluginId, pluginVersion, pluginClass, pluginPath.toAbsolutePath().toString());
        this.pluginPath = pluginPath;
        this.pluginFileName = pluginPath.toFile().getName();
    }


    @Override
    public String getPluginClassPath() {
        return pluginClassPath;
    }

    @Override
    public Set<PluginLibInfo> getPluginLibInfo() {
        return pluginLibInfo;
    }

    @Override
    public Set<String> getIncludeMainResourcePatterns() {
        return includeMainResourcePatterns;
    }

    @Override
    public Set<String> getExcludeMainResourcePatterns() {
        return excludeMainResourcePatterns;
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    @Override
    public String getConfigFileLocation() {
        return configFileLocation;
    }

    @Override
    public Path getInsidePluginPath() {
        return pluginPath;
    }

    @Override
    public String getPluginFileName() {
        return pluginFileName;
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public PluginDescriptor toPluginDescriptor() {
        Path pluginPath = getInsidePluginPath();
        if(getType() == PluginType.DEV) {
            // dev模式 插件路径展示项目目录
            pluginPath = pluginPath.getParent().getParent();
        }
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor(
                getPluginId(), getPluginVersion(), getPluginBootstrapClass(), pluginPath.toAbsolutePath().toString()
        );
        descriptor.setType(getType());
        descriptor.setDescription(getDescription());
        descriptor.setProvider(getProvider());
        descriptor.setRequires(getRequires());
        descriptor.setLicense(getLicense());
        return descriptor;
    }

}
