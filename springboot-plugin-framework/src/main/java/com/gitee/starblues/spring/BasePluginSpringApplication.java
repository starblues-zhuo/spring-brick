package com.gitee.starblues.spring;

import com.gitee.starblues.spring.environment.PluginEnvironmentProcessor;
import com.gitee.starblues.spring.environment.PluginLocalConfigFileProcessor;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;
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
    private final PluginBeanDefinitionLoader beanDefinitionLoader;
    private final String configFileName;
    private final List<PluginEnvironmentProcessor> environmentProcessors;


    public BasePluginSpringApplication(ClassLoader classLoader,
                                       Class<?> primarySources){
        this(classLoader, primarySources, null);
    }

    public BasePluginSpringApplication(ClassLoader classLoader,
                                       Class<?> primarySources,
                                       String configFileName){
        Assert.isNotNull(classLoader, "classLoader 不能为空");
        Assert.isNotNull(primarySources, "primarySources 不能为空");

        this.beanFactory = new DefaultListableBeanFactory();
        this.beanFactory.setBeanClassLoader(classLoader);

        this.applicationContext = new PluginApplicationContext(beanFactory, classLoader);
        this.beanDefinitionLoader = new PluginBeanDefinitionLoader(beanFactory, primarySources);
        this.configFileName = configFileName;
        this.environmentProcessors = new ArrayList<>();
        addDefaultEnvironmentProcessor();
    }


    protected void addDefaultEnvironmentProcessor(){
        PluginLocalConfigFileProcessor configFileProcessor = new PluginLocalConfigFileProcessor();
        if(!ObjectUtils.isEmpty(configFileName)){
            configFileProcessor.setSearchNames(configFileName);
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
    public GenericApplicationContext run() {
        synchronized (isStarted){
            if(isStarted.get()){
                throw new RuntimeException("已经运行了PluginSpringApplication, 无法再运行");
            }
            processEnvironment();
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
    public GenericApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
