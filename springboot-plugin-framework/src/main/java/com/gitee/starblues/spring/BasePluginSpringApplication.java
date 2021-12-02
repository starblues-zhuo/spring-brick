package com.gitee.starblues.spring;

import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.environment.PluginEnvironmentProcessor;
import com.gitee.starblues.spring.environment.PluginLocalConfigFileProcessor;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * PluginSpringApplication
 * @author starBlues
 * @version 3.0.0
 */
public class BasePluginSpringApplication implements PluginSpringApplication{

    private final AtomicBoolean isStarted = new AtomicBoolean(false);


    private final DefaultListableBeanFactory beanFactory;
    private final PluginApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;
    private final PluginBeanDefinitionLoader beanDefinitionLoader;
    private final String configFileName;
    private final List<PluginEnvironmentProcessor> environmentProcessors;


    public BasePluginSpringApplication(GenericApplicationContext mainApplicationContext,
                                       ClassLoader classLoader,
                                       Class<?> primarySources){
        this(mainApplicationContext, classLoader, primarySources, null);
    }

    public BasePluginSpringApplication(GenericApplicationContext mainApplicationContext,
                                       ClassLoader classLoader,
                                       Class<?> primarySources,
                                       String configFileName){
        Assert.isNotNull(classLoader, "classLoader 不能为空");
        Assert.isNotNull(primarySources, "primarySources 不能为空");

        this.beanFactory = new PluginListableBeanFactory(mainApplicationContext);
        this.beanFactory.setBeanClassLoader(classLoader);

        this.configuration = mainApplicationContext.getBean(AutoIntegrationConfiguration.class);

        this.applicationContext = new PluginApplicationContext(beanFactory, classLoader);
        this.beanDefinitionLoader = new PluginBeanDefinitionLoader(beanFactory, primarySources);
        this.configFileName = configFileName;
        this.environmentProcessors = new ArrayList<>();
        addDefaultEnvironmentProcessor();
    }


    protected void addDefaultEnvironmentProcessor(){
        PluginLocalConfigFileProcessor configFileProcessor = new PluginLocalConfigFileProcessor(configuration);
        if(!ObjectUtils.isEmpty(configFileName)){
            configFileProcessor.setSearchNames(PluginFileUtils.getFileName(configFileName));
        }
        addEnvironmentProcessor(configFileProcessor);
    }

    public void addEnvironmentProcessor(PluginEnvironmentProcessor environmentProcessor){
        if(environmentProcessor == null){
            return;
        }
        environmentProcessors.add(environmentProcessor);
    }


    @Override
    public ConfigurableApplicationContext run() {
        synchronized (isStarted){
            if(isStarted.get()){
                throw new RuntimeException("已经运行了PluginSpringApplication, 无法再运行");
            }
            processEnvironment();
            addPluginEnvironment();
            addDefaultProcessor();
            loadBean();
            refresh();
            isStarted.set(true);
            return applicationContext;
        }

    }

    protected void processEnvironment() {
        List<PluginEnvironmentProcessor> orderPluginEnvironmentProcessor =
                CommonUtils.order(environmentProcessors, Ordered::getOrder);
        for (PluginEnvironmentProcessor environmentProcessor : orderPluginEnvironmentProcessor) {
            environmentProcessor.postProcessEnvironment(applicationContext.getEnvironment(),
                    applicationContext.getResourceLoader());
        }
    }


    private void addPluginEnvironment() {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> pluginEnvironment = new HashMap<>();
        pluginEnvironment.put(AutoIntegrationConfiguration.ENABLE_KEY, false);
        pluginEnvironment.put(AutoIntegrationConfiguration.ENABLE_STARTER_KEY, false);
        environment.getPropertySources().addFirst(new MapPropertySource("pluginEnvironment", pluginEnvironment));
    }

    private void addDefaultProcessor() {
        // 注册 ConfigurationPropertiesBindingPostProcessor, 用于将配置信息绑定到Bean上
        ConfigurationPropertiesBindingPostProcessor.register(applicationContext);
    }


    protected void loadBean() {
        beanDefinitionLoader.load();
    }

    protected void refresh() {
        applicationContext.refresh();
    }

    @Override
    public void close() {
        synchronized (isStarted){
            if(!isStarted.get()){
                throw new RuntimeException("PluginSpringApplication没有运行, 不能close");
            }
            applicationContext.close();
            isStarted.set(false);
        }
    }

    @Override
    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
