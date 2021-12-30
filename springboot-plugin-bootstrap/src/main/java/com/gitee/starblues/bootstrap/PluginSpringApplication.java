package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginSpringApplication extends SpringApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SpringPluginProcessor pluginProcessor;
    private final ProcessorContext processorContext;

    private final GenericApplicationContext applicationContext;
    private final DefaultListableBeanFactory beanFactory;
    private final ResourceLoader resourceLoader;
    private final ConfigurePluginEnvironment configurePluginEnvironment;

    public PluginSpringApplication(SpringPluginProcessor pluginProcessor,
                                   ProcessorContext processorContext,
                                   Class<?>... primarySources) {
        super(primarySources);
        this.pluginProcessor = pluginProcessor;
        this.processorContext = processorContext;
        this.resourceLoader = processorContext.getResourceLoader();
        this.beanFactory = new PluginListableBeanFactory(processorContext.getMainApplicationContext());
        this.applicationContext = new PluginApplicationContext(beanFactory, processorContext);
        this.configurePluginEnvironment = new ConfigurePluginEnvironment(processorContext.getPluginDescriptor());
        setDefaultConfig();
    }

    private void setDefaultConfig(){
        setResourceLoader(resourceLoader);
        setBannerMode(Banner.Mode.OFF);
        setEnvironment(new StandardEnvironment());
        setWebApplicationType(WebApplicationType.NONE);
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

    @Override
    public ConfigurableApplicationContext run(String... args) {
        try {
            processorContext.setApplicationContext(this.applicationContext);
            pluginProcessor.initialize(processorContext);
            return super.run(args);
        } catch (Exception e) {
            pluginProcessor.failure(processorContext);
            logger.error("启动插件[{}]失败. {}",
                    processorContext.getPluginDescriptor().getPluginId(),
                    e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        pluginProcessor.refreshBefore(processorContext);
        super.refresh(applicationContext);
        pluginProcessor.refreshAfter(processorContext);
    }

}
