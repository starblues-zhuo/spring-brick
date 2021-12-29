package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author starBlues
 * @version 1.0
 */
public class CglibProxyInvokeFactory implements ProxyInvokeFactory {

    private final Object source;

    public CglibProxyInvokeFactory(Object source) {
        this.source = source;
    }

    @Override
    public <T> T getObject(Class<T> interfacesClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfacesClass);
        enhancer.setCallback(new MethodInvoke(source));
        enhancer.setClassLoader(interfacesClass.getClassLoader());
        try {
            return (T)enhancer.create();
        } catch (Exception e){
            System.out.println(interfacesClass);
            e.printStackTrace();
            return null;
        }
    }

    private static class MethodInvoke implements MethodInterceptor {

        private final Object source;

        private MethodInvoke(Object source) {
            this.source = source;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Method delegateMethod = ReflectionUtils.findMethod(source.getClass(), method.getName(),
                    method.getParameterTypes());
            if(delegateMethod == null){
                throw ReflectionUtils.getNoSuchMethodException(source.getClass(), method.getName(),
                        method.getParameterTypes());
            }
            return delegateMethod.invoke(source, objects);
        }
    }
}
