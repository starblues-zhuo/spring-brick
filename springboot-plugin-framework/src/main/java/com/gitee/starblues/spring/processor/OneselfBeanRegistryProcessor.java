package com.gitee.starblues.spring.processor;

import com.gitee.starblues.integration.IntegrationExtendPoint;
import com.gitee.starblues.integration.application.EmptyPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.extract.ExtractFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 * @see IntegrationExtendPoint
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
        beanFactory.registerSingleton("extractFactory", ExtractFactory.getInstant());
    }

    @Override
    public RunMode runMode() {
        return RunMode.ONESELF;
    }
}
