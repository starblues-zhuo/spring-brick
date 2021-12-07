package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.listener.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class OneselfApplicationContextListener implements ApplicationListener<ApplicationEvent> {

    private final PluginSpringApplicationRunListeners listeners;

    private final SpringPluginRegistryInfo registryInfo;

    public OneselfApplicationContextListener(SpringPluginRegistryInfo registryInfo){
        this.registryInfo = registryInfo;
        listeners = new PluginSpringApplicationRunListeners(ListenerRunMode.ONESELF);
        addListeners();
    }


    public void addListeners(){
        listeners.addListener(new ClassScannerListener());
        listeners.addListener(new NecessaryBeanRegistryListener());
        listeners.addListener(new InvokeOtherPluginRegistryListener());
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if(event instanceof ApplicationStartingEvent){
                listeners.starting(registryInfo);
            }
            if(event instanceof ApplicationEnvironmentPreparedEvent){
                listeners.environmentPrepared(registryInfo);
            }
            if(event instanceof ApplicationContextInitializedEvent){
                listeners.refreshPrepared(registryInfo);
            }
            if(event instanceof ApplicationPreparedEvent){
                listeners.refreshPrepared(registryInfo);
            }
            if(event instanceof ApplicationStartedEvent){
                listeners.started(registryInfo);
            }
            if(event instanceof ApplicationFailedEvent){
                listeners.failed(registryInfo, ((ApplicationFailedEvent)event).getException());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
