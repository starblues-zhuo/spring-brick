package com.gitee.starblues.utils;

import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * AOP 无法找到插件类的解决工具类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class AopUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AopUtils.class);
    private static final String AOP_NAME = "org.springframework.aop.config.internalAutoProxyCreator";

    private static AbstractAutoProxyCreator abstractAutoProxyCreator;

    private AopUtils(){}

    /**
     * 解决AOP无法代理到插件类的问题
     * @param applicationContext 插件包装类
     */
    public static synchronized void registered(ApplicationContext applicationContext) {
        if(!applicationContext.containsBean(AOP_NAME)){
            LOG.warn("Not found " + AOP_NAME + ", And Plugin AOP can't used");
            return;
        }
        Object bean = applicationContext.getBean(AOP_NAME);
        if(bean instanceof AbstractAutoProxyCreator){
            abstractAutoProxyCreator = (AbstractAutoProxyCreator) bean;
        } else {
            LOG.warn(AOP_NAME + " type is not AbstractAutoProxyCreator, And Plugin AOP can't used");
        }
    }

    /**
     * 解决AOP无法代理到插件类的问题
     * @param pluginWrapper 插件包装类
     */
    public static synchronized void resolveAop(PluginWrapper pluginWrapper){
        if(abstractAutoProxyCreator == null){
            LOG.warn(AOP_NAME + " is null, And Plugin AOP can't used");
            return;
        }
        abstractAutoProxyCreator.setBeanClassLoader(pluginWrapper.getPluginClassLoader());
    }

    /**
     * 恢复AOP 的 BeanClassLoader
     */
    public static synchronized void recoverAop(){
        if(abstractAutoProxyCreator == null){
            return;
        }
        abstractAutoProxyCreator.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
    }

}
