package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.InvokeBeanRegistrar;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 处理插件中类之间相互调用的的功能. 主要获取被调用者的对象,  然后存储到被调用者容器中
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginInvokePostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PluginInvokePostProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            try {
                Set<String> supperBeanNames = pluginRegistryInfo.getExtension(InvokeBeanRegistrar.SUPPLIER_KEY);
                if(supperBeanNames == null || supperBeanNames.isEmpty()){
                    continue;
                }
                GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
                for (String supperBeanName : supperBeanNames) {
                    if(pluginApplicationContext.containsBean(supperBeanName)){
                        Object bean = pluginApplicationContext.getBean(supperBeanName);
                        InvokeBeanRegistrar.addSupper(pluginId, supperBeanName, getTarget(bean));
                    }
                }
            } catch (Exception e){
                log.error("Process plugin '{}' supper bean exception.", pluginId, e);
            }
        }
    }


    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception{
        // 什么也不做
    }

    /**
     * 获取 目标对象. 解决@Supplier中开启事务，@Caller中调用异常
     * fix: https://gitee.com/starblues/springboot-plugin-framework-parent/issues/I4BOSK
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception Exception
     */
    public Object getTarget(Object proxy) throws Exception {
        //不是代理对象
        if(!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else {
            //cglib
            return getCglibProxyTargetObject(proxy);
        }
    }

    private Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
    }

}
