package com.gitee.starblues.spring;

/**
 * 自定义ApplicationContext
 * @author starBlues
 * @version 3.0.0
 */
public interface ApplicationContext extends AutoCloseable {

    /**
     * 得到 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getSpringBeanFactory();

}
