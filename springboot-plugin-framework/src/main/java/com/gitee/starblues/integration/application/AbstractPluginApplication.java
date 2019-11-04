package com.gitee.starblues.integration.application;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 公用的的插件应用
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public abstract class AbstractPluginApplication implements PluginApplication {

    protected final PluginListenerFactory listenerFactory = new PluginListenerFactory();


    @Override
    public void addExtension(AbstractExtension extension) {
        if(extension == null){
            return;
        }
        extension.setPluginApplication(this);
        ExtensionFactory.addExtension(extension);
    }

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
            throw new BeanCreationException("Not Found IntegrationConfiguration, Please define " +
                    "IntegrationConfiguration to Spring Bean.");
        }
        return configuration;
    }

}
