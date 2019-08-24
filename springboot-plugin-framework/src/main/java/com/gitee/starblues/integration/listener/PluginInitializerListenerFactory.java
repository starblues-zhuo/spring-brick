package com.gitee.starblues.integration.listener;

import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件初始化监听者工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginInitializerListenerFactory implements PluginInitializerListener {

    private final List<PluginInitializerListener> pluginInitializerListeners = new ArrayList<>();

    public final ApplicationContext mainApplicationContext;

    public PluginInitializerListenerFactory(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void before() {
        try {
            for (PluginInitializerListener pluginInitializerListener : pluginInitializerListeners) {
                pluginInitializerListener.before();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        try {
            for (PluginInitializerListener pluginInitializerListener : pluginInitializerListeners) {
                pluginInitializerListener.complete();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failure(Throwable throwable) {
        try {
            for (PluginInitializerListener pluginInitializerListener : pluginInitializerListeners) {
                pluginInitializerListener.failure(throwable);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加监听者
     * @param pluginInitializerListener pluginInitializerListener
     */
    public void addPluginInitializerListeners(PluginInitializerListener pluginInitializerListener){
        if(pluginInitializerListener != null){
            pluginInitializerListeners.add(pluginInitializerListener);
        }
    }

}
