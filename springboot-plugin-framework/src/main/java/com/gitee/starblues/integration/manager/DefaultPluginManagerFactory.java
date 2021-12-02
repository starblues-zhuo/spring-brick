package com.gitee.starblues.integration.manager;

import com.gitee.starblues.core.*;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.Assert;

import java.util.List;

/**
 * 默认的 PluginManagerFactory
 * @author starBlues
 * @version 3.0.0
 */
@Deprecated
public class DefaultPluginManagerFactory implements PluginManagerFactory{

    private final IntegrationConfiguration configuration;

    public DefaultPluginManagerFactory(IntegrationConfiguration configuration) {
        this.configuration = Assert.isNotNull(configuration, "参数 configuration 不能为空");
    }

    @Override
    public PluginManager getPluginManager() {
        RuntimeMode runtimeMode = Assert.isNotEmpty(configuration.environment(),
                "配置[environment]不能为空");
        String mainPackageName = Assert.isNotEmpty(configuration.mainPackage(),
                "配置[mainPackage]不能为空");
        List<String> pluginPaths = Assert.isNotEmpty(configuration.pluginPath(),
                "配置[pluginPath]不能为空");
        RealizeProvider realizeProvider = new DefaultRealizeProvider(runtimeMode);
        return new DefaultPluginManager(realizeProvider, pluginPaths);
    }
}
