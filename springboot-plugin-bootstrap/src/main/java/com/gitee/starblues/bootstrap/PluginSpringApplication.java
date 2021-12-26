package com.gitee.starblues.bootstrap;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginSpringApplication extends SpringApplication {

    private final PluginDescriptor pluginDescriptor;

    private final ConfigurableApplicationContext applicationContext;
    private final DefaultListableBeanFactory beanFactory;
    private final ResourceLoader resourceLoader;
    private final ConfigurePluginEnvironment configurePluginEnvironment;

    public PluginSpringApplication(PluginInteractive pluginInteractive, Class<?>... primarySources) {
        super(primarySources);
        this.pluginDescriptor = pluginInteractive.getPluginDescriptor();
        this.resourceLoader = new DefaultResourceLoader(getPluginClassLoader());
        this.beanFactory = new PluginListableBeanFactory();
        this.applicationContext = new PluginApplicationContext(beanFactory, resourceLoader);
        this.configurePluginEnvironment = new ConfigurePluginEnvironment(pluginDescriptor);
        setDefaultConfig();
    }

    private void setDefaultConfig(){
        setResourceLoader(resourceLoader);
        setBannerMode(Banner.Mode.OFF);
        setEnvironment(new StandardEnvironment());
    }

    @Override
    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        super.configureEnvironment(environment, args);
        configurePluginEnvironment.configureEnvironment(environment, args);
    }

    @Override
    protected ConfigurableApplicationContext createApplicationContext() {
        return applicationContext;
    }

    private ClassLoader getPluginClassLoader(){
        return PluginSpringApplication.class.getClassLoader();
    }

}
