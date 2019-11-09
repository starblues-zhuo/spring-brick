package com.gitee.starblues.factory.process.post.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.factory.PluginInfoContainer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.classs.group.CallerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.SupplierGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.utils.AopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 处理插件中类之间相互调用的的功能
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginInvokePostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String KEY_SUPPERS = "PluginInvokePostProcessorSuppers";
    private final String KEY_CALLERS = "PluginInvokePostProcessorCallers";

    private final GenericApplicationContext applicationContext;
    private final SpringBeanRegister springBeanRegister;

    public PluginInvokePostProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            AopUtils.resolveAop(pluginRegistryInfo.getPluginWrapper());
            try {
                List<Class<?>> suppers = pluginRegistryInfo.getGroupClasses(SupplierGroup.GROUP_ID);
                if(suppers == null){
                    continue;
                }
                processSupper(pluginRegistryInfo, suppers);
            } finally {
                AopUtils.recoverAop();
            }
        }
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            AopUtils.resolveAop(pluginRegistryInfo.getPluginWrapper());
            try {
                List<Class<?>> callers = pluginRegistryInfo.getGroupClasses(CallerGroup.GROUP_ID);
                if(callers == null){
                    continue;
                }
                processCaller(pluginRegistryInfo, callers);
            } finally {
                AopUtils.recoverAop();
            }
        }
    }


    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception{
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            Set<String> supperNames = pluginRegistryInfo.getProcessorInfo(getKey(KEY_SUPPERS, pluginRegistryInfo));
            Set<String> callerNames = pluginRegistryInfo.getProcessorInfo(getKey(KEY_CALLERS, pluginRegistryInfo));
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            unregister(pluginId, supperNames);
            unregister(pluginId, callerNames);
        }
    }

    /**
     * 处理被调用者
     * @param pluginRegistryInfo 插件注册的信息
     * @param supperClasses 被调用者集合
     * @throws Exception 处理异常
     */
    private void processSupper(PluginRegistryInfo pluginRegistryInfo,
                               List<Class<?>> supperClasses) throws Exception {
        if(supperClasses.isEmpty()){
            return;
        }
        Set<String> beanNames = new HashSet<>();
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (Class<?> supperClass : supperClasses) {
            if(supperClass == null){
                continue;
            }
            Supplier supplier = supperClass.getAnnotation(Supplier.class);
            if(supplier == null){
                continue;
            }
            String beanName = supplier.value();
            if(PluginInfoContainer.existRegisterBeanName(beanName)){
                String error = MessageFormat.format(
                        "Plugin {0} : Bean @Supplier name {1} already exist of {2}",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(), beanName, supperClass.getName());
                throw new Exception(error);
            }
            springBeanRegister.registerOfSpecifyName(pluginId, beanName, supperClass);
            beanNames.add(beanName);
        }
        pluginRegistryInfo.addProcessorInfo(getKey(KEY_SUPPERS, pluginRegistryInfo), beanNames);
    }

    /**
     * 处理调用者
     * @param pluginRegistryInfo 插件注册的信息
     * @param callerClasses 调用者集合
     * @throws Exception 处理异常
     */
    private void processCaller(PluginRegistryInfo pluginRegistryInfo, List<Class<?>> callerClasses) throws Exception {
        if(callerClasses == null || callerClasses.isEmpty()){
            return;
        }
        Set<String> beanNames = new HashSet<>();
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (Class<?> callerClass : callerClasses) {
            Caller caller = callerClass.getAnnotation(Caller.class);
            if(caller == null){
                continue;
            }
            Object supper = applicationContext.getBean(caller.value());
            if(supper == null){
                return;
            }
            String beanName = springBeanRegister.register(pluginId, callerClass, (beanDefinition) ->{
                beanDefinition.getPropertyValues().add("callerInterface", callerClass);
                beanDefinition.getPropertyValues().add("supper", supper);
                beanDefinition.setBeanClass(CallerInterfaceFactory.class);
                beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            });
            beanNames.add(beanName);
        }
        pluginRegistryInfo.addProcessorInfo(getKey(KEY_CALLERS, pluginRegistryInfo), beanNames);
    }

    /**
     * 得到往RegisterPluginInfo->processorInfo 保存的key
     * @param key key前缀
     * @param pluginRegistryInfo 插件注册的信息
     * @return String
     */
    private String getKey(String key, PluginRegistryInfo pluginRegistryInfo){
        return key + "_" + pluginRegistryInfo.getPluginWrapper().getPluginId();
    }

    /**
     * 通过beanName卸载
     * @param pluginId 插件id
     * @param beanNames beanNames集合
     */
    private void unregister(String pluginId, Set<String> beanNames){
        if(beanNames == null || beanNames.isEmpty()){
            return;
        }
        for (String beanName : beanNames) {
            springBeanRegister.unregister(pluginId, beanName);
        }
    }

    /**
     * 代理类
     */
    private static class ProxyHandler implements InvocationHandler {

        private final Object supplier;
        private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private ProxyHandler(Object supplier) {
            this.supplier = supplier;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Caller.Method callerMethod = method.getAnnotation(Caller.Method.class);
            if(callerMethod == null){
                return notAnnotationInvoke(method, args);
            } else {
                return annotationInvoke(method, callerMethod, args);
            }
        }

        /**
         * 有注解的调用
         * @param method 调用接口的方法
         * @param callerMethod 调用者方法注解
         * @param args 传入参数
         * @return 返回值
         * @throws Throwable 异常
         */
        private Object annotationInvoke(Method method, Caller.Method callerMethod, Object[] args) throws Throwable{
            String callerMethodName = callerMethod.value();
            Class<?> aClass = supplier.getClass();
            Method[] methods = aClass.getMethods();
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
                return notAnnotationInvoke(method, args);
            }
            Class<?>[] parameterTypes = supplierMethod.getParameterTypes();
            if(parameterTypes.length != args.length){
                // 参数不匹配
                return notAnnotationInvoke(method, args);
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
            Object invokeReturn = supplierMethod.invoke(supplier, supplierArgs);
            return getReturnObject(invokeReturn, method);
        }

        /**
         * 没有注解调用
         * @param method 调用接口的方法
         * @param args 传入参数
         * @return 返回值
         * @throws Throwable 异常
         */
        private Object notAnnotationInvoke(Method method, Object[] args) throws Throwable{
            String name = method.getName();
            Class<?>[] argClasses = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argClasses[i] = args[i].getClass();
            }
            Method supplierMethod = supplier.getClass().getMethod(name, argClasses);
            Object invokeReturn = supplierMethod.invoke(supplier, args);
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
            if(returnType == invokeReturn.getClass()){
                return invokeReturn;
            } else {
                String json = OBJECT_MAPPER.writeValueAsString(invokeReturn);
                return OBJECT_MAPPER.readValue(json, returnType);
            }
        }


    }

    /**
     * 调用者的接口工厂
     * @param <T> 接口泛型
     */
    private static class CallerInterfaceFactory<T> implements FactoryBean<T> {

        private Class<T> callerInterface;
        private Object supper;


        @Override
        public T getObject() throws Exception {
            ClassLoader classLoader = callerInterface.getClassLoader();
            Class<?>[] interfaces = new Class[]{callerInterface};
            ProxyHandler proxy = new ProxyHandler(supper);
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

        public Object getSupper() {
            return supper;
        }

        public void setSupper(Object supper) {
            this.supper = supper;
        }
    }

}
