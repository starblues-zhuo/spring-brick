package com.gitee.starblues.spring.processor;

import com.gitee.starblues.integration.application.EmptyPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class OneselfBeanRegistryProcessor implements SpringPluginProcessor{

    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        PluginApplication pluginApplication = new EmptyPluginApplication();
        beanFactory.registerSingleton("pluginApplication", pluginApplication);
        beanFactory.registerSingleton("pluginOperator", pluginApplication.getPluginOperator());
        beanFactory.registerSingleton("pluginUser", pluginApplication.getPluginUser());
    }

    @Override
    public RunMode runMode() {
        return RunMode.ONESELF;
    }
}
