package com.gitee.starblues.utils;

import org.pf4j.PluginWrapper;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AopUtils {

    private static InfrastructureAdvisorAutoProxyCreator infrastructureAdvisorAutoProxyCreator;

    /**
     * 解决AOP无法代理到插件类的问题
     * @param applicationContext 插件包装类
     */
    public static synchronized void registered(ApplicationContext applicationContext) {
        Object bean = applicationContext.getBean("org.springframework.aop.config.internalAutoProxyCreator");
        if(bean instanceof InfrastructureAdvisorAutoProxyCreator){
            infrastructureAdvisorAutoProxyCreator = (InfrastructureAdvisorAutoProxyCreator) bean;
        }
    }

    /**
     * 解决AOP无法代理到插件类的问题
     * @param pluginWrapper 插件包装类
     */
    public static synchronized void resolveAop(PluginWrapper pluginWrapper){
        if(infrastructureAdvisorAutoProxyCreator == null){
            return;
        }
        infrastructureAdvisorAutoProxyCreator.setBeanClassLoader(pluginWrapper.getPluginClassLoader());
    }

    /**
     * 恢复AOP 的 BeanClassLoader
     */
    public static synchronized void recoverAop(){
        if(infrastructureAdvisorAutoProxyCreator == null){
            return;
        }
        infrastructureAdvisorAutoProxyCreator.setBeanClassLoader(ClassUtils.getDefaultClassLoader());
    }

}
