package com.gitee.starblues.spring.process.before;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.realize.PluginUtils;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.BeforeRefreshProcessor;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class RegisterNecessaryBean implements BeforeRefreshProcessor {


    @Override
    public void registry(SpringPluginRegistryInfo registryInfo) {

        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();

        PluginDescriptor pluginDescriptor = registryInfo.getPluginWrapper().getPluginDescriptor();
        applicationContext.getBeanFactory().registerSingleton("pluginDescriptor",
                pluginDescriptor);
        applicationContext.getBeanFactory().registerSingleton("pluginUtils", new PluginUtils(applicationContext));
    }

    @Override
    public void unRegistry(SpringPluginRegistryInfo registryInfo) {

    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }
}
