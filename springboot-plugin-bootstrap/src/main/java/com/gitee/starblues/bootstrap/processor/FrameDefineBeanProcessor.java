package com.gitee.starblues.bootstrap.processor;


import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 框架内置bean注册
 * @author starBlues
 * @version 3.0.0
 */
public class FrameDefineBeanProcessor implements SpringPluginProcessor {

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        ConfigurableApplicationContext applicationContext = context.getApplicationContext();
        InsidePluginDescriptor pluginDescriptor = context.getPluginDescriptor();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("pluginDescriptor", pluginDescriptor.toPluginDescriptor());
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }

}
