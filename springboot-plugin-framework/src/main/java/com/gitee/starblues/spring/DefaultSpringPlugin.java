package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;
import com.gitee.starblues.spring.process.AfterRefreshProcessor;
import com.gitee.starblues.spring.process.AfterRefreshProcessorFactory;
import com.gitee.starblues.spring.process.BeforeRefreshProcessor;
import com.gitee.starblues.spring.process.BeforeRefreshProcessorFactory;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultSpringPlugin implements SpringPlugin {

    private final Map<String, SpringPluginRegistryInfo> registryInfoMap = new ConcurrentHashMap<>();


    private final GenericApplicationContext mainApplicationContext;

    private final BeforeRefreshProcessor beforeRefreshProcessor;
    private final AfterRefreshProcessor afterRefreshProcessor;


    public DefaultSpringPlugin(GenericApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
        this.beforeRefreshProcessor = new BeforeRefreshProcessorFactory(mainApplicationContext);
        this.afterRefreshProcessor = new AfterRefreshProcessorFactory(mainApplicationContext);
    }

    @Override
    public synchronized void registry(PluginWrapper pluginWrapper) throws Exception {
        SpringPluginRegistryInfo registryInfo = createRegistryInfo(pluginWrapper);
        beforeRefreshProcessor.registry(registryInfo);
        registryInfo.getPluginSpringApplication().run();
        afterRefreshProcessor.registry(registryInfo);
        registryInfoMap.put(pluginWrapper.getPluginId(), registryInfo);
        PluginInfoContainers.addPluginApplicationContext(pluginWrapper.getPluginId(),
                registryInfo.getPluginSpringApplication().getApplicationContext());
    }

    @Override
    public synchronized void unRegistry(String pluginId) throws Exception {
        SpringPluginRegistryInfo springPluginRegistryInfo = registryInfoMap.get(pluginId);
        if(springPluginRegistryInfo == null){
            return;
        }
        beforeRefreshProcessor.unRegistry(springPluginRegistryInfo);
        afterRefreshProcessor.unRegistry(springPluginRegistryInfo);
        springPluginRegistryInfo.getPluginSpringApplication().close();
        registryInfoMap.remove(pluginId);
        PluginInfoContainers.removePluginApplicationContext(pluginId);
    }

    protected SpringPluginRegistryInfo createRegistryInfo(PluginWrapper pluginWrapper){
        PluginSpringApplication springApplication = createSpringApplication(pluginWrapper);
        return new DefaultSpringPluginRegistryInfo(
                pluginWrapper, springApplication, mainApplicationContext
        );
    }

    protected PluginSpringApplication createSpringApplication(PluginWrapper pluginWrapper){
        return new BasePluginSpringApplication(
                mainApplicationContext,
                pluginWrapper.getPluginClassLoader(), pluginWrapper.getPluginClass(),
                pluginWrapper.getPluginDescriptor().getConfigFileName()
        );
    }

}
