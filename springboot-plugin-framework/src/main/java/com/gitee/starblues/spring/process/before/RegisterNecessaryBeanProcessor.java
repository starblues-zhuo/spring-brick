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
public class RegisterNecessaryBeanProcessor implements BeforeRefreshProcessor {


    @Override
    public void registryOfBefore(SpringPluginRegistryInfo registryInfo) {

        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();

        PluginDescriptor pluginDescriptor = registryInfo.getPluginWrapper().getPluginDescriptor();
        applicationContext.getBeanFactory().registerSingleton("pluginDescriptor",
                pluginDescriptor);
        applicationContext.getBeanFactory().registerSingleton("pluginUtils", new PluginUtils(applicationContext));
    }

    @Override
    public void unRegistryOfBefore(SpringPluginRegistryInfo registryInfo) {

    }

    @Override
    public OrderPriority orderOfBefore() {
        return OrderPriority.getHighPriority();
    }
}
