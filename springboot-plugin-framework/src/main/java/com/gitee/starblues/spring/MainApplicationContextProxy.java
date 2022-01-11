package com.gitee.starblues.spring;

/**
 * 主程序 ApplicationContext 的实现
 * @author starBlues
 * @version 3.0.0
 */
public class MainApplicationContextProxy extends ApplicationContextProxy implements MainApplicationContext{

    public MainApplicationContextProxy(Object targetBeanFactory) {
        super(targetBeanFactory);
    }

    public MainApplicationContextProxy(Object targetBeanFactory, AutoCloseable autoCloseable) {
        super(targetBeanFactory, autoCloseable);
    }
}
