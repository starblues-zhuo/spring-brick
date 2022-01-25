package com.gitee.starblues.bootstrap;

import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

/**
 * 空的MainApplicationContext实现
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyMainApplicationContext implements MainApplicationContext {

    private final SpringBeanFactory springBeanFactory = new EmptySpringBeanFactory();

    @Override
    public SpringBeanFactory getSpringBeanFactory() {
        return springBeanFactory;
    }

    @Override
    public void close() throws Exception {

    }
}
