package com.gitee.starblues.spring;

import com.gitee.starblues.utils.Assert;

/**
 * 基本的ApplicationContext
 * @author starBlues
 * @version 3.0.0
 */
public class GenericApplicationContext implements ApplicationContext{

    public final AutoCloseable autoCloseable;

    protected SpringBeanFactory springBeanFactory;

    public GenericApplicationContext() {
        this(null);
    }

    public GenericApplicationContext(AutoCloseable autoCloseable) {
        this.autoCloseable = autoCloseable;
    }

    public void setSpringBeanFactory(SpringBeanFactory springBeanFactory){
        this.springBeanFactory = Assert.isNotNull(springBeanFactory, "参数 springBeanFactory 不能为空");
    }

    @Override
    public SpringBeanFactory getSpringBeanFactory() {
        return springBeanFactory;
    }

    @Override
    public void close() throws Exception {
        if(autoCloseable != null){
            autoCloseable.close();
        }
    }
}
