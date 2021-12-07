package com.gitee.starblues.spring.processor.invoke;

import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author starBlues
 * @version 1.0
 */
public class InvokeBeanFactory<T> implements FactoryBean<T> {

    private SpringPluginRegistryInfo registryInfo;
    private Class<T> callerInterface;
    private Caller callerAnnotation;

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        ClassLoader classLoader = callerInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{callerInterface};
        InvokeProxyHandler proxy = new InvokeProxyHandler(callerAnnotation);
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    @Override
    public Class<?> getObjectType() {
        return callerInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setRegistryInfo(SpringPluginRegistryInfo registryInfo) {
        this.registryInfo = registryInfo;
    }

    public void setCallerInterface(Class<T> callerInterface) {
        this.callerInterface = callerInterface;
    }

    public void setCallerAnnotation(Caller callerAnnotation) {
        this.callerAnnotation = callerAnnotation;
    }

}
