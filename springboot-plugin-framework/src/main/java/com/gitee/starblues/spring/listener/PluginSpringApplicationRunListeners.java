package com.gitee.starblues.spring.listener;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginSpringApplicationRunListeners implements PluginSpringApplicationRunListener{

    private final static Logger LOGGER = LoggerFactory.getLogger(PluginSpringApplicationRunListeners.class);


    private final List<PluginSpringApplicationRunListener> runListeners = new ArrayList<>();
    private final ListenerRunMode listenerRunMode;

    public PluginSpringApplicationRunListeners(ListenerRunMode listenerRunMode) {
        this.listenerRunMode = listenerRunMode;
    }

    public void addListener(PluginSpringApplicationRunListener listener){
        if(listener != null){
            runListeners.add(listener);
        }
    }

    @Override
    public void starting(SpringPluginRegistryInfo registryInfo) throws Exception{
        publicListener(listener -> listener.starting(registryInfo));
    }

    @Override
    public void environmentPrepared(SpringPluginRegistryInfo registryInfo) throws Exception{
        publicListener(listener -> listener.environmentPrepared(registryInfo));
    }

    @Override
    public void refreshPrepared(SpringPluginRegistryInfo registryInfo) throws Exception{
        publicListener(listener -> listener.refreshPrepared(registryInfo));
    }

    @Override
    public void started(SpringPluginRegistryInfo registryInfo) throws Exception{
        publicListener(listener -> listener.started(registryInfo));
    }

    @Override
    public void failed(SpringPluginRegistryInfo registryInfo, Throwable exception) {
        try {
            publicListener(listener -> listener.failed(registryInfo, exception));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(SpringPluginRegistryInfo registryInfo) throws Exception {
        try {
            publicListener(listener -> listener.stop(registryInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ListenerRunMode runMode() {
        return ListenerRunMode.ALL;
    }

    private void publicListener(Consumer<PluginSpringApplicationRunListener> consumer) throws Exception{
        for (PluginSpringApplicationRunListener listener : runListeners) {
            ListenerRunMode listenerRunMode = listener.runMode();
            if(listenerRunMode == ListenerRunMode.ALL || listenerRunMode == this.listenerRunMode){
                try {
                    consumer.accept(listener);
                } catch (Exception e){
                    LOGGER.error("监听者 <{}> 执行失败. {}", listener.getClass().getName(), e.getMessage(), e);
                    throw e;
                }
            }
        }
    }

    @FunctionalInterface
    private interface Consumer<T>{
        void accept(T t) throws Exception;
    }

}
