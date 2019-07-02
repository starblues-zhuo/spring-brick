package com.gitee.starblues.integration.listener;


import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;

/**
 * 配置解析注解为 @Configuration 的bean
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SetConfigurationInitializerListener implements PluginInitializerListener {

    private final ApplicationContext applicationContext;

    public SetConfigurationInitializerListener(ApplicationContext mainApplicationContext) {
        this.applicationContext = mainApplicationContext;
    }

    @Override
    public void before() {
        // no thing
    }

    @Override
    public void complete() {
        ConfigurationClassPostProcessor configurationClassPostProcessor =
                applicationContext.getBean(ConfigurationClassPostProcessor.class);
        configurationClassPostProcessor.postProcessBeanDefinitionRegistry((BeanDefinitionRegistry)applicationContext);
    }

    @Override
    public void failure(Throwable throwable) {
        // no thing
    }
}
