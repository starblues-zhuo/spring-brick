package com.gitee.starblues.spring;

/**
 * @author starBlues
 * @version 1.0
 */
public class MainApplicationContextProxy extends ApplicationContextProxy implements MainApplicationContext{

    public MainApplicationContextProxy(Object targetBeanFactory) {
        super(targetBeanFactory);
    }

    public MainApplicationContextProxy(Object targetBeanFactory, AutoCloseable autoCloseable) {
        super(targetBeanFactory, autoCloseable);
    }
}
