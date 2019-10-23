package com.gitee.starblues.utils;

import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyProcessorSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AOP 无法找到插件类的解决工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class AopUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AopUtils.class);

    private static AtomicBoolean isRecover = new AtomicBoolean(true);

    private static final List<ProxyWrapper> PROXY_WRAPPERS = new ArrayList<>();

    private AopUtils(){}

    /**
     * 解决AOP无法代理到插件类的问题
     * @param applicationContext 插件包装类
     */
    public static synchronized void registered(ApplicationContext applicationContext) {
        Map<String, ProxyProcessorSupport> beansOfType = applicationContext
                .getBeansOfType(ProxyProcessorSupport.class);
        if(beansOfType.isEmpty()){
            LOG.warn("Not found ProxyProcessorSupports, And Plugin AOP can't used");
            return;
        }
        for (ProxyProcessorSupport support : beansOfType.values()) {
            if(support == null){
                continue;
            }
            ProxyWrapper proxyWrapper = new ProxyWrapper();
            proxyWrapper.setProxyProcessorSupport(support);
            PROXY_WRAPPERS.add(proxyWrapper);
        }
    }

    /**
     * 解决AOP无法代理到插件类的问题
     * @param pluginWrapper 插件包装类
     */
    public static synchronized void resolveAop(PluginWrapper pluginWrapper){
        if(PROXY_WRAPPERS.isEmpty()){
            LOG.warn("ProxyProcessorSupports is empty, And Plugin AOP can't used");
            return;
        }
        if(!isRecover.get()){
            throw new RuntimeException("Not invoking resolveAop(). And can not AopUtils.resolveAop");
        }
        isRecover.set(false);
        ClassLoader pluginClassLoader = pluginWrapper.getPluginClassLoader();
        for (ProxyWrapper proxyWrapper : PROXY_WRAPPERS) {
            ProxyProcessorSupport proxyProcessorSupport = proxyWrapper.getProxyProcessorSupport();
            ClassLoader classLoader = getClassLoader(proxyProcessorSupport);
            proxyWrapper.setOriginalClassLoader(classLoader);
            proxyProcessorSupport.setProxyClassLoader(pluginClassLoader);
        }
    }

    /**
     * 恢复AOP 的 BeanClassLoader
     */
    public static synchronized void recoverAop(){
        if(PROXY_WRAPPERS.isEmpty()){
            return;
        }
        for (ProxyWrapper proxyWrapper : PROXY_WRAPPERS) {
            ProxyProcessorSupport proxyProcessorSupport = proxyWrapper.getProxyProcessorSupport();
            proxyProcessorSupport.setProxyClassLoader(proxyWrapper.getOriginalClassLoader());
        }
        isRecover.set(true);
    }

    /**
     * 反射获取代理支持处理者的ClassLoader属性值
     * @param proxyProcessorSupport proxyProcessorSupport
     * @return ClassLoader
     */
    private static ClassLoader getClassLoader(ProxyProcessorSupport proxyProcessorSupport){
        Class aClass = proxyProcessorSupport.getClass();
        while (aClass != null){
            if(aClass != ProxyProcessorSupport.class){
                aClass = aClass.getSuperclass();
                continue;
            }
            Field[] declaredFields = aClass.getDeclaredFields();
            if(declaredFields == null || declaredFields.length == 0){
                break;
            }
            for (Field field : declaredFields) {
                if(Objects.equals("proxyClassLoader", field.getName()) || field.getType() == ClassLoader.class){
                    field.setAccessible(true);
                    try {
                        Object o = field.get(proxyProcessorSupport);
                        if(o instanceof ClassLoader){
                            return (ClassLoader) o;
                        } else {
                            LOG.warn("Get {} classLoader type not is ClassLoader type,  And Return DefaultClassLoader",
                                    aClass.getName());
                            return ClassUtils.getDefaultClassLoader();
                        }
                    } catch (IllegalAccessException e) {
                        LOG.error("Get {} classLoader failure {}, And Return DefaultClassLoader",
                                aClass.getName(),
                                e.getMessage());
                        return ClassUtils.getDefaultClassLoader();
                    }
                }
            }

        }
        LOG.warn("Not found classLoader field, And Return DefaultClassLoader",
                aClass.getName());
        return ClassUtils.getDefaultClassLoader();
    }



    /**
     * 代理包装类
     */
    private static class ProxyWrapper{
        ProxyProcessorSupport proxyProcessorSupport;
        ClassLoader originalClassLoader;

        ProxyProcessorSupport getProxyProcessorSupport() {
            return proxyProcessorSupport;
        }

        void setProxyProcessorSupport(ProxyProcessorSupport proxyProcessorSupport) {
            this.proxyProcessorSupport = proxyProcessorSupport;
        }

        ClassLoader getOriginalClassLoader() {
            return originalClassLoader;
        }

        void setOriginalClassLoader(ClassLoader originalClassLoader) {
            this.originalClassLoader = originalClassLoader;
        }

        @Override
        public String toString() {
            return "ProxyWrapper{" +
                    "proxyProcessorSupport=" + proxyProcessorSupport +
                    ", originalClassLoader=" + originalClassLoader +
                    '}';
        }
    }

}
