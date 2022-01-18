package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.extract.DefaultExtractFactory;
import com.gitee.starblues.spring.extract.ExtractFactory;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;

/**
 * 默认的插件交互实现
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginInteractive implements PluginInteractive{

    private final InsidePluginDescriptor pluginDescriptor;
    private final MainApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;
    private final InvokeSupperCache invokeSupperCache;
    private final OpExtractFactory opExtractFactory;

    public DefaultPluginInteractive(InsidePluginDescriptor pluginDescriptor,
                                    MainApplicationContext mainApplicationContext,
                                    IntegrationConfiguration configuration,
                                    InvokeSupperCache invokeSupperCache) {
        this.pluginDescriptor = pluginDescriptor;
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = configuration;
        this.invokeSupperCache = invokeSupperCache;
        this.opExtractFactory = createOpExtractFactory();
    }

    protected OpExtractFactory createOpExtractFactory(){
        DefaultExtractFactory defaultExtractFactory = (DefaultExtractFactory)ExtractFactory.getInstant();
        return (OpExtractFactory) defaultExtractFactory.getTarget();
    }


    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public InvokeSupperCache getInvokeSupperCache() {
        return invokeSupperCache;
    }

    @Override
    public OpExtractFactory getOpExtractFactory() {
        return opExtractFactory;
    }
}
