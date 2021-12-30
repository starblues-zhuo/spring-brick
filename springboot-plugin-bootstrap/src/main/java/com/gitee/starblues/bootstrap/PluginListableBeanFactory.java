package com.gitee.starblues.bootstrap;

import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginListableBeanFactory extends DefaultListableBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainApplicationContext applicationContext;

    public PluginListableBeanFactory(MainApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    @Nullable String requestingBeanName,
                                    @Nullable Set<String> autowiredBeanNames,
                                    @Nullable TypeConverter typeConverter) throws BeansException {

        try {
            return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        } catch (BeansException e){
            return resolveDependencyFromMain(descriptor);
        }
    }

    private Object resolveDependencyFromMain(DependencyDescriptor descriptor){
        String dependencyName = descriptor.getDependencyName();
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        if(!ObjectUtils.isEmpty(dependencyName) && springBeanFactory.containsBean(dependencyName)){
            return springBeanFactory.getBean(dependencyName);
        } else {
            try {
                return springBeanFactory.getBean(descriptor.getDependencyType());
            } catch (Exception e){
                throw new NoSuchBeanDefinitionException(descriptor.getDependencyType());
            }
        }
    }

}
