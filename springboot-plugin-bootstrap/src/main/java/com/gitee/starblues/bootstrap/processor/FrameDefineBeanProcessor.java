package com.gitee.starblues.bootstrap.processor;


import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.ExtendPointConfiguration;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 框架内置bean注册
 * @author starBlues
 * @version 3.0.0
 */
public class FrameDefineBeanProcessor implements SpringPluginProcessor {

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        InsidePluginDescriptor pluginDescriptor = context.getPluginDescriptor();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("pluginDescriptor", pluginDescriptor.toPluginDescriptor());

        if(context.runMode() == ProcessorContext.RunMode.ONESELF){
            beanFactory.registerSingleton("integrationConfiguration", new AutoIntegrationConfiguration());
            applicationContext.registerBean(ExtendPointConfiguration.class);
        }
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.ALL;
    }

}
