//package com.gitee.starblues.spring;
//
//import java.lang.reflect.Proxy;
//
///**
// * @author starBlues
// * @version 1.0
// */
//public class DiffProxyInvokeFactory implements ProxyInvokeFactory{
//
//    private final Object invokeSource;
//    private final ClassLoader invokeClassLoader;
//
//    public DiffProxyInvokeFactory(Object invokeSource) {
//        this(invokeSource, null);
//    }
//
//    public DiffProxyInvokeFactory(Object invokeSource, ClassLoader invokeClassLoader) {
//        this.invokeSource = invokeSource;
//        if(invokeClassLoader == null){
//            this.invokeClassLoader = invokeSource.getClass().getClassLoader();
//        } else {
//            this.invokeClassLoader = invokeClassLoader;
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T getObject(Class<T> interfacesClass) {
//        Class<?>[] interfaces = new Class[]{ interfacesClass };
//        ProxyInvoke proxy = new ProxyInvoke(invokeSource);
//        return (T) Proxy.newProxyInstance(invokeClassLoader, interfaces, proxy);
//    }
//}
