package com.gitee.starblues.bootstrap;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginApplicationContext extends AnnotationConfigApplicationContext {

    public PluginApplicationContext(DefaultListableBeanFactory beanFactory,
                                    ResourceLoader resourceLoader) {
        super(beanFactory);
        setResourceLoader(resourceLoader);
    }

    @Override
    public String getApplicationName() {
        return "jpa-plugin1";
    }
}
