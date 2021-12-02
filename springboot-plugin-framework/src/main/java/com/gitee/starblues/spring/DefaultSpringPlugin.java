package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.processor.AbstractProcessorFactory;
import com.gitee.starblues.spring.processor.DefaultProcessorFactory;
import com.gitee.starblues.spring.processor.ProcessorRunMode;
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
    private final IntegrationConfiguration configuration;

    private final AbstractProcessorFactory processorFactory;


    public DefaultSpringPlugin(GenericApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.processorFactory = new DefaultProcessorFactory(mainApplicationContext, configuration,
                ProcessorRunMode.PLUGIN);
    }

    @Override
    public synchronized void registry(PluginWrapper pluginWrapper) throws Exception {
        SpringPluginRegistryInfo registryInfo = createRegistryInfo(pluginWrapper);
        processorFactory.registryOfBefore(registryInfo);
        registryInfo.getPluginSpringApplication().run();
        processorFactory.registryOfAfter(registryInfo);
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
        processorFactory.unRegistryOfAfter(springPluginRegistryInfo);
        processorFactory.unRegistryOfBefore(springPluginRegistryInfo);
        springPluginRegistryInfo.getPluginSpringApplication().close();
        registryInfoMap.remove(pluginId);
        PluginInfoContainers.removePluginApplicationContext(pluginId);
    }

    protected SpringPluginRegistryInfo createRegistryInfo(PluginWrapper pluginWrapper){
        PluginSpringApplication springApplication = createSpringApplication(pluginWrapper);
        return new DefaultSpringPluginRegistryInfo(
                pluginWrapper, springApplication, mainApplicationContext, configuration
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
