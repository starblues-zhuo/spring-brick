package com.basic.example.plugin2;

import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author starBlues
 * @version 1.0
 */
@Component
public class PluginInterceptorR implements PluginInterceptorRegister, HandlerInterceptor {

    @Override
    public void registry(PluginInterceptorRegistry registry) {
        registry.addInterceptor(this, PluginInterceptorRegistry.Type.PLUGIN);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("拦截器进入插件2");
    }
}
