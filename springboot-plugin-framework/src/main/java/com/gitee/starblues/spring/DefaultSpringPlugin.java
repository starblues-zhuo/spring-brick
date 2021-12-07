package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import org.springframework.context.support.GenericApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultSpringPlugin implements SpringPlugin {

    private final Map<String, SpringPluginRegistryInfo> registryInfoMap = new ConcurrentHashMap<>();

    private final GenericApplicationContext mainApplicationContext;

    public DefaultSpringPlugin(GenericApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public synchronized void registry(PluginWrapper pluginWrapper) throws Exception {
        SpringPluginRegistryInfo registryInfo = createRegistryInfo(pluginWrapper);
        registryInfo.getPluginSpringApplication().run();
        registryInfoMap.put(pluginWrapper.getPluginId(), registryInfo);
    }

    @Override
    public synchronized void unRegistry(String pluginId) throws Exception {
        SpringPluginRegistryInfo springPluginRegistryInfo = registryInfoMap.get(pluginId);
        if(springPluginRegistryInfo == null){
            return;
        }
        springPluginRegistryInfo.getPluginSpringApplication().close();
        registryInfoMap.remove(pluginId);
    }

    @Override
    public Map<String, SpringPluginRegistryInfo> getPluginRegistryInfos() {
        return new HashMap<>(registryInfoMap);
    }

    protected SpringPluginRegistryInfo createRegistryInfo(PluginWrapper pluginWrapper){
        return new DefaultSpringPluginRegistryInfo(
                pluginWrapper, mainApplicationContext
        );
    }

}
