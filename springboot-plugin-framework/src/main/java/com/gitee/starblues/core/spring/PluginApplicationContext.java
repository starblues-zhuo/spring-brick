package com.gitee.starblues.core.spring;

import com.gitee.starblues.utils.Assert;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件 ApplicationContext
 * @author starBlues
 * @version 3.0.0
 */
public class PluginApplicationContext extends AnnotationConfigApplicationContext {

    private ResourceLoader resourceLoader;

    public PluginApplicationContext(DefaultListableBeanFactory beanFactory, ClassLoader classLoader) {
        super(beanFactory);
        Assert.isNotNull(classLoader, "classLoader 不能为空");
        this.resourceLoader = new DefaultResourceLoader(classLoader);
        setClassLoader(classLoader);
        setResourceLoader(resourceLoader);
        setEnvironment(new StandardEnvironment());
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
