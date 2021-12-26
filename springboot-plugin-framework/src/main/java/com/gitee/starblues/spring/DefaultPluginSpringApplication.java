package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.environment.PluginEnvironmentProcessor;
import com.gitee.starblues.spring.environment.PluginLocalConfigFileProcessor;
import com.gitee.starblues.spring.listener.*;
import com.gitee.starblues.spring.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.processor.SpringPluginProcessorFactory;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ResourceLoader;

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
public class DefaultPluginSpringApplication implements PluginSpringApplication{

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    private final GenericApplicationContext mainApplicationContext;

    private final DefaultListableBeanFactory beanFactory;
    private final PluginApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;
    private final List<PluginEnvironmentProcessor> environmentProcessors;

    private final SpringPluginRegistryInfo registryInfo;

    private SpringPluginProcessor springPluginProcessor;

    public DefaultPluginSpringApplication(GenericApplicationContext mainApplicationContext,
                                          SpringPluginRegistryInfo registryInfo){
        Assert.isNotNull(mainApplicationContext, "参数 mainApplicationContext 不能为空");
        Assert.isNotNull(registryInfo, "参数 registryInfo 不能为空");
        this.mainApplicationContext = mainApplicationContext;
        this.registryInfo = registryInfo;

        PluginWrapper pluginWrapper = registryInfo.getPluginWrapper();
        ClassLoader classLoader = pluginWrapper.getPluginClassLoader();
        this.beanFactory = new PluginListableBeanFactory(mainApplicationContext);
        this.beanFactory.setBeanClassLoader(classLoader);
        this.configuration = mainApplicationContext.getBean(AutoIntegrationConfiguration.class);
        this.applicationContext = new PluginApplicationContext(beanFactory, classLoader);
        this.environmentProcessors = new ArrayList<>();
        addDefaultEnvironmentProcessor(pluginWrapper);

        springPluginProcessor = new SpringPluginProcessorFactory(SpringPluginProcessor.RunMode.PLUGIN);
    }

    protected void addDefaultEnvironmentProcessor(PluginWrapper pluginWrapper){
        String configFileName = pluginWrapper.getPluginDescriptor().getConfigFileName();
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
    public GenericApplicationContext run() throws Exception{
        synchronized (isStarted){
            if(isStarted.get()){
                throw new RuntimeException("已经运行了PluginSpringApplication, 无法再运行");
            }
            try {
                springPluginProcessor.initialize(mainApplicationContext);
                processEnvironment();
                addPluginEnvironment();
                addDefaultProcessor();
                springPluginProcessor.refreshBefore(registryInfo);
                refresh();
                springPluginProcessor.refreshAfter(registryInfo);
                isStarted.set(true);
                return applicationContext;
            } catch (Exception e){
                springPluginProcessor.failure(registryInfo);
                throw e;
            }
        }
    }

    private PluginSpringApplicationRunListeners getRunListeners() {
        PluginSpringApplicationRunListeners runListeners = new PluginSpringApplicationRunListeners(ListenerRunMode.PLUGIN);
        addDefaultListeners(runListeners);
        return runListeners;
    }

    protected void addDefaultListeners(PluginSpringApplicationRunListeners runListeners){
        runListeners.addListener(new ClassScannerListener());
        runListeners.addListener(new BeanRegistryListener());
        runListeners.addListener(new NecessaryBeanRegistryListener());
        runListeners.addListener(new InvokeOtherPluginRegistryListener());
        runListeners.addListener(new PluginControllerRegistryListener(mainApplicationContext));
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
        applicationContext.register(PropertySourcesPlaceholderConfigurer.class);
    }


    protected void loadBean() {
        //beanDefinitionLoader.load();
    }

    protected void refresh() {
        applicationContext.refresh();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

    @Override
    public void close() {
        synchronized (isStarted){
            if(!isStarted.get()){
                throw new RuntimeException("PluginSpringApplication没有运行, 不能close");
            }
            try {
                springPluginProcessor.close(registryInfo);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                applicationContext.close();
                isStarted.set(false);
            }
        }
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return applicationContext.getResourceLoader();
    }
}
