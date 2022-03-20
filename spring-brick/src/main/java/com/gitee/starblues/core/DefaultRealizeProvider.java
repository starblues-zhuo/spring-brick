/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core;

import com.gitee.starblues.core.checker.ComposePluginBasicChecker;
import com.gitee.starblues.core.checker.DefaultPluginBasicChecker;
import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.descriptor.ComposeDescriptorLoader;
import com.gitee.starblues.core.descriptor.DevPluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.ProdPluginDescriptorLoader;
import com.gitee.starblues.core.scanner.BasePluginScanner;
import com.gitee.starblues.core.scanner.DevPathResolve;
import com.gitee.starblues.core.scanner.PluginScanner;
import com.gitee.starblues.core.scanner.ProdPathResolve;
import com.gitee.starblues.core.version.SemverVersionInspector;
import com.gitee.starblues.core.version.VersionInspector;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.Assert;
import org.springframework.context.ApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultRealizeProvider implements RealizeProvider {

    private PluginScanner pluginScanner;
    private PluginBasicChecker pluginBasicChecker;
    private PluginDescriptorLoader pluginDescriptorLoader;
    private VersionInspector versionInspector;

    protected final IntegrationConfiguration configuration;
    protected final ApplicationContext applicationContext;

    public DefaultRealizeProvider(IntegrationConfiguration configuration,
                                  ApplicationContext applicationContext){
        this.configuration = Assert.isNotNull(configuration, "参数 configuration 不能为空");
        this.applicationContext = Assert.isNotNull(applicationContext, "参数 configuration 不能为空");
    }

    @Override
    public void init() {
        BasePluginScanner basePluginScanner = new BasePluginScanner();
        if(configuration.environment() == RuntimeMode.DEV){
            basePluginScanner.setPathResolve(new DevPathResolve());
        } else {
            basePluginScanner.setPathResolve(new ProdPathResolve());
        }
        setPluginScanner(basePluginScanner);
        setPluginBasicChecker(new ComposePluginBasicChecker(applicationContext));
        setPluginDescriptorLoader(new ComposeDescriptorLoader(pluginBasicChecker));
        setVersionInspector(new SemverVersionInspector());
    }

    public void setPluginScanner(PluginScanner pluginScanner) {
        this.pluginScanner = Assert.isNotNull(pluginScanner, "pluginScanner 不能为空");
    }

    public void setPluginBasicChecker(PluginBasicChecker pluginBasicChecker) {
        this.pluginBasicChecker =  Assert.isNotNull(pluginBasicChecker,
                "pluginBasicChecker 不能为空");
    }

    public void setPluginDescriptorLoader(PluginDescriptorLoader pluginDescriptorLoader) {
        this.pluginDescriptorLoader = Assert.isNotNull(pluginDescriptorLoader,
                "pluginDescriptorLoader 不能为空");
    }

    public void setVersionInspector(VersionInspector versionInspector) {
        this.versionInspector = Assert.isNotNull(versionInspector, "versionInspector 不能为空");
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        return configuration.environment();
    }

    @Override
    public PluginScanner getPluginScanner() {
        return Assert.isNotNull(pluginScanner, "PluginScanner 实现不能为空");
    }

    @Override
    public PluginBasicChecker getPluginBasicChecker() {
        return Assert.isNotNull(pluginBasicChecker, "pluginBasicChecker 实现不能为空");
    }

    @Override
    public PluginDescriptorLoader getPluginDescriptorLoader() {
        return Assert.isNotNull(pluginDescriptorLoader, "PluginDescriptorLoader 实现不能为空");
    }

    @Override
    public VersionInspector getVersionInspector() {
        return Assert.isNotNull(versionInspector, "VersionInspector 实现不能为空");
    }
}
