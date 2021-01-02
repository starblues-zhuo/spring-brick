package com.gitee.starblues.factory;

import com.gitee.starblues.factory.process.pipe.bean.name.PluginAnnotationBeanNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * Spring bean注册者, 向Spring注册Bean时, 必须使用该对象进行注册
 *
 * @author starBlues
 * @version 2.1.0
 */
public class SpringBeanRegister {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanRegister.class);

    private final GenericApplicationContext applicationContext;

    public SpringBeanRegister(ApplicationContext applicationContext){
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }

    /**
     * 默认注册
     *
     * @param pluginId   插件id
     * @param aClass     类名
     * @return 注册的bean名称
     */
    public String register(String pluginId, Class<?> aClass) {
        return register(pluginId, aClass, null);
    }

    /**
     * 默认注册
     * @param pluginId 插件id
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     * @return 注册的bean名称
     */
    public String register(String pluginId, Class<?> aClass,
                           Consumer<AnnotatedGenericBeanDefinition> consumer) {
        AnnotatedGenericBeanDefinition beanDefinition = new
                AnnotatedGenericBeanDefinition(aClass);

        BeanNameGenerator beanNameGenerator =
                new PluginAnnotationBeanNameGenerator(pluginId);
        String beanName = beanNameGenerator.generateBeanName(beanDefinition, applicationContext);
        if(PluginInfoContainer.existRegisterBeanName((beanName))){
            String error = MessageFormat.format("Bean name {0} already exist of {1}",
                    beanName, aClass.getName());
            logger.debug(error);
            return beanName;
        }
        if(consumer != null){
            consumer.accept(beanDefinition);
        }
        PluginInfoContainer.addRegisterBeanName(pluginId, beanName);
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
        try {
            applicationContext.getBean(beanName);
        } catch (BeansException e) {
            logger.warn(e.getMessage());
        }
        return beanName;
    }

    /**
     * 指定bean名称注册
     * @param pluginId 插件id
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     */
    public void registerOfSpecifyName(String pluginId, String beanName, Class<?> aClass){
        registerOfSpecifyName(pluginId, beanName, aClass, null);
    }

    /**
     * 指定bean名称注册
     * @param pluginId 插件id
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     * @param consumer 注册异常
     */
    public void registerOfSpecifyName(String pluginId,
                                      String beanName,
                                      Class<?> aClass,
                                      Consumer<AnnotatedGenericBeanDefinition> consumer) {
        AnnotatedGenericBeanDefinition beanDefinition = new
                AnnotatedGenericBeanDefinition(aClass);
        if(PluginInfoContainer.existRegisterBeanName((beanName))){
            String error = MessageFormat.format("Bean name {0} already exist of {1}",
                    beanName, aClass.getName());
            throw new RuntimeException(error);
        }
        if(consumer != null){
            consumer.accept(beanDefinition);
        }
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
        PluginInfoContainer.addRegisterBeanName(pluginId, beanName);
    }



    /**
     * 卸载bean
     * @param pluginId 插件id
     * @param beanName bean名称
     */
    public void unregister(String pluginId, String beanName){
        try {
            applicationContext.removeBeanDefinition(beanName);
        } catch (Exception e){
            logger.error("Remove plugin '{}' bean {} error. {}", pluginId, beanName, e.getMessage());
        } finally {
            PluginInfoContainer.removeRegisterBeanName(pluginId, beanName);
        }
    }


}
