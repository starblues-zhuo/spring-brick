package com.gitee.starblues.bootstrap.processor.interceptor;

/**
 * 插件拦截器注册者
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginInterceptorRegister {

    /**
     * 拦截器注册者
     * @param registry 注册对象
     */
    void registry(PluginInterceptorRegistry registry);


}
