package com.gitee.starblues.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.support.GenericApplicationContext;

import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * Spring bean注册者, 向Spring注册Bean时, 必须使用该对象进行注册
 *
 * @author starBlues
 * @version 2.4.1
 */
public class SpringBeanRegister {

    private static final Logger logger = LoggerFactory.getLogger(SpringBeanRegister.class);

    private final GenericApplicationContext applicationContext;

    public SpringBeanRegister(GenericApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }


    public boolean exist(String name){
        return applicationContext.containsBean(name);
    }


    /**
     * 基于class注册一个bean
     *
     * @param aClass     类名
     * @return 注册的bean名称
     */
    public String register(Class<?> aClass) {
        return register(aClass, null);
    }


    /**
     * 基于class注册一个bean
     *
     * @param pluginId   插件id
     * @param aClass     类名
     * @return 注册的bean名称
     */
    @Deprecated
    public String register(String pluginId, Class<?> aClass) {
        return register(pluginId, aClass, null);
    }


    /**
     * 基于class注册一个bean, 可自定义 BeanDefinition
     *
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     * @return 注册的bean名称
     */
    public String register(Class<?> aClass,
                           Consumer<AnnotatedGenericBeanDefinition> consumer) {
        AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(aClass);
        beanDefinition.setBeanClass(aClass);
        BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
        String beanName = beanNameGenerator.generateBeanName(beanDefinition, applicationContext);

        if(applicationContext.containsBean(beanName)){
            String error = MessageFormat.format("Bean name {0} already exist of {1}",
                    beanName, aClass.getName());
            logger.debug(error);
            return beanName;
        }
        if(consumer != null){
            consumer.accept(beanDefinition);
        }
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
        return beanName;
    }


    /**
     * 基于class注册一个bean, 可自定义 BeanDefinition
     *
     * @param pluginId 插件id
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     * @return 注册的bean名称
     */
    @Deprecated
    public String register(String pluginId, Class<?> aClass,
                           Consumer<AnnotatedGenericBeanDefinition> consumer) {
        return register(aClass, consumer);
    }

    /**
     * 指定bean名称注册
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     */
    public void registerOfSpecifyName(String beanName, Class<?> aClass){
        registerOfSpecifyName(beanName, aClass, null);
    }

    /**
     * 指定bean名称注册
     * @param pluginId 插件id
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     */
    @Deprecated
    public void registerOfSpecifyName(String pluginId, String beanName, Class<?> aClass){
        registerOfSpecifyName(pluginId, beanName, aClass, null);
    }


    /**
     * 指定bean名称注册, 可自定义 BeanDefinition
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     */
    public void registerOfSpecifyName(String beanName,
                                      Class<?> aClass,
                                      Consumer<AnnotatedGenericBeanDefinition> consumer) {
        AnnotatedGenericBeanDefinition beanDefinition = new
                AnnotatedGenericBeanDefinition(aClass);
        if(applicationContext.containsBean(beanName)){
            String error = MessageFormat.format("Bean name {0} already exist of {1}",
                    beanName, aClass.getName());
            throw new RuntimeException(error);
        }
        if(consumer != null){
            consumer.accept(beanDefinition);
        }
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
    }


    /**
     * 指定bean名称注册, 可自定义 BeanDefinition
     * @param pluginId 插件id
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     */
    @Deprecated
    public void registerOfSpecifyName(String pluginId,
                                      String beanName,
                                      Class<?> aClass,
                                      Consumer<AnnotatedGenericBeanDefinition> consumer) {
        registerOfSpecifyName(beanName, aClass, consumer);
    }

    /**
     * 注册单例
     * @param name 单例名称
     * @param object 对象
     */
    public void registerSingleton(String name, Object object){
        DefaultListableBeanFactory listableBeanFactory = applicationContext.getDefaultListableBeanFactory();
        if(!listableBeanFactory.containsSingleton(name)){
            listableBeanFactory.registerSingleton(name, object);
        }
    }

    /**
     * 注册BeanDefinitionHolder
     * @param definitionHolder BeanDefinitionHolder
     */
    public void registerBeanDefinition(BeanDefinitionHolder definitionHolder) {

        String beanName = definitionHolder.getBeanName();
        applicationContext.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                applicationContext.registerAlias(beanName, alias);
            }
        }
    }

    /**
     * 销毁单例
     * @param name 单例名称
     */
    public void destroySingleton(String name){
        DefaultListableBeanFactory listableBeanFactory = applicationContext.getDefaultListableBeanFactory();
        if(listableBeanFactory.containsSingleton(name)){
            listableBeanFactory.destroySingleton(name);
        }
    }

    /**
     * 销毁所有单例
     */
    public void destroySingletons(){
        DefaultListableBeanFactory listableBeanFactory = applicationContext.getDefaultListableBeanFactory();
        listableBeanFactory.destroySingletons();
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
        }
    }


}
