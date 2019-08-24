package com.gitee.starblues.factory;

import com.gitee.starblues.factory.process.pipe.bean.name.PluginAnnotationBeanNameGenerator;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.text.MessageFormat;
import java.util.function.Consumer;

/**
 * Spring bean注册者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SpringBeanRegister {

    private final GenericApplicationContext applicationContext;

    public SpringBeanRegister(ApplicationContext applicationContext){
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }

    /**
     * 默认注册
     * @param aClass 类名
     * @return 注册的bean名称
     */
    public String register(Class<?> aClass) {
        return register(null, aClass, null);
    }

    /**
     * 默认注册
     * @param namePrefix bean名称前缀
     * @param aClass 类名
     * @return 注册的bean名称
     */
    public String register(String namePrefix, Class<?> aClass) {
        return register(namePrefix, aClass, null);
    }


    /**
     * 默认注册
     * @param aClass 类名
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     * @return 注册的bean名称
     */
    public String register(Class<?> aClass, Consumer<AnnotatedGenericBeanDefinition> consumer) {
        return register(null, aClass, consumer);
    }

    /**
     * 默认注册
     * @param namePrefix bean名称前缀
     * @param aClass 注册的类
     * @param consumer 自定义处理AnnotatedGenericBeanDefinition
     * @return 注册的bean名称
     */
    public String register(String namePrefix, Class<?> aClass, Consumer<AnnotatedGenericBeanDefinition> consumer) {
        AnnotatedGenericBeanDefinition beanDefinition = new
                AnnotatedGenericBeanDefinition(aClass);
        if(namePrefix == null){
            namePrefix = "";
        }
        BeanNameGenerator beanNameGenerator =
                new PluginAnnotationBeanNameGenerator(namePrefix);
        String beanName = beanNameGenerator.generateBeanName(beanDefinition, applicationContext);
        if(PluginInfoContainer.existRegisterBeanName((beanName))){
            String error = MessageFormat.format("Bean name {0} already exist of {1}",
                    beanName, aClass.getName());
            throw new RuntimeException(error);
        }
        if(consumer != null){
            consumer.accept(beanDefinition);
        }
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
        PluginInfoContainer.addRegisterBeanName(beanName);
        return beanName;
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
     * @param beanName 指定的bean名称
     * @param aClass 注册的类
     * @param consumer 注册异常
     */
    public void registerOfSpecifyName(String beanName, Class<?> aClass, Consumer<AnnotatedGenericBeanDefinition> consumer) {
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
        PluginInfoContainer.addRegisterBeanName(beanName);
        applicationContext.registerBeanDefinition(beanName, beanDefinition);
    }



    /**
     * 卸载bean
     * @param beanName bean名称
     */
    public void unregister(String beanName){
        applicationContext.removeBeanDefinition(beanName);
        PluginInfoContainer.removeRegisterBeanName(beanName);
    }




}
