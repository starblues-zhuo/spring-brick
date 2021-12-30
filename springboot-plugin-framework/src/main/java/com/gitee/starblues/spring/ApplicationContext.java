package com.gitee.starblues.spring;

/**
 * @author starBlues
 * @version 1.0
 */
public interface ApplicationContext extends AutoCloseable {

    /**
     * 得到 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getSpringBeanFactory();

}
