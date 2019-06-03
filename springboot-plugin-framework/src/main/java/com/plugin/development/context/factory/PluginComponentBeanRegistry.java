package com.plugin.development.context.factory;

import com.plugin.development.annotation.ApplyMainBean;
import com.plugin.development.exception.PluginBeanFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-28 15:33
 * @Update Date Time:
 * @see
 */

public class PluginComponentBeanRegistry implements PluginBeanRegistry<String> {

    private final Logger log = LoggerFactory.getLogger(PluginComponentBeanRegistry.class);


    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public PluginComponentBeanRegistry(ApplicationContext applicationContext) {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory)
                applicationContext.getAutowireCapableBeanFactory();
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }


    @Override
    public String registry(Object object) throws PluginBeanFactoryException {
        if(object == null){
            throw new PluginBeanFactoryException("object can not is null");
        }
        try {
            Class<?> aClass = object.getClass();
            if(isComponent(aClass)){
                String beanName = aClass.getName();
                if(!defaultListableBeanFactory.containsSingleton(beanName)){
                    ApplyMainBean applyMainBean = aClass.getAnnotation(ApplyMainBean.class);
                    if(applyMainBean != null) {
                        try {
                            defaultListableBeanFactory.autowireBean(object);
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    }
                    defaultListableBeanFactory.registerSingleton(beanName, object);
                    return beanName;
                } else {
                    throw new PluginBeanFactoryException("beanName : "+ beanName + " , class: " +
                            aClass + " already existed!");
                }
            } else {
                throw new PluginBeanFactoryException(object.getClass().getName() + "Not found @Component Annotation");
            }
        } catch (Exception e){
            throw new PluginBeanFactoryException(e);
        }
    }

    @Override
    public void unRegistry(String beanName) {
        if(defaultListableBeanFactory.containsSingleton(beanName)){
            defaultListableBeanFactory.destroySingleton(beanName);
        }
    }



    /**
     * 是否存在 Component 注解
     * @param type
     * @return
     */
    private boolean isComponent(Class<?> type) {
        if (AnnotationUtils.findAnnotation(type, Component.class) != null) {
            return true;
        }
        if (type.getName().matches(".*\\$_.*closure.*") || type.isAnonymousClass()
                || type.getConstructors() == null || type.getConstructors().length == 0) {
            return false;
        }
        return true;
    }

}
