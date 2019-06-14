package com.plugin.development.integration;

import com.plugin.development.context.*;
import com.plugin.development.integration.operator.DefaultPluginOperator;
import com.plugin.development.integration.operator.PluginOperator;
import com.plugin.development.integration.user.DefaultPluginUser;
import com.plugin.development.integration.user.PluginUser;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 开发者直接使用的。插件应用
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultPluginApplication implements ApplicationContextAware, PluginApplication {

    private final Logger log = LoggerFactory.getLogger(DefaultPluginApplication.class);

    private ApplicationContext applicationContext;
    private PluginManager pluginManager;


    private PluginUser pluginUser;
    private PluginOperator pluginOperator;
    private PluginContextFactory pluginContextFactory;

    private List<PluginSpringBeanListener> pluginSpringBeanListeners = new ArrayList<>();

    public DefaultPluginApplication() {
        this(null, null);
    }

    public DefaultPluginApplication(PluginContextFactory pluginContextFactory) {
        this(pluginContextFactory, null);
    }

    public DefaultPluginApplication(List<PluginSpringBeanListener> pluginSpringBeanListeners) {
        this(null, pluginSpringBeanListeners);
    }

    public DefaultPluginApplication(PluginContextFactory pluginContextFactory,
                                    List<PluginSpringBeanListener> pluginSpringBeanListeners) {
        this.pluginContextFactory = pluginContextFactory;
        if(pluginSpringBeanListeners != null && !pluginSpringBeanListeners.isEmpty()){
            this.pluginSpringBeanListeners.addAll(pluginSpringBeanListeners);
        }
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Objects.requireNonNull(applicationContext);
        this.applicationContext = applicationContext;
        this.pluginManager = applicationContext.getBean(PluginManager.class);
        if(pluginContextFactory == null){
            PluginContext pluginContext = new DefaultPluginContext(this.applicationContext);
            PluginContextFactory pluginContextFactory =
                    new AnnotationConfigPluginContextFactory(pluginContext);
            for (PluginSpringBeanListener pluginSpringBeanListener : pluginSpringBeanListeners) {
                if(pluginSpringBeanListener != null){
                    pluginContextFactory.addListener(pluginSpringBeanListener);
                }
            }
            this.pluginContextFactory = pluginContextFactory;
        }
        try {
            IntegrationConfiguration configuration = applicationContext.getBean(IntegrationConfiguration.class);
            this.pluginUser = new DefaultPluginUser(this.applicationContext, this.pluginManager);
            this.pluginOperator = new DefaultPluginOperator(configuration, this.pluginContextFactory,
                    this.pluginManager);
        } catch (Exception e){
            throw new BeanCreationException("Instant PluginUser or PluginOperator Failure : " + e.getMessage(), e);
        }
    }


    @Override
    public PluginOperator getPluginOperator() {
        assertInjected();
        return pluginOperator;
    }

    @Override
    public PluginUser getPluginUser() {
        assertInjected();
        return pluginUser;
    }


    @Override
    public void addListener(PluginSpringBeanListener pluginSpringBeanListener){
        assertInjected();
        if(pluginSpringBeanListener != null){
            pluginContextFactory.addListener(pluginSpringBeanListener);
        }
    }


    /**
     * 检查注入
     */
    private void assertInjected() {
        if (this.applicationContext == null) {
            throw new RuntimeException("ApplicationContext is null, Please check whether the DefaultPluginApplication is injected");
        }
        if(this.pluginManager == null){
            throw new RuntimeException("PluginManager is null, Please check whether the PluginManager is injected");
        }
        if(this.pluginUser == null){
            throw new RuntimeException("PluginUser is null, " +
                    "Please check whether the PluginManager or ApplicationContext is injected");
        }
        if(this.pluginOperator == null){
            throw new RuntimeException("PluginOperator is null," +
                    " Please check whether the PluginManager or ApplicationContext is injected");
        }
    }
}
