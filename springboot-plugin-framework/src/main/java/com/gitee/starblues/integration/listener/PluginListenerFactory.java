package com.gitee.starblues.integration.listener;

import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件监听工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginListenerFactory implements PluginListener{

    private final List<PluginListener> listeners = new ArrayList<>();
    private final List<Class> listenerClasses = new ArrayList<>();

    @Override
    public void registry(String pluginId) {
        for (PluginListener listener : listeners) {
            try {
                listener.registry(pluginId);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unRegistry(String pluginId) {
        for (PluginListener listener : listeners) {
            try {
                listener.unRegistry(pluginId);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failure(String pluginId, Throwable throwable) {
        for (PluginListener listener : listeners) {
            try {
                listener.failure(pluginId, throwable);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加监听者
     * @param pluginListener 插件监听者
     */
    public void addPluginListener(PluginListener pluginListener){
        if(pluginListener != null){
            listeners.add(pluginListener);
        }
    }

    /**
     * 添加监听者
     * @param pluginListenerClass 插件监听者Class类
     * @param <T> 插件监听者类。继承 PluginListener
     */
    public <T extends PluginListener> void addPluginListener(Class<T> pluginListenerClass){
        if(pluginListenerClass != null){
            synchronized (listenerClasses){
                listenerClasses.add(pluginListenerClass);
            }
        }
    }

    public <T extends PluginListener> void buildListenerClass(GenericApplicationContext applicationContext){
        if(applicationContext == null){
            return;
        }
        synchronized (listenerClasses){
            for (Class<T> listenerClass : listenerClasses) {
                applicationContext.registerBean(listenerClass);
                T bean = applicationContext.getBean(listenerClass);
                listeners.add(bean);
            }
            listenerClasses.clear();
        }
    }

    /**
     * 得到监听者
     * @return 监听者集合
     */
    public List<PluginListener> getListeners() {
        return listeners;
    }

    /**
     * 得到监听者class
     * @return 监听者class集合
     */
    public List<Class> getListenerClasses() {
        return listenerClasses;
    }
}
