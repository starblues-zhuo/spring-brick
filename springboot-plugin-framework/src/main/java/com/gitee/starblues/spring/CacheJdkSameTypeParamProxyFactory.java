package com.gitee.starblues.spring;

import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可缓存的代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public class CacheJdkSameTypeParamProxyFactory extends JdkSameTypeParamProxyFactory{

    private final Map<Method, Method> methodCache = new ConcurrentHashMap<>();

    public CacheJdkSameTypeParamProxyFactory(Object target) {
        super(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = methodCache.get(method);
        if(targetMethod == null){
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            targetMethod = ReflectionUtils.findMethod(target.getClass(), method.getName(), paramTypes);
            if(targetMethod != null){
                methodCache.put(method, targetMethod);
            } else {
                throw ReflectionUtils.getNoSuchMethodException(target.getClass(), method.getName(), paramTypes);
            }
        }
        return targetMethod.invoke(target, args);
    }

}
