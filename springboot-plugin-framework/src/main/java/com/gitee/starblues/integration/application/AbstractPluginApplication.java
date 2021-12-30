package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.listener.PluginStateListenerFactory;
import org.pf4j.PluginStateListener;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 公用的的插件应用
 *
 * @author starBlues
 * @version 2.4.4
 */
public abstract class AbstractPluginApplication implements PluginApplication {

    protected final PluginListenerFactory listenerFactory = new PluginListenerFactory();
    protected final PluginStateListenerFactory pluginStateListenerFactory = new PluginStateListenerFactory();


    @Override
    public void addListener(PluginListener pluginListener) {
        this.listenerFactory.addPluginListener(pluginListener);
    }

    @Override
    public <T extends PluginListener> void addListener(Class<T> pluginListenerClass) {
        listenerFactory.addPluginListener(pluginListenerClass);
    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {
        if(pluginListeners == null || pluginListeners.isEmpty()){
            return;
        }
        for (PluginListener pluginListener : pluginListeners) {
            this.listenerFactory.addPluginListener(pluginListener);
        }
    }

    @Override
    public void addPf4jStateListener(PluginStateListener pluginListener) {
        pluginStateListenerFactory.addStateListener(pluginListener);
    }

    @Override
    public <T extends PluginStateListener> void addPf4jStateListener(Class<T> pluginListenerClass) {
        pluginStateListenerFactory.addStateListener(pluginListenerClass);
    }

    @Override
    public void addPf4jStateListener(List<PluginStateListener> pluginListeners) {
        if(pluginListeners == null || pluginListeners.isEmpty()){
            return;
        }
        for (PluginStateListener pluginListener : pluginListeners) {
            this.pluginStateListenerFactory.addStateListener(pluginListener);
        }
    }

    /**
     * 子类可通过Application 获取插件定义的配置
     * @param applicationContext applicationContext
     * @return IntegrationConfiguration
     */
    protected IntegrationConfiguration getConfiguration(ApplicationContext applicationContext){
        IntegrationConfiguration configuration = null;
        try {
            configuration = applicationContext.getBean(IntegrationConfiguration.class);
        } catch (Exception e){
            // no show exception
        }
        if(configuration == null){
            throw new BeanCreationException("没有发现 <IntegrationConfiguration> Bean, " +
                    "请在 Spring 容器中将 <IntegrationConfiguration> 定义为Bean");
        }
        return configuration;
    }

}
