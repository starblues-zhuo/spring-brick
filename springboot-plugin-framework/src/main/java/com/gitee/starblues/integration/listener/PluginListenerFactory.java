package com.gitee.starblues.integration.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件监听工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginListenerFactory implements PluginListener{

    private List<PluginListener> listeners = new ArrayList<>();

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
     * 得到监听者
     * @return 监听者集合
     */
    public List<PluginListener> getListeners() {
        return listeners;
    }
}
