package com.gitee.starblues.spring.processor;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.realize.PluginUtils;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 框架 bean 注册
 * @author starBlues
 * @version 1.0
 */
public class FrameDefineBeanRegistryProcessor implements SpringPluginProcessor{

    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        PluginDescriptor pluginDescriptor = registryInfo.getPluginWrapper().getPluginDescriptor();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("pluginDescriptor", pluginDescriptor);
        beanFactory.registerSingleton("pluginUtils", new PluginUtils(applicationContext));
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }
}
