package com.gitee.starblues.factory.process.pipe.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;
import com.gitee.starblues.factory.process.pipe.classs.group.CallerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.SupplierGroup;
import com.gitee.starblues.factory.process.post.bean.PluginInvokePostProcessor;
import org.pf4j.util.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件互相调用的bean注册者
 * @author starBlues
 * @version 2.4.0
 */
public class InvokeBeanRegistrar implements PluginBeanRegistrar{

    private final static Map<String, Map<String, Object>> PLUGIN_SUPPER_MAP = new ConcurrentHashMap<>(4);

    public static final String SUPPLIER_KEY = "Invoke_Supplier";

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        registrySupper(pluginRegistryInfo);
        registryCall(pluginRegistryInfo);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PLUGIN_SUPPER_MAP.remove(pluginRegistryInfo.getPluginWrapper().getPluginId());
    }

    /**
     * 处理被调用者
     * @param pluginRegistryInfo 插件注册的信息
     * @throws Exception 处理异常
     */
    private void registrySupper(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> supperClasses = pluginRegistryInfo.getGroupClasses(SupplierGroup.GROUP_ID);
        if(supperClasses.isEmpty()){
            return;
        }
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        Set<String> beanNames = new HashSet<>(supperClasses.size());
        for (Class<?> supperClass : supperClasses) {
            if(supperClass == null){
                continue;
            }
            Supplier supplier = supperClass.getAnnotation(Supplier.class);
            if(supplier == null){
                continue;
            }
            String beanName = supplier.value();
            if(springBeanRegister.exist(beanName)){
                String error = MessageFormat.format(
                        "Plugin {0} : Bean @Supplier name {1} already exist of {2}",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(), beanName, supperClass.getName());
                throw new Exception(error);
            }
            springBeanRegister.registerOfSpecifyName(beanName, supperClass);
            beanNames.add(beanName);
        }
        pluginRegistryInfo.addExtension(SUPPLIER_KEY, beanNames);
    }


    private void registryCall(PluginRegistryInfo pluginRegistryInfo) {
        List<Class<?>> callerClasses = pluginRegistryInfo.getGroupClasses(CallerGroup.GROUP_ID);
        if(callerClasses == null || callerClasses.isEmpty()){
            return;
        }
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        for (Class<?> callerClass : callerClasses) {
            Caller caller = callerClass.getAnnotation(Caller.class);
            if(caller == null){
                continue;
            }
            springBeanRegister.register(callerClass, (beanDefinition) ->{
                beanDefinition.getPropertyValues().add("callerInterface", callerClass);
                beanDefinition.getPropertyValues().add("callerAnnotation", caller);
                beanDefinition.setBeanClass(CallerInterfaceFactory.class);
                beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            });
        }

    }

    public static void addSupper(String pluginId, String name, Object o){
        Map<String, Object> superMap = PLUGIN_SUPPER_MAP.computeIfAbsent(pluginId, k -> new HashMap<>(4));
        superMap.put(name, o);
    }

    public static Object getSupper(String name){
        for (Map<String, Object> superMap : PLUGIN_SUPPER_MAP.values()) {
            Object o = superMap.get(name);
            if(o != null){
                return o;
            }
        }
        return null;
    }

    public static Object getSupper(String pluginId, String name){
        Map<String, Object> superMap = PLUGIN_SUPPER_MAP.get(pluginId);
        if(superMap == null || superMap.isEmpty()){
            return null;
        }
        return superMap.get(name);
    }


    /**
     * 调用者的接口工厂
     * @param <T> 接口泛型
     */
    private static class CallerInterfaceFactory<T> implements FactoryBean<T> {

        private Class<T> callerInterface;
        private Caller callerAnnotation;

        @Override
        public T getObject() throws Exception {
            ClassLoader classLoader = callerInterface.getClassLoader();
            Class<?>[] interfaces = new Class[]{callerInterface};
            ProxyHandler proxy = new ProxyHandler(callerAnnotation);
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

        public Class<T> getCallerInterface() {
            return callerInterface;
        }

        public void setCallerInterface(Class<T> callerInterface) {
            this.callerInterface = callerInterface;
        }

        public Caller getCallerAnnotation() {
            return callerAnnotation;
        }

        public void setCallerAnnotation(Caller callerAnnotation) {
            this.callerAnnotation = callerAnnotation;
        }
    }



    /**
     * 代理类
     */
    private static class ProxyHandler implements InvocationHandler {

        private final Caller callerAnnotation;

        private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private ProxyHandler(Caller callerAnnotation) {
            this.callerAnnotation = callerAnnotation;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object supplierObject = null;
            String pluginId = callerAnnotation.pluginId();
            if(StringUtils.isNullOrEmpty(pluginId)){
                supplierObject = getSupper(callerAnnotation.value());
            } else {
                supplierObject = getSupper(pluginId, callerAnnotation.value());
            }
            if(supplierObject == null){
                if(StringUtils.isNullOrEmpty(pluginId)){
                    throw new Exception("Not found '" + callerAnnotation.value() + "' supplier object");
                } else {
                    throw new Exception("Not found '" + callerAnnotation.value() + "' supplier object in plugin '" +
                            pluginId + "'");
                }
            }

            Caller.Method callerMethod = method.getAnnotation(Caller.Method.class);
            if(args == null){
                args = new Object[]{};
            }
            if(callerMethod == null){
                return notAnnotationInvoke(method, supplierObject, args);
            } else {
                return annotationInvoke(method, callerMethod, supplierObject, args);
            }
        }

        /**
         * 有注解的调用
         * @param method 调用接口的方法
         * @param callerMethod 调用者方法注解
         * @param supplierObject 调用者对象
         * @param args 传入参数
         * @return 返回值
         * @throws Throwable 异常
         */
        private Object annotationInvoke(Method method, Caller.Method callerMethod,
                                        Object supplierObject, Object[] args) throws Throwable{

            String callerMethodName = callerMethod.value();
            Class<?> supplierClass = supplierObject.getClass();
            Method[] methods = supplierClass.getMethods();
            Method supplierMethod = null;
            for (Method m : methods) {
                Supplier.Method supplierMethodAnnotation = m.getAnnotation(Supplier.Method.class);
                if(supplierMethodAnnotation == null){
                    continue;
                }
                if(Objects.equals(supplierMethodAnnotation.value(), callerMethodName)){
                    supplierMethod = m;
                    break;
                }
            }
            if(supplierMethod == null){
                // 如果为空, 说明没有找到被调用者的注解, 则走没有注解的代理调用。
                return notAnnotationInvoke(method, supplierObject, args);
            }
            Class<?>[] parameterTypes = supplierMethod.getParameterTypes();
            if(parameterTypes.length != args.length){
                // 参数不匹配
                return notAnnotationInvoke(method, supplierObject, args);
            }
            Object[] supplierArgs = new Object[args.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                Object arg = args[i];
                if(parameterType == arg.getClass()){
                    supplierArgs[i] = arg;
                } else {
                    // 类型不匹配, 尝试使用json序列化
                    String json = OBJECT_MAPPER.writeValueAsString(arg);
                    Object serializeObject = OBJECT_MAPPER.readValue(json, parameterType);
                    supplierArgs[i] = serializeObject;
                }
            }
            Object invokeReturn = supplierMethod.invoke(supplierObject, supplierArgs);
            return getReturnObject(invokeReturn, method);
        }

        /**
         * 没有注解调用
         * @param method 调用接口的方法
         * @param supplierObject 提供者对象
         * @param args 传入参数
         * @return 返回值
         * @throws Throwable 异常
         */
        private Object notAnnotationInvoke(Method method, Object supplierObject, Object[] args) throws Throwable{
            String name = method.getName();
            Class<?>[] argClasses = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argClasses[i] = args[i].getClass();
            }
            Class<?> supplierClass = supplierObject.getClass();
            Method supplierMethod = supplierClass.getMethod(name, argClasses);
            Object invokeReturn = supplierMethod.invoke(supplierObject, args);
            return getReturnObject(invokeReturn, method);
        }


        /**
         * 得到返回值对象
         * @param invokeReturn 反射调用后返回的对象
         * @param method 调用接口的方法
         * @return 返回值对象
         * @throws Throwable Throwable
         */
        private Object getReturnObject(Object invokeReturn, Method method) throws Throwable{
            if(invokeReturn == null){
                return null;
            }
            Class<?> returnType = method.getReturnType();
            if(ClassUtils.isAssignable(invokeReturn.getClass(),returnType)){
                return invokeReturn;
            } else {
                String json = OBJECT_MAPPER.writeValueAsString(invokeReturn);
                return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructType(method.getGenericReturnType()) );
            }
        }
    }

}
