//package com.gitee.starblues.spring;
//
//import com.gitee.starblues.utils.ObjectUtils;
//import com.gitee.starblues.utils.ReflectionUtils;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author starBlues
// * @version 1.0
// */
//public class ProxyInvoke implements InvocationHandler {
//
//    private final Object source;
//
//    public ProxyInvoke(Object source) {
//        this.source = source;
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        String name = method.getName();
//        Method invokeMethod = findMethod(name, args);
//        Class<?>[] parameterTypes = invokeMethod.getParameterTypes();
//        Object[] invokeArgs = new Object[args.length];
//        for (int i = 0; i < parameterTypes.length; i++) {
//            Class<?> parameterType = parameterTypes[i];
//            Object arg = args[i];
//            Class<?> aClass = arg.getClass();
//            if(parameterType == aClass){
//                // 类型一致
//                invokeArgs[i] = arg;
//            } else {
//                // 类型不一致
//                ProxyInvokeFactory factory = new DiffProxyInvokeFactory(
//                        arg, parameterType.getClassLoader()
//                );
//                invokeArgs[i] = factory.getObject();
//            }
//        }
//        return invokeMethod.invoke(source, invokeArgs);
//    }
//
//    private Method findMethod(String sourceMethodName, Object[] args) throws Exception{
//        List<Method> methods = ReflectionUtils.findMethods(source.getClass(), sourceMethodName);
//        if(ObjectUtils.isEmpty(methods)){
//            throw new NoSuchMethodException("Not found method:" + sourceMethodName);
//        }
//        for (Method m : methods) {
//            if (m.getParameterCount() != args.length) {
//                continue;
//            }
//            Class<?>[] parameterTypes = m.getParameterTypes();
//            int matchCount = 0;
//            for (int j = 0; j < parameterTypes.length; j++) {
//                Class<?> parameterType = parameterTypes[j];
//                Class<?> aClass = args[j].getClass();
//                if (Objects.equals(parameterType.getName(), aClass.getName())) {
//                    matchCount++;
//                }
//            }
//            if(matchCount == parameterTypes.length){
//                return m;
//            }
//        }
//        throw new NoSuchMethodException("Not found method:" + sourceMethodName);
//    }
//
//
//}
