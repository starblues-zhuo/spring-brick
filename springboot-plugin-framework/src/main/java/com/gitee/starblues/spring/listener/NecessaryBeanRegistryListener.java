package com.gitee.starblues.spring.listener;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.realize.PluginUtils;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class NecessaryBeanRegistryListener implements PluginSpringApplicationRunListener {

    @Override
    public void refreshPrepared(SpringPluginRegistryInfo registryInfo) throws Exception {
        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        PluginDescriptor pluginDescriptor = registryInfo.getPluginWrapper().getPluginDescriptor();
        applicationContext.getBeanFactory().registerSingleton("pluginDescriptor",
                pluginDescriptor);
        applicationContext.getBeanFactory().registerSingleton("pluginUtils",
                new PluginUtils(applicationContext));
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority().down(-10);
    }

    @Override
    public ListenerRunMode runMode() {
        return ListenerRunMode.ALL;
    }

}
