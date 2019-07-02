package com.gitee.starblues.factory;

import com.gitee.starblues.exception.PluginFactoryException;
import org.pf4j.PluginWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 通知的插件工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class NoticePluginFactory implements PluginFactory, PluginListenerContext{

    private final List<PluginListener> pluginListeners = new ArrayList<>(5);
    private final PluginFactory pluginFactory;

    public NoticePluginFactory(PluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }

    @Override
    public FactoryType factoryType() {
        return pluginFactory.factoryType();
    }

    @Override
    public void registry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        pluginFactory.registry(pluginWrapper);
        notifyEvent(pluginListeners, (pluginListener -> {
            try {
                pluginListener.registryEvent(pluginFactory.factoryType(), pluginWrapper.getPluginId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void unRegistry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        pluginFactory.unRegistry(pluginWrapper);
        notifyEvent(pluginListeners, (pluginListener -> {
            try {
                pluginListener.unRegistryEvent(pluginFactory.factoryType(), pluginWrapper.getPluginId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public int order() {
        return pluginFactory.order();
    }


    @Override
    public void addListener(PluginListener pluginListener) {
        if(pluginListener != null){
            pluginListeners.add(pluginListener);
        }
    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {
        if(pluginListeners != null && !pluginListeners.isEmpty()){
            pluginListeners.addAll(pluginListeners);
        }
    }

    /**
     * 通知事件
     * @param consumer consumer
     */
    private void notifyEvent(List<PluginListener> pluginListeners,
                             Consumer<PluginListener> consumer){
        if(pluginListeners == null){
            return;
        }
        for (PluginListener pluginListener : pluginListeners) {
            consumer.accept(pluginListener);
        }
    }


}
