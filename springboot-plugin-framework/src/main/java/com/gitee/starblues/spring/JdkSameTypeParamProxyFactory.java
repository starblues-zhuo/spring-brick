package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author starBlues
 * @version 1.0
 */
public class JdkSameTypeParamProxyFactory implements ProxyFactory, InvocationHandler {

    protected final Object target;

    public JdkSameTypeParamProxyFactory(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(Class<T> interfacesClass) {
        ClassLoader classLoader = interfacesClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{ interfacesClass };
        return (T) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ReflectionUtils.invoke(target, method.getName(), args);
    }

}
