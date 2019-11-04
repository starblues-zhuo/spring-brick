package com.gitee.starblues.extension.resources.resolver;

import com.gitee.starblues.integration.IntegrationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ResourceWebMvcConfigurer implements WebMvcConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(ResourceWebMvcConfigurer.class);


    private final IntegrationConfiguration configuration;

    public ResourceWebMvcConfigurer(ApplicationContext applicationContext) {
        this.configuration = applicationContext.getBean(IntegrationConfiguration.class);
    }



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + configuration.pluginStaticResourcePathPrefix() + "/**")
                .resourceChain(configuration.isCachePluginStaticResources())
                .addResolver(new PluginResourceResolver());
    }


}
