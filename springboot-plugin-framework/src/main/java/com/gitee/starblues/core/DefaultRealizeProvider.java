package com.gitee.starblues.core;

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
        PluginDescriptorLoader pluginDescriptorLoader = null;
        if(configuration.environment() == RuntimeMode.DEV){
            basePluginScanner.setPathResolve(new DevPathResolve());
            pluginDescriptorLoader = new DevPluginDescriptorLoader();
        } else {
            basePluginScanner.setPathResolve(new ProdPathResolve());
            pluginDescriptorLoader = new ProdPluginDescriptorLoader();
        }

        setPluginScanner(basePluginScanner);
        setPluginDescriptorLoader(pluginDescriptorLoader);
        setVersionInspector(new SemverVersionInspector());
    }

    public void setPluginScanner(PluginScanner pluginScanner) {
        this.pluginScanner = Assert.isNotNull(pluginScanner, "pluginScanner 不能为空");
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
    public PluginDescriptorLoader getPluginDescriptorLoader() {
        return Assert.isNotNull(pluginDescriptorLoader, "PluginDescriptorLoader 实现不能为空");
    }

    @Override
    public VersionInspector getVersionInspector() {
        return Assert.isNotNull(versionInspector, "VersionInspector 实现不能为空");
    }
}
