package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.plugin.CacheRegistryInfo;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultProcessorContext extends CacheRegistryInfo implements ProcessorContext{

    private final PluginInteractive pluginInteractive;
    private final Class<? extends SpringPluginBootstrap> runnerClass;
    private final MainApplicationContext mainApplicationContext;
    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;

    private final IntegrationConfiguration configuration;

    private GenericApplicationContext applicationContext;

    public DefaultProcessorContext(PluginInteractive pluginInteractive,
                                   Class<? extends SpringPluginBootstrap> runnerClass) {
        this.pluginInteractive = pluginInteractive;
        this.runnerClass = runnerClass;
        this.classLoader = getPluginClassLoader();
        this.resourceLoader = new DefaultResourceLoader(this.classLoader);
        this.mainApplicationContext = pluginInteractive.getMainApplicationContext();
        this.configuration = pluginInteractive.getConfiguration();
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginInteractive.getPluginDescriptor();
    }

    @Override
    public Class<? extends SpringPluginBootstrap> getRunnerClass() {
        return runnerClass;
    }

    @Override
    public PluginInteractive getPluginInteractive() {
        return pluginInteractive;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public SpringBeanFactory getMainBeanFactory() {
        return mainApplicationContext.getSpringBeanFactory();
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        if(applicationContext == null){
            throw new IllegalStateException("GenericApplicationContext 未初始化!");
        }
        return applicationContext;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public void setApplicationContext(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    protected ClassLoader getPluginClassLoader(){
        return ClassUtils.getDefaultClassLoader();
    }
}
