package com.gitee.starblues.spring;

/**
 *
 * @author starBlues
 * @version 3.0.0
 */
public class ApplicationContextProxy extends GenericApplicationContext{

    public ApplicationContextProxy(Object targetBeanFactory,
                                   AutoCloseable autoCloseable) {
        super(autoCloseable);
        setSpringBeanFactory(createSpringBeanFactory(targetBeanFactory));
    }

    public ApplicationContextProxy(Object targetBeanFactory) {
        super();
        setSpringBeanFactory(createSpringBeanFactory(targetBeanFactory));
    }

    protected SpringBeanFactory createSpringBeanFactory(Object targetBeanFactory){
        ProxyFactory proxyFactory = new CacheJdkSameTypeParamProxyFactory(targetBeanFactory);
        return proxyFactory.getObject(SpringBeanFactory.class);
    }
}
