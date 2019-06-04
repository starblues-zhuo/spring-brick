package com.plugin.development.context.factory;

import com.plugin.development.annotation.ApplyMainBean;
import com.plugin.development.exception.PluginBeanFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description: 注册插件中 @Component注解修饰 的bean
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
            Component component = AnnotationUtils.findAnnotation(aClass, Component.class);
            if(component == null){
                throw new PluginBeanFactoryException(object.getClass().getName() + "Not found @Component Annotation");
            }
            String beanName;
            if(StringUtils.isEmpty(component.value())){
                beanName = aClass.getName();
            } else {
                beanName = component.value();
            }
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



}
