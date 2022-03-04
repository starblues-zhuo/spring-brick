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

import com.gitee.starblues.common.DependencyPlugin;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.Assert;

import java.util.List;

import static com.gitee.starblues.common.PluginDescriptorKey.*;

/**
 * 默认插件描述者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginDescriptor implements PluginDescriptor {

    private final String pluginId;
    private final String pluginVersion;
    private final String pluginBootstrapClass;
    private final String pluginPath;


    private PluginType type;
    private String description;
    private String requires;
    private String provider;
    private String license;

    private List<DependencyPlugin> dependencyPlugins;

    public DefaultPluginDescriptor(String pluginId, String pluginVersion,
                                   String pluginClass, String pluginPath) {
        this.pluginId = Assert.isNotEmpty(pluginId, PLUGIN_ID + "不能为空");
        this.pluginVersion = Assert.isNotEmpty(pluginVersion, PLUGIN_VERSION + "不能为空");
        this.pluginBootstrapClass = Assert.isNotEmpty(pluginClass, PLUGIN_BOOTSTRAP_CLASS + "不能为空");
        this.pluginPath = Assert.isNotNull(pluginPath, "插件路径[pluginPath]不能为空");
        check();
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setRequires(String requires) {
        this.requires = requires;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }

    void setLicense(String license) {
        this.license = license;
    }

    void setType(PluginType type) {
        this.type = type;
    }

    void setDependencyPlugins(List<DependencyPlugin> dependencyPlugins) {
        this.dependencyPlugins = dependencyPlugins;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public String getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public String getPluginBootstrapClass() {
        return pluginBootstrapClass;
    }

    @Override
    public String getPluginPath() {
        return pluginPath;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getRequires() {
        return requires;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public List<DependencyPlugin> getDependencyPlugin() {
        return dependencyPlugins;
    }


    @Override
    public PluginType getType() {
        return type;
    }

    private void check(){
        String illegal = PackageStructure.getIllegal(pluginId);
        if(illegal != null){
            throw new PluginException(this, "插件id不能包含:" + illegal);
        }
        illegal = PackageStructure.getIllegal(pluginVersion);
        if(illegal != null){
            throw new PluginException(this, "插件版本号不能包含:" + illegal);
        }
    }

}
