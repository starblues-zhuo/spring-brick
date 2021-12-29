package com.gitee.starblues.bootstrap.processor;


import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class FrameDefineBeanProcessor implements SpringPluginProcessor {

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        ConfigurableApplicationContext applicationContext = context.getApplicationContext();
        PluginDescriptor pluginDescriptor = context.getPluginDescriptor();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("pluginDescriptor", pluginDescriptor);
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }

}
