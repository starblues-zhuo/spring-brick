package com.gitee.starblues.bootstrap.processor.invoke;

import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * 反射调用其他插件定义的接口bean工厂
 * @author starBlues
 * @version 3.0.0
 */
public class InvokeBeanFactory<T> implements FactoryBean<T> {

    private Class<T> callerInterface;
    private Caller callerAnnotation;
    private InvokeSupperCache invokeSupperCache;

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        ClassLoader classLoader = callerInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{callerInterface};
        InvokeProxyHandler proxy = new InvokeProxyHandler(callerAnnotation, invokeSupperCache);
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

    public void setCallerInterface(Class<T> callerInterface) {
        this.callerInterface = callerInterface;
    }

    public void setCallerAnnotation(Caller callerAnnotation) {
        this.callerAnnotation = callerAnnotation;
    }

    public void setInvokeSupperCache(InvokeSupperCache invokeSupperCache) {
        this.invokeSupperCache = invokeSupperCache;
    }
}
