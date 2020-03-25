package com.gitee.starblues.extension.resources.resolver;

import com.gitee.starblues.extension.resources.StaticResourceExtension;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册插件的WebMvc的配置
 *
 * @author zhangzhuo
 * @version 2.2.1
 */
public class ResourceWebMvcConfigurer implements WebMvcConfigurer {


    public ResourceWebMvcConfigurer() {
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathPattern = "/" + StaticResourceExtension.getPluginStaticResourcePathPrefix() + "/**";
        ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(pathPattern);
        CacheControl cacheControl = StaticResourceExtension.getPluginStaticResourcesCacheControl();
        if(cacheControl != null){
            resourceHandlerRegistration.setCacheControl(cacheControl);
        } else {
            resourceHandlerRegistration.setCacheControl(CacheControl.noStore());
        }
        resourceHandlerRegistration
                .resourceChain(false)
                .addResolver(new PluginResourceResolver());
    }


}
