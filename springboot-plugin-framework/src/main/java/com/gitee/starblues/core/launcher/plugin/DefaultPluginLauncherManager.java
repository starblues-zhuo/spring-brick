package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.MainApplicationContextReflection;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultPluginLauncherManager implements PluginLauncherManager{

    private final Map<String, PluginRegistryInfo> pluginRegistryInfoMap = new ConcurrentHashMap<>();

    private final MainApplicationContext mainApplicationContext;

    public DefaultPluginLauncherManager(GenericApplicationContext mainGenericApplicationContext,
                                        IntegrationConfiguration configuration) {
        this.mainApplicationContext = new MainApplicationContextReflection(mainGenericApplicationContext, configuration);
    }

    @Override
    public synchronized void start(PluginDescriptor pluginDescriptor) throws Exception{
        if(pluginRegistryInfoMap.containsKey(pluginDescriptor.getPluginId())){
            throw new Exception("已经存在插件 : " + pluginDescriptor.getPluginId());
        }
        PluginInteractive pluginInteractive = new DefaultPluginInteractive(pluginDescriptor,
                mainApplicationContext);
        PluginLauncher pluginLauncher = new PluginLauncher(pluginInteractive);
        Object pluginApplicationContext = pluginLauncher.run();
        PluginRegistryInfo pluginRegistryInfo = new DefaultPluginRegistryInfo(pluginApplicationContext,
                pluginLauncher);
        pluginRegistryInfoMap.put(pluginDescriptor.getPluginId(), pluginRegistryInfo);
    }

    @Override
    public synchronized void stop(String pluginId) throws Exception{

    }
}
