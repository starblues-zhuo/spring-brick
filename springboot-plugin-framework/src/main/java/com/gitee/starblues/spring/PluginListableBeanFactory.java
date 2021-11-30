package com.gitee.starblues.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginListableBeanFactory extends DefaultListableBeanFactory {

    private final DefaultListableBeanFactory mainDefaultListableBeanFactory;


    public PluginListableBeanFactory(GenericApplicationContext applicationContext) {
        this.mainDefaultListableBeanFactory = applicationContext.getDefaultListableBeanFactory();
    }


    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    @Nullable String requestingBeanName,
                                    @Nullable Set<String> autowiredBeanNames,
                                    @Nullable TypeConverter typeConverter) throws BeansException{

        try {
            return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        } catch (Exception e){
            return mainDefaultListableBeanFactory.resolveDependency(descriptor,
                    requestingBeanName, autowiredBeanNames, typeConverter);
        }
    }

}
