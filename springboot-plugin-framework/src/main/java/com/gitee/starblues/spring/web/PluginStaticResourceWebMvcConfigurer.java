package com.gitee.starblues.spring.web;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件静态资源webMvc配置
 * @author starBlues
 * @version 3.0.0
 */
public class PluginStaticResourceWebMvcConfigurer implements WebMvcConfigurer {

    private final PluginStaticResourceConfig resourceConfig;

    public PluginStaticResourceWebMvcConfigurer(PluginStaticResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathPattern = "/" + resourceConfig.getPathPrefix() + "/**";
        ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(pathPattern);
        CacheControl cacheControl = resourceConfig.getCacheControl();
        if(cacheControl != null){
            resourceHandlerRegistration.setCacheControl(cacheControl);
        } else {
            resourceHandlerRegistration.setCacheControl(CacheControl.noStore());
        }
        resourceHandlerRegistration
                .resourceChain(false)
                .addResolver(new PluginStaticResourceResolver());
    }


}
