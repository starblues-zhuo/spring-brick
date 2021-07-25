package com.gitee.starblues.integration.listener;

import com.gitee.starblues.utils.SpringBeanUtils;
import org.pf4j.PluginStateListener;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 插件状态监听器工厂
 * @author starBlues
 * @version 2.4.4
 * @since 2021-06-09
 */
public class PluginStateListenerFactory {

    private final List<PluginStateListener> listeners = new ArrayList<>();
    private final List<Class> listenerClasses = new ArrayList<>();

    public void addStateListener(PluginStateListener pluginStateListener){
        if(pluginStateListener == null){
            return;
        }
        listeners.add(pluginStateListener);
    }

    public <T extends PluginStateListener> void addStateListener(Class<T> pluginStateListenerClass){
        if(pluginStateListenerClass == null){
            return;
        }
        listenerClasses.add(pluginStateListenerClass);
    }

    public <T extends PluginStateListener> List<PluginStateListener> buildListenerClass(GenericApplicationContext applicationContext) {
        if (applicationContext == null) {
            return listeners;
        }
        synchronized (listenerClasses) {
            if(listenerClasses.isEmpty()){
                return listeners;
            }
            // 搜索Spring容器中的监听器
            List<PluginStateListener> pluginListeners = SpringBeanUtils.getBeans(applicationContext, PluginStateListener.class);
            if(pluginListeners.isEmpty()){
                pluginListeners = new ArrayList<>();
            }
            for (Class<T> listenerClass : listenerClasses) {
                // 兼容 spring 4.x
                applicationContext.registerBeanDefinition(listenerClass.getName(),
                        BeanDefinitionBuilder.genericBeanDefinition(listenerClass).getBeanDefinition());
                T bean = applicationContext.getBean(listenerClass);
                pluginListeners.add(bean);
            }
            for (PluginStateListener pluginListener : pluginListeners) {
                boolean find = false;
                for (PluginStateListener listener : listeners) {
                    if(Objects.equals(listener, pluginListener)){
                        find = true;
                        break;
                    }
                }
                // 防止监听器重复注册
                if(!find){
                    listeners.add(pluginListener);
                }
            }
            listenerClasses.clear();
        }
        return listeners;
    }


}
