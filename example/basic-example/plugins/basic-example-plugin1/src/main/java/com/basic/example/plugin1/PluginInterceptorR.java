package com.basic.example.plugin1;

import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegistration;
import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.annotation.Resource;

/**
 * @author starBlues
 * @version 1.0
 */
@Component
public class PluginInterceptorR implements PluginInterceptorRegister {

    @Resource
    private PluginInterceptor1 pluginInterceptor1;


    @Override
    public void registry(PluginInterceptorRegistry registry) {
        registry.addInterceptor(pluginInterceptor1, PluginInterceptorRegistry.Type.PLUGIN)
                .addPathPatterns("plugin1/**");
    }
}
